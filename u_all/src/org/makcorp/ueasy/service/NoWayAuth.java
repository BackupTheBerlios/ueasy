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
 * This authentication won't let anyone doing anything.
 * It can be usefull if you want to use the service directly by handling the Java Virtual Machine
 */
public class NoWayAuth implements Auth
{
	public boolean acceptsFrom (InetAddress address)
	{
		return false;
	}
	
	public boolean acceptsPass (String pass)
	{
		return false;
	}
}
