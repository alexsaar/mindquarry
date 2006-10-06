package com.mindquarry.persistence.xmlbeans.xml;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.cocoon.serialization.Serializer;
import org.apache.cocoon.serialization.XMLSerializer;
import org.apache.excalibur.xml.sax.XMLizable;

import com.mindquarry.common.xml.EntityXmlizer;
import com.mindquarry.common.xml.EntityXmlizerFactory;
import com.mindquarry.persistence.xmlbeans.XmlBeansPersistenceTestBase;
import com.mindquarry.types.teamspace.Teamspace;
import com.mindquarry.types.user.Email;
import com.mindquarry.types.user.User;

public class XmlBeansEntityXmlizerTest extends XmlBeansPersistenceTestBase {
    
    private static final String USER_NS_URI = "http://www.mindquarry.com/ns/schema/user";
    private static final String USERS_LOCALNAME = "users";
    
    private EntityXmlizerFactory xmlizerFactory_;
    
    /**
     * @see com.mindquarry.jcr.jackrabbit.JCRTestBase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String xmlizerFactoryName = EntityXmlizerFactory.class.getName();
        xmlizerFactory_ = (EntityXmlizerFactory) lookup(xmlizerFactoryName);
    }

    public void testXmlBeansEntityXmlizer() throws Exception {
        
        String[] userIds = new String[] {"bastian", "lars", "alex", "alexs"};
        List<XMLizable> xmlizableUsers = new LinkedList<XMLizable>();
        for (String userId : userIds) {
            User user = makeUser(userId);
            xmlizableUsers.add(xmlizerFactory_.newEntityXmlizer(user));
        }
        
        XMLizable xmlizableUserList = new XMLizableListWrapper(
                USER_NS_URI, USERS_LOCALNAME, xmlizableUsers);
        
        Teamspace teamspace = makeTeamspace();
        EntityXmlizer teamspaceXmlizer = 
            xmlizerFactory_.newEntityXmlizer(teamspace);
        
        teamspaceXmlizer.beforeEndEntityElement(xmlizableUserList);
        
        
        ByteArrayOutputStream actualOut = new ByteArrayOutputStream();
        
        Serializer serializer = (Serializer) lookup(XMLSerializer.ROLE);
        serializer.setOutputStream(actualOut);
                
        serializer.startDocument();        
        teamspaceXmlizer.toSAX(serializer);        
        serializer.endDocument();
    }
    
    private User makeUser(String userId) {
        User result = User.Factory.newInstance();
        
        result.setId(userId);
        result.setPassword("password");
        
        result.setName("firstname");
        result.setSurname("lastname");
        
        Email email = result.addNewEmail();
        email.setAddress("firstname.lastname@mindquarry.com");
        email.setIsConversationRecipient(true);
        
        return result;
    }
    
    private Teamspace makeTeamspace() {
        Teamspace teamspace = Teamspace.Factory.newInstance();
        teamspace.setDescription("a great description");
        teamspace.setId("mindquarry");
        teamspace.setName("Mindquarry");
        
        return teamspace;
    }
}
