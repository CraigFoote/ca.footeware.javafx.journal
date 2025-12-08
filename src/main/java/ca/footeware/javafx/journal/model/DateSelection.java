package ca.footeware.javafx.journal.model;

import java.time.LocalDate;

/**
 * Encapsulates a selection in the calendar, storing old (previously selected)
 * and new selection dates.
 *
 * @param oldDate {@link LocalDate}
 * @param newDate {@link LocalDate}
 */
public record DateSelection(LocalDate oldDate, LocalDate newDate) {
}
