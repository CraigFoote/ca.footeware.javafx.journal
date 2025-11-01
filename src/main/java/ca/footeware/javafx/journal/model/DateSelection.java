package ca.footeware.javafx.journal.model;

import java.time.LocalDate;

/**
 * Encapsulates a selection in the calendar, storing old and new selection
 * dates.
 */
public record DateSelection(LocalDate oldDate, LocalDate newDate) {
}
