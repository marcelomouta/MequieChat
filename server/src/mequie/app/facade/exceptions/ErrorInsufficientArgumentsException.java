package mequie.app.facade.exceptions;

import mequieclient.app.facade.exceptions.MequieException;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * Esta classe representa uma exepcao que pode ser lancada caso nao haja 
 * todos os arguments necessarios
 */
public class ErrorInsufficientArgumentsException extends MequieException {

	public ErrorInsufficientArgumentsException(){
		super("ERROR argumentos insuficientes");
	}

}
