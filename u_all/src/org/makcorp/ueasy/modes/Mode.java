/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.modes;

/**
 * interface for the UEditor wysiwyg edition modes
 * an edition mode is specified by:
 *  - a fonction for translating the UClient's working format (UXHTML) into the right one
 *  - a function for the opposite translation
 *  
 *  You can develop modes for Wiki, spip, RTF, RSS, and all virtually any XML format
 */

public class Mode
{
	/** translate the data from the format of this mode to UXHTML */
	public String toUxhtml (String data)
	{
		return "!"+data;
	}

	/** translate the data from UXHTML to the format of this mode */
	public String fromUxhtml (String data)
	{
		return "!"+data;
	}
}
