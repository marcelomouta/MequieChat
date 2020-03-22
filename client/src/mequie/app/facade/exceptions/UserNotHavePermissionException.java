package mequie.app.facade.exceptions;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * This class represents an exception that is thrown when a user doesn't have permission 
 * to execute an operation
 */
@SuppressWarnings("serial")
public class UserNotHavePermissionException extends MequieException {

	public UserNotHavePermissionException(){
		super("ERROR user does not have permission to execute that action");
	}
}
