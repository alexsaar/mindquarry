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
package com.mindquarry.persistence.client;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.mindquarry.common.persistence.Session;
import com.mindquarry.persistence.castor.CastorSessionFactoryStandalone;
import com.mindquarry.persistence.castor.CastorSessionStandalone;

/**
 * Command line client to be used for working with the Mindquarry persistence
 * layer.
 * 
 * @author <a href="mailto:alexander(dot)saar(at)mindquarry(dot)com">Alexander
 *         Saar</a>
 */
public class CommandLine {
    private static final String O_REPO = "r"; //$NON-NLS-1$

    private static final String O_PERSIST = "a"; //$NON-NLS-1$

    private static final String O_DEL = "d"; //$NON-NLS-1$

    private static final String O_USER = "u"; //$NON-NLS-1$

    private static final String O_PWD = "p"; //$NON-NLS-1$

    private static final char VALUE_SEPARATOR = ' ';

    private Logger logger;

    /**
     * The options object for the command line applications.
     */
    private static final Options options;

    // static initialization of command line options
    static {
        Option repo = new Option(O_REPO, "repository", true,
                "The endpoint where the persistence layer is available."); //$NON-NLS-1$
        repo.setRequired(true);

        Option persist = new Option(O_PERSIST, "add", true,
                "A list of XML files containing the types that should "
                        + "be stored in the persistence layer.");
        persist.setValueSeparator(VALUE_SEPARATOR);

        Option delete = new Option(O_DEL, "delete", true,
                "A list of URIs describing the types that should "
                        + "be deleted from the persistence layer.");
        delete.setValueSeparator(VALUE_SEPARATOR);

        OptionGroup actions = new OptionGroup();
        actions.setRequired(true);
        actions.addOption(persist);
        actions.addOption(delete);

        Option user = new Option(O_USER, "user", true,
                "The user name to be used for login and setup the repository.");
        user.setRequired(true);

        Option pwd = new Option(O_PWD, "password", true,
                "The password to be used for login and setup the repository.");
        pwd.setRequired(true);

        options = new Options();
        options.addOption(repo);
        options.addOptionGroup(actions);
        options.addOption(user);
        options.addOption(pwd);
    }

    /**
     * The main entry point of the application.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Starting persistence client...");

        CommandLine cl = new CommandLine();
        cl.run(args);

        System.out.println("Persistence actions finished.");
    }

    /**
     * Evaluates the given command line arguments, initializes connection to the
     * persistence layer and starts the specified actions.
     * 
     * @param args the command line arguments
     */
    private void run(String[] args) throws Exception {
        logger = new ConsoleLogger(ConsoleLogger.LEVEL_WARN);

        // create the parser
        org.apache.commons.cli.CommandLine line = null;
        CommandLineParser parser = new GnuParser();
        try {
            // parse the command line arguments
            line = parser.parse(options, args);
        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed. Reason: "
                    + exp.getLocalizedMessage());
            printUsage();
            return;
        }
        // init factory configuration
        DefaultConfiguration config = new DefaultConfiguration("credentials"); //$NON-NLS-1$
        config.setAttribute("login", line.getOptionValue(O_USER)); //$NON-NLS-1$
        config.setAttribute("password", line.getOptionValue(O_PWD)); //$NON-NLS-1$
        config.setAttribute("rmi", line.getOptionValue(O_REPO)); //$NON-NLS-1$
        
        Map<String, String> mappings = namespaceMappings();
        DefaultConfiguration allMappingsConfig = 
            new DefaultConfiguration("mappings");
        config.addChild(allMappingsConfig);
        
        for (String prefix : mappings.keySet()) {
            DefaultConfiguration mappingConfig = 
                new DefaultConfiguration("mapping");
            mappingConfig.setAttribute("prefix", prefix);
            mappingConfig.setAttribute("namespace", mappings.get(prefix));
            allMappingsConfig.addChild(mappingConfig);
        }

        // init connection to persistence layer
        CastorSessionFactoryStandalone factory = new CastorSessionFactoryStandalone();
        factory.enableLogging(logger);
        factory.configure(config);
        factory.initialize();

        CastorSessionStandalone session = factory.currentSession();

        // check what actions are specified
        if (line.hasOption(O_PERSIST)) {
            persistTypes(line.getOptionValues(O_PERSIST), session);
        } else if (line.hasOption(O_DEL)) {
            deleteTypes(line.getOptionValues(O_DEL), session);
        }
        session.commit();
    }

    private void persistTypes(String[] optionValues, 
            CastorSessionStandalone session) throws Exception {
        
        for (String value : optionValues) {
            System.out.println("Start adding of " + value);

            FileReader fileReader = new FileReader(new File(value));
            session.persistFromReader(fileReader);
        }
    }

    private void deleteTypes(String[] optionValues, Session session) {
        // TODO implement deletion of types
    }

    /**
     * Automatically generate and print the help statement.
     */
    private void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar persistence-client-<version>.jar", //$NON-NLS-1$
                options, true);
    }
    
    private Map<String, String> namespaceMappings() {
        Map<String, String> mappings = new HashMap<String, String>();
        mappings.put("user", "http://www.mindquarry.com/ns/schema/user");
        mappings.put("team", "http://www.mindquarry.com/ns/schema/teamspace");
        mappings.put("tag", "http://www.mindquarry.com/ns/schema/tag");
        mappings.put("wiki", "http://www.mindquarry.com/ns/schema/wiki");
        mappings.put("task", "http://www.mindquarry.com/ns/schema/task");
        mappings.put("cnv", "http://www.mindquarry.com/ns/schema/conversation");
        return mappings;
    }
}
