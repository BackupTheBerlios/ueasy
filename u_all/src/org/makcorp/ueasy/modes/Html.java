/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.modes;

import java.io.*;

/**
 * Html Mode
 * Just pass the data through, since it is already HTML
 */
public class Html extends Mode
{
	public String toUxhtml (String data)
	{
		return data;
	}

	public String fromUxhtml (String data)
	{
		return data;
	}
	
}
