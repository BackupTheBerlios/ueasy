/**
 * uEasy (http://ueasy.berlios.de)
 * Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 * This program is distributed under the terms of the GNU General Public License
 * See the whole license text in the "LICENSE" file.
 */
package org.makcorp.ueasy.ueditor;

import java.net.URL;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.event.ActionListener;
import java.awt.Color;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.ImageIcon;
import javax.swing.Action;
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.awt.Toolkit;
import java.io.StringReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;

import org.makcorp.ueasy.ueditor.component.JButtonNoFocus;
import org.makcorp.ueasy.ueditor.action.*;

public class DialogsBuilder extends DefaultHandler
{
	private HashMap actions; // all the standard-known actions. others can be defined in UEditor
	private HashMap menus; // stores the JMenus objects
	private ArrayList menusOrder; // used to remember the order of the menus
	
	private ActionListener listener;
	private UEditor editor;
	private JMenuBar jMenuBar;
	private JToolBar jToolBar;

	/**
	 * @param actions some extra actions known by the UEditor only
	 */
	public DialogsBuilder (ActionListener listener, UEditor editor, JMenuBar jMenuBar, JToolBar jToolBar, HashMap extraActions)
	{
		this.listener = listener;
		this.editor = editor;
		this.jMenuBar = jMenuBar;
		this.jToolBar = jToolBar;
		menus = new HashMap(10);
		menusOrder = new ArrayList(10);

		//Build the actions map containing the Actions already existing

		actions = new HashMap (70);
		actions.putAll(extraActions);

		/* Collect the actions that the JTextPane is naturally aware of */
		Action[] actionsArray = editor.getTextPane().getActions();
		HashMap jtpActions = new HashMap();
		for(int i = 0; i < actionsArray.length; i++)
		{
			Action a = actionsArray[i];
			jtpActions.put(a.getValue(Action.NAME), a);
		}

		// Format
		actions.put("clearformat", new CustomAction(editor, new HTML.UnknownTag("")));
		actions.put("bold",      new StyledEditorKit.BoldAction());
		actions.put("italic",    new StyledEditorKit.ItalicAction());
		actions.put("underline", new StyledEditorKit.UnderlineAction());
		actions.put("strike",      new FormatAction(editor, HTML.Tag.STRIKE));
		actions.put("big",         new FormatAction(editor, HTML.Tag.BIG));
		actions.put("small",       new FormatAction(editor, HTML.Tag.SMALL));
		actions.put("superscript", new FormatAction(editor, HTML.Tag.SUP));
		actions.put("subscript",   new FormatAction(editor, HTML.Tag.SUB));
		actions.put("heading1",   new FormatAction(editor, HTML.Tag.H1));
		actions.put("heading2",   new FormatAction(editor, HTML.Tag.H2));
		actions.put("heading3",   new FormatAction(editor, HTML.Tag.H3));
		actions.put("heading4",   new FormatAction(editor, HTML.Tag.H4));
		actions.put("heading5",   new FormatAction(editor, HTML.Tag.H5));
		actions.put("heading6",   new FormatAction(editor, HTML.Tag.H6));
		actions.put("blockquote", new FormatAction(editor, HTML.Tag.BLOCKQUOTE));
		actions.put("pre",        new FormatAction(editor, HTML.Tag.PRE));
		actions.put("strong",     new FormatAction(editor, HTML.Tag.STRONG));
		actions.put("tt",         new FormatAction(editor, HTML.Tag.TT));
		actions.put("span",       new FormatAction(editor, HTML.Tag.SPAN));
		actions.put("serif",      (Action)jtpActions.get("font-family-Serif"));
		actions.put("sansserif",  (Action)jtpActions.get("font-family-SansSerif"));
		actions.put("monospaced", (Action)jtpActions.get("font-family-Monospaced"));
		actions.put("aqua",    new StyledEditorKit.ForegroundAction("editor", new Color(  0,255,255)));
		actions.put("black",   new StyledEditorKit.ForegroundAction("editor", new Color(  0,  0,  0)));
		actions.put("blue",    new StyledEditorKit.ForegroundAction("editor", new Color(  0,  0,255)));
		actions.put("fuschia", new StyledEditorKit.ForegroundAction("editor", new Color(255,  0,255)));
		actions.put("gray",    new StyledEditorKit.ForegroundAction("editor", new Color(128,128,128)));
		actions.put("green",   new StyledEditorKit.ForegroundAction("editor", new Color(  0,128,  0)));
		actions.put("lime",    new StyledEditorKit.ForegroundAction("editor", new Color(  0,255,  0)));
		actions.put("maroon",  new StyledEditorKit.ForegroundAction("editor", new Color(128,  0,  0)));
		actions.put("navy",    new StyledEditorKit.ForegroundAction("editor", new Color(  0,  0,128)));
		actions.put("olive",   new StyledEditorKit.ForegroundAction("editor", new Color(128,128,  0)));
		actions.put("purple",  new StyledEditorKit.ForegroundAction("editor", new Color(128,  0,128)));
		actions.put("red",     new StyledEditorKit.ForegroundAction("editor", new Color(255,  0,  0)));
		actions.put("silver",  new StyledEditorKit.ForegroundAction("editor", new Color(192,192,192)));
		actions.put("teal",    new StyledEditorKit.ForegroundAction("editor", new Color(  0,128,128)));
		actions.put("white",   new StyledEditorKit.ForegroundAction("editor", new Color(255,255,255)));
		actions.put("yellow",  new StyledEditorKit.ForegroundAction("editor", new Color(255,255,  0)));
		// Align
		actions.put("alignleft",      new StyledEditorKit.AlignmentAction("dummy", StyleConstants.ALIGN_LEFT));
		actions.put("aligncenter",    new StyledEditorKit.AlignmentAction("dummy", StyleConstants.ALIGN_CENTER));
		actions.put("alignright",     new StyledEditorKit.AlignmentAction("dummy", StyleConstants.ALIGN_RIGHT));
		actions.put("alignjustified", new StyledEditorKit.AlignmentAction("dummy", StyleConstants.ALIGN_JUSTIFIED));
		// Misc
		actions.put("insertanchor", new CustomAction(editor, HTML.Tag.A));
		actions.put("unorderedlist", new ListAutomationAction(editor, HTML.Tag.UL));
		actions.put("orderedlist",   new ListAutomationAction(editor, HTML.Tag.OL));
		actions.put("listitem", new FormatAction(editor, HTML.Tag.LI));
		actions.put("selectfont", new CustomAction(editor, HTML.Tag.FONT));
		actions.put("hrule", (Action)jtpActions.get("InsertHR"));
		// Edition
		actions.put("cut", new DefaultEditorKit.CutAction());
		actions.put("copy", new DefaultEditorKit.CopyAction());
		actions.put("paste", new DefaultEditorKit.PasteAction());
		actions.put("selectall", (Action)jtpActions.get(DefaultEditorKit.selectAllAction));
		actions.put("selectparagraph", (Action)jtpActions.get(DefaultEditorKit.selectParagraphAction));
		actions.put("selectline", (Action)jtpActions.get(DefaultEditorKit.selectLineAction));
		actions.put("selectword", (Action)jtpActions.get(DefaultEditorKit.selectWordAction));

	}

