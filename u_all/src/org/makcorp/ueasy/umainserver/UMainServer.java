/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.umainserver;

import java.io.FileInputStream;
import java.util.Properties;
import java.io.IOException;

import org.apache.log4j.*;

import org.makcorp.ueasy.service.Service;
import org.makcorp.ueasy.service.ServiceServer;
import org.makcorp.ueasy.modes.*;
import org.makcorp.ueasy.uflow.*;
import org.makcorp.ueasy.ucxclient.*;
import org.makcorp.ueasy.usxserver.*;
import org.makcorp.ueasy.aml.*;

/**
 * The main class for the uEasy server
 * The requests of the UXmlRpcServer on this class will be redirected to the UTextClient
 * Usage: java UMainServer -port-
 */

public class UMainServer implements UServer, Service {
	
	/** properties file, the modules are chosen in that file */
	public static final String PROPERTIES_FILE = "umainserver.properties";
	
	/** the Uflow server module */
	private UServer server = null;
	/** the UFlow client module */
	private UClient client = null;
	/** the translation module */
	private Translator translator = null;
	/** the AML factory module */
	private AmlFactory amlFactory = null;
	
	/** logging utility */
	static Logger log = Logger.getRootLogger();
	
	/*
	 * Constructor
	 * It loads the modules according to the properties file
	 */
	public UMainServer () 
	{
		/** the ServiceServer module */
		ServiceServer serviceServer = null;
		
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(PROPERTIES_FILE));
		}
		catch (java.io.FileNotFoundException e)
		{
			log.fatal ("File "+PROPERTIES_FILE+" not found.", e);
			System.exit(1);
		}
		catch (java.io.IOException e)
		{
			log.fatal ("I/O error while reading "+PROPERTIES_FILE, e);
			System.exit(1);
		}
		
		try {
			log.info ("Loading Modules");
			
			log.info ("client:"+p.getProperty("client"));
			log.info ("amlfactory:"+p.getProperty("amlfactory"));
			log.info ("server:"+p.getProperty("server"));
			log.info ("translator:"+p.getProperty("translator"));
			log.info ("serviceserver:"+p.getProperty("serviceserver"));
			
			client        = (UClient)      Class.forName(p.getProperty("client"       )).newInstance();
			amlFactory    = (AmlFactory)   Class.forName(p.getProperty("amlfactory"   )).newInstance();
			server        = (UServer)      Class.forName(p.getProperty("server"       )).newInstance();
			translator    = (Translator)   Class.forName(p.getProperty("translator"   )).newInstance();
			serviceServer = (ServiceServer)Class.forName(p.getProperty("serviceserver")).newInstance();
		}
		catch (ClassNotFoundException e) {
			log.fatal ("Bad classes in properties", e);
			System.exit(1);
		}
		catch (InstantiationException e) {
			log.fatal ("Bad classes in properties", e);
			System.exit(1);
		}
		catch (IllegalAccessException e) {
			log.fatal ("Bad classes in properties", e);
			System.exit(1);
		}

		log.debug (client.toString()+amlFactory.toString()+server.toString()+translator.toString());
		client.setUServer(this);

		// start the ServiceServer that will enable admins to test/reload/stop this Service
		serviceServer.addService(this);
		serviceServer.start();
		
		log.info ("UMainServer started");
		System.out.println ("UMainServer started");
	}

	/**
	 * Run the uEasy server
	 * Usage: java UMainServer
	 */
	public static void main(String args[])
	{
		new UMainServer();
	}
	
	/*----------------------------- Implements UServer -----------------------------------*/

	public UrOpen runOpen (String id, String doc)
	{
		// run the OPEN request on the server
		UrOpen urOpen = server.runOpen (id, doc);

		int error = urOpen.getError();
		String errorMsg = urOpen.getErrorMessage();
		String modeName = urOpen.getModeName();
		String lang = urOpen.getLang();
		String data = urOpen.getData();
		String actions = "";
		
		try {
			// get the AML file for this mode and this language
			actions = amlFactory.getAmlString(modeName, lang).toString();
		}
		catch (Exception e)
		{
			log.error ("getting AML", e);
			if (error == UReply.OK) error = UReply.RESOURCE_NOT_FOUND;
			errorMsg += "\nError while getting AML";
		}
		
		// Data translation to UXHTML
		String translatedData = "null";
		try {
			// translate the document from its format to UXHTML
			translatedData = translator.toUxhtml(modeName,data);
		}
		catch ( TranslatorException e)
		{
			log.error ("translating from "+modeName+" to UXHTML", e);
			if (error == UReply.OK) error = UReply.BAD_REQUEST;
			errorMsg += "\nTranslation from "+modeName+" to UXHTML failed.";
		}
		return new UrOpen (error, errorMsg, urOpen.getName(), modeName, lang,
				urOpen.getTransmission(), actions, urOpen.getCss(), translatedData);
	}

	public UReply runSave (String id, String doc, String modeName, String transmission, String data)
	{
		// Data translation from UXHTML
		String translatedData = "null";
		try {
			translatedData = translator.fromUxhtml(modeName, data);
		}
		catch ( TranslatorException e)
		{
			log.error ("Translating from "+modeName+" to UXHTML", e);
			// if an error occured, return an empty reply with appropriate error number and message
			return new UReply (UReply.BAD_REQUEST,
					"Translation from "+modeName+" to UXHTML failed.");
		}
		UReply u = server.runSave (id, doc, transmission, modeName, translatedData);
		log.debug (u);
		return u;
	}

	public UReply runClose (String id, String doc)
	{
		return server.runClose (id, doc);
	}
	public UrList runList (String id, String doc, String list)
	{
		return server.runList (id, doc, list);
	}
	public UrGetMedia runGetMedia (String id, String doc, String media)
	{
		return server.runGetMedia (id, doc, media);
	}

	/*----------------------------- Implements Service -----------------------------------*/

	public void serviceTest ()
	{
		log.error ("serviceTest : Not implemented yet");
	}

	public void serviceReload ()
	{
		log.error ("serviceReload: Not implemented yet");
	}

	public void serviceStop ()
	{
		log.info ("Exitting");
		System.exit(0);
	}
}
