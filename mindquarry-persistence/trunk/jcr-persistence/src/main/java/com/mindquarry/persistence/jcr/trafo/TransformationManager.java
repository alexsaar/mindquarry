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
package com.mindquarry.persistence.jcr.trafo;

import java.util.HashMap;
import java.util.Map;

import com.mindquarry.persistence.jcr.cmds.CommandManager;
import com.mindquarry.persistence.jcr.model.EntityType;
import com.mindquarry.persistence.jcr.model.Model;

/**
 * Add summary documentation here.
 *
 * @author
 * <a href="mailto:bastian.steinert(at)mindquarry.com">Bastian Steinert</a>
 */
public class TransformationManager {

    private Model model_;
    private CommandManager commandManager_;
    private TransformerRegistry transformerRegistry_;
    
    private Map<EntityType, Transformer> entityTransformers_;
    
    public TransformationManager(Model model, CommandManager commandManager) {
        model_ = model;
        commandManager_ = commandManager;
        entityTransformers_ = new HashMap<EntityType, Transformer>();
    }
    
    public void initialize(CommandManager commandManager) {
        transformerRegistry_ = new TransformerRegistry(this);
        for (EntityType entityType : model_.allEntityTypes()) {
            Transformer transformer = new EntityTransformer(entityType);
            transformer.initialize(transformerRegistry_);            
            entityTransformers_.put(entityType, transformer);
        }
    }
    
    public boolean isPartOfModel(Class<?> clazz) {
        return model_.entityType(clazz) != null;
    }
    
    public String entityFolderName(Object entity) {
        return model_.entityFolderName(entity);
    }
    
    public Transformer entityTransformer(Object entity) {
        return entityTransformers_.get(entityType(entity));
    }
    
    public Transformer entityTransformerByFolder(String folder) {
        EntityType entityType = model_.entityTypeForFolder(folder);
        return entityTransformers_.get(entityType);
    }
    
    public EntityType entityType(Object entity) {
        return model_.entityType(entity.getClass());
    }
    
    public Transformer createReferenceTransformer(Class<?> referenceesClazz) {
        Transformer result =
            new ReferenceTransformer(referenceesClazz, commandManager_);
        result.initialize(transformerRegistry_);
        return result;
    }
}
