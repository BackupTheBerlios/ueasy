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
 * Tests only
 */
public class ServiceClientCracker
{
	public static void main (String[] args) throws IOException
	{
		if (args.length != 3)
		{
			System.err.println ("Usage: java ServiceClient <host> <port> <message>");
			System.exit(-1);
		}
		
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String msg = args[2];

		Socket socket = null;
		PrintWriter out = null;

		try {
			socket = new Socket (host, port);
			out = new PrintWriter(socket.getOutputStream(), true);
		}
		catch (UnknownHostException e) {
			System.err.println("Don't know host " + host);
			System.exit(1);
		}
		catch (IOException e) {
			System.err.println("Couldn't get I/O");
			System.exit(1);
		}

		out.println (msg);

		out.close();
		socket.close();
	}
}
