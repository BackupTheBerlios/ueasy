/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.service;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * This client can be used to send signals to the service server using a password file
 */

public class PassSocketServiceClient extends SocketServiceClient
{
	public PassSocketServiceClient (int port, String msg)
	{
		super (port, msg, "");
		
		// retrieve the path of the password file
		String passFile = null;
		Properties p;
		try {
			p = new Properties();
			p.load(new FileInputStream(PassAuth.PROPERTIES_FILE));
			passFile = p.getProperty ("passfile");
		}
		catch (Exception e) {
			System.err.println("Can't read properties file :"+PassAuth.PROPERTIES_FILE+", you should check permissions");
			System.exit(1);
		}
		try {
			FileInputStream fis = new FileInputStream(passFile);
			BufferedReader reader = new BufferedReader (new InputStreamReader(fis));
			pass = reader.readLine();
		}
		catch (Exception e) {
			System.out.println("Can't read password file :"
					+passFile+", you should check permissions");
			System.exit(1);
		}
		this.pass = pass;
	}

	public static void main (String[] args) throws IOException
	{
		if (args.length != 1)
		{
			System.out.println("Usage: java ServiceClient <message>");
			System.exit(1);
		}

		String msg = args[0];

		int port = -1;
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(SocketServiceServer.PROPERTIES_FILE));
		}
		catch (java.io.FileNotFoundException e)
		{
			System.err.println ("File "+SocketServiceServer.PROPERTIES_FILE+" not found.");
			e.printStackTrace();
			System.exit(1);
		}
		catch (java.io.IOException e)
		{
			System.err.println ("I/O error while reading "+SocketServiceServer.PROPERTIES_FILE);
			e.printStackTrace();
			System.exit(1);
		}
		String portString = p.getProperty ("port");
		try {
			port = Integer.parseInt(portString);
		}
		catch (NumberFormatException e)
		{
			System.err.println ("Wrong port format:"+port);
			e.printStackTrace();
			System.exit(1);
		}
		
		new PassSocketServiceClient(port, msg).start();
	}
}
