/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.service;

import java.net.InetAddress;
import org.apache.log4j.Logger;

/**
 * This Auth authenticates all users from localhost.
 * This is sometimes quite easy for a cracker to get a user, so you should avoid this security scheme
 */
public class LocalAuth implements Auth
{
	/** logging utility */
	Logger log = Logger.getRootLogger();
	
	/** checks wether a user from this address can be authenticated */
	public boolean acceptsFrom (InetAddress address)
	{
		return isLocal(address);
	}
	
	/** checks wether the user's password allows him to be authenticated */
	public boolean acceptsPass (String pass)
	{
		// No password is needed
		return true;
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
