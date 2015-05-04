package pos.tagger;

public class TaggedWord implements Comparable<TaggedWord> {
	public String word;
	public String pos;
	public double confidence;

	@Override
	public int compareTo(TaggedWord o) {
		if (this.confidence > o.confidence)
			return 1;
		else if (this.confidence < o.confidence)
			return -1;
		else
			return 0;
	}
}
