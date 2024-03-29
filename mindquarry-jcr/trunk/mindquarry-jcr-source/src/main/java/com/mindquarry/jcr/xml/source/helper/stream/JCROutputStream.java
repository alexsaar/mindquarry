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
package com.mindquarry.jcr.xml.source.helper.stream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.cocoon.CascadingIOException;
import org.apache.cocoon.util.WildcardMatcherHelper;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.mindquarry.common.index.IndexClient;
import com.mindquarry.jcr.xml.source.JCRConstants;
import com.mindquarry.jcr.xml.source.JCRSourceFactory;
import com.mindquarry.jcr.xml.source.handler.SAXToJCRNodesConverter;
import com.mindquarry.jcr.xml.source.helper.XMLFileSourceHelper;

/**
 * OutputStream to be used for writing to the {@link XMLFileSourceHelper}.
 * 
 * @author <a href="mailto:alexander(dot)saar(at)mindquarry(dot)com">Alexander
 *         Saar</a>
 * @author <a href="mailto:alexander(dot)klimetschek(at)mindquarry(dot)com">
 *         Alexander Klimetschek</a>
 */
public class JCROutputStream extends ByteArrayOutputStream {
    private boolean isClosed = false;

    private final Node node;

    private final Session session;

    private IndexClient iClient;

    private String uri;
    
    private final boolean isVersioned;

    public JCROutputStream(Node node, Session session, IndexClient iClient,
            String uri) {
        // more size at the beginning
        super(1024);
        
        this.node = node;
        this.session = session;
        this.iClient = iClient;
        this.uri = uri;
        
        boolean isVersionableNode;
        try {
            isVersionableNode = node.isNodeType(JCRConstants.MIX_VERSIONABLE);
        } catch (RepositoryException e) {
            isVersionableNode = false;
        }
        this.isVersioned = isVersionableNode;
    }

    /**
     * @see java.io.ByteArrayOutputStream#close()
     */
    @Override
    public void close() throws IOException {
        if (isClosed) {
            return;
        }
        super.close();
        isClosed = true;
        
        try {
        	if (isVersioned) {
        		node.checkout();
        	}

            if (node.hasNode(JCRConstants.JCR_CONTENT)) {
                Node content = node.getNode(JCRConstants.JCR_CONTENT);
                if (JCRSourceFactory.isXMLResource(content)) {
                    deleteChildren();
                    try {
                        writeXML(content);
                    } catch (SAXException e) {
                        throw new CascadingIOException("XML is not well-formed", e);
                    }
                } else {
                    writeBinary(content);
                }
            } else {
                // jcr:content node needs to be created
                if (isXML()) {
                    try {
                        createXML(node);
                    } catch (SAXException se) {
                        throw new CascadingIOException("XML is not well-formed", se);
                    }
                } else {
                    createBinary(node);
                }
            }
            // don't forget to commit
            session.save();
            
            if (isVersioned) {
        		node.checkin();
        	}
            index(uri);
        } catch (RepositoryException e) {
            throw new CascadingIOException("Unable to write to repository "
                    + e.getLocalizedMessage(), e);
        }
    }

    private void index(String uri) {
        // check if the path of the JCR source matches one of the excludes
        // patterns
        boolean index = true;
        for (String template : JCRSourceFactory.iExcludes) {
            if (WildcardMatcherHelper.match(template, uri) != null) {
                index = false;
                break;
            }
        }
        if (index) {
            // use index client to notify the indexer about the delete
            List<String> changedPaths = new ArrayList<String>();
            List<String> deletedPaths = new ArrayList<String>();
            changedPaths.add(uri);
            iClient.index(changedPaths, deletedPaths);
        }
    }

    private void createXML(Node node) throws IOException, SAXException {
        try {
            Node content = node.addNode(JCRConstants.JCR_CONTENT, JCRConstants.XT_DOCUMENT);
            writeXML(content);
        } catch (RepositoryException e) {
            throw new CascadingIOException("Unable to write XML content into repository: "
                    + e.getLocalizedMessage(), e);
        }
    }

    private void createBinary(Node node) throws IOException {
        try {
            Node content = node.addNode(JCRConstants.JCR_CONTENT, JCRConstants.NT_RESOURCE);
            writeBinary(content);
        } catch (RepositoryException e) {
            throw new CascadingIOException("Unable to write binary content into repository: "
                    + e.getLocalizedMessage(), e);
        }
    }

    private void writeXML(Node content) throws IOException, SAXException {
        createSaxParser().parse(
                // no need to copy the byte array (toByteArray()), since we have access to it
                new ByteArrayInputStream(this.buf, 0, this.size()),
                new SAXToJCRNodesConverter(content)
        );
    }

    private void writeBinary(Node content) throws IOException {
        try {
            // no need to copy the byte array (toByteArray()), since we have access to it
            content.setProperty(JCRConstants.JCR_DATA,
                    new ByteArrayInputStream(this.buf, 0, this.size()));
            content.setProperty(JCRConstants.JCR_MIMETYPE,
                    "application/octetstream"); //$NON-NLS-1$
            content.setProperty(JCRConstants.JCR_LASTMODIFIED,
                    new GregorianCalendar());
        } catch (ValueFormatException e) {
            throw new IOException("Invalid value format: "
                    + e.getLocalizedMessage());
        } catch (VersionException e) {
            throw new IOException("Invalid Version" + e.getLocalizedMessage());
        } catch (LockException e) {
            throw new IOException("Resource is locked"
                    + e.getLocalizedMessage());
        } catch (ConstraintViolationException e) {
            throw new IOException("Constrains violated: "
                    + e.getLocalizedMessage());
        } catch (PathNotFoundException e) {
            throw new IOException("Path not found: " + e.getLocalizedMessage());
        } catch (RepositoryException e) {
            throw new IOException("Unable to write to repository: "
                    + e.getLocalizedMessage());
        }
    }

    private void deleteChildren() throws IOException {
        // remove old content
        try {
            NodeIterator nit = node.getNode(JCRConstants.JCR_CONTENT).getNodes();
            while (nit.hasNext()) {
                nit.nextNode().remove();
            }
        } catch (UnsupportedRepositoryOperationException e) {
            throw new IOException("Locking is not supported by repository: "
                    + e.getLocalizedMessage());
        } catch (LockException e) {
            throw new IOException("Unable to get lock: "
                    + e.getLocalizedMessage());
        } catch (AccessDeniedException e) {
            throw new IOException("Access to repository denied: "
                    + e.getLocalizedMessage());
        } catch (InvalidItemStateException e) {
            throw new IOException("Item state is invalid: "
                    + e.getLocalizedMessage());
        } catch (RepositoryException e) {
            throw new IOException("Unable to write to repository "
                    + e.getLocalizedMessage());
        }
    }

    private boolean isXML() {
        try {
            createSaxParser().parse(
                    // no need to copy the byte array (toByteArray()), since we have access to it
                    new ByteArrayInputStream(this.buf, 0, this.size()),
                    new DefaultHandler()
            );
            return true;
        } catch (SAXException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    private SAXParser createSaxParser() {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        try {
            return parserFactory.newSAXParser();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Cannot create XML SAX parser", e);
        } catch (SAXException e) {
            throw new RuntimeException("Cannot create XML SAX parser", e);
        }
    }

    public boolean canCancel() {
        // cancle not implemented
        return false;
    }

    public void cancel() throws IOException {
        // TODO: cancel is possible before session.save() is called -> implement
    }
}
