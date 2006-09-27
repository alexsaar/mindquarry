/**
 * Copyright (C) 2006 MindQuarry GmbH, All Rights Reserved
 */
package com.mindquarry.jcr.xml.source.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Converter for JCR nodes to SAX events.
 * 
 * @author <a
 *         href="mailto:alexander(dot)klimetschek(at)mindquarry(dot)com">Alexander
 *         Klimetschek</a>
 * @author <a href="mailto:alexander(dot)saar(at)mindquarry(dot)com">Alexander
 *         Saar</a>
 */
public class JCRNodesToSAXConverter {
    /**
     * Converts a JCR node with jcr:content type xt:document into SAX events.
     * 
     * @param node the node to be converted
     * @param handler the SAX handler to be used
     * @throws SAXException thrown if something goes wrong during processing of
     *         the JCR nodes
     */
    public static void convertToSAX(Node node, ContentHandler handler)
            throws SAXException {
        handler.startDocument();

        try {
            convertChildsToSAX(node.getNode("jcr:content"), handler);
        } catch (Exception e) {
            throw new SAXException(
                    "An error occured while converting JCR nodes to SAX.", e);
        }
        handler.endDocument();
    }

    private static void convertChildsToSAX(Node node, ContentHandler handler)
            throws RepositoryException, SAXException, IOException {
        Session session = node.getSession();
        Workspace ws = session.getWorkspace();
        NamespaceRegistry nr = ws.getNamespaceRegistry();

        NodeIterator nit = node.getNodes();
        while (nit.hasNext()) {
            Node child = nit.nextNode();

            if (child.isNodeType("xt:element")) {
                // String namespaceURI = "";
                // String localName = "";
                // String qName = "";
                //
                // String[] parts = child.getName().split(":");
                // if (parts.length == 2) {
                // localName = parts[1];
                // qName = child.getName();
                //
                // String prefix = parts[0];
                // namespaceURI = nr.getURI(prefix);
                // } else {
                // qName = parts[0];
                // }
                AttributesImpl atts = new AttributesImpl();
                PropertyIterator pit = child.getProperties();

                while (pit.hasNext()) {
                    Property prop = pit.nextProperty();
                    if (prop.getName().startsWith("jcr")) {
                        continue;
                    }
                    String qName = prop.getName();
                    String namespaceURI = "";
                    String localName = "";
                    
                    String prefix = getPrefix(prop.getName());
                    if(prefix != null) {
                        localName = getLocalName(prop.getName());
                        namespaceURI = getNamespace(prefix, nr);
                    } else {
                        localName = prop.getName();
                    }
                    atts.addAttribute(namespaceURI, localName, qName,
                            "CDATA", prop.getString());
                }
                String qName = child.getName();
                String namespaceURI = "";
                String localName = "";
                
                String prefix = getPrefix(child.getName());
                if(prefix != null) {
                    localName = getLocalName(child.getName());
                    namespaceURI = getNamespace(prefix, nr);
                } else {
                    localName = child.getName();
                }
                handler.startElement(namespaceURI, localName, qName, atts);

                convertChildsToSAX(child, handler);

                handler.endElement(namespaceURI, localName, qName);
            } else if (child.isNodeType("xt:text")) {
                InputStream is = child.getProperty("xt:characters").getStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();

                int b;
                while ((b = is.read()) != -1) {
                    os.write(b);
                }
                char[] characters = new String(os.toByteArray()).toCharArray();
                handler.characters(characters, 0, characters.length);
            }
        }
    }

    private static String getNamespace(String prefix, NamespaceRegistry nr)
            throws NamespaceException, RepositoryException {
        return nr.getURI(prefix);
    }

    private static String getLocalName(String name) throws NamespaceException,
            RepositoryException {
        String[] parts = name.split(":");
        if (parts.length == 2) {
            return parts[0];
        } else {
            return parts[1];
        }
    }

    private static String getPrefix(String name) throws NamespaceException,
            RepositoryException {
        String[] parts = name.split(":");
        if (parts.length == 2) {
            return parts[1];
        } else {
            return null;
        }
    }
}
