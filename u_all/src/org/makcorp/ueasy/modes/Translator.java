/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.modes;

public interface Translator
{
	/** translate the data from the format of this mode to UXHTML */
	public String toUxhtml (String strMode, String data) throws TranslatorException;

	/** translate the data from UXHTML to the format of this mode */
	public String fromUxhtml (String strMode, String data) throws TranslatorException;
}
