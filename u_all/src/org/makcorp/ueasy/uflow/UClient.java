/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.uflow;

/** interface for the Uflow clients */
public interface UClient
{
	/** Sets the Uflow server to which the uFlow requests of this client will be transmitted */
	public void setUServer (UServer uServer);
}
