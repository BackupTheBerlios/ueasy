/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.usxserver;

import org.makcorp.ueasy.uflow.*;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.log4j.*;
import java.util.Vector;

/**
 * Handles a uFlow request.
 * This class is meant to be initialized by a UXmlRpcServer.
 */
public class UHandler implements XmlRpcHandler {

	private UServer server;
	/** logging utility */
	Logger log = Logger.getRootLogger();
	
	/**
	 * Initialises the UHandler.
	 * @param server the UServer to which requests will be sent
	 */
	public UHandler (UServer server)
	{
		this.server = server;
	}

	/**
	 * this method is called every time a uFlow request is sent to the UXmlRpcServer.
	 */
	public Object execute (String method, Vector params) throws Exception {
		
		log.debug ("UHandler: Received: method="+method+" params="+params.toString()+" server="+server.toString());
		
		/* Check the method validity */
		if ( !method.equals("OPEN") && !method.equals("SAVE") && !method.equals("CLOSE")
				&& !method.equals("LIST") && !method.equals("GET_MEDIA"))
		{
			log.error ("UHandler: BAD_REQUEST");
			return new UReply(UReply.BAD_REQUEST).getVector();
		}
		
		/* What will be replied to the client */
		UReply ureply = new UReply (UReply.BAD_REQUEST, "Bad request name or parameters number");
	
		String id = (String)params.elementAt(0);
		String doc = (String)params.elementAt(1);
		
		try {
			log.debug ("debug0");

			if (method.equals ("OPEN") && params.size()==2 )
				ureply = (UrOpen) server.runOpen(id, doc);
			
			if (method.equals ("SAVE") && params.size()==5 )
			{
				log.debug (params);
				ureply = server.runSave(id, doc, (String)params.elementAt(2),(String)params.elementAt(3),(String)params.elementAt(4));
				log.debug ("#"+ureply);
			}
			
			if (method.equals ("CLOSE") && params.size()==2 )
			{
				log.debug (">-close");
				ureply = server.runClose(id, doc);
				log.debug ("->close");
			}
			
			if (method.equals ("LIST") && params.size()==3 )
				ureply = (UrList) server.runList(id, doc, (String)params.elementAt(2));
			
			if (method.equals ("GET_MEDIA") && params.size()==3 )
				ureply = (UrGetMedia) server.runGetMedia(id, doc, (String)params.elementAt(2));

			log.debug ("debug1 "+ureply.toString());
		}
		catch (ClassCastException e)
		{
			log.error ("Handling reply", e);
			ureply = new UReply (UReply.BAD_REPLY);
		}
		log.debug ("UHandler: Replying: "+ureply.toString());
		return ureply.getVector();
	}
}
