/*
 * Coypright (c) 2006 Mindquarry GmbH, Potsdam, Germany 
 */
package com.mindquarry.jcr.jackrabbit;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;

/**
 * This extension of the Cocoon internal Jackrabbit repository component ensures
 * the {@link JackrabbitInitializer} component runs and initializes the
 * repository.
 * 
 * @author <a href="mailto:alexander(dot)klimetschek(at)mindquarry(dot)com">
 *         Alexander Klimetschek</a>
 */
public class JackrabbitRepository extends
        org.apache.cocoon.jcr.JackrabbitRepository {

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.cocoon.jcr.AbstractRepository#service(org.apache.avalon.framework.service.ServiceManager)
     */
    @Override
    public void service(ServiceManager manager) throws ServiceException {
        super.service(manager);
    }

    /* (non-Javadoc)
     * @see org.apache.cocoon.jcr.JackrabbitRepository#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    @Override
    public void configure(Configuration config) throws ConfigurationException {
        super.configure(config);

        // must run the initializer on repository startup
        try {
            this.manager.lookup(JackrabbitInitializer.ROLE);
        } catch (ServiceException e) {
            // do nothing here, repository does not get initialized
            //e.printStackTrace();
        }
    }

}
