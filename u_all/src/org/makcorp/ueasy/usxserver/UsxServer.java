/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.usxserver;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.XmlRpc;
import org.apache.log4j.Logger;

import org.makcorp.ueasy.uflow.*;

/**
 * Uflow Socket Xml-rpc Server
 * This class contains a Xml-Rpc server that runs on the specified port of the running host.
 * It analyses requests issued on its port and resends those requests to the specified UServer
 */

public class UsxServer implements UClient
{	
	public final static String PROPERTIES_FILE = "usxserver.properties";
	/** the XML-RPC web server, listening to a socket specified in the properties file */
	private WebServer webServer;
	/** logging utility */
	Logger log = Logger.getRootLogger();
	
	/**
	 * Creates a new UsxServer
	 * @param server the server to which the request will be sent
	 */
	public UsxServer ()
	{
		Properties p = new Properties();
		try {
			p.load (new FileInputStream (PROPERTIES_FILE));
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
			webServer = new WebServer (Integer.parseInt(p.getProperty("port")));
		}
		catch (IOException e) {
			log.fatal ("Xml-Rpc WebServer couldn't start. Maybe the specified port is already open", e);
			System.exit(1);
		}
		XmlRpc.setKeepAlive (true);
		
	}

	/**
	 * Sets the UServer to which the requests will be transmitted.
	 * @param uServer the UServer
	 */
	public void setUServer (UServer uServer)
	{
		webServer.addHandler("$default", new UHandler(uServer));
		log.info ("UsxServer running.");
	}
}
