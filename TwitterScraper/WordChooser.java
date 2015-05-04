import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class WordChooser {
	public static final String dict12AnnotationsRegex = "[:&#<^=+]";

	public static void main(String[] args) {
		int numWords = 10000;
		String outputFile = "dictionary/six12chosen_words";
		String inputFile = "dictionary/6of12.txt";

		try (BufferedReader reader = new BufferedReader(new FileReader(
				inputFile));
				PrintWriter writer = new PrintWriter(new File(outputFile))) {
			List<String> dictionary = new ArrayList<String>();
			String word;
			while ((word = reader.readLine()) != null) {
				word = word.trim().replaceAll(dict12AnnotationsRegex, "");
				if (word.length() >= 4) {
					dictionary.add(word);
				}
			}
			int counter = 0;
      System.out.println(dictionary.size());
			while (counter < numWords && !dictionary.isEmpty()) {
				int randomIndex = (int) (Math.random() * dictionary.size());
				writer.println(dictionary.remove(randomIndex));
				counter++;
			}
			System.out.println("done");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
