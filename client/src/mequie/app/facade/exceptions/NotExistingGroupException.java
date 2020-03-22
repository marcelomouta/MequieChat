package mequie.app.facade.exceptions;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * This class represents an exception that is thrown when a group doesn't exists
 */
@SuppressWarnings("serial")
public class NotExistingGroupException extends MequieException {

	public NotExistingGroupException(){
		super("ERROR this group does not exist");
	}
}