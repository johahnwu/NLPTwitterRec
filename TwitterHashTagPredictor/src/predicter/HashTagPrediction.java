package predicter;

public class HashTagPrediction implements Comparable<HashTagPrediction>{
	String hashtag;
	double confidence;
	@Override
	public int compareTo(HashTagPrediction o) {
		if (this.confidence > o.confidence)
			return 1;
		else if (this.confidence < o.confidence)
			return -1;
		else
			return 0;
	}
}
