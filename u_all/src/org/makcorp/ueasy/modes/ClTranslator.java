/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.modes;

import java.io.*;
import org.apache.log4j.Logger;

/**
 * Translator based on the Class loading mechanism
 * By now the mode classes have to be in the classpath
 */
public class ClTranslator implements Translator
{
	//List modes; // contains the already loaded modes
	/** logging utility */
	Logger log = Logger.getRootLogger();
	
	/** translate the data from the format of this mode to UXHTML */
	public String toUxhtml (String strMode, String data) throws TranslatorException
	{
		return getMode(strMode).toUxhtml(data);
	}
		
	/** translate the data from UXHTML to the format of this mode */
	public String fromUxhtml (String strMode, String data) throws TranslatorException
	{
		return getMode(strMode).fromUxhtml(data);
	}
	
	/** Dynamically loads the appropriate Mode */
	private Mode getMode (String strMode)  throws TranslatorException // todo: utiliser list pour ne pas recharger à tous les coups
	{
		try {
			Class mode = Class.forName (strMode);
			return (Mode)((mode).newInstance());
		}
		catch (Exception e)
		{
			log.error ("Mode "+strMode+" not found", e);
			throw new TranslatorException ("Mode "+strMode+" not found.");
		}
	}

	/** Test only
	 * Usage: mode datafile
	 */
	public static void main(String[] args)
	{
		String data = "";
		try {
			String thisLine;
			FileInputStream fin =  new FileInputStream(args[1]);
			BufferedReader myInput = new BufferedReader (new InputStreamReader(fin));
			while ((thisLine = myInput.readLine()) != null) {  
				data += thisLine + "\n";
			}
		}
		catch (Exception e) { e.printStackTrace(); }

		try {
			Translator t = new ClTranslator();
			String u = t.toUxhtml(args[0], data);
			System.out.println(u);
			System.out.println(t.fromUxhtml(args[0], data));
		}
		catch (TranslatorException e)
		{
			e.printStackTrace();
		}
	}
}
