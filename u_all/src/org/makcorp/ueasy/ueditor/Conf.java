/**
 * uEasy (http://ueasy.berlios.de)
 * Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 * This program is distributed under the terms of the GNU General Public License
 * See the whole license text in the "LICENSE" file.
 */
package org.makcorp.ueasy.ueditor;

/**
 * this class provides a common way of sending configuration parameter to the ueditor, no matter what
 * the editor really is (an applet, an application, a Java Web Start, ...)
 */
public class Conf {
	
	/** the identificator of the human editor */
	protected String id;
	/** the human-unreadable identificator for the document to open first */
	protected String document;
	protected boolean showToolBar;
	protected boolean showViewSource;
	protected String lang;
	protected boolean exclusive;
	protected boolean showMenuIcons;

	/** constructor */
	public Conf(
			String id,
			String document,
			boolean showToolBar,
			boolean showViewSource,
			String lang,
			boolean exclusive,
			boolean showMenuIcons)
	{
		this.id = id;
		this.document = document;
		this.showToolBar = showToolBar;
		this.showViewSource = showViewSource;
		this.lang = lang;
		this.exclusive = exclusive;
		this.showMenuIcons = showMenuIcons;
	}

	
	/* getters */
	
	public String getId ()
	{
		return id;
	}

	/** the document is the document to be edited first, the user will be able to edit other documents after this one */
	public String getDocument ()
	{
		return document;
	}
	
	public boolean isShowToolBar ()
	{
		return showToolBar;
	}

	public boolean isShowViewSource ()
	{
		return showViewSource;
	}

	public String getLang ()
	{
		return lang;
	}

	public boolean isExclusive ()
	{
		return exclusive;
	}

	public boolean isShowMenuIcons ()
	{
		return showMenuIcons;
	}

	public String toString ()
	{
		return "id:"+getId()+" document:"+getDocument()+" showToolBar:"+isShowToolBar()
			+" showViewSource:"+isShowViewSource()+" lang:"+getLang()
			+" exclusive:"+isExclusive()+" showMenuIcons:"+isShowMenuIcons();
	}
}
