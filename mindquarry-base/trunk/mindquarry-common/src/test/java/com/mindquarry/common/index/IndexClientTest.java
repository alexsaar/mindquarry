/**
 * Copyright (C) 2006 Mindquarry GmbH, All Rights Reserved
 */
package com.mindquarry.common.index;

import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.service.ServiceException;

import com.mindquarry.common.test.AvalonSpringContainerTestBase;

/**
 * Add summary documentation here.
 * 
 * @author <a href="mailto:alexander(dot)saar(at)mindquarry(dot)com">Alexander
 *         Saar</a>
 */
public class IndexClientTest extends AvalonSpringContainerTestBase {
    public void testIndexUpdate() throws ServiceException {
        List<String> deletedPaths = new ArrayList<String>();
        //deletedPaths.add("jcr:///teamspaces/mindquarry/tasks/task3.xml");
        List<String> changedPaths = new ArrayList<String>();
        changedPaths.add("jcr:///teamspaces/mindquarry/tasks/task3.xml");

        SolrIndexClient iClient = new SolrIndexClient();
        iClient.setSolrLogin("admin");
        iClient.setSolrPassword("admin");
        iClient.setSolrEndpoint("http://localhost:8888/solr/update");

        iClient.indexSynch(changedPaths, deletedPaths);
        
        
        SolrIndexClient indexClient = (SolrIndexClient) lookup(IndexClient.ROLE);
        assertEquals("admin", indexClient.getSolrLogin());
    }
}
