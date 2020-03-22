package mequie.app.facade.exceptions;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * This class represents an exception that is thrown when an operation asked doesn't
 * have all the arguments needed
 */
@SuppressWarnings("serial")
public class ErrorInsufficientArgumentsException extends MequieException {

	public ErrorInsufficientArgumentsException(){
		super("ERROR insufficient number of arguments");
	}

}
