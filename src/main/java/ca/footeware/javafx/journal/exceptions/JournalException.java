/**
 * 
 */
package ca.footeware.javafx.journal.exceptions;

/**
 * 
 */
public class JournalException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public JournalException(String message) {
		super(message);
	}
	
	public JournalException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
