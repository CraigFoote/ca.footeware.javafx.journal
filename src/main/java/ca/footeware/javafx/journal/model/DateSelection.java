package ca.footeware.javafx.journal.model;

import java.time.LocalDate;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

/**
 * Encapsulates a selection in the calendar, storing old and new artifacts and
 * the source.
 */
public record DateSelection(LocalDate oldDate, LocalDate newDate, String oldEntry, String newEntry, Label label) {

	public StringProperty getNewEntryProperty() {
		return new SimpleStringProperty(newEntry);
	}
}
