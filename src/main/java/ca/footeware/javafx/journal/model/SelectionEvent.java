package ca.footeware.javafx.journal.model;

import java.time.LocalDate;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Provides both previous and new selections as well as their matching journal
 * entries.
 */
public class SelectionEvent extends Event {

	public static final EventType<SelectionEvent> ANY = new EventType<>(Event.ANY, "ANY");
	public static final EventType<SelectionEvent> DATE_SELECTED = new EventType<>(ANY, "DATE_SELECTED");
	private static final long serialVersionUID = 1L;
	private LocalDate oldDate;
	private String oldEntry;
	private LocalDate newDate;
	private String newEntry;

	/**
	 * Constructor.
	 * 
	 * @param eventType    {@link EventType}
	 * @param newEntry     {@link String}
	 * @param oldEntry     {@link String}
	 * @param date         {@link LocalDate}
	 * @param selectedDate
	 */
	public SelectionEvent(EventType<? extends Event> eventType, LocalDate oldDate, String oldEntry, LocalDate newDate,
			String newEntry) {
		super(eventType);
		this.oldDate = oldDate;
		this.oldEntry = oldEntry;
		this.newDate = newDate;
		this.newEntry = newEntry;
	}

	/**
	 * Gets the previous selected date.
	 * 
	 * @return {@link LocalDate}
	 */
	public LocalDate getOldDate() {
		return oldDate;
	}

	/**
	 * Gets the newly selected date.
	 * 
	 * @return {@link LocalDate}
	 */
	public LocalDate getNewDate() {
		return newDate;
	}

	/**
	 * Gets the previous selected date's journal entry
	 * 
	 * @return {@link String}
	 */
	public String getOldEntry() {
		return oldEntry;
	}

	/**
	 * Gets the newly selected date's journal entry.
	 * 
	 * @return {@link String}
	 */
	public String getNewEntry() {
		return newEntry;
	}
}
