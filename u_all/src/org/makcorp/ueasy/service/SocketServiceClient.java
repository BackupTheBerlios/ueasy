/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.service;

import java.io.PrintWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Client for handling a Service served by a SocketServiceServer
 * TODO: allow clients to connect to another host than localhost
 */
public class SocketServiceClient
{
	protected int port;
	protected String msg;
	protected String pass;

	public SocketServiceClient (int port, String msg, String pass)
	{
		this.port = port;
		this.msg = msg;
		this.pass = pass;
	}

	public void start ()
	{
		Socket socket = null;
		PrintWriter out = null;

		try {
			socket = new Socket (InetAddress.getLocalHost(), port);
			out = new PrintWriter(socket.getOutputStream(), true);
		}
		catch (IOException e) {
			System.out.println("Couldn't get I/O");
			e.printStackTrace();
			System.exit(1);
		}

		out.println (msg);
		out.println(pass);

		out.close();
		try {
			socket.close();
		}
		catch (IOException e) {
			System.err.println ("Error while closing connexion");
		}
	}
	
	public static void main (String[] args) throws IOException
	{
		if (args.length > 3 || args.length < 2)
		{
			System.out.println("Usage: java ServiceClient <port> <message> <password>");
			System.exit(1);
		}
		
		int port = Integer.parseInt(args[0]);
		String msg = args[1];
		String pass="";
		if (args.length == 3) pass = args[2];

		new SocketServiceClient(port, msg, pass).start();
	}
}
