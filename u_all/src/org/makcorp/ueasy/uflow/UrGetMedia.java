/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.uflow;

import java.util.Vector;

/**
 * Wrapper for the uFlow reply to the GET_MEDIA request
 * See uFlow specification on http://ueasy.berlios.de/uflow.html
 */
public class UrGetMedia extends UReply
{

	private String name;
	private String url;
	private String mime;
	private String transmission;
	private String data;

	public UrGetMedia (int error, String errorMsg, String name, String url, String mime, String transmission,  String data)
	{
		super (error, errorMsg);
		this.name = name;
		this.url = url;
		this.mime = mime;
		this.transmission = transmission;
		this.data = data;
	}

	public Vector getReplyData ()
	{
		Vector v = new Vector ();
		v.add (name);
		v.add (url);
		v.add (mime);
		v.add (transmission);
		v.add (data);
		return v;
	}
	
	public String getName ()
	{
		return name;
	}
	
	/** get the URL that will be used to get the media when the document will be published */
	public String getUrl ()
	{
		return url;
	}

	/** get the MIME type of this media */
	public String getMime()
	{
		return mime;
	}

	public String getTransmission ()
	{
		return transmission;
	}
	
	/** get the media in base 64 */
	public String getData ()
	{
		return data;
	}

	public String toString ()
	{
		return "UrGetMedia "+error+" "+errorMsg+" "+name+" "+url+" "+mime+" "+transmission+" "+data;
	}
}
