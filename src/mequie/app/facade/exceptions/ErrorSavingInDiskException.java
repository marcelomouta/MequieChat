package mequie.app.facade.exceptions;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * Esta classe representa uma exepcao que pode ser lancada caso nao haja 
 * um erro ao escrever no disco
 */
@SuppressWarnings("serial")
public class ErrorSavingInDiskException extends MequieException {
	
	public ErrorSavingInDiskException(){
		super("ERROR ao gravar no disco");
	}
}
