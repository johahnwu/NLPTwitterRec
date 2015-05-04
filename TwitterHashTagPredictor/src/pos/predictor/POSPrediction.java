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

public class POSPrediction {
	private TwitterPOSTagger posTagger;

	public POSPrediction() throws IOException {
		posTagger = new TwitterPOSTagger();
	}

	public POSPrediction(TwitterPOSTagger tagger) {
		posTagger = tagger;
	}

	public POSPredictionModel trainModel(InputFileReader reader)
			throws IOException {
		POSPredictionModel predictionModel = new POSPredictionModel();
		Map<String, Double> unigramModel = predictionModel.getModel();
		Map<String, Double> bigramModel = new HashMap<String, Double>();
		String tweet;
		while ((tweet = reader.getNextLine()) != null) {
			TweetHashTagTuple tuple = Utils.convertInputToHashTagTuple(tweet);
			// some of the input is bad
			if (tuple == null)
				continue;

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

		double totalHashTags = 0.0;
		// find the total number of hash tags
		for (String partOfSpeech : unigramModel.keySet()) {
			totalHashTags += unigramModel.get(partOfSpeech);
		}
		// normalize each part of speech by the total number of hash tags
		for (String partOfSpeech : unigramModel.keySet()) {
			unigramModel.put(partOfSpeech, unigramModel.get(partOfSpeech)
					/ totalHashTags);
		}

		for (String bigramPOS : bigramModel.keySet()) {
			bigramModel.put(bigramPOS, bigramModel.get(bigramPOS)
					/ totalHashTags);
		}

		// create map that contains both unigram and bigram
		Map<String, Double> finalModel = new HashMap<String, Double>(
				unigramModel);
		finalModel.putAll(bigramModel);

		predictionModel.setModel(finalModel);
		return predictionModel;
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
		POSPrediction prediction = new POSPrediction();
		System.out.println("Loading input files");
		InputFileReader fileReader = new InputFileReader(new File(inputFile));
		System.out.println("Training pos model...");
		POSPredictionModel trainedModel = prediction.trainModel(fileReader);
		System.out.println("Writing model to file");
		trainedModel.writeModelToFile(new File(outputFile));
		System.out.println("Done!");
	}
}
