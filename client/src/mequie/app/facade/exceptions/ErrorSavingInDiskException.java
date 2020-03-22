package mequie.app.facade.exceptions;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * This class represents an exception that is thrown when saving to disk fails
 */
@SuppressWarnings("serial")
public class ErrorSavingInDiskException extends MequieException {
	
	public ErrorSavingInDiskException(){
		super("ERROR saving to disk. Information may not persist the next time server restarts");
	}
}
