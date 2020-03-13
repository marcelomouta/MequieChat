package mequie.app.facade.exceptions;

/**
 * 
 * @author 51021 Pedro Marques,51103 David Almeida,51468 Bruno Freitas
 * Esta classe representa uma excepcao que pode ser lancada caso aconteca
 * algo de errado na decorrer do programa
 */
@SuppressWarnings("serial")
public class MequieException extends Exception{

	public MequieException(String message){
		super(message);
	}
}