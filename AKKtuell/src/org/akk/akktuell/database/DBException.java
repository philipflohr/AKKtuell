package org.akk.akktuell.database;

/**
 * This class is an Exception thrown after a serious database error has occurred.
 * It can contain the exception causing this exception to be thrown or at least a message
 * describing the error.
 * @author Florian Muenchbach
 *
 */
public class DBException extends Exception {
	private static final long serialVersionUID = -7959140788234473004L;
	private final Exception sourceException;
	private final String message;

	/**
	 * Creates a new {@link DBException}.
	 * At least one of the arguments must be different from {@code null}.
	 * @param e the causing this exception to be thrown.
	 * @param message a message explaining the error occurred.
	 */
	public DBException(Exception e, String message) {
		if (e == null && message == null) {
			throw new IllegalArgumentException("At least one argument should be != null");
		}
		this.message = message;
		this.sourceException = e;
	}

	@Override
	public String toString() {
		return this.message;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	/**
	 * Returns the message of this {@link DBException} and of the exception it contains
	 * (if any).
	 * @return the message of this {@link DBException} and of the exception it contains.
	 */
	public String getFullMessage() {
		return this.message + "\n" + this.sourceException.getMessage();
	}

	/**
	 * Returns the exception causing this exception to be thrown or null if not set.
	 * @return the exception causing this exception to be thrown or null.
	 */
	public Exception getSourceException() {
		return this.sourceException;
	}
}
