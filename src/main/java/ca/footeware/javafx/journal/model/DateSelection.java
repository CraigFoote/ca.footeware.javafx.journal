package ca.footeware.javafx.journal.model;

import java.time.LocalDate;

/**
 * Encapsulates a selection in the calendar, storing old and new artifacts and
 * the source.
 */
public record DateSelection(LocalDate oldDate, LocalDate newDate, String oldEntry, String newEntry) {
}
