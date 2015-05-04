package pos.predictor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import pos.tagger.TaggedWord;

public class POSPredictionModel {

	private Map<String, Double> posTagMultipliers;
	public static final String START_TAG = " <S>";
	public static final String NO_MODEL_FILE = "models/pos_nomodel";

	public POSPredictionModel() {
		this(null);
	}

	public POSPredictionModel(String modelFileName) {
		posTagMultipliers = new HashMap<String, Double>();
		if (modelFileName != null)
			loadModel(modelFileName);
	}

	public Map<String, Double> getModel() {
		return this.posTagMultipliers;
	}

	public void setModel(Map<String, Double> newModel) {
		posTagMultipliers = newModel;
	}

	public void loadModel(String modelFileName) {
		try (BufferedReader inputModelReader = new BufferedReader(
				new FileReader(modelFileName))) {
			posTagMultipliers.clear();
			String line;
			while ((line = inputModelReader.readLine()) != null) {
				String[] splitLine = line.split("\\s+");
				String pos = splitLine[0];
				double multiplier = Double.valueOf(splitLine[1]);
				if (posTagMultipliers.containsKey(pos)) {
					System.err.println(String.format(
							"%s already exists in the cache with value %.4f",
							pos, posTagMultipliers.get(pos)));
					System.err.println(String.format(
							"Replacing %.4f with %.4f",
							posTagMultipliers.get(pos), multiplier));
				}
				posTagMultipliers.put(pos, multiplier);

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeModelToFile(File file) {
		try (PrintWriter outputWriter = new PrintWriter(file)) {
			for (String key : posTagMultipliers.keySet()) {
				outputWriter.println(key + "\t" + posTagMultipliers.get(key));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Multiply the confidence in that the POS is correctly tagged with
	 * probability of that POS tag being relevant. Returns 0 if the POS is not
	 * recognized in the cachedPOSTags
	 * 
	 * @param tw
	 *            the Tagged Word containing word, pos, and confidence
	 * @return confidence of pos tag * trained confidence
	 */
	public double calculateConfidence(TaggedWord tw) {
		if (posTagMultipliers.containsKey(tw.pos))
			return interpolateConfidence(tw.confidence)
					* posTagMultipliers.get(tw.pos);
		return 0;
	}

	private double interpolateConfidence(double d) {
		return .5 + .5 * d;
	}

	public static String concatWordsWithDelimiter(String a, String b) {
		return a + "/" + b;
	}

	public static void main(String[] args) {
		POSPredictionModel currentModel = new POSPredictionModel(NO_MODEL_FILE);
		Map<String, Double> model = currentModel.getModel();
		for (String s : model.keySet()) {
			System.out.println(s + ":" + model.get(s));
		}
	}
}
