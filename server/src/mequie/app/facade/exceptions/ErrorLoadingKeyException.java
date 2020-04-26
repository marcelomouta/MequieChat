package mequie.app.facade.exceptions;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * This class represents an exception that is thrown when loading a key fails
 */
@SuppressWarnings("serial")
public class ErrorLoadingKeyException extends MequieException {

	public ErrorLoadingKeyException(String group) {
		super("Error loading the user's key of the group " + group);
	}

}
