package mequie.app.facade.exceptions;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * This class represents an exception that is thrown when user authentication fails
 */
@SuppressWarnings("serial")
public class AuthenticationFailedException extends MequieException {

	public AuthenticationFailedException(){
		super("ERROR authentication failed. User authenticity could not be verified");
	}
}

