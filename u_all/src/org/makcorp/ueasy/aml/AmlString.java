/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.aml;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class is intended to make AML building faster
 * By now it is not exploited
 * Feel free to finish the work
 */
public class AmlString
{
	protected List list;
	private String string = null;
	
	public AmlString ()
	{
		list = new ArrayList(50);
	}

	public AmlString (String string)
	{
		this.string = string;
	}

	public void addAction (String key, String label) 
	{
		this.addActionLine ("<action key=\""+key+"\" label=\""+label+"\"/>");
	}

	public void addActionLine (String line)
	{
		list.add (line+"\n");
	}
	
	public String toString()
	{
		if (string != null) return string;
		
		// StringBuffer is used for better performance in concatenations
		StringBuffer r = new StringBuffer();
		//r.append("<?xml version=\"1.0\"?>\n<actions>\n");
		
		Iterator iter = list.iterator();
		while (iter.hasNext())
			r.append ((String)iter.next());

		//r.append ("</actions>");
		return r.toString();
	}
}
