/**
 * 
 */
package mequie.app.facade.exceptions;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * This class represents an exception that is thrown when it fails to retrieve the user group keys
 */
@SuppressWarnings("serial")
public class ErrorRetrievingUserKeysException extends MequieException {
	public ErrorRetrievingUserKeysException(){
		this("ERROR retrieving user group keys.");
	}

	public ErrorRetrievingUserKeysException(String message) {
		super(message);
	}
}