	/**
	 * Load buttons and menus on the dialogs according to the AML String
	 * @param source raw "AML" String. See uEasy spec for a description of AML
	 */
	public void parse (String aml)
	{
		InputSource is = new InputSource(new StringReader(aml));
		XMLReader parser;
		try {
			// I should also try to load MinML XML parser
			// try to load Apache's parser
			parser = XMLReaderFactory.createXMLReader("org.apache.crimson.parser.XMLReaderImpl");
			parser.setContentHandler(this);
			System.out.println("Parsing");
			parser.parse (is);
		}
		catch (SAXException e1) {
			try {
				// if apache's XML parser is not available, load the default XML parser
				System.out.println("Trying with default parser");
				parser = XMLReaderFactory.createXMLReader();
				parser.setContentHandler(this);
				System.out.println("Parsing");
				parser.parse (is);
			}
			catch (SAXException e2) {
				// no XML parser available
				System.err.println ("no XML parser available or error in the XML or no AML file found");
			}
			catch (java.io.IOException e) {
				e.printStackTrace();
			}
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
		}
		
		// Fill the JMenuBar with all the created menus
		Iterator i = menusOrder.iterator();
		while (i.hasNext())
			jMenuBar.add((JMenu)menus.get((String)i.next()));
	}

	public void startElement(String uri, String name, String qName, Attributes atts)
	{
		if (!name.equals("action")) return;

		String key = atts.getValue("key");
		String label = atts.getValue("label");
		String menu = atts.getValue("menu");
		String button = atts.getValue("button");
		String icon = atts.getValue("icon");
		System.out.println("key="+key+" label="+label+" menu="+menu+" button="+button+" icon="+icon);
		
		boolean actionAvailable = actions.containsKey(key);
		Action action = null;
		if (actionAvailable) action = (Action)actions.get(key);

		// Build and place the menu item in the right menu, if needed
		if (menu!=null && menu.length()>0)
		{
			// Select the menu where the item is to be added, or create it if it does not exist
			JMenu jMenu = null;
			if (menus.containsKey(menu))
			{
				jMenu = (JMenu)menus.get(menu);
			}
			else
			{
				jMenu = new JMenu(menu);
				menus.put(menu,jMenu);
				menusOrder.add(menu);
			}
			
			// Create and place the menu item
			JMenuItem jmi;
			if (actionAvailable) 
			{
				jmi = new JMenuItem (action);
			}
			else {
				jmi = new JMenuItem (label);
				jmi.setActionCommand (key);
			}
			jmi.setText (label);
			jmi.addActionListener (listener);
			if (icon!=null) jmi.setIcon(getIcon(icon));
			jMenu.add(jmi);
		}

		//Build and place the button, if needed
		if (button!=null && button.equals("1"))
		{
			JButtonNoFocus jbtn;
			if(actionAvailable)
			{
				jbtn = new JButtonNoFocus (action);
			}
			else
			{
				jbtn = new JButtonNoFocus();
				jbtn.setActionCommand (key);
			}
			jbtn.addActionListener (listener);
			jbtn.setIcon(getIcon(icon));
			jbtn.setText(null);
			jbtn.setToolTipText(label);
			jToolBar.add(jbtn);
		}
		System.out.println("Dialog "+key+" added.");
	}

	/**
	 * Convenience method for fetching icon images from jar file
	 */
	private ImageIcon getIcon(String iconName)
	{
		System.out.println(iconName);
		URL url = getClass().getResource("icons/" + iconName + "HK.gif");
		System.out.println (url);
		if(url==null) url = getClass().getResource("icons/" + "Empty" + "HK.gif");
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
	}

}
