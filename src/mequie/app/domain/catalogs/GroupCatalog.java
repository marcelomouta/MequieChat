package mequie.app.domain.catalogs;

import java.util.HashSet;
import java.util.Set;
import mequie.app.domain.Group;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * Esta classe representa o Catalogo de Grupos que contem todos 
 * os grupos do sistema
 */

 public class GroupCatalog {
    private static GroupCatalog INSTANCE;
    private Set<Group> groups = new HashSet<>();

    private GroupCatalog(){}

    public static GroupCatalog getInstance(){
		if(INSTANCE == null){
			INSTANCE = new GroupCatalog();
		}
		return INSTANCE;
    }
    
    public boolean addGroup(Group grupo) {
        return groups.add(grupo);
    }

    public Group getGroupByID(String groupID) {
        for (Group g : groups) {
            if (g.getGoupID().equals(groupID)) {
                return g;
            }
        }

        return null;
    }
 }