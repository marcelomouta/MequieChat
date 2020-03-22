package mequie.app.facade.exceptions;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * This class represents an exception that is thrown when removing an user from a group fails
 */
@SuppressWarnings("serial")
public class ErrorRemovingUserOfGroupException extends MequieException {

	public ErrorRemovingUserOfGroupException(){
		this("ERROR removing user from group. User does not belong to this group");
	}

	public ErrorRemovingUserOfGroupException(String message) {
		super(message);
	}
}