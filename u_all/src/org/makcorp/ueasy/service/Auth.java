/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.service;

import java.net.InetAddress;

public interface Auth {
	
	/** checks wether a user from this address can be authenticated */
	public boolean acceptsFrom (InetAddress address);
	/** checks wether the user's password allows him to be authenticated */
	public boolean acceptsPass (String pass);
}
