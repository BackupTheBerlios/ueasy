/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.service;

import java.security.SecureRandom;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.Writer;
import java.util.Random;
import org.apache.log4j.Logger;

/**
 * this SocketServiceServer uses a PassAuth authentication scheme
 */
public class PassSocketServiceServer extends SocketServiceServer
{
	/** logging utility */
	Logger log = Logger.getRootLogger();
	
	public void initializeAuth() {
		String propertiesFile = PassAuth.PROPERTIES_FILE;
		p = new Properties();
		try {
			p.load(new FileInputStream(propertiesFile));
		}
		catch (java.io.FileNotFoundException e)
		{
			log.fatal ("File "+propertiesFile+" not found.", e);
			System.exit(1);
		}
		catch (java.io.IOException e)
		{
			log.fatal ("I/O error while reading "+propertiesFile, e);
			System.exit(1);
		}
		String passFile = p.getProperty("passfile");
		String pass = this.generatePass();
		// Writing the password into the pass file
		try {
			FileOutputStream fos = new FileOutputStream(passFile);
			Writer w = new BufferedWriter(new OutputStreamWriter(fos));
			w.write(pass);
			w.flush();
			w.close();
		}
		catch (Exception e) {
			 log.fatal("Error while writing password to pass file: "+passFile+" you should check permissions.", e);
			 System.exit(1);
		}
	}

	/**
	 * A random pass is generated.
	 * One could extend this class to prompt for a pass instead and store it with java.security.MessageDigest's MD5
	 */
	private String generatePass()
	{
		// Since java.util.Random is not real random, the generate string could be easily found.
		// Java's random seed is based on the milliseconds.
		// A cracker should scan the port until the server starts, and test the
		// passwords corresponding to the milliseconds around the start-up time.
		// That's why I use SecureRandom
		return Long.toString(Math.abs(new SecureRandom().nextLong()));
	}
	
	public Auth getAuth()
	{
		return new PassAuth();
	}
}
