/**
 * Copyright (C) 2006 Mindquarry GmbH, All Rights Reserved
 */
package com.mindquarry.teamspace;


/**
 * Add summary documentation here.
 *
 * @author 
 * <a href="mailto:bastian.steinert(at)mindquarry.com">your full name</a>
 */
public interface User extends UserRO {
    
    /**
     * Setter for password.
     *
     * @param name the password to set
     */
    void setPassword(String password);
    
    /**
     * Setter for name.
     *
     * @param name the name to set
     */
    void setName(String name);
    
    /**
     * Setter for surname.
     *
     * @param name the surname to set
     */
    void setSurname(String surname);

    /**
     * Setter for email.
     *
     * @param email the email to set
     */
    void setEmail(String email);
}