package io;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class InputFileReader {
	private List<File> filesToRead;
	private BufferedReader currentReader;

	public InputFileReader() {
		filesToRead = new LinkedList<File>();
		currentReader = null;
	}

	public void addFileSystem(File file) {
		if (!file.isDirectory()) {
			filesToRead.add(file);
		} else {
			File[] dirFiles = file.listFiles();
			for (File subFile : dirFiles) {
				addFileSystem(subFile);
			}
		}
	}

	public String getNextLine() throws IOException {
		if (currentReader == null) {
			currentReader = readNextFile();
			if (currentReader == null)
				return null;
		}
		String line = currentReader.readLine();
		while (line == null) {
			currentReader = readNextFile();
			if (currentReader == null)
				return null;
			line = currentReader.readLine();
		}
		return line;
	}

	private BufferedReader readNextFile() throws IOException {
		if (filesToRead.isEmpty())
			return null;
		if (currentReader != null)
			currentReader.close();
		return new BufferedReader(new FileReader(filesToRead.remove(0)));
	}

	private List<File> getFilesToRead() {
		return filesToRead;
	}

}
