

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HashTagPrediction {
	
	String trainingTweetPath = "examples.txt"; 
	String testingTweetPath = "examplesTest.txt"; 
	String tweetHashDelim = "%"; 
	int defaultTopK = 10; 
	boolean useDefaultTopK = false; 
	
	HashMap<String, HashSet<String>> hfm = new HashMap<String, HashSet<String>>(); 
	HashMap<String, HashMap<String, Integer>> thfm  = new HashMap<String, HashMap<String, Integer>>(); 
	HashMap<String, Integer> idf = new HashMap<String, Integer>(); 
	List<String> trainingTweets;
	List<String> testingTweets;
	
	public HashTagPrediction(){
		trainingTweets = null; 
		testingTweets = null; 
	}
	
	public void train(){
		try {
			trainingTweets = Files.readAllLines(Paths.get(trainingTweetPath), Charset.defaultCharset());
		} catch (IOException e) {
			System.out.println(e); 
		} 
		
		for (String line: trainingTweets){
			String[] tweetAndHash = line.split(tweetHashDelim); 
			System.out.println(tweetAndHash[1]); 
			if(tweetAndHash.length == 2){
				//Create the three datastructures. 
				//TODO Porter Stemming
				//TODO All lowercase(?)
				//TODO remove punctuation(?)
				
				String[] wordsInSentence = tweetAndHash[0].split("\\s+");
				String[] hashtags = tweetAndHash[1].split("\\s+"); 
				//add hashtags to some data structure.
				
				//for thfm
				HashSet<String> wordsAddedToIdf = new HashSet<String>();  
				for (String word: wordsInSentence){
					HashMap<String, Integer> wordHashTagFreq; 
					if(thfm.containsKey(word)){
						wordHashTagFreq = thfm.get(word);
					}
					else{
						wordHashTagFreq = new HashMap<String, Integer>(); 
					}
					for(String ht: hashtags){
						if(wordHashTagFreq.containsKey(ht)){
							wordHashTagFreq.put(ht, wordHashTagFreq.get(ht) + 1); 
						}
						else{
							wordHashTagFreq.put(ht, 1); 
						}
					}
					
					//for idf 
					if(!wordsAddedToIdf.contains(word)){
						wordsAddedToIdf.add(word); 
						if(idf.containsKey(word)){
							idf.put(word, idf.get(word) + 1);
						}
						else{
							idf.put(word, 1); 
						}
					}
				}
				
				//for hfm
				for(String ht: hashtags){
					HashSet<String> wordsAssociatedWithHash;
					if(hfm.containsKey(ht)){
						wordsAssociatedWithHash = hfm.get(ht); 
					}
					else{
						wordsAssociatedWithHash = new HashSet<String>();
					}
					for(String word: wordsInSentence){
						wordsAssociatedWithHash.add(word);
					}
					hfm.put(ht, wordsAssociatedWithHash); 
				}
			}
		}
	}
	
	public void test(){
		//Predicting
		try {
			testingTweets = Files.readAllLines(Paths.get(testingTweetPath), Charset.defaultCharset());
			
		} catch (IOException e) {
			System.out.println(e); 
		}
		int correct = 0;
		int incorrect = 0; 
		int topK = 10; 
		for(String line: testingTweets){
			String[] tweetAndHash = line.split(tweetHashDelim); 
			if(tweetAndHash.length == 2){
				String tweet = tweetAndHash[0];
				ArrayList<String> hashtags = new ArrayList<String>(Arrays.asList(tweetAndHash[1].split("\\s+"))); 
				ArrayList<String> predictedHashTags = predictTweet(tweet, topK);
				if(EvaluateHelp.getSinglePrecisionApproach2(predictedHashTags, hashtags)){
					correct++; 
				}
				else{
					incorrect++; 
				}
				
			}
		}
		System.out.println(EvaluateHelp.precision(correct, incorrect)); 
		
	}
	
	
	public ArrayList<String> predictTweet(String tweet, int topK){
		String[] wordsInSentence = tweet.split("\\s+");
		HashMap<String, Double> recHashTags = new HashMap<String, Double>(); 
		for (String word: wordsInSentence){
			double idfVal = idf.get(word); 
			//loop through all hashtags co-occuring with word
			HashMap<String, Integer> hashAssocWithWordAsMap = thfm.get(word); 
			int totalHashTagFreq = 0; 
			for (Integer freq : hashAssocWithWordAsMap.values()){
				totalHashTagFreq = totalHashTagFreq + freq; 
			}
			for (Map.Entry<String, Integer> entry: hashAssocWithWordAsMap.entrySet()){
				String hashtag = entry.getKey(); 
				Integer freq = entry.getValue(); 
				double hf = freq * 1.0 / totalHashTagFreq; 
				double ihu = Math.log(thfm.size() * 1.0 / hfm.get(hashtag).size()); 
				double hashTagScore = idfVal * hf * ihu;
				if (recHashTags.containsKey(hashtag)){
					recHashTags.put(hashtag, recHashTags.get(hashtag) + hashTagScore);
				}
				else{
					recHashTags.put(hashtag, hashTagScore);
				}
			}
		}
		
		return this.findTopK(recHashTags, topK); 
	}
	
	public ArrayList<String> findTopK (HashMap<String, Double> result, int topK){
		ValueComparator comp =  new ValueComparator(result);
        TreeMap<String,Double> resultTreeMap = new TreeMap<String,Double>(comp);
        resultTreeMap.putAll(result);
        ArrayList<String> returnTopK = new ArrayList<String>(); 
        int useThisTopK = useDefaultTopK ? defaultTopK : topK; 
        int count = 0; 
        for (Map.Entry<String, Double> entry: resultTreeMap.entrySet()){
        	
        	returnTopK.add(entry.getKey()); 
        	count++; 
        	if(count >= useThisTopK){
        		break;
        	}
        }
        return returnTopK; 
	}
	
	class ValueComparator implements Comparator<String> {

	    Map<String, Double> base;
	    public ValueComparator(Map<String, Double> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with equals.    
	    public int compare(String a, String b) {
	        if (base.get(a) >= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
	
	
}
