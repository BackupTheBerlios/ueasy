/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.uflow;

import java.util.Vector;

/**
 * Base class for uFlow replies' wrapper classes
 * Subclasses can give more precise informations
 * See uFlow specification on http://ueasy.berlios.de/uflow.html
 */
public class UReply {

	/* Error codes : begin */

	/** no error */
	public static final int OK = 0;
	/** internal error without any other precision */
	public static final int INTERNAL = 100;
	/** uFlow server not found */
	public static final int SERVER_NOT_FOUND = 110;
	/** a bad request has been received */
	public static final int BAD_REQUEST = 120;
	/** a bad reply has been received */
	public static final int BAD_REPLY = 121;
	/** resource file not found */
	public static final int RESOURCE_NOT_FOUND = 200;
	
	/* Error codes : end */
	
	protected int error = INTERNAL;
	protected String errorMsg;
	
	public UReply (int error)
	{
		this (error, "");
	}
	
	public UReply (int error, String errorMsg)
	{
		this.error = error;
		this.errorMsg = errorMsg;
	}
	
	/**
	 * Return the error code for this UReply
	 * @return the error code
	 */
	public int getError ()
	{
		return error;
	}
	
	public String getErrorMessage ()
	{
		return errorMsg;
	}
	
	/**
	 * By default there is no other_data so this is null.
	 * This can be overriden by subclasses
	 * @return a Vector containing what uFlow's specification calls 'other_data'
	 */
	public Vector getReplyData ()
	{
		return null;
	}

	/**
	 * This computes the vector that can be sent via XML-RPC
	 * @return the vector that can be sent via XML-RPC
	 */
	public Vector getVector ()
	{
		Vector v = new Vector ();
		v.add (new Integer(error));
		v.add (errorMsg);
		if (this.getReplyData() != null) v.addAll (this.getReplyData());
		return v;
	}
	
	public String toString ()
	{
		return "UReply " + error + " " + errorMsg;
	}
}
