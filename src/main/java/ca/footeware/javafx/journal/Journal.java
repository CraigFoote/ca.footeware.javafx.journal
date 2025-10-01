/**
 * 
 */
package ca.footeware.javafx.journal;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 */
public class Journal {

	private String password;
	private Map<String, String> map;
	private File file;

	/**
	 * Constructor.
	 * @param file {@link File}
	 * @param password {@link String}
	 */
	public Journal(File file, String password) {
		this.file = file;
		this.password = password;
		this.map = new TreeMap<>();
	}

	/**
	 * Adds an entry to the journal.
	 * 
	 * @param key
	 * @param value
	 */
	public void addEntry(String key, String value) {
		// @TODO: Encrypt value before storing
		map.put(key, value);
	}

	/**
	 * Gets an entry from the journal.
	 * 
	 * @param key {@link String}
	 * @return String {@link String}
	 */
	public String getEntry(String key) {
		// @TODO: Decrypt value before returning
		return map.get(key);
	}

	/**
	 * Gets all entries from the journal.
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, String> getEntries() {
		return map;
	}

	/**
	 * Saves the journal to disk.
	 */
	public void save() {
		map.forEach((k, v) -> System.out.println(k + ": " + v));
	}
}
