package com.cmput414w17.medical.image;

/**
 * An exception that indicates a file type is not supported by the application.
 * 
 * @author David Yee
 *
 */
public class UnsupportedFileTypeException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnsupportedFileTypeException() {
		super();
	}

	public UnsupportedFileTypeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnsupportedFileTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedFileTypeException(String message) {
		super(message);
	}

	public UnsupportedFileTypeException(Throwable cause) {
		super(cause);
	}

}
