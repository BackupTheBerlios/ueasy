/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.uapplet;

import javax.swing.JApplet;

import org.makcorp.ueasy.uflow.*;
import org.makcorp.ueasy.usxclient.*;
import org.makcorp.ueasy.ueditor.*;

/**
 * the ueasy client as an applet for wysiwyg editing through a network via XML-RPC uflow
 * The applet has to be embedded in a webpage containing parameters described in the UClient doc
 * The navigator that browse the webpage should accept javascript and cookies, and must have a javaplugin
 *
 * About netscape's javascript library: 
 * Because the netscape.javascript seems not to be free software, I removed it.
 * As a consequence, you can't use a id cookie.
 * However, if you want to use it, do the following:
 *  - uncomment the /// comments (three slashes) in this file
 *  - rename CookieRetriever.java.non-free to CookieRetriever.java
 *  - add javaplugin.jar or equivalent in the classpath for compilation, you can find it at java.sun.com
 */

public class UApplet extends JApplet implements UServer {

	/** the uFlow server */
	private UServer server;
	
	public void init()
	{
		///System.out.println("cookies: "+(String)netscape.javascript.JSObject.getWindow(this).eval("document.cookie"));
		
		/* Get the informations from the applet parameters */
		String p_serverUrl = getParameter("U_XMLRPC_SERVER");
		///String p_cookieName = getParameter("U_COOKIE_NAME");
		String p_document = getParameter("U_DOCUMENT");
		String p_id = getParameter("U_ID");
		boolean p_showToolBar = getParameter("TOOLBAR")==null || !getParameter("TOOLBAR").toLowerCase().equals("false");
		boolean p_showViewSource = getParameter("SOURCEVIEW")==null || !getParameter("SOURCEVIEW").toLowerCase().equals("false");
		String p_lang = getParameter("LANG");
		boolean p_exclusive = getParameter("EXCLUSIVE")==null || !getParameter("EXCLUSIVE").toLowerCase().equals("false");
		boolean p_showMenuIcons = getParameter("MENUICONS")==null || !getParameter("MENUICONS").toLowerCase().equals("false");
		
		/* Get the id from the cookie */
		///if (p_id == null) p_id = CookieRetriever.read (this, p_cookieName);
		if (p_id == null) p_id = "unknown";
		
		/* Create the UsxClient that will be able to connect to the server */
		try {
			server = new UsxClient(p_serverUrl);
		}
		catch (java.net.MalformedURLException e)
		{
			System.out.println("Malformed Server Name: "+p_serverUrl);
		}
		
		
		System.out.println ("serverUrl: "+p_serverUrl);
		System.out.println ("id: "+p_id);

		/* configuration parameters for the UEditor */
		Conf uEditorConf = new Conf (p_id, p_document, p_showToolBar,
				p_showViewSource, p_lang, p_exclusive, p_showMenuIcons);
		System.out.println (uEditorConf);
		
		/* create the UClient and call it into the applet root pane */
		UEditor pane = new UEditor (this, uEditorConf);
		setRootPane (pane);
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

	/** finalization */
	/*
	protected void finalize () throws Throwable
	{
		userver.runSave (id, doc);
		return;
	}*/
}
