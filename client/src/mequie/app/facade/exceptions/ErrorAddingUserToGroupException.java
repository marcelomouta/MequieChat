package mequie.app.facade.exceptions;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * This class represents an exception that is thrown when adding a user to a group fails
 */
@SuppressWarnings("serial")
public class ErrorAddingUserToGroupException extends MequieException {

	public ErrorAddingUserToGroupException(){
		super("ERROR adding a user to a group. User already in this group");
	}
}