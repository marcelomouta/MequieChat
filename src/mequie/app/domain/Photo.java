package mequie.app.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * Esta classe representa um curador. Um curador eh um utilizador que pode adicionar 
 * videos e criar playlists manualmente
 */
public class Photo extends Message {
	
	private String file_path;
	
	/**
	 * 
	 * @param username - nome do curador
	 * @param pass - password do curador
	 */
	public Curador(String username, String pass) {
		super(username, pass);
	}
	

}
