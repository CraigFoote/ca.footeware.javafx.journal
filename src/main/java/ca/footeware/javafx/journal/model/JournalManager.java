/**
 *
 */
package ca.footeware.javafx.journal.model;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import ca.footeware.javafx.journal.exceptions.JournalException;

/**
 * Provides a read/write interface to the {@link Journal}.
 */
public class JournalManager {

	public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static Journal journal;

	/**
	 * Add an entry to the journal.
	 *
	 * @param key   {@link String} a date in the format YYYY-MM-DD.
	 * @param value {@link String} the text of the entry, to be encrypted
	 * @throws JournalException
	 */
	public static void addEntry(String key, String value) throws JournalException {
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
	public static void createNewJournal(String path, String name, String password) throws IOException {
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
	public static String getEntry(String formattedDate) throws JournalException {
		try {
			return journal.getEntry(String.valueOf(formattedDate));
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | InvalidAlgorithmParameterException | InvalidKeySpecException e) {
			throw new JournalException("Error: " + e.getMessage() + ". Was the password correct?", e);
		}
	}

	/**
	 * Get the dates/keys from the journal.
	 *
	 * @return {@link List} of {@link String}
	 */
	public static List<String> getEntryDates() {
		List<String> keys = new ArrayList<>();
		Map<String, String> entries = journal.getEntries();
		for (String key : entries.keySet()) {
			keys.add(key);
		}
		return keys;
	}

	/**
	 * Get the first journal entry date (key).
	 *
	 * @return {@link LocalDate}
	 */
	public static LocalDate getFirstEntryDate() {
		List<String> entryDates = getEntryDates();
		if (!entryDates.isEmpty()) {
			return LocalDate.parse(entryDates.get(0), dateFormatter);
		}
		return null;
	}

	/**
	 * Get the last journal entry date (key).
	 *
	 * @return {@link LocalDate}
	 */
	public static LocalDate getLastEntryDate() {
		List<String> entryDates = getEntryDates();
		if (!entryDates.isEmpty()) {
			String formattedDate = entryDates.get(entryDates.size() - 1);
			return LocalDate.parse(formattedDate, dateFormatter);
		}
		return null;
	}

	/**
	 * Get the next journal entry after the provided date.
	 *
	 * @param selectedDate {@link LocalDate}
	 * @return {@link LocalDate} may be the same as the provided date
	 */
	public static LocalDate getNextEntryDate(LocalDate selectedDate) {
		List<String> entryDates = getEntryDates();
		switch (entryDates.size()) {
		case 0 -> {
			return selectedDate;
		}
		case 1 -> {
			LocalDate entryDate = LocalDate.parse(entryDates.get(0), dateFormatter);
			return entryDate.isAfter(selectedDate) ? entryDate : selectedDate;
		}
		default -> {
			/*
			 * Parse over entryDates. If selectedDate is before the first entryDate, return
			 * the entryDate. Else if there's a following entryDate, and the selectedDate is
			 * either equal to that second entryDate, or between the two entryDates, return
			 * the second entryDate.
			 */
			for (int i = 0; i < entryDates.size(); i++) {
				String entryDate1 = entryDates.get(i);
				LocalDate date1 = LocalDate.parse(entryDate1, dateFormatter);
				if (selectedDate.isBefore(date1)) {
					return date1;
				} else if ((i + 1) < entryDates.size()) {
					String entryDate2 = entryDates.get(i + 1);
					LocalDate date2 = LocalDate.parse(entryDate2, dateFormatter);
					if (date1.isEqual(selectedDate) || (date1.isBefore(selectedDate) && date2.isAfter(selectedDate))) {
						return date2; // next entry
					}
				}
			}
		}
		}
		// fallback is same date
		return selectedDate;
	}

	/**
	 * Get the previous journal entry before the provided date.
	 *
	 * @param selectedDate {@link LocalDate}
	 * @return {@link LocalDate} may be the same as the provided date if there's no
	 *         previous entry
	 */
	public static LocalDate getPreviousEntryDate(LocalDate selectedDate) {
		List<String> entryDates = getEntryDates();
		switch (entryDates.size()) {
		case 0 -> {
			return selectedDate;
		}
		case 1 -> {
			LocalDate entryDate = LocalDate.parse(entryDates.get(0), dateFormatter);
			return entryDate.isBefore(selectedDate) ? entryDate : selectedDate;
		}
		default -> {
			/*
			 * Parse over entryDates backwards. If selectedDate is after the last entryDate,
			 * return the entryDate. Else, if there's a following (prior) entryDate, and the
			 * selectedDate is after that second entryDate, or between the two entryDates,
			 * return the first entryDate.
			 */
			for (int i = entryDates.size() - 1; i >= 0; i--) {
				String entryDate1 = entryDates.get(i);
				LocalDate date1 = LocalDate.parse(entryDate1, dateFormatter);
				if (selectedDate.isAfter(date1)) {
					return date1;
				} else if ((i - 1) >= 0) {
					String entryDate2 = entryDates.get(i - 1);
					LocalDate date2 = LocalDate.parse(entryDate2, dateFormatter);
					if (selectedDate.isAfter(date2) || (date2.isBefore(selectedDate) && date1.isAfter(selectedDate))) {
						return date2; // previous entry
					}
				}
			}
		}
		}
		// fallback is same date
		return selectedDate;
	}

	/**
	 * Opens an existing journal at the specified file path and using the provided
	 * password.
	 *
	 * @param path     {@link String}
	 * @param password {@link String}
	 * @throws IOException      if the file is not found
	 * @throws JournalException if the password is incorrect
	 */
	public static void openJournal(String path, String password) throws IOException, JournalException {
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
	 * Save the journal to file.
	 *
	 * @throws JournalException
	 */
	public static void saveJournal() throws JournalException {
		try {
			journal.save();
		} catch (IOException e) {
			throw new JournalException("Error saving journal.", e);
		}
	}

	/**
	 * Constructor, hidden because all methods are static.
	 */
	private JournalManager() {
	}
}
