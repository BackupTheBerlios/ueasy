/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.usxclient;

import org.makcorp.ueasy.uflow.*;
import org.apache.xmlrpc./*Simple*/XmlRpcClient;
import java.util.Vector;
import java.io.*;
import java.util.Iterator;
import java.net.MalformedURLException;

/**
 * Sends UFlow requests to a uFlow XML-RPC server, typically to the UsxServer implementation
 * TODO: use something lighter than XmlRpcClient
 */
public class UsxClient extends /*Simple*/XmlRpcClient implements UServer {
	
	/**
	 * Creates a new XmlRpcServer for a resource
	 * @param server the XML-RPC server, it's something like "http://host:port/"
	 */
	public UsxClient (String server) throws MalformedURLException
	{
		super (server);
		//XmlRpc.setKeepAlive(true);
	}

	public UrOpen runOpen (String id, String doc)
	{
		System.out.println (">--- runOpen "+id+" "+doc);
		return (UrOpen)uRequest ("OPEN", id, doc);
	}
	
	/**
	 * By now, this client only uses 'DATA' transmission (see uFlow spec about transmission)
	 */
	public UReply runSave (String id, String doc, String modeName, String transmission, String data)
	{
		System.out.println (">--- runSave "+id+" "+doc+" transmission=DATA data="+data);
		return uRequest ("SAVE", id, doc, modeName, "DATAz", data);
	}

	public UReply runClose (String id, String doc)
	{
		System.out.println (">--- runClose "+id+" "+doc);
		return uRequest ("CLOSE", id, doc);
	}

	public UrList runList (String id, String doc, String list)
	{
		System.out.println (">--- runList "+id+" "+doc+" list="+list);
		return (UrList)uRequest ("LIST", id, doc, list);
	}

	public UrGetMedia runGetMedia (String id, String doc, String media)
	{
		System.out.println (">--- runGetMedia "+id+" "+doc+" media="+media);
		return (UrGetMedia)uRequest ("GET_MEDIA", id, doc, media);
	}
	
	
	/** Convenient alias for requests with less than 2 parameters */
	private UReply uRequest (String method, String id, String doc)
	{
		return uRequest (method, id, doc, null, null, null);
	}
	
	private UReply uRequest (String method, String id, String doc, String arg)
	{
		return uRequest (method, id, doc, arg, null, null);
	}
	
	/**
	 * Sends the XML-RPC request according to the uFlow format
	 * @return the reply of the server as a UReply object 
	 */
	private UReply uRequest (String method, String id, String doc, String arg1, String arg2, String arg3)
	{
		/* the object received from the xmlrpc server, it is a vector with command-dependant size */
		Vector vector = new Vector();
		/* what will be replied to the calling client */
		UReply reply = null;

		int error = UReply.OK;
		
		/* the vector to be used as param by the xmlrpc client */
		Vector params = new Vector();
		params.add (id);
		params.add (doc);
		if(arg1 != null) params.add((String)arg1); // there are always 3 or less arguments (see uFlow spec)
		if(arg2 != null) params.add((String)arg2);
		if(arg3 != null) params.add((String)arg3);
		
		/* execute the request */
		try {
			System.out.println("->-- METHOD="+method+" params="+params.toString());
			vector = (Vector)this.execute (method, params);
			System.out.println("-->- "+vector.toString()+" "+vector.getClass().getName());
		}
		catch(org.apache.xmlrpc/*.applet*/.XmlRpcException e)
			{ e.printStackTrace(); error = UReply.BAD_REPLY; }
		catch(java.io.IOException e)
			{ e.printStackTrace(); error = UReply.SERVER_NOT_FOUND; }
		
		/* retrieve the error code */
		if (((Integer)vector.elementAt(0)).intValue() != UReply.OK) // do not erase any previous error
			error = ((Integer)vector.elementAt(0)).intValue();

		String errorMsg = (String)vector.elementAt(1);
		
		/* build the appropriate reply */	
		if (method.equals ("OPEN"))
			reply = new UrOpen (error, errorMsg, (String)vector.elementAt(2), (String)vector.elementAt(3),
					 (String)vector.elementAt(4), (String)vector.elementAt(5),
					 (String)vector.elementAt(6), (String)vector.elementAt(7),
					 (String)vector.elementAt(8));
		if (method.equals ("LIST"))
			reply = new UrList (error, errorMsg, (String)vector.elementAt(2), (String)vector.elementAt(3),
					 (String)vector.elementAt(4), (String)vector.elementAt(5));
		if (method.equals ("GET_MEDIA"))
			reply = new UrGetMedia (error, errorMsg, (String)vector.elementAt(2), (String)vector.elementAt(3),
					 (String)vector.elementAt(4), (String)vector.elementAt(5),
					 (String)vector.elementAt(6));
		
		/* case for SAVE and CLOSE */
		if (reply == null)
			reply = new UReply (error, errorMsg);

		System.out.println ("---> "+reply.toString());
		return reply;
	}

	/* Test only */
	public static void main(String args[]) {
	
		UServer server = null;
		try {
			server = new UsxClient ("http://localhost:8080");
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println(server.runOpen("myid","mydoc"));
		System.out.println(server.runSave("myid","mydoc","org.makcorp.ueasy.modes.Html","LOCAL","ceci\n<i>est</i> un\n test"));
		System.out.println(server.runClose("myid","mydoc"));
		System.out.println(server.runList ("myid","mydoc", "mylist"));
		System.out.println(server.runGetMedia ("myid","mydoc", "mymedia"));
	}
}
