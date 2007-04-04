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
package com.mindquarry.persistence.jcr.cmds;

import com.mindquarry.persistence.jcr.JcrNode;
import com.mindquarry.persistence.jcr.JcrSession;
import com.mindquarry.persistence.jcr.Persistence;
import com.mindquarry.persistence.jcr.Pool;
import com.mindquarry.persistence.jcr.model.EntityType;
import com.mindquarry.persistence.jcr.model.Model;
import com.mindquarry.persistence.jcr.trafo.TransformationManager;
import com.mindquarry.persistence.jcr.trafo.Transformer;

/**
 * Add summary documentation here.
 *
 * @author 
 * <a href="mailto:bastian.steinert(at)mindquarry.com">Bastian Steinert</a>
 */
class ReadCommand implements Command {

    private JcrNode entityNode_;
    private Persistence persistence_;    
        
    public void initialize(Persistence persistence, Object... objects) {
        entityNode_ = (JcrNode) objects[0];
        persistence_ = persistence;
    }
    
    /**
     * @see com.mindquarry.persistence.jcr.mapping.Command#execute(javax.jcr.Session)
     */
    public Object execute(JcrSession session) {
        Object entity;
        Pool pool = session.getPool();
        if (pool.containsEntryForNode(entityNode_)) {
            entity = pool.entityByNode(entityNode_);
        }
        else {
            JcrEntityFolderNode folderNode = makeEntityFolder();
            JcrNode node = folderNode.getEntityNode(entityNode_.getName());
            entity = entityTransformer().readFromJcr(node);            
            pool.put(entity, node);
        }        
        return entity; 
    }
    
    private JcrEntityFolderNode makeEntityFolder() {
        JcrNode folderNode = entityNode_.getParent();
        return new JcrEntityFolderNode(folderNode, entityType());
    }
    
    private Transformer entityTransformer() {
        String folder = parentFolderName();
        return getTransformationManager().entityTransformerByFolder(folder);
    }
    
    private EntityType entityType() {
        String folder = parentFolderName();
        
        EntityType result = null;
        for (EntityType entityType : getModel().allEntityTypes()) {
            if (entityType.usesJcrFolder(folder)) {
                result = entityType;
                break;
            }
        }
        return result;
    }
    
    private String parentFolderName() {
        return entityNode_.getParent().getName();
    }
    
    EntityType entityTypeByFolder(String folder) {
        EntityType result = null;
        for (EntityType entityType : getModel().allEntityTypes()) {
            if (entityType.usesJcrFolder(folder)) {
                result = entityType;
                break;
            }
        }
        return result;
    }
    
    private Model getModel() {
        return persistence_.getModel();
    }
    
    private TransformationManager getTransformationManager() {
        return persistence_.getTransformationManager();
    }
}
