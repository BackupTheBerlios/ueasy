/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.ustand;

import javax.swing.JFrame;
import java.util.Properties;
import java.io.FileInputStream;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.makcorp.ueasy.uflow.*;
import org.makcorp.ueasy.usxclient.*;
import org.makcorp.ueasy.ueditor.*;

/**
 * this is a standalone client for wysiwyg editing through a network via XML-RPC uflow
 */
public class UStand extends JFrame implements UServer
{
	/** the uFlow server */
	private UServer server;
	/** configuration parameters from the properties file */
	private Properties conf;

	/**
	 * Main method
	 * @param s the configuration file to be used. If none is specified, it is supposed to be ustand.properties
	 */
	public static void main(String s[])
	{	
		String confFile;
		if (s.length < 1 ) confFile = "ustand.properties";
		else confFile = s[0];
		new UStand (confFile);
	}

	public UStand (String confFile)
	{
		super ("UEasy Standalone Client");

		conf = new Properties();
		// load key-value pairs from conf file
		try {
			FileInputStream fis = new FileInputStream(confFile);
			conf.load(fis);
		}
		catch (Exception e)
		{
			System.out.println ("Error while accessing configuration file \""+confFile+"\"");
			e.printStackTrace ();
			System.exit(1);
		}
		
		/* Get the informations from the conf file */
		String p_serverUrl = getParameter("U_XMLRPC_SERVER");
		String p_id = getParameter ("U_ID");
		String p_document = getParameter("U_DOCUMENT");
		boolean p_showToolBar = getParameter("TOOLBAR")==null || !getParameter("TOOLBAR").toLowerCase().equals("false");
		boolean p_showViewSource = getParameter("SOURCEVIEW")==null || !getParameter("SOURCEVIEW").toLowerCase().equals("false");
		String p_lang = getParameter("LANG");
		boolean p_exclusive = getParameter("EXCLUSIVE")==null || !getParameter("EXCLUSIVE").toLowerCase().equals("false");
		boolean p_showMenuIcons = getParameter("MENUICONS")==null || !getParameter("MENUICONS").toLowerCase().equals("false");
	
		
		/* Create the UsxClient that will be able to connect to the server */
		try {
			server = new UsxClient(p_serverUrl);
		}
		catch (java.net.MalformedURLException e)
		{
			System.out.println("Malformed Server Name: "+p_serverUrl);
		}
		
		
		System.out.println ("serverUrl: "+p_serverUrl);
		System.out.println ("Id: "+p_id);

		/* configuration parameters for the UEditor */
		Conf uEditorConf = new Conf (p_id, p_document, p_showToolBar,
				p_showViewSource, p_lang, p_exclusive, p_showMenuIcons);
		System.out.println (uEditorConf);
		
		/* create the UEditor and call it into a JFrame  root pane */
		UEditor pane = new UEditor (this, uEditorConf);
		this.setRootPane (pane);
		
    		this.addWindowListener
		(
			new WindowAdapter()
			{
        		public void windowClosing(WindowEvent e) {System.exit(0);}
			}
		);

		
		this.pack();
    		this.setVisible(true);

	}

	/** Get a parameter in the conf file */
	protected String getParameter (String key)
	{
		return conf.getProperty (key);
	}


	/** Implementing UServer */
	
	public UrOpen runOpen (String id, String document)
	{
		return server.runOpen (id, document);
	}
	public UReply runSave (String id, String document, String modeName, String transmission, String data)
	{
		return server.runSave (id, document, modeName, transmission, data);
	}
	public UReply runClose (String id, String document)
	{
		return server.runClose (id, document);
	}
	public UrList runList (String id, String document, String list)
	{
		return server.runList (id, document, list);
	}
	public UrGetMedia runGetMedia (String id, String document, String media)
	{
		return server.runGetMedia (id, document, media);
	}
}
