/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.uflow;

import java.util.Vector;

/**
 * Wrapper for the uFlow reply to the LIST request
 * See uFlow specification on http://ueasy.berlios.de/uflow.html
 */
public class UrList extends UReply
{
	private String name;
	private String dirs;
	private String documents;
	private String medias;

	public UrList (int error, String errorMsg, String name, String dirs, String documents, String medias)
	{
		super (error, errorMsg);
		this.name = name;
		this.dirs = dirs;
		this.documents = documents;
		this.medias = medias;
	}

	public Vector getReplyData ()
	{
		Vector v = new Vector ();
		v.add (name);
		v.add (dirs);
		v.add (documents);
		v.add(medias);
		return v;
	}
	
	public String getName ()
	{
		return name;
	}
	
	public String getDirs ()
	{
		return dirs;
	}

	public String getDocuments ()
	{
		return documents;
	}

	public String getMedias ()
	{
		return medias;
	}
	
	public String toString ()
	{
		return "UrList "+error+" "+errorMsg+" "+name+" "+dirs+" "+documents+" "+medias;
	}
}
