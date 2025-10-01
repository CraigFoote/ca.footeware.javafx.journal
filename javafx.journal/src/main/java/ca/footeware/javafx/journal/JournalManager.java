/**
 * 
 */
package ca.footeware.javafx.journal;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 */
public class JournalManager {

	private static Journal journal;

	/**
	 * Creates a new journal at the specified path with the specified name and
	 * password.
	 * 
	 * @param path     {@link String}
	 * @param name     {@link String}
	 * @param password {@link String}
	 * @throws IOException
	 */
	public static void createNewJournal(String path, String name, String password) throws IOException {
		File file = new File(path + File.separator + name);
		if (file.exists()) {
			throw new IOException("File already exists: " + file.getAbsolutePath());
		}
		journal = new Journal(file, password);
	}

	public static void openJournal(String path, String name, String password) throws IOException {
		File file = new File(path + File.separator + name);
		if (!file.exists()) {
			throw new IOException("File already exists: " + file.getAbsolutePath());
		}
		journal = new Journal(file, password);
		Properties properties = new Properties();
		properties.load(JournalManager.class.getResourceAsStream(file.getAbsolutePath()));
		properties.keySet().forEach(key -> System.out.println(key + ": " + properties.get(key)));
	}

	public void addEntry(String key, String value) {
		journal.addEntry(key, value);
	}

	public void saveJournal() {
		journal.save();
	}
}
