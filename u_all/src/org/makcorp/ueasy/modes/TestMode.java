/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.modes;

public class TestMode extends Mode
{
	public String toUxhtml (String data)
	{
		return "to_"+data.toUpperCase();
	}

	public String fromUxhtml (String data)
	{
		return "from_"+data.toLowerCase();
	}
}
