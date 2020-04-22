package mequie.app.domain.catalogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mequie.app.domain.Group;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 * 
 * This class represents a Group Catalog that contains all groups
 * of the system
 */

 public class GroupCatalog {
    private static GroupCatalog INSTANCE;
    
    // all groups of the system
    private Set<Group> groups = new HashSet<>();

    private GroupCatalog(){}

    /**
     * 
     * @retrun the instance of this class
     */
    public static GroupCatalog getInstance(){
		if(INSTANCE == null){
			INSTANCE = new GroupCatalog();
		}
		return INSTANCE;
    }
    
    /**
     * 
     * @param group the group to be added
     * @return true if group was successfully added to the catalog
     */
    public boolean addGroup(Group grupo) {
        return groups.add(grupo);
    }

    /**
     * 
     * @param groupID the groupID of the possible group
     * @return the group if a group with groupID id exists or null
     */
    public Group getGroupByID(String groupID) {
        for (Group g : groups) {
            if (g.getGroupID().equals(groupID)) {
                return g;
            }
        }

        return null;
    }
    
    /**
     * 
     * @return a list of all groups
     */
    public List<Group> getAllGroups() {
    	return new ArrayList<>(groups);
    }
 }