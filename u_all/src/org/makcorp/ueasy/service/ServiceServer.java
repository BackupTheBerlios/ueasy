/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.service;

public interface ServiceServer
{
	public void addService(Service service);
	public void start();
}
