/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.service;

import java.util.Properties;
import java.net.InetAddress;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.apache.log4j.Logger;

/**
 * This Auth authenticates users according to a password stored on the server side.
 * The password file should be a file with permissions "rw-------"
 * This is a good security scheme for systems with real security, you should avoid it on Windows
 */
public class PassAuth implements Auth
{
	public static String PROPERTIES_FILE = "passauth.properties";
	/** logging utility */
	Logger log = Logger.getRootLogger();
	
	public boolean acceptsFrom (InetAddress address)
	{
		// Users can authenticate from anywhere
		return true;
	}
	
	/** checks wether the user's password allows him to be authenticated */
	public boolean acceptsPass (String pass)
	{
		String passFile = null;
		String theRightPass = null;
		Properties p;
		try {
			p = new Properties();
			p.load(new FileInputStream(PROPERTIES_FILE));
		}
		catch (Exception e) {
			System.err.println("Can't read PassAuth's properties file :"+PassAuth.PROPERTIES_FILE+", you should check permissions");
			return false;
		}
		try {							
			passFile = p.getProperty("passfile");
			FileInputStream fis =  new FileInputStream(passFile);
			BufferedReader reader = new BufferedReader (new InputStreamReader(fis));
			theRightPass = reader.readLine();
		}
		catch (Exception e) {
			System.err.println("Can't read PassAuth's password file :"+passFile+", you should check permissions");
			return false;
		}
		if (theRightPass == null) 
		{
			log.error("Can't read PassAuth's password file :"+PassAuth.PROPERTIES_FILE+", you should check permissions");
			return false;
		}
		//log.info(pass+"(right="+theRightPass+")");
		if (pass.equals(theRightPass)) return true;
		return false;
	}
	
	/**
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
			log.error ("getting local host address", e);
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
