import java.util.HashMap;
import java.util.HashSet;

public class ModelHelp {
	
	public static void printTHFM(HashMap<String, HashMap<String, Integer>> thfm){
		System.out.println(THFM); 
		for (Map.Entry<String, HashMap<String, Integer>> entry: thfm.entrySet()){
			String word = entry.getKey(); 
			HashMap<String, Integer> hashValueMap = entry.getValue(); 
			System.out.println(word); 
			for(Map.Entry<String, Integer> entry2: hashValueMap.entrySet()){
				String hashtag = entry2.getKey(); 
				int val = entry2.getValue(); 
				System.out.printf("    %-15s %d\n", hashtag, val); 
			}
		}
	}
}