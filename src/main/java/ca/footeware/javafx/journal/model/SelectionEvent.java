package ca.footeware.javafx.journal.model;

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
	private transient DateSelection selection;

	/**
	 * Constructor.
	 *
	 * @param eventType    {@link EventType}
	 * @param oldSelection {@link DateSelection}
	 */
	public SelectionEvent(EventType<? extends Event> eventType, DateSelection selection) {
		super(eventType);
		this.selection = selection;
	}

	public DateSelection getSelection() {
		return selection;
	}

}
