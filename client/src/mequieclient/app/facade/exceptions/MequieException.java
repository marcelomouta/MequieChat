package mequieclient.app.facade.exceptions;

/**
 * 
 * @author 51021 Pedro Marques,51103 David Almeida,51468 Bruno Freitas
 * 
 * This class represents an exception that is thrown when something fails
 */
@SuppressWarnings("serial")
public class MequieException extends Exception{

	public MequieException(String message){
		super(message);
	}
}