/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.uflow;

import java.util.Vector;

/**
 * Wrapper for the uFlow reply to the OPEN request
 * See uFlow specification on http://ueasy.berlios.de/uflow.html
 */
public class UrOpen extends UReply
{
	private String name;
	private String modeName;
	private String lang;
	private String transmission;
	private String mode;
	private String css;
	private String data;
	
	/**
	 * Build a new UrOpen
	 */
	public UrOpen(int error, String errorMsg, String name, String modeName, String lang, String transmission, String mode, String css, String data)
	{
		super (error, errorMsg);
		this.name = name;
		this.modeName = modeName;
		this.lang = lang;
		this.transmission = transmission;
		this.mode = mode;
		this.css = css;
		this.data = data;
	}
	
	public Vector getReplyData ()
	{
		Vector v = new Vector ();
		v.add (name);
		v.add (modeName);
		v.add (lang);
		v.add (transmission);
		v.add (mode);
		v.add (css);
		v.add (data);
		return v;
	}

	public String getName ()
	{
		return name;
	}
	
	public String getModeName ()
	{
		return modeName;
	}

	public String getLang ()
	{
		return lang;
	}

	public String getTransmission ()
	{
		return transmission;
	}

	public String getMode ()
	{
		return mode;
	}

	public String getCss ()
	{
		return css;
	}
	
	public String getData()
	{
		return data;
	}

	public String toString ()
	{
		return "UrOpen "+error+" "+errorMsg+" "+name+" "+modeName+" "+lang+" "+transmission+" "+mode+" "+css+" "+data;
	}
}
