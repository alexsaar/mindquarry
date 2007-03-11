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
package com.mindquarry.persistence.jcr.session;

import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import com.mindquarry.persistence.jcr.JcrPersistenceInternalException;
import com.mindquarry.persistence.jcr.mapping.Command;
import com.mindquarry.persistence.jcr.mapping.MappingManager;
import com.mindquarry.persistence.jcr.mapping.Operations;

/**
 * Add summary documentation here.
 *
 * @author
 * <a href="mailto:bastian.steinert(at)mindquarry.com">Bastian Steinert</a>
 */
class JcrSession implements com.mindquarry.common.persistence.Session {

    private Repository repository_;
    private MappingManager mappingManager_;
    
    JcrSession(MappingManager mappingManager, Repository repository) {
        
        repository_ = repository;
        mappingManager_ = mappingManager;
    }
    
    private MappingManager mappingManager() {
        return mappingManager_;
    }
    
    private Command createPersistCommand(Object entity) {
        return mappingManager().createCommand(entity, Operations.PERSIST);
    }
    
    /**
     * @see com.mindquarry.common.persistence.Session#commit()
     */
    public void commit() {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.mindquarry.common.persistence.Session#delete(java.lang.Object)
     */
    public boolean delete(Object arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see com.mindquarry.common.persistence.Session#persist(java.lang.Object)
     */
    public void persist(Object entity) {
        Session session;
        try {
            session = repository_.login(new SimpleCredentials("alexander.saar", "mypwd".toCharArray()));
        } catch (LoginException e) {
            throw new JcrPersistenceInternalException(e);
        } catch (RepositoryException e) {
            throw new JcrPersistenceInternalException(e);
        }
        //createPersistCommand(entity).execute(session);
    }

    /**
     * @see com.mindquarry.common.persistence.Session#query(java.lang.String, java.lang.Object[])
     */
    public List<Object> query(String arg0, Object[] arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.mindquarry.common.persistence.Session#update(java.lang.Object)
     */
    public void update(Object arg0) {
        // TODO Auto-generated method stub

    }
}