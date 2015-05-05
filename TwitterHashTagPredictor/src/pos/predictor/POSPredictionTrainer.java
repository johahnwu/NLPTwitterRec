package pos.predictor;

import io.InputFileReader;
import io.TweetHashTagTuple;
import io.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pos.tagger.TaggedWord;
import pos.tagger.TwitterPOSTagger;

public class POSPredictionTrainer {
	private TwitterPOSTagger posTagger;
	private Map<String, Double> unigramModel;
	private Map<String, Double> bigramModel;

	public POSPredictionTrainer() throws IOException {
		posTagger = new TwitterPOSTagger();
	}

	public POSPredictionTrainer(TwitterPOSTagger tagger) {
		posTagger = tagger;
	}

	public POSPredictionModel trainModel(List<TweetHashTagTuple> trainingSet) {
		initializeModels();
		for (TweetHashTagTuple tuple : trainingSet) {
			addTupleToModels(tuple);
		}
		return new POSPredictionModel(normalizeAndCombineModels());
	}

	public POSPredictionModel trainModel(InputFileReader reader)
			throws IOException {
		initializeModels();
		String tweet;
		while ((tweet = reader.getNextLine()) != null) {
			TweetHashTagTuple tuple = Utils.convertInputToHashTagTuple(tweet);
			// some of the input is bad
			if (tuple == null)
				continue;
			addTupleToModels(tuple);
		}

		return new POSPredictionModel(normalizeAndCombineModels());
	}

	private void initializeModels() {
		unigramModel = new HashMap<String, Double>();
		bigramModel = new HashMap<String, Double>();
	}

	private void addTupleToModels(TweetHashTagTuple tuple) {
		// get the tagged words/pos
		List<TaggedWord> taggedWords = posTagger.tagSentence(tuple.text);

		// previous POS for bigram model
		String previousPOS = POSPredictionModel.START_TAG;
		for (TaggedWord tw : taggedWords) {
			String partOfSpeech = tw.pos;
			String sanitizedWord = Utils.sanitizeWordToHashTag(tw.word);

			if (!unigramModel.containsKey(partOfSpeech)) {
				unigramModel.put(partOfSpeech, 0.0);
			}

			String bigramPOS = POSPredictionModel.concatWordsWithDelimiter(
					previousPOS, partOfSpeech);

			if (!bigramModel.containsKey(bigramPOS)) {
				bigramModel.put(bigramPOS, 0.0);
			}
			if (tuple.hashTags.contains(sanitizedWord)) {
				unigramModel.put(partOfSpeech,
						unigramModel.get(partOfSpeech) + 1);
				bigramModel.put(bigramPOS, bigramModel.get(bigramPOS) + 1);
			}

			previousPOS = partOfSpeech;
		}
	}

	private Map<String, Double> normalizeAndCombineModels() {
		double maximumProb = 0.0;
		// find the highest value in the value set
		for (String partOfSpeech : unigramModel.keySet()) {
			// totalHashTags += unigramModel.get(partOfSpeech);
			maximumProb = Math.max(maximumProb, unigramModel.get(partOfSpeech));
		}
		// normalize each part of speech probability
		for (String partOfSpeech : unigramModel.keySet()) {
			unigramModel.put(partOfSpeech, unigramModel.get(partOfSpeech)
					/ maximumProb);
		}

		maximumProb = 0.0;
		for (String bigramPOS : bigramModel.keySet()) {
			maximumProb = Math.max(maximumProb, bigramModel.get(bigramPOS));
		}
		for (String bigramPOS : bigramModel.keySet()) {
			bigramModel
					.put(bigramPOS, bigramModel.get(bigramPOS) / maximumProb);
		}

		// create map that contains both unigram and bigram
		// we just throw it all into the same map
		Map<String, Double> finalModel = new HashMap<String, Double>(
				unigramModel);
		finalModel.putAll(bigramModel);
		return finalModel;
	}

	public static void main(String[] args) throws IOException {
		String inputFile = "data/randomized_tweets";
		String outputFile = "models/normal_posmodel";

		int index = 0;
		while (index < args.length) {
			if (args[index].equalsIgnoreCase("--inputFile")) {
				inputFile = args[index + 1];
				index += 2;
			} else if (args[index].equalsIgnoreCase("--outputFile")) {
				outputFile += args[index + 1];
				index += 2;
			} else {
				index += 1;
			}
		}

		System.out.println("Initiating prediction model.");
		POSPredictionTrainer predictionTrainer = new POSPredictionTrainer();
		System.out.println("Loading input files");
		InputFileReader fileReader = new InputFileReader(new File(inputFile));
		System.out.println("Training pos model...");
		POSPredictionModel trainedModel = predictionTrainer
				.trainModel(fileReader);
		System.out.println("Writing model to file");
		trainedModel.writeModelToFile(new File(outputFile));
		System.out.println("Done!");
	}
}
