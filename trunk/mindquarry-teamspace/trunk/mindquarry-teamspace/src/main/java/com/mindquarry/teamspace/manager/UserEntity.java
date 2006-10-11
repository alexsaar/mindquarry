package com.mindquarry.teamspace.manager;

import java.util.HashSet;
import java.util.Set;

import com.mindquarry.common.persistence.EntityBase;
import com.mindquarry.teamspace.TeamspaceRO;
import com.mindquarry.teamspace.User;

public class UserEntity extends EntityBase implements User {
    
    private String name;
    
    Set<String> teamspaceReferences;


    /**
     * 
     */
    public UserEntity() {
        id = "".intern();
        name = "".intern();
        teamspaceReferences = new HashSet<String>();
    }

    /**
     * @see com.mindquarry.types.teamspace.User#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * @see com.mindquarry.types.teamspace.User#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see com.mindquarry.types.teamspace.User#getTeamspaces()
     */
    public Set<String> getTeamspaceReferences() {
        return teamspaceReferences;
    }

    /**
     * Setter for teamspaceReferences.
     *
     * @param teamspaceReferences the teamspaceReferences to set
     */
    public void setTeamspaceReferences(Set<String> teamspaces) {
        this.teamspaceReferences = teamspaces;
    }

    /**
     * @see com.mindquarry.teamspace.UserRO#isMemberOf(com.mindquarry.teamspace.TeamspaceRO)
     */
    public boolean isMemberOf(TeamspaceRO teamspace) {
        return teamspaceReferences.contains(teamspace.getId());
    }
}