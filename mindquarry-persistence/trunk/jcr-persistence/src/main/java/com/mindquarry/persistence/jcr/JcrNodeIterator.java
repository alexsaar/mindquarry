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
package com.mindquarry.persistence.jcr;

import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.NodeIterator;


/**
 * @author
 * <a href="mailto:bastian.steinert(at)mindquarry.com">Bastian Steinert</a>
 */
public class JcrNodeIterator implements Iterator<JcrNode>, Iterable<JcrNode> {

    private NodeIterator nodeIterator_;
    private JcrSession session_;
    
    public JcrNodeIterator(NodeIterator nodeIterator, JcrSession session) {
        session_ = session;
        nodeIterator_ = nodeIterator;
    }

    public JcrNode nextNode() {
        return new JcrNode(nodeIterator_.nextNode(), session_);
    }

    public long getPosition() {
        return nodeIterator_.getPosition();
    }

    public long getSize() {
        return nodeIterator_.getSize();
    }

    public void skip(long skipNum) {
        nodeIterator_.skip(skipNum);
    }

    public boolean hasNext() {
        return nodeIterator_.hasNext();
    }

    public JcrNode next() {
        return new JcrNode((Node) nodeIterator_.next(), session_);
    }

    public void remove() {
        nodeIterator_.remove();
    }

    public Iterator<JcrNode> iterator() {
        return this;
    }
}
