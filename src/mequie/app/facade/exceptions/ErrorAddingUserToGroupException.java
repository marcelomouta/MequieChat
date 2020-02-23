package mequie.app.facade.exceptions;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * Esta classe representa uma exepcao que pode ser lancada caso nao haja 
 * um erro ao criar um grupo
 */
@SuppressWarnings("serial")
public class ErrorAddingUserToGroupException extends MequieException {

	public ErrorAddingUserToGroupException(){
		super("ERROR ao adicionar o utilizador ao grupo");
	}
}