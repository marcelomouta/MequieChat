package mequieclient.app.facade.exceptions;

import mequieclient.app.facade.exceptions.MequieException;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * Esta classe representa uma exepcao que pode ser lancada caso um user 
 * nao tenha permissao para fazer uma acao
 */
@SuppressWarnings("serial")
public class UserNotHavePermissionException extends MequieException {

	public UserNotHavePermissionException(){
		super("ERROR user nao tem permissao para fazer a acao");
	}
}
