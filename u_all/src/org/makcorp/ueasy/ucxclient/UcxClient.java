/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.ucxclient;

import org.makcorp.ueasy.uflow.*;

import java.io.*;
import java.util.Properties;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory; 
import javax.xml.parsers.FactoryConfigurationError; 
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import org.apache.log4j.*;

/**
 * UFlow Command-driven Xml-Rpc Client
 */
public class UcxClient implements UServer
{
	public final static String PROPERTIES_FILE = "ucxclient.properties";
	private Properties conf;
	private DocumentBuilder builder;
	/** logging utility */
	static Logger log = Logger.getRootLogger();
	
	public UcxClient()
	{
		conf = new Properties();
		// load key-value pairs from conf file
		try {
			FileInputStream fis = new FileInputStream(PROPERTIES_FILE);
			conf.load(fis);
		}
		catch (Exception e)
		{
			log.fatal ("reading UcxClient properties", e);
			System.exit(1);
		}
		Document document;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			log.fatal ("loading parser configuration", e);
			System.exit(1);
		}
	}
	
	/** Convenient method to get the error in a UFlow XML-RPC Document */
	private static int getError (Document doc)
	{
		NodeList values = doc.getDocumentElement().getElementsByTagName("int");
		return Integer.parseInt(((Element)values.item(0)).getChildNodes().item(0).getNodeValue());
	}
	
	/** Convenient method to get a string param in a XML-RPC Document */
	private static String getParam (Document doc, int index)
	{
		NodeList values = doc.getDocumentElement().getElementsByTagName("string");
		return ((Element)values.item(index)).getChildNodes().item(0).getNodeValue();
	}


	/** Implements UServer */
	
	public UrOpen runOpen (String id, String doc)
	{
		String command = conf.getProperty ("OPEN");
		command += " " + id;
		command += " " + doc;
		
		int error = UReply.INTERNAL;
		String errorMsg = "";
		String name = "";
		String modeName = "";
		String lang = "";
		String transmission = "";
		String mode = "";
		String css = "";
		String data = "";
		
		try {
			// execute the command
			Process p = Runtime.getRuntime().exec(command);
			// parse the command's output into a Document
			Document reply = builder.parse (p.getInputStream());

			error = getError (reply);
			errorMsg = getParam (reply, 0);
			name     = getParam (reply, 1);
			modeName = getParam (reply, 2);
			lang     = getParam (reply, 3);
			mode     = getParam (reply, 4);
			css      = getParam (reply, 5);
			data     = getParam (reply, 6);
		} 
		catch (Exception e) {
			log.error ("requesting the server", e);
			error = UReply.BAD_REPLY;
		}
		return new UrOpen (error, errorMsg, name, modeName, lang, "DATA,DATA,DATA", mode, css, data);
	}

	/**
	 * In this implementation, the data is stored in a temp file and only the
	 * reference to that file is transmitted via XML-RPC
	 * ie: transmission is considered to be set to 'LOCAL' wether it is or not
	 */
	public UReply runSave (String id, String doc, String modeName, String transmission, String data)
	{
		String command = conf.getProperty ("SAVE");
		command += " " + id;
		command += " " + doc;
		command += " " + modeName;
		command += " " + transmission;
		int error = UReply.INTERNAL;
		String errorMsg = "";
		
		/* Put the data in a temporary file, then send its reference to the commmand */
		File tempFile = null;
		try {
			/* creating a temp file with a unique file name in the temp directory */
			tempFile = File.createTempFile ("uflowsaving_" + id + "_"
					+ Long.toString(new Date().getTime()) + "_" + Double.toString(Math.random()), null);
			FileWriter tempFileWriter = new FileWriter (tempFile, false); // false: do not append text
			tempFileWriter.write (data);
			tempFileWriter.close ();
		}
		catch (IOException e)
		{
			log.error ("creating a temp file", e);
			error = UReply.INTERNAL;
		}

		command += " " + tempFile.getAbsolutePath();
		log.debug("%- "+command);
		
		try {
			Process p = Runtime.getRuntime().exec(command);
			Document reply = builder.parse (p.getInputStream());

			BufferedReader input = new BufferedReader (new InputStreamReader(
						Runtime.getRuntime().exec(command).getInputStream()));
			String line;
			while ((line = input.readLine()) != null) log.debug ("% "+line);
			
			error = getError (reply);
			errorMsg = getParam (reply, 0);
		} 
		catch (Exception e) {
			log.error ("requesting the server", e);
			error = UReply.INTERNAL;
		}
		try {
			tempFile.delete();
		}
		catch (SecurityException e)
		{
			log.error ("deleting a temp file", e);
			error = UReply.INTERNAL;
		}

		return new UReply(error, errorMsg);
	}

	public UReply runClose (String id, String doc)
	{
		String command = conf.getProperty ("CLOSE");
		command += " " + id;
		command += " " + doc;
		int error = UReply.INTERNAL;
		String errorMsg = "";
		
		try {
			Process p = Runtime.getRuntime().exec(command);
			Document reply = builder.parse (p.getInputStream());
			
			error = getError (reply);
			errorMsg = getParam (reply, 0);
		} 
		catch (Exception e) {
			log.error ("requesting the server", e);
			error = UReply.INTERNAL;
		}

		return new UReply(error, errorMsg);
	}

	public UrList runList (String id, String doc, String list)
	{
		String command = conf.getProperty ("LIST");
		command += " " + id;
		command += " " + doc;
		int error = UReply.INTERNAL;
		String errorMsg = "";
		String name = "";
		String dirs = "";
		String documents = "";
		String medias = "";
		
		try {
			Process p = Runtime.getRuntime().exec(command);
			Document reply = builder.parse (p.getInputStream());
			
			error = getError (reply);
			errorMsg = getParam (reply, 0);
			name = getParam (reply, 1);
			dirs = getParam (reply, 2);
			documents = getParam (reply, 3);
			medias = getParam (reply, 4);
		} 
		catch (Exception e) {
			log.error ("requesting the server", e);
			error = UReply.INTERNAL;
		}

		return new UrList (error, errorMsg, name, dirs, documents, medias);
	}

	public UrGetMedia runGetMedia (String id, String doc, String media)
	{
		String command = conf.getProperty ("GET_MEDIA");
		command += " " + id;
		command += " " + doc;
		int error = UReply.INTERNAL;
		String errorMsg = "";
		String name = "";
		String url = "";
		String mime = "";
		String transmission = "";
		String data = "";
		
		try {
			Process p = Runtime.getRuntime().exec(command);
			Document reply = builder.parse (p.getInputStream());
			
			error = getError (reply);
			errorMsg = getParam (reply, 0);
			name = getParam (reply, 1);
			url = getParam (reply, 2);
			mime = getParam (reply, 3);
			transmission = getParam (reply, 4);
			data = getParam (reply, 5);
		} 
		catch (Exception e) {
			log.error ("requesting server", e);
			error = UReply.INTERNAL;
		}

		return new UrGetMedia (error, errorMsg, name, url, mime, transmission, data);
	}
	
	/** Testing only  */
	public static void main(String args[]) {
		
		UServer server = new UcxClient();

		System.out.println(server.runOpen("myid","mydoc"));
		System.out.println(server.runSave("myid","mydoc","org.makcorp.ueasy.modes.Html","LOCAL","ceci\n<i>est</i> un\n test"));
		System.out.println(server.runClose("myid","mydoc"));
		System.out.println(server.runList ("myid","mydoc", "mylist"));
		System.out.println(server.runGetMedia ("myid","mydoc", "mymedia"));
	}
}
