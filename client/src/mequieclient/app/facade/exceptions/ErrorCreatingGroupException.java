package mequieclient.app.facade.exceptions;

import mequieclient.app.facade.exceptions.MequieException;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * Esta classe representa uma exepcao que pode ser lancada caso nao haja 
 * um erro ao criar um grupo
 */
@SuppressWarnings("serial")
public class ErrorCreatingGroupException extends MequieException {

	public ErrorCreatingGroupException(){
		super("ERROR ao criar o grupo");
	}
}