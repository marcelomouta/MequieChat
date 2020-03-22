package mequie.app.facade.exceptions;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * This class represents an exception that is thrown when a user doesn't exists
 */
@SuppressWarnings("serial")
public class NotExistingUserException extends MequieException {

	public NotExistingUserException(){
		super("ERROR user does not exist");
	}
}