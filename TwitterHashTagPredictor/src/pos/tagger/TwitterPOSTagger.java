package pos.tagger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import cmu.arktweetnlp.RunTagger.Decoder;
import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Twokenize;
import cmu.arktweetnlp.impl.ModelSentence;
import cmu.arktweetnlp.impl.Sentence;

public class TwitterPOSTagger {
	Tagger tagger;

	public Decoder decoder = Decoder.GREEDY;
	public static final String COARSE_MODEL = "models/model.20120919";
	public static final String PENN_MODEL = "models/model.ritter_ptb_alldata_fixed.20130723";

	public TwitterPOSTagger() throws IOException {
		this(PENN_MODEL);
	}

	public TwitterPOSTagger(String modelFile) throws IOException {
		tagger = new Tagger();
		tagger.loadModel(modelFile);
	}

	public void runTagger(String inputFile, String outputFile) {
		try (BufferedReader input = new BufferedReader(
				new FileReader(inputFile));
				PrintWriter output = new PrintWriter(outputFile);) {
			String line;
			while ((line = input.readLine()) != null) {
				List<TaggedWord> taggedSentence = tagSentence(line);
				for (TaggedWord tw : taggedSentence) {
					writeTaggedWordToOutput(tw, output);
				}
				output.println();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void writeTaggedWordToOutput(TaggedWord tw, PrintWriter pw) {
		pw.println(String
				.format("%s\t%s\t%.4f", tw.word, tw.pos, tw.confidence));
	}

	public List<TaggedWord> tagSentence(String text) {
		List<TaggedWord> out = new ArrayList<TaggedWord>();
		Sentence sentence = new Sentence();
		sentence.tokens = Twokenize.tokenizeRawTweetText(text);
		ModelSentence modelSentence = null;

		if (sentence.T() > 0) {
			modelSentence = new ModelSentence(sentence.T());
			tagger.featureExtractor.computeFeatures(sentence, modelSentence);
			goDecode(modelSentence);
		}
		for (int t = 0; t < sentence.T(); t++) {
			TaggedWord tw = new TaggedWord();
			tw.word = sentence.tokens.get(t);
			tw.pos = tagger.model.labelVocab.name(modelSentence.labels[t]);
			tw.confidence = modelSentence.confidences[t];
			out.add(tw);
		}
		return out;
	}

	/** Runs the correct algorithm (make config option perhaps) **/
	public void goDecode(ModelSentence mSent) {
		if (decoder == Decoder.GREEDY) {
			tagger.model.greedyDecode(mSent, true);
		} else if (decoder == Decoder.VITERBI) {
			// if (showConfidence) throw new
			// RuntimeException("--confidence only works with greedy decoder right now, sorry, yes this is a lame limitation");
			tagger.model.viterbiDecode(mSent);
		}
	}

	public static void main(String[] args) throws IOException {
		String inputFile = "examples/random_input";
		String outputFile = "random_output";
		String modelFile = PENN_MODEL;

		int index = 0;
		while (index < args.length) {
			if (args[index].equalsIgnoreCase("--inputFile")) {
				inputFile = args[index + 1];
				index += 2;
			} else if (args[index].equalsIgnoreCase("--outputFile")) {
				outputFile = args[index + 1];
				index += 2;
			} else if (args[index].equalsIgnoreCase("--modelFile")) {
				modelFile = args[index + 1];
				index += 2;
			} else {
				index += 1;
			}
		}

		TwitterPOSTagger posTagger = new TwitterPOSTagger(modelFile);
		posTagger.runTagger(inputFile, outputFile);
	}
}
