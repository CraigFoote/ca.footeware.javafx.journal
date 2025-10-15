/**
 * 
 */
package ca.footeware.javafx.journal;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * 
 */
public class JournalManager {

	private static Journal journal;

	/**
	 * Add an entry to the journal.
	 * 
	 * @param key   {@link String} a date in the format YYYY-MM-DD.
	 * @param value {@link String} the text of the entry, to be encrypted
	 * @throws JournalException
	 */
	static void addEntry(String key, String value) throws JournalException {
		try {
			journal.addEntry(key, value);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | InvalidAlgorithmParameterException | InvalidKeySpecException e) {
			throw new JournalException("Error adding entry to journal.", e);
		}
	}

	/**
	 * Creates a new journal at the specified path with the specified name and
	 * password.
	 * 
	 * @param path     {@link String}
	 * @param name     {@link String}
	 * @param password {@link String}
	 * @throws IOException
	 */
	static void createNewJournal(String path, String name, String password) throws IOException {
		File file = new File(path + File.separator + name);
		if (file.exists()) {
			throw new IOException("File already exists: " + file.getAbsolutePath());
		}
		boolean newFile = file.createNewFile();
		if (!newFile) {
			throw new IOException("");
		}
		journal = new Journal(file, password);
	}

	/**
	 * Get a day's journal entry.
	 * 
	 * @param formattedDate {@link String} in format yyyy-MM-dd.
	 * @return {@link String} may be null
	 * @throws JournalException
	 */
	static String getEntry(String formattedDate) throws JournalException {
		try {
			return journal.getEntry(String.valueOf(formattedDate));
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | InvalidAlgorithmParameterException | InvalidKeySpecException e) {
			throw new JournalException("Error: " + e.getMessage() + ". Was the password correct?", e);
		}
	}

	/**
	 * Opens an existing journal of the specified file path and using the provided
	 * password.
	 * 
	 * @param path     {@link String}
	 * @param password {@link String}
	 * @throws IOException      if the file is not found
	 * @throws JournalException if the password is incorrect
	 */
	static void openJournal(String path, String password) throws IOException, JournalException {
		File file = new File(path);
		if (!file.exists()) {
			throw new IOException("File not found: " + file.getAbsolutePath());
		}
		if (!file.canWrite()) {
			throw new IOException("File is read-only: " + file.getAbsolutePath());
		}
		journal = new Journal(file, password);
		if (!journal.testPassword()) {
			throw new JournalException("Incorrect password.");
		}
	}

	/**
	 * Save the journal.
	 * 
	 * @throws JournalException
	 */
	static void saveJournal() throws JournalException {
		try {
			journal.save();
		} catch (IOException e) {
			throw new JournalException("Error saving journal.", e);
		}
	}

	private JournalManager() {
	}
}
