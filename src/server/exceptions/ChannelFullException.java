package server.exceptions;

public class ChannelFullException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3596284335004083523L;
	
	public ChannelFullException (String message) {
		super(message);
	}

	public ChannelFullException() {
		super();
	}
}
