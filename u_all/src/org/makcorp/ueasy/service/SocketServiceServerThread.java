/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.service;

import java.net.Socket;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * Thread handling a single client
 * The requests to the server should look like this:
 * password
 * command
 */
public class SocketServiceServerThread extends Thread
{
	private Socket socket;
	/** the object that will be read by the SocketServiceServer */
	private Command command;
	/** authentication utility used */
	private Auth auth;
	/** logging utility */
	Logger log = Logger.getRootLogger();

	public SocketServiceServerThread(Socket socket, Command command, Auth auth)
	{
		this.socket = socket;
		this.command = command;
		this.auth = auth;
	}

	public void start()
	{
		try {
			// don't accept the command if it's not from local host
			if (auth.acceptsFrom(socket.getInetAddress()))
			{
				BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream()));
				
				String line = in.readLine();
				String pass = in.readLine();
				
				if (auth.acceptsPass(pass)) command.add(line);

				in.close();
				socket.close();
			}
		}
		catch (IOException e) {
			log.error ("reading from socket", e);
		}
	}

	/*
	 * Checks wether the specified address is local host
	 */
	protected boolean isLocal(InetAddress address)
	{
		InetAddress local = null;
		try {
			local = InetAddress.getLocalHost();
		}
		catch (java.net.UnknownHostException e)
		{
			log.error ("getting local host", e);
			return false;
		}

		byte[] a1 = address.getAddress();
		byte[] a2 = local.getAddress();
		
		if (a1.length != a2.length) return false;
		for (int i=0; i<a1.length; i++)
			if (a1[i] != a2[i])
				return false;
		return true;
	}
}
