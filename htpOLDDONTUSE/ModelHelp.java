import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ModelHelp {
	
	public static void printTHFM(HashMap<String, HashMap<String, Integer>> thfm){
		System.out.println("THFM"); 
		for (Map.Entry<String, HashMap<String, Integer>> entry: thfm.entrySet()){
			String word = entry.getKey(); 
			HashMap<String, Integer> hashValueMap = entry.getValue(); 
			System.out.println(word); 
			for(Map.Entry<String, Integer> entry2: hashValueMap.entrySet()){
				String hashtag = entry2.getKey(); 
				int val = entry2.getValue(); 
				//System.out.println(hashtag + val); 
				System.out.printf("    %-7s %d\n", hashtag, val); 
			}
			System.out.println(); 
		}
	}

	public static void printHFM(HashMap<String, HashSet<String>> hfm){
		for (Map.Entry<String, HashSet<String>> entry: hfm.entrySet()){
			String hashtag = entry.getKey(); 
			int val = entry.getValue().size(); 
			System.out.printf("%-7s %d\n", hashtag, val); 
		}
	}

	public static void printIDF(HashMap<String, Integer> idf){
		for (Map.Entry<String, Integer> entry: idf.entrySet()){
			String word = entry.getKey();
			int docNum = entry.getValue(); 
			System.out.printf("%-7s %d\n", word, docNum); 
		}
	}
}