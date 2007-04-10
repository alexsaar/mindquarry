/*
 * Copyright (C) 2006-2007 Mindquarry GmbH, All Rights Reserved
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 */
package com.mindquarry.auth;

import com.mindquarry.user.AbstractUserRO;


/**
 * The interface defines the administrative / management contract with
 * the Authorization component. You can create a Right representing 
 * a single operation at a resource. Multiple right provides can be
 * bundled to a profile. Rights as well as profile can be granted 
 * and withdrawn users and groups.    
 *
 * @author 
 * <a href="mailto:bastian.steinert(at)mindquarry.com">Bastian Steinert</a>
 */
public interface AuthorizationAdmin extends AuthorizationCheck {
    
    public static final String ROLE = AuthorizationAdmin.class.getName();
    
    // creates right with name "{operation}: {resource}";
    RightRO createRight(String resource, String operation);
    
    RightRO createRight(String name, String resource, String operation);
    
    void deleteRight(RightRO right);
    
    
    ProfileRO createProfile(String profileId);
    
    void addRight(RightRO right, ProfileRO profile);
    
    void removeRight(RightRO right, ProfileRO profile);
    
    
    void addAllowance(RightRO right, AbstractUserRO user);
    void removeAllowance(RightRO right, AbstractUserRO user);

    void addAllowance(ProfileRO profile, AbstractUserRO user);
    void removeAllowance(ProfileRO profile, AbstractUserRO user);
    
    void addDenial(RightRO right, AbstractUserRO user);
    void removeDenial(RightRO right, AbstractUserRO user);
    
    void addDenial(ProfileRO profile, AbstractUserRO user);
    void removeDenial(ProfileRO profile, AbstractUserRO user);
}

