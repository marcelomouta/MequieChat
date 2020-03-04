package mequie.app.facade.exceptions;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * Esta classe representa uma exepcao que pode ser lancada caso nao haja 
 * um erro ao escrever no disco
 */
@SuppressWarnings("serial")
public class ErrorSavingGroupInDiskException extends MequieException {
	
	public ErrorSavingGroupInDiskException(){
		super("ERROR ao gravar o grupo no disco");
	}
}
