/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.modes;

import java.io.*;

/**
 * Wiki Mode
 * Wiki has an specific easy text formating syntax
 * @see phpwiki.sf.net
 */
public class Docbook extends Mode
{
	public String toUxhtml (String data)
	{
		data = data.replaceAll("<book>","<html>");
		data = data.replaceAll("<superscript>", "<sup>");
		data = data.replaceAll("<indexterm><primary>(.*)</primary><see>(.*)</see></indexterm>","<a href=\"\">$1</a>");
		data = data.replaceAll("\n\t\\*(.*)","<li>$1</li>");
		data = data.replaceAll("\n\t\t\\*(.*)","<li>$1</li>");
		data = data.replaceAll("\n\t:\t(.*)","<blockquote>$1</blockquote>");
		// bold & italic
		data = data.replaceAll("''''(.*)''''","<b><i>$1</i></b>");
		data = data.replaceAll("'''(.*)'''","<b>$1</b>");
		data = data.replaceAll("''(.*)''","<i>$1</i>");
		data = data.replaceAll("\n(.*)","\n<br>$1");
		return data;
	}

	public String fromUxhtml (String data)
	{
		data = data.replaceAll("\n *","");
		data = data.replaceAll("\n","");
		data = data.replaceAll("<html>","");
		data = data.replaceAll("<head>.*</head>","");
		data = data.replaceAll("<body>","");
		data = data.replaceAll("</body>","");
		data = data.replaceAll("</html>","");
		data = data.replaceAll("<hr>","----");
		data = data.replaceAll("<ul>","");
		data = data.replaceAll("</ul>","");
		data = data.replaceAll("<ol>","");
		data = data.replaceAll("</ol>","");
		data = data.replaceAll("<li>","\n\t*");
		data = data.replaceAll("</li>","");
		data = data.replaceAll("<p>","\n");
		data = data.replaceAll("</p>","\n");
		data = data.replaceAll("<blockquote>","\t:\t");
		data = data.replaceAll("</blockquote>","");
		// bold & italic (wiki doesn't have underline as far as I know (at least in the phpwiki version)
		data = data.replaceAll("<i><b>","''''");
		data = data.replaceAll("</b></i>","''''");
		data = data.replaceAll("<b><i>","''''");
		data = data.replaceAll("</i></b>","''''");
		data = data.replaceAll("<i>","''");
		data = data.replaceAll("</i>","''");
		data = data.replaceAll("<b>","'''");
		data = data.replaceAll("</b>","'''");
		data = data.replaceAll("<br>","\n");
		// html entities
		data = data.replaceAll("&lt;","<");
		data = data.replaceAll("&gt;",">");
		data = data.replaceAll("&amp;","&");
		System.out.println (data);
		return data;
	}
	
	public static void main(String[] args)
	{
		Wiki wiki = new Wiki();
		String a = "iehg iMuzhrezMiehruz ze----ier''hgz''iu-------zkgzren";

		try {
			String a2="";
			String thisLine;
			FileInputStream fin =  new FileInputStream(args[0]);
			BufferedReader myInput = new BufferedReader (new InputStreamReader(fin));
			while ((thisLine = myInput.readLine()) != null) {  
				a2 += thisLine + "\n";
			}
			a=a2;
		}
		catch (Exception e) { e.printStackTrace(); }

		String sep = "---------------------------------------------------------------------------------";
		
		System.out.println(a+sep);
		String b = wiki.toUxhtml(a);
		System.out.println(b+sep);
		String c = wiki.fromUxhtml(b);
		System.out.println(c);
	}
}
