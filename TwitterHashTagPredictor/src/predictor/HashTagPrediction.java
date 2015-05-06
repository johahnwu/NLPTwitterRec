package predictor;

public class HashTagPrediction implements Comparable<HashTagPrediction> {
	public String hashtag;
	public double confidence;

	@Override
	public int compareTo(HashTagPrediction o) {
		if (this.confidence > o.confidence)
			return 1;
		else if (this.confidence < o.confidence)
			return -1;
		else
			return 0;
	}

	@Override
	public String toString() {
		return "[" + hashtag + ", " + confidence + "]";
	}
}
