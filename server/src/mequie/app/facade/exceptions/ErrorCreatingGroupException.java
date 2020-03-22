package mequie.app.facade.exceptions;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * This class represents an exception that is thrown when creating a group fails
 */
@SuppressWarnings("serial")
public class ErrorCreatingGroupException extends MequieException {

	public ErrorCreatingGroupException(){
		super("ERROR creating group. Group already exists");
	}
}