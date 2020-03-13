package mequieClient.app.facade.exceptions;

import mequieClient.app.facade.exceptions.MequieException;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * Esta classe representa uma exepcao que pode ser lancada caso nao haja 
 * um erro ao criar um grupo
 */
@SuppressWarnings("serial")
public class AuthenticationFailedException extends MequieException {

	public AuthenticationFailedException(){
		super("ERROR na autenticacao. Username ou password errados");
	}
}

