/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.uflow;

/**
 * Interface for classes that are able to handle documents by performing uFlow operations
 */

public interface UServer {

	/**
	 * Opens the document
	 * @return the OpenResult containing the error code and the data for the document
	 */
	public UrOpen runOpen (String id, String doc);

	/**
	 * Saves the data to the file
	 * @param data the data to be saved
	 */
	public UReply runSave (String id, String doc, String modeName, String transmission, String data);
	
	/**
	 * Closes the document.
	 * It only removes the lock for the file, it does NOT save the file
	 * @return the error code
	 */
	public UReply runClose (String id, String doc);
	 
	/**
	 * Gets the list of medias in the specified medias list
	 * @param dir the list where medias can be found
	 * @return the UrListMedias object containing a list of medias (separated by ':')
	 */
	public UrList runList (String id, String doc, String list);

	/**
	 * Gets the specified media
	 * @param media the identification of the media
	 * @return the UrGetMedia object 
	 */
	public UrGetMedia runGetMedia (String id, String doc, String media);
}
