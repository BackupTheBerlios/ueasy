/*
GNU Lesser General Public License

UEditor - Base Java Swing HTML Editor & Viewer Class (Basic Version)
Copyright (C) 2000-2002  Howard A Kistler

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.makcorp.ueasy.ueditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.JRootPane;
import javax.swing.JLabel;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.CannotUndoException;

import org.makcorp.ueasy.ueditor.action.*;
import org.makcorp.ueasy.ueditor.component.*;
import org.makcorp.ueasy.util.Translatrix;
import org.makcorp.ueasy.uflow.*;

/** 
 * UEditor
 * Main class for editing and saving HTML in a Java text component
 */
public class UEditor extends JRootPane implements ActionListener, DocumentListener
{

	/** uFlow server */
	private UServer server;
	/** the identifier of the user */
	private String id;
	/** the identifier of the document actually edited */
	private String document;
	/** the human-readable name of the document actually edited */
	private String documentFriendlyName;
	/** contains configuration parameters */
	private Conf conf;
	/** the mode used */
	private String modeName;
						
	
	/* Components */
	private JSplitPane jspltDisplay;
	private JTextPane jtpMain;
	private ExtendedHTMLEditorKit htmlKit;
	private HTMLDocument htmlDoc;
	private StyleSheet styleSheet;
	private JTextPane jtpSource;
	private JScrollPane jspSource;
	private JToolBar jToolBar;
	private JLabel jlblStatus;
	private JMenuBar jMenuBar;
	private JComboBoxNoFocus jcmbStyleSelector;
	private Frame frameHandler;
	private Selector documentsSelector;

	protected UndoManager undoMngr;
	protected UndoAction undoAction;
	protected RedoAction redoAction;

	private final String appName = "UEditor";

	// Creates a highlighted TABLE around a new FORM so that it may be more easily edited */
	private final boolean useFormIndicator = true;
	private final String clrFormIndicator = "#cccccc";

	// Variables
	private int iSplitPos = 0;
	private boolean exclusiveEdit = true;

	private String lastSearchFindTerm     = null;
	private String lastSearchReplaceTerm  = null;
	private boolean lastSearchCaseSetting = false;
	private boolean lastSearchTopSetting  = false;

	private File currentFile = null;

	private int indent = 0;
	private final int indentStep = 4;

	// File extensions for MutableFilter
	private final String[] extsHTML = { "html", "htm", "shtml" };
	private final String[] extsCSS  = { "css" };
	private final String[] extsIMG  = { "gif", "jpg", "jpeg", "png" };
	private final String[] extsRTF  = { "rtf" };
	private final String[] extsSer  = { "ser" };

	/** Master Constructor
	  */
	public UEditor(UServer server, Conf conf)
	{
		super();
		this.server = server;

		// load configuration parameters
		this.conf = conf;
		System.out.println (conf);
		this.id = conf.getId();
		this.document = conf.getDocument();
		boolean showViewSource = conf.isShowViewSource();
		boolean showMenuIcons = conf.isShowMenuIcons();
		boolean exclusiveEdit = true;
		String sLang = conf.getLang();
		boolean debugMode = true;
		boolean showToolBar = conf.isShowToolBar();

		getRootPane().putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);
		
		URL urlStyleSheet = null;

		frameHandler = new Frame();

		// Localize for language
		Translatrix.setBundleName("org.makcorp.ueasy.ueditor.LanguageResources");
		Locale baseLocale = (Locale)null;
		//if(sLang != null)
		//{
		//	baseLocale = new Locale(sLang, sCountry); // TODO: "fr_FR" -> "fr" "FR"
		//}
		Translatrix.setLocale(baseLocale);

		// Create the editor kit, document, and stylesheet
		jtpMain = new JTextPane();
		htmlKit = new ExtendedHTMLEditorKit();
		htmlDoc = (HTMLDocument)(htmlKit.createDefaultDocument());
		styleSheet = htmlDoc.getStyleSheet();
		htmlKit.setDefaultCursor(new Cursor(Cursor.TEXT_CURSOR));

		// Set up the text pane
		jtpMain.setEditorKit(htmlKit);
		jtpMain.setDocument(htmlDoc);
		jtpMain.setMargin(new Insets(4, 4, 4, 4));

		jtpMain.setCaretPosition(0);
		jtpMain.getDocument().addDocumentListener(this);

		// Create the source text area
		jtpSource = new JTextPane();
		jtpSource.setBackground(new Color(212, 212, 212));
		jtpSource.setSelectionColor(new Color(255, 192, 192));
		jtpSource.setText(jtpMain.getText());
		jtpSource.getDocument().addDocumentListener(this);

		// Add CaretListener for tracking caret location events
		jtpMain.addCaretListener(new CaretListener()
		{
			public void caretUpdate(CaretEvent ce)
			{
				handleCaretPositionChange(ce);
			}
		});

		// Set up the undo features
		undoMngr = new UndoManager();
		undoAction = new UndoAction();
		redoAction = new RedoAction();
		jtpMain.getDocument().addUndoableEditListener(new CustomUndoableEditListener());

		// Create the scroll area for the text pane
		JScrollPane jspViewport = new JScrollPane(jtpMain);
		jspViewport.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jspViewport.setPreferredSize(new Dimension(400, 400));
		jspViewport.setMinimumSize(new Dimension(32, 32));

		// Create the scroll area for the source viewer
		jspSource = new JScrollPane(jtpSource);
		jspSource.setPreferredSize(new Dimension(400, 100));
		jspSource.setMinimumSize(new Dimension(32, 32));

		jspltDisplay = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		jspltDisplay.setTopComponent(jspViewport);
		if(showViewSource)
		{
			jspltDisplay.setBottomComponent(jspSource);
		}
		else
		{
			jspltDisplay.setBottomComponent(null);
		}

		iSplitPos = jspltDisplay.getDividerLocation();

		registerDocumentStyles();

		jlblStatus = new JLabel(this.appName);
		jlblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jToolBar   = new JToolBar(JToolBar.HORIZONTAL);

		// Add the components to the app
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(jspltDisplay, BorderLayout.CENTER);
		this.getContentPane().add(jToolBar, BorderLayout.NORTH);
		this.getContentPane().add(jlblStatus, BorderLayout.SOUTH);
		
		// Open the default document provided by the Conf object
		this.open (id, document);
	}
	
	public void open(String id, String document)
	{
		// Open the document via uflow
		UrOpen urOpen = server.runOpen(id, document);
		
		// Get data from result
		String sStyleSheet = urOpen.getCss();
		this.modeName = urOpen.getModeName();
		String mode = urOpen.getMode();
		this.documentFriendlyName = urOpen.getName();
		
		jlblStatus.setText (documentFriendlyName);
		
		// Parse the stylesheet string
		try {
			styleSheet.loadRules(new StringReader(sStyleSheet), null);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// Create the dialogs

		jcmbStyleSelector = new JComboBoxNoFocus();
		jcmbStyleSelector.setAction(new StylesAction(jcmbStyleSelector));
		jToolBar.add(jcmbStyleSelector);
		
		HashMap extraActions=new HashMap(2);
		extraActions.put("undo",undoAction);
		extraActions.put("redo",redoAction);
		
		jMenuBar = new JMenuBar();
		new DialogsBuilder(this, this, jMenuBar, jToolBar, extraActions).parse(mode);
		this.setJMenuBar(jMenuBar);

		
		// Put the document data
		if(styleSheet != null)
		{
			htmlDoc = new HTMLDocument(styleSheet);
		}
		else
		{
			htmlDoc = (HTMLDocument)(htmlKit.createDefaultDocument());
		}
		try {
			htmlKit.insertHTML(htmlDoc, 0, "<p>"+urOpen.getData()+"</p>", 0, 0, HTML.Tag.P); // TODO could be better ...
		}
		catch (Exception e) { e.printStackTrace(); }

		registerDocument(htmlDoc);
		currentFile = null;
		updateTitle();
		refreshOnUpdate(); 
	}

	/* ActionListener method */
	public void actionPerformed(ActionEvent ae)
	{
		try {
			String command = ae.getActionCommand();
			if (command==null) return;

			System.out.println(command);

			if(command.equals("uopen"))
			{
				if (documentsSelector == null)
					documentsSelector = new Selector (server, id, document, Selector.DOCUMENTS);
				
				String document = documentsSelector.getSelection();
				System.out.println (document);
				this.open (id, document);
			}
			if(command.equals("usave"))
			{
				UReply ureply = server.runSave (id, document, modeName, "DATA", jtpMain.getText());
				if (ureply.getError() != UReply.OK)
					System.err.println ("UFlow error "+ureply.getError());
			}

			if(command.equals("newdoc"))
			{
				if(styleSheet != null)
				{
					htmlDoc = new HTMLDocument(styleSheet);
				}
				else
				{
					htmlDoc = (HTMLDocument)(htmlKit.createDefaultDocument());
				}
				jtpMain.setText("<HTML><BODY>he</BODY></HTML>");
				jtpSource.setText(jtpMain.getText());
				htmlKit.insertHTML(htmlDoc, 0, "<p>hello</p>", 0, 0, HTML.Tag.P);
				//try {
				//	htmlKit.read(new StringReader("<html><body>ho</body></html>"), htmlDoc, 0);
				//}
				//catch (Exception e) { e.printStackTrace(); }
				registerDocument(htmlDoc);
				currentFile = null;
				updateTitle();
			}
			else if(command.equals("openhtml"))
			{
				openDocument(null);
			}
			else if(command.equals("opencss"))
			{
				openStyleSheet(null);
			}
			else if(command.equals("save"))
			{
				writeOut((HTMLDocument)(jtpMain.getDocument()), currentFile);
				updateTitle();
			}
			else if(command.equals("saveas"))
			{
				writeOut((HTMLDocument)(jtpMain.getDocument()), null);
			}
			else if(command.equals("savebody"))
			{
				writeOutFragment((HTMLDocument)(jtpMain.getDocument()),"body");
			}
			else if(command.equals("savertf"))
			{
				writeOutRTF((StyledDocument)(jtpMain.getStyledDocument()));
			}
			else if(command.equals("describe"))
			{
				System.out.println("------------DOCUMENT------------");
				System.out.println("Content Type : " + jtpMain.getContentType());
				System.out.println("Editor Kit   : " + jtpMain.getEditorKit());
				System.out.println("Doc Tree     :");
				System.out.println("");
				describeDocument(jtpMain.getStyledDocument());
				System.out.println("--------------------------------");
				System.out.println("");
			}
			else if(command.equals("describecss"))
			{
				System.out.println("-----------STYLESHEET-----------");
				System.out.println("Stylesheet Rules");
				java.util.Enumeration rules = styleSheet.getStyleNames();
				while(rules.hasMoreElements())
				{
					String ruleName = (String)(rules.nextElement());
					Style styleRule = styleSheet.getStyle(ruleName);
					System.out.println(styleRule.toString());
				}
				System.out.println("--------------------------------");
				System.out.println("");
			}
			else if(command.equals("whattags"))
			{
				System.out.println("Caret Position : " + jtpMain.getCaretPosition());
				AttributeSet attribSet = jtpMain.getCharacterAttributes();
				java.util.Enumeration attribs = attribSet.getAttributeNames();
				System.out.println("Attributes     : ");
				while(attribs.hasMoreElements())
				{
					String attribName = attribs.nextElement().toString();
					System.out.println("                 " + attribName + " | " + attribSet.getAttribute(attribName));
				}
			}
			else if(command.equals("toggletoolbar"))
			{
				jToolBar.setVisible (!jToolBar.isVisible());
			}
			else if(command.equals("viewsource"))
			{
				toggleSourceWindow();
			}
			else if(command.equals("serialize"))
			{
				serializeOut((HTMLDocument)(jtpMain.getDocument()));
			}
			else if(command.equals("readfromser"))
			{
				serializeIn();
			}
			else if(command.equals("inserttable"))
			{
				String[] fieldNames = { "rows", "cols", "border", "spacing", "padding" };
				String[] fieldTypes = { "text", "text", "text",   "text",    "text" };
				insertTable((Hashtable)null, fieldNames, fieldTypes);
			}
			else if(command.equals("inserttablerow"))
			{
				insertTableRow();
			}
			else if(command.equals("inserttablecell"))
			{
				insertTableCell();
			}
			else if(command.equals("insertparagraph"))
			{
				insertParagraph();
			}
			else if(command.equals("insertbreak"))
			{
				insertBreak();
			}
			else if(command.equals("insertimage"))
			{
				insertImage(null);
			}
			else if(command.equals("insertnbsp"))
			{
				insertNonbreakingSpace();
			}
			else if(command.equals("insertform"))
			{
				String[] fieldNames = { "name", "method", "enctype" };
				String[] fieldTypes = { "text", "text",   "text" };
				insertFormElement(HTML.Tag.FORM, "form", (Hashtable)null, fieldNames, fieldTypes, true);
			}
			else if(command.equals("inserttextfield"))
			{
				Hashtable htAttribs = new Hashtable();
				htAttribs.put("type", "text");
				String[] fieldNames = { "name", "value", "size", "maxlength" };
				String[] fieldTypes = { "text", "text",  "text", "text" };
				insertFormElement(HTML.Tag.INPUT, "input", htAttribs, fieldNames, fieldTypes, false);
			}
			else if(command.equals("inserttextarea"))
			{
				String[] fieldNames = { "name", "rows", "cols" };
				String[] fieldTypes = { "text", "text", "text" };
				insertFormElement(HTML.Tag.TEXTAREA, "textarea", (Hashtable)null, fieldNames, fieldTypes, true);
			}
			else if(command.equals("insertcheckbox"))
			{
				Hashtable htAttribs = new Hashtable();
				htAttribs.put("type", "checkbox");
				String[] fieldNames = { "name", "checked" };
				String[] fieldTypes = { "text", "bool" };
				insertFormElement(HTML.Tag.INPUT, "input", htAttribs, fieldNames, fieldTypes, false);
			}
			else if(command.equals("insertradiobutton"))
			{
				Hashtable htAttribs = new Hashtable();
				htAttribs.put("type", "radio");
				String[] fieldNames = { "name", "checked" };
				String[] fieldTypes = { "text", "bool" };
				insertFormElement(HTML.Tag.INPUT, "input", htAttribs, fieldNames, fieldTypes, false);
			}
			else if(command.equals("insertpassword"))
			{
				Hashtable htAttribs = new Hashtable();
				htAttribs.put("type", "password");
				String[] fieldNames = { "name", "value", "size", "maxlength" };
				String[] fieldTypes = { "text", "text",  "text", "text" };
				insertFormElement(HTML.Tag.INPUT, "input", htAttribs, fieldNames, fieldTypes, false);
			}
			else if(command.equals("insertbutton"))
			{
				Hashtable htAttribs = new Hashtable();
				htAttribs.put("type", "button");
				String[] fieldNames = { "name", "value" };
				String[] fieldTypes = { "text", "text" };
				insertFormElement(HTML.Tag.INPUT, "input", htAttribs, fieldNames, fieldTypes, false);
			}
			else if(command.equals("insertbuttonsubmit"))
			{
				Hashtable htAttribs = new Hashtable();
				htAttribs.put("type", "submit");
				String[] fieldNames = { "name", "value" };
				String[] fieldTypes = { "text", "text" };
				insertFormElement(HTML.Tag.INPUT, "input", htAttribs, fieldNames, fieldTypes, false);
			}
			else if(command.equals("insertbuttonreset"))
			{
				Hashtable htAttribs = new Hashtable();
				htAttribs.put("type", "reset");
				String[] fieldNames = { "name", "value" };
				String[] fieldTypes = { "text", "text" };
				insertFormElement(HTML.Tag.INPUT, "input", htAttribs, fieldNames, fieldTypes, false);
			}
			else if(command.equals("find"))
			{
				doSearch((String)null, (String)null, false, lastSearchCaseSetting, lastSearchTopSetting);
			}
			else if(command.equals("findagain"))
			{
				doSearch(lastSearchFindTerm, (String)null, false, lastSearchCaseSetting, false);
			}
			else if(command.equals("replace"))
			{
				doSearch((String)null, (String)null, true, lastSearchCaseSetting, lastSearchTopSetting);
			}
			else if(command.equals("exit"))
			{
				frameHandler.dispose();
				System.exit(0);
			}
			else if(command.equals("helpabout"))
			{
				SimpleInfoDialog sidAbout = new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("About"), true, Translatrix.getTranslationString("AboutMessage"), SimpleInfoDialog.INFO);
			}
		}
		catch(IOException ioe)
		{
			logException("IOException in actionPerformed method", ioe);
			SimpleInfoDialog sidAbout = new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("Error"), true, Translatrix.getTranslationString("ErrorIOException"), SimpleInfoDialog.ERROR);
		}
		catch(BadLocationException ble)
		{
			logException("BadLocationException in actionPerformed method", ble);
			SimpleInfoDialog sidAbout = new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("Error"), true, Translatrix.getTranslationString("ErrorBadLocationException"), SimpleInfoDialog.ERROR);
		}
		catch(NumberFormatException nfe)
		{
			logException("NumberFormatException in actionPerformed method", nfe);
			SimpleInfoDialog sidAbout = new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("Error"), true, Translatrix.getTranslationString("ErrorNumberFormatException"), SimpleInfoDialog.ERROR);
		}
		catch(ClassNotFoundException cnfe)
		{
			logException("ClassNotFound Exception in actionPerformed method", cnfe);
			SimpleInfoDialog sidAbout = new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("Error"), true, Translatrix.getTranslationString("ErrorClassNotFoundException "), SimpleInfoDialog.ERROR);
		}
		catch(RuntimeException re)
		{
			logException("RuntimeException in actionPerformed method", re);
			SimpleInfoDialog sidAbout = new SimpleInfoDialog(this.getFrame(), Translatrix.getTranslationString("Error"), true, Translatrix.getTranslationString("ErrorRuntimeException"), SimpleInfoDialog.ERROR);
		}
	}

	/* DocumentListener methods */
	public void changedUpdate(DocumentEvent de)	{ handleDocumentChange(de); }
	public void insertUpdate(DocumentEvent de)	{ handleDocumentChange(de); }
	public void removeUpdate(DocumentEvent de)	{ handleDocumentChange(de); }
	public void handleDocumentChange(DocumentEvent de)
	{
		if(!exclusiveEdit)
		{
			if(jspSource.isShowing())
			{
				if(de.getDocument() instanceof HTMLDocument)
				{
					jtpSource.getDocument().removeDocumentListener(this);
					jtpSource.setText(jtpMain.getText());
					jtpSource.getDocument().addDocumentListener(this);
				}
				else if(de.getDocument() instanceof PlainDocument || de.getDocument() instanceof DefaultStyledDocument)
				{
					jtpMain.getDocument().removeDocumentListener(this);
					jtpMain.setText(jtpSource.getText());
					jtpMain.getDocument().addDocumentListener(this);
				}
			}
		}
	}

	/** Method for setting a document as the current document for the text pane
	  * and re-registering the controls and settings for it
	  */
	public void registerDocument(HTMLDocument htmlDoc)
	{
		jtpMain.setDocument(htmlDoc);
		jtpMain.getDocument().addUndoableEditListener(new CustomUndoableEditListener());
		jtpMain.getDocument().addDocumentListener(this);
		purgeUndos();
		registerDocumentStyles();
	}

	/** Method for locating the available CSS style for the document and adding
	  * them to the styles selector
	  */
	public void registerDocumentStyles()
	{
		if(jcmbStyleSelector == null || htmlDoc == null)
		{
			return;
		}
		jcmbStyleSelector.setEnabled(false);
		jcmbStyleSelector.removeAllItems();
		jcmbStyleSelector.addItem(Translatrix.getTranslationString("NoCSSStyle"));
		for(Enumeration e = htmlDoc.getStyleNames(); e.hasMoreElements();)
		{
			String name = (String) e.nextElement();
			if(name.length() > 0 && name.charAt(0) == '.')
			{
				jcmbStyleSelector.addItem(name.substring(1));
			}
		}
		jcmbStyleSelector.setEnabled(true);
	}

	/** Method for inserting an HTML Table
	  * NOTE : This is a much-improved version from earlier releases,
	  *        but still displays some quirky behaviour.
	  */
	private void insertTable(Hashtable attribs, String[] fieldNames, String[] fieldTypes)
	throws IOException, BadLocationException, RuntimeException, NumberFormatException
	{
		int caretPos = jtpMain.getCaretPosition();
		StringBuffer compositeElement = new StringBuffer("<TABLE");
		if(attribs != null && attribs.size() > 0)
		{
			Enumeration attribEntries = attribs.keys();
			while(attribEntries.hasMoreElements())
			{
				Object entryKey   = attribEntries.nextElement();
				Object entryValue = attribs.get(entryKey);
				if(entryValue != null && entryValue != "")
				{
					compositeElement.append(" " + entryKey + "=" + '"' + entryValue + '"');
				}
			}
		}
		int rows = 0;
		int cols = 0;
		if(fieldNames != null && fieldNames.length > 0)
		{
			PropertiesDialog propertiesDialog = new PropertiesDialog(this.getFrame(), fieldNames, fieldTypes, Translatrix.getTranslationString("FormDialogTitle"), true);
			propertiesDialog.show();
			for(int iter = 0; iter < fieldNames.length; iter++)
			{
				String fieldName = fieldNames[iter];
				String propValue = propertiesDialog.getFieldValue(fieldName);
				if(propValue != null && propValue != "" && propValue.length() > 0)
				{
					int attribValue = Integer.parseInt(propValue);
					if(fieldName.equals("rows"))
					{
						rows = attribValue;
					}
					else if(fieldName.equals("cols"))
					{
						cols = attribValue;
					}
					else
					{
						compositeElement.append(" " + fieldName + "=" + '"' + propValue + '"');
					}
				}
			}
		}
		compositeElement.append(">");
		for(int i = 0; i < rows; i++)
		{
			compositeElement.append("<TR>");
			for(int j = 0; j < cols; j++)
			{
				compositeElement.append("<TD></TD>");
			}
			compositeElement.append("</TR>");
		}
		compositeElement.append("</TABLE>");
		htmlKit.insertHTML(htmlDoc, caretPos, compositeElement.toString(), 0, 0, HTML.Tag.TABLE);
		jtpMain.setCaretPosition(caretPos + 1);
		this.repaint();
	}

	/** Method for inserting a row into an HTML Table
	  */
	private void insertTableRow()
	{
		Element	element = htmlDoc.getCharacterElement(jtpMain.getCaretPosition());
		Element elementParent = element.getParentElement();
		int startPoint  = -1;
		int columnCount = -1;
		while(elementParent != null && !elementParent.getName().equals("body"))
		{
			if(elementParent.getName().equals("tr"))
			{
				startPoint  = elementParent.getStartOffset();
				columnCount = elementParent.getElementCount();
				break;
			}
			else
			{
				elementParent = elementParent.getParentElement();
			}
		}
		if(startPoint > -1 && columnCount > -1)
		{
			jtpMain.setCaretPosition(startPoint);
	 		StringBuffer sRow = new StringBuffer();
 			sRow.append("<TR>");
 			for(int i = 0; i < columnCount; i++)
 			{
 				sRow.append("<TD></TD>");
 			}
 			sRow.append("</TR>");
 			ActionEvent actionEvent = new ActionEvent(jtpMain, 0, "insertTableRow");
 			new HTMLEditorKit.InsertHTMLTextAction("insertTableRow", sRow.toString(), HTML.Tag.TABLE, HTML.Tag.TR).actionPerformed(actionEvent);
 			refreshOnUpdate(); // optional refresh code
 		}
	}

	/** Method for inserting a cell into an HTML Table
	  */
	private void insertTableCell()
	{
		String sCell = "<TD></TD>";
		ActionEvent actionEvent = new ActionEvent(jtpMain, 0, "insertTableCell");
		new HTMLEditorKit.InsertHTMLTextAction("insertTableCell", sCell, HTML.Tag.TR, HTML.Tag.TD, HTML.Tag.TH, HTML.Tag.TD).actionPerformed(actionEvent);
		refreshOnUpdate(); // optional refresh code
	}

	/** Method for inserting a paragraph (P) element
	  */
	private void insertParagraph()
	throws IOException, BadLocationException, RuntimeException
	{
		int caretPos = jtpMain.getCaretPosition();
		htmlKit.insertHTML(htmlDoc, caretPos, "<P>hy</P>", 0, 0, HTML.Tag.P);
		jtpMain.setCaretPosition(caretPos + 1);
		refreshOnUpdate(); // optional refresh code
	}
	
	/** Method for inserting a break (BR) element
	  */
	private void insertBreak()
	throws IOException, BadLocationException, RuntimeException
	{
		int caretPos = jtpMain.getCaretPosition();
		htmlKit.insertHTML(htmlDoc, caretPos, "<BR>", 0, 0, HTML.Tag.BR);
		jtpMain.setCaretPosition(caretPos + 1);
	}

	/** Method for inserting a non-breaking space (&nbsp;)
	  */
	private void insertNonbreakingSpace()
	throws IOException, BadLocationException, RuntimeException
	{
		int caretPos = jtpMain.getCaretPosition();
		htmlDoc.insertString(caretPos, "\240", jtpMain.getInputAttributes());
		jtpMain.setCaretPosition(caretPos + 1);
	}

	/** Method for inserting a form element
	  */
	private void insertFormElement(HTML.Tag baseTag, String baseElement, Hashtable attribs, String[] fieldNames, String[] fieldTypes, boolean hasClosingTag)
	throws IOException, BadLocationException, RuntimeException
	{
		int caretPos = jtpMain.getCaretPosition();
		StringBuffer compositeElement = new StringBuffer("<" + baseElement);
		if(attribs != null && attribs.size() > 0)
		{
			Enumeration attribEntries = attribs.keys();
			while(attribEntries.hasMoreElements())
			{
				Object entryKey   = attribEntries.nextElement();
				Object entryValue = attribs.get(entryKey);
				if(entryValue != null && entryValue != "")
				{
					compositeElement.append(" " + entryKey + "=" + '"' + entryValue + '"');
				}
			}
		}
		if(fieldNames != null && fieldNames.length > 0)
		{
			PropertiesDialog propertiesDialog = new PropertiesDialog(this.getFrame(), fieldNames, fieldTypes, Translatrix.getTranslationString("FormDialogTitle"), true);
			propertiesDialog.show();
			for(int iter = 0; iter < fieldNames.length; iter++)
			{
				String fieldName = fieldNames[iter];
				String propValue = propertiesDialog.getFieldValue(fieldName);
				if(propValue != null && propValue.length() > 0)
				{
					if(fieldName.equals("checked"))
					{
						if(propValue.equals("true"))
						{
							compositeElement.append(" " + fieldName + "=" + '"' + propValue + '"');
						}
					}
					else
					{
						compositeElement.append(" " + fieldName + "=" + '"' + propValue + '"');
					}
				}
			}
		}
		// --- Convenience for editing, this makes the FORM visible
		if(useFormIndicator && baseElement.equals("form"))
		{
			compositeElement.append(" bgcolor=" + '"' + clrFormIndicator + '"');
		}
		// --- END
		compositeElement.append(">");
		if(hasClosingTag)
		{
			compositeElement.append("</" + baseElement + ">");
		}
		htmlKit.insertHTML(htmlDoc, caretPos, compositeElement.toString(), 0, 0, baseTag);
		// jtpMain.setCaretPosition(caretPos + 1);
		this.repaint();
	}

	/** Method to initiate a find/replace operation
	  */
	private void doSearch(String searchFindTerm, String searchReplaceTerm, boolean bIsFindReplace, boolean bCaseSensitive, boolean bStartAtTop)
	{
		boolean bReplaceAll = false;
		if(searchFindTerm == null || (bIsFindReplace && searchReplaceTerm == null))
		{
			SearchDialog sdSearchInput = new SearchDialog(this.getFrame(), Translatrix.getTranslationString("SearchDialogTitle"), true, bIsFindReplace, bCaseSensitive, bStartAtTop);
			searchFindTerm    = sdSearchInput.getFindTerm();
			searchReplaceTerm = sdSearchInput.getReplaceTerm();
			bCaseSensitive    = sdSearchInput.getCaseSensitive();
			bStartAtTop       = sdSearchInput.getStartAtTop();
			bReplaceAll       = sdSearchInput.getReplaceAll();
		}
		if(searchFindTerm != null && (!bIsFindReplace || searchReplaceTerm != null))
		{
			if(bReplaceAll)
			{
				int results = findText(searchFindTerm, searchReplaceTerm, bCaseSensitive, 0);
				int findOffset = results;
				if(results > -1)
				{
					findOffset = ((findOffset - searchFindTerm.length()) + searchReplaceTerm.length()) + 1;
					while(results > -1)
					{
						results    = findText(searchFindTerm, searchReplaceTerm, bCaseSensitive, findOffset);
						findOffset = ((results - searchFindTerm.length()) + searchReplaceTerm.length()) + 1;
					}
				}
				else
				{
					SimpleInfoDialog sidWarn = new SimpleInfoDialog(this.getFrame(), "", true, Translatrix.getTranslationString("ErrorNoOccurencesFound") + ":\n" + searchFindTerm, SimpleInfoDialog.WARNING);
				}
			}
			else
			{
				int results = findText(searchFindTerm, searchReplaceTerm, bCaseSensitive, (bStartAtTop ? 0 : jtpSource.getCaretPosition()));
				if(results == -1)
				{
					SimpleInfoDialog sidWarn = new SimpleInfoDialog(this.getFrame(), "", true, Translatrix.getTranslationString("ErrorNoMatchFound") + ":\n" + searchFindTerm, SimpleInfoDialog.WARNING);
				}
			}
			lastSearchFindTerm    = new String(searchFindTerm);
			if(searchReplaceTerm != null)
			{
				lastSearchReplaceTerm = new String(searchReplaceTerm);
			}
			else
			{
				lastSearchReplaceTerm = (String)null;
			}
			lastSearchCaseSetting = bCaseSensitive;
			lastSearchTopSetting  = bStartAtTop;
		}
	}

	/** Method for finding (and optionally replacing) a string in the text
	  */
	private int findText(String findTerm, String replaceTerm, boolean bCaseSenstive, int iOffset)
	{
		JTextPane jtpFindSource;
		if(jspSource.isShowing() || jtpSource.hasFocus())
		{
			jtpFindSource = jtpSource;
		}
		else
		{
			jtpFindSource = jtpMain;
		}
		int searchPlace = -1;
		try
		{
			javax.swing.text.Document baseDocument = jtpFindSource.getDocument();
			searchPlace =
				(bCaseSenstive ?
					baseDocument.getText(0, baseDocument.getLength()).indexOf(findTerm, iOffset) :
					baseDocument.getText(0, baseDocument.getLength()).toLowerCase().indexOf(findTerm.toLowerCase(), iOffset)
				);
			if(searchPlace > -1)
			{
				if(replaceTerm != null)
				{
					AttributeSet attribs = null;
					if(baseDocument instanceof HTMLDocument)
					{
						Element element = ((HTMLDocument)baseDocument).getCharacterElement(searchPlace);
						attribs = element.getAttributes();
					}
			        baseDocument.remove(searchPlace, findTerm.length());
	        		baseDocument.insertString(searchPlace, replaceTerm, attribs);
	        	}
	        	else
	        	{
	        		jtpFindSource.requestFocus();
	        		jtpFindSource.select(searchPlace, searchPlace + findTerm.length());
	        	}
	        }
        }
        catch(BadLocationException ble)
        {
			logException("BadLocationException in actionPerformed method", ble);
			SimpleInfoDialog sidAbout = new SimpleInfoDialog(this.getFrame(),
					Translatrix.getTranslationString("Error"), true,
					Translatrix.getTranslationString("ErrorBadLocationException"),
					SimpleInfoDialog.ERROR);
		}
		return searchPlace;
	}

	/** Method for inserting an image
	  */
	private void insertImage(File whatImage)
	throws IOException, BadLocationException, RuntimeException
	{
		if(whatImage == null) {
			whatImage = getImageFromChooser(".", extsIMG, Translatrix.getTranslationString("FiletypeIMG"));
		}
		if(whatImage != null)
		{
			int caretPos = jtpMain.getCaretPosition();
			htmlKit.insertHTML(htmlDoc, caretPos, "<IMG SRC=" + whatImage + ">", 0, 0, HTML.Tag.IMG);
			jtpMain.setCaretPosition(caretPos + 1);
			this.repaint();
		}
	}

	/** Method for saving text as a complete HTML document
	  */
	private void writeOut(HTMLDocument doc, File whatFile)
	throws IOException, BadLocationException
	{
		if(whatFile == null)
		{
			whatFile = getFileFromChooser(".", JFileChooser.SAVE_DIALOG, extsHTML, Translatrix.getTranslationString("FiletypeHTML"));
		}
		if(whatFile != null)
		{
			FileWriter fw = new FileWriter(whatFile);
			htmlKit.write(fw, doc, 0, doc.getLength());
			fw.flush();
			fw.close();
			currentFile = whatFile;
			updateTitle();
		}
		this.repaint();
	}

	/** Method for saving text as an HTML fragment
	  */
	private void writeOutFragment(HTMLDocument doc, String containingTag)
	throws IOException, BadLocationException
	{
		File whatFile = getFileFromChooser(".", JFileChooser.SAVE_DIALOG, extsHTML, Translatrix.getTranslationString("FiletypeHTML"));
		if(whatFile != null)
		{
			FileWriter fw = new FileWriter(whatFile);
//			Element eleBody = locateElementInDocument((StyledDocument)doc, containingTag);
//			htmlKit.write(fw, doc, eleBody.getStartOffset(), eleBody.getEndOffset());
			String docTextCase = jtpSource.getText().toLowerCase();
			int tagStart       = docTextCase.indexOf("<" + containingTag.toLowerCase());
			int tagStartClose  = docTextCase.indexOf(">", tagStart) + 1;
			String closeTag    = "</" + containingTag.toLowerCase() + ">";
			int tagEndOpen     = docTextCase.indexOf(closeTag);
			if(tagStartClose < 0) { tagStartClose = 0; }
			if(tagEndOpen < 0 || tagEndOpen > docTextCase.length()) { tagEndOpen = docTextCase.length(); }
			String bodyText = jtpSource.getText().substring(tagStartClose, tagEndOpen);
			fw.write(bodyText, 0, bodyText.length());
			fw.flush();
			fw.close();
		}
		this.repaint();
	}

	/** Method for saving text as an RTF document
	  */
	private void writeOutRTF(StyledDocument doc)
	throws IOException, BadLocationException
	{
		File whatFile = getFileFromChooser(".", JFileChooser.SAVE_DIALOG, extsRTF, Translatrix.getTranslationString("FiletypeRTF"));
		if(whatFile != null)
		{
			FileOutputStream fos = new FileOutputStream(whatFile);
			RTFEditorKit rtfKit = new RTFEditorKit();
			rtfKit.write(fos, doc, 0, doc.getLength());
			fos.flush();
			fos.close();
		}
		this.repaint();
	}

	/** Method to invoke loading HTML into the app
	  */
	private void openDocument(File whatFile)
	throws IOException, BadLocationException
	{
		if(whatFile == null)
		{
			whatFile = getFileFromChooser(".", JFileChooser.OPEN_DIALOG, extsHTML, Translatrix.getTranslationString("FiletypeHTML"));
		}
		if(whatFile != null)
		{
			try
			{
				loadDocument(whatFile, null);
			}
			catch(ChangedCharSetException ccse)
			{
				String charsetType = ccse.getCharSetSpec().toLowerCase();
				int pos = charsetType.indexOf("charset");
				if(pos == -1)
				{
					throw ccse;
				}
				while(pos < charsetType.length() && charsetType.charAt(pos) != '=')
				{
					pos++;
				}
				pos++; // Places file cursor past the equals sign (=)
				String whatEncoding = charsetType.substring(pos).trim();
				loadDocument(whatFile, whatEncoding);
			}
		}
		this.repaint();
	}

	/** Method for loading HTML document into, including document encoding setting
	  */
	private void loadDocument(File whatFile, String whatEncoding)
	throws IOException, BadLocationException
	{
		Reader r = null;
		htmlDoc = (HTMLDocument)(htmlKit.createDefaultDocument());
		try
		{
			if(whatEncoding == null)
			{
				r = new InputStreamReader(new FileInputStream(whatFile));
			}
			else
			{
				r = new InputStreamReader(new FileInputStream(whatFile), whatEncoding);
				htmlDoc.putProperty("IgnoreCharsetDirective", new Boolean(true));
			}
			htmlKit.read(r, htmlDoc, 0);
			r.close();
			registerDocument(htmlDoc);
			jtpSource.setText(jtpMain.getText());
			currentFile = whatFile;
			updateTitle();
		}
		finally
		{
			if(r != null)
			{
				r.close();
			}
		}
	}

	/** Method for loading a Stylesheet into the app
	  */
	private void openStyleSheet(File fileCSS)
	throws IOException
	{
		if(fileCSS == null)
		{
			fileCSS = getFileFromChooser(".", JFileChooser.OPEN_DIALOG, extsCSS, Translatrix.getTranslationString("FiletypeCSS"));
		}
		if(fileCSS != null)
		{
			String currDocText = jtpMain.getText();
			htmlDoc = (HTMLDocument)(htmlKit.createDefaultDocument());
			styleSheet = htmlDoc.getStyleSheet();
			URL cssUrl = fileCSS.toURL();
			InputStream is = cssUrl.openStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			styleSheet.loadRules(br, cssUrl);
			br.close();
			htmlDoc = new HTMLDocument(styleSheet);
			registerDocument(htmlDoc);
			jtpMain.setText(currDocText);
			jtpSource.setText(jtpMain.getText());
		}
		this.repaint();
	}

	/** Method for serializing the document out to a file
	  */
	public void serializeOut(HTMLDocument doc)
	throws IOException
	{
		File whatFile = getFileFromChooser(".", JFileChooser.SAVE_DIALOG, extsSer, Translatrix.getTranslationString("FiletypeSer"));
		if(whatFile != null)
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(whatFile));
			oos.writeObject(doc);
			oos.flush();
			oos.close();
		}
		this.repaint();
	}

	/** Method for reading in a serialized document from a file
	  */
	public void serializeIn()
	throws IOException, ClassNotFoundException
	{
		File whatFile = getFileFromChooser(".", JFileChooser.OPEN_DIALOG, extsSer, Translatrix.getTranslationString("FiletypeSer"));
		if(whatFile != null)
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(whatFile));
			htmlDoc = (HTMLDocument)(ois.readObject());
			ois.close();
			registerDocument(htmlDoc);
			validate();
		}
		this.repaint();
	}

	/** Method for obtaining a File for input/output using a JFileChooser dialog
	  */
	private File getFileFromChooser(String startDir, int dialogType, String[] exts, String desc)
	{
		JFileChooser jfileDialog = new JFileChooser(startDir);
		jfileDialog.setDialogType(dialogType);
		jfileDialog.setFileFilter(new MutableFilter(exts, desc));
		int optionSelected = JFileChooser.CANCEL_OPTION;
		if(dialogType == JFileChooser.OPEN_DIALOG)
		{
			optionSelected = jfileDialog.showOpenDialog(this);
		}
		else if(dialogType == JFileChooser.SAVE_DIALOG)
		{
			optionSelected = jfileDialog.showSaveDialog(this);
		}
		else // default to an OPEN_DIALOG
		{
			optionSelected = jfileDialog.showOpenDialog(this);
		}
		if(optionSelected == JFileChooser.APPROVE_OPTION)
		{
			return jfileDialog.getSelectedFile();
		}
		return (File)null;
	}

	/** Method for obtaining an Image for input using a custom JFileChooser dialog
	  */
	private File getImageFromChooser(String startDir, String[] exts, String desc)
	{
		ImageFileChooser jImageDialog = new ImageFileChooser(startDir);
		jImageDialog.setDialogType(JFileChooser.CUSTOM_DIALOG);
		jImageDialog.setFileFilter(new MutableFilter(exts, desc));
		jImageDialog.setDialogTitle("Select Image To Insert");
		int optionSelected = JFileChooser.CANCEL_OPTION;
		optionSelected = jImageDialog.showDialog(this, Translatrix.getTranslationString("Insert"));
		if(optionSelected == JFileChooser.APPROVE_OPTION)
		{
			return jImageDialog.getSelectedFile();
		}
		return (File)null;
	}

	/** Method for describing the node hierarchy of the document
	  */
	private void describeDocument(StyledDocument doc)
	{
		Element[] elements = doc.getRootElements();
		for(int i = 0; i < elements.length; i++)
		{
			indent = indentStep;
			for(int j = 0; j < indent; j++) { System.out.print(" "); }
			System.out.print(elements[i]);
			traverseElement(elements[i]);
			System.out.println("");
		}
	}

	/** Traverses nodes for the describing method
	  */
	private void traverseElement(Element element)
	{
		indent += indentStep;
		for(int i = 0; i < element.getElementCount(); i++)
		{
			for(int j = 0; j < indent; j++) { System.out.print(" "); }
			System.out.print(element.getElement(i));
			traverseElement(element.getElement(i));
		}
		indent -= indentStep;
	}

	/** Method to locate a node element by name
	  */
	private Element locateElementInDocument(StyledDocument doc, String elementName)
	{
		Element[] elements = doc.getRootElements();
		for(int i = 0; i < elements.length; i++)
		{
			if(elements[i].getName().equalsIgnoreCase(elementName))
			{
				return elements[i];
			}
			else
			{
				Element rtnElement = locateChildElementInDocument(elements[i], elementName);
				if(rtnElement != null)
				{
					return rtnElement;
				}
			}
		}
		return (Element)null;
	}

	/** Traverses nodes for the locating method
	  */
	private Element locateChildElementInDocument(Element element, String elementName)
	{
		for(int i = 0; i < element.getElementCount(); i++)
		{
			if(element.getElement(i).getName().equalsIgnoreCase(elementName))
			{
				return element.getElement(i);
			}
		}
		return (Element)null;
	}

	/** Convenience method for obtaining the JTextPane
	  */
	public JTextPane getTextPane()
	{
		return jtpMain;
	}

	/** Convenience method for obtaining the application as a Frame
	  */
	public Frame getFrame()
	{
		return frameHandler;
	}

	/** Convenience method for setting the parent Frame
	  */
	public void setFrame(Frame parentFrame)
	{
		frameHandler = parentFrame;
	}

	/** Convenience method for obtaining the pre-generated menu bar
	  */
	public JMenuBar getMenuBar()
	{
		return jMenuBar;
	}

	/** Convenience method for obtaining the current file handle
	  */
	public File getCurrentFile()
	{
		return currentFile;
	}

	/** Convenience method for obtaining the application name
	  */
	public String getAppName()
	{
		return appName;
	}

	/** Convenience method for obtaining the document text
	  */
	public String getDocumentText()
	{
		return jtpMain.getText();
	}

	/** Convenience method for obtaining the document text
	  * contained within a tag pair
	  */
	public String getDocumentSubText(String tagBlock)
	{
		return getSubText(jtpMain.getText(), tagBlock);
	}

	/** Method for extracting the text within a tag
	  */
	private String getSubText(String docText, String containingTag)
	{
		String docTextCase = jtpSource.getText().toLowerCase();
		int tagStart       = docTextCase.indexOf("<" + containingTag.toLowerCase());
		int tagStartClose  = docTextCase.indexOf(">", tagStart) + 1;
		String closeTag    = "</" + containingTag.toLowerCase() + ">";
		int tagEndOpen     = docTextCase.indexOf(closeTag);
		if(tagStartClose < 0) { tagStartClose = 0; }
		if(tagEndOpen < 0 || tagEndOpen > docTextCase.length()) { tagEndOpen = docTextCase.length(); }
		return jtpSource.getText().substring(tagStartClose, tagEndOpen);
	}

	/** Convenience method for setting the document text
	  */
	public void setDocumentText(String sText)
	{
		jtpMain.setText(sText);
		((HTMLEditorKit)(jtpMain.getEditorKit())).setDefaultCursor(new Cursor(Cursor.TEXT_CURSOR));
		jtpSource.setText(jtpMain.getText());
		((HTMLEditorKit)(jtpSource.getEditorKit())).setDefaultCursor(new Cursor(Cursor.TEXT_CURSOR));
	}

	/** Convenience method for obtaining the document text
	  */
	private void updateTitle()
	{
		frameHandler.setTitle(appName + (currentFile == null ? "" : " - " + currentFile.getName()));
	}

	/** Convenience method for clearing out the UndoManager
	  */
	public void purgeUndos()
	{
		if(undoMngr != null)
		{
			undoMngr.discardAllEdits();
			undoAction.updateUndoState();
			redoAction.updateRedoState();
		}
	}

	/** Convenience method for refreshing and displaying changes
	  */
	public void refreshOnUpdate()
	{
		jtpMain.setText(jtpMain.getText());
		jtpSource.setText(jtpMain.getText());
		purgeUndos();
		this.repaint();
	}

	/** Convenience method for fetching icon images from jar file
	  */
	private ImageIcon getEkitIcon(String iconName)
	{
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icons/" + iconName + "HK.gif")));
	}

	/** Convenience method for outputting exceptions
	  */
	private void logException(String internalMessage, Exception e)
	{
		System.err.println(internalMessage);
		e.printStackTrace(System.err);
	}

	/** Convenience method for toggling source window visibility
	  */
	private void toggleSourceWindow()
	{
		if(!(jspSource.isShowing()))
		{
			jtpSource.setText(jtpMain.getText());
			jspltDisplay.setRightComponent(jspSource);
			if(exclusiveEdit)
			{
				jspltDisplay.setDividerLocation(0);
				jspltDisplay.setEnabled(false);
			}
			else
			{
				jspltDisplay.setDividerLocation(iSplitPos);
				jspltDisplay.setEnabled(true);
			}
		}
		else
		{
			jtpMain.setText(jtpSource.getText());
			iSplitPos = jspltDisplay.getDividerLocation();
			jspltDisplay.remove(jspSource);
			jtpMain.requestFocus();
		}
		this.validate();
		//jcbmiViewSource.setSelected(jspSource.isShowing());
		//jtbtnViewSource.setSelected(jspSource.isShowing());
	}

	/** Searches the specified element for CLASS attribute setting
	  */
	private String findStyle(Element element)
	{
		AttributeSet as = element.getAttributes();
		if(as == null)
		{
			return null;
		}
		Object val = as.getAttribute(HTML.Attribute.CLASS);
		if(val != null && (val instanceof String))
		{
			return (String)val;
		}
		for(Enumeration e = as.getAttributeNames(); e.hasMoreElements();)
		{
			Object key = e.nextElement();
			if(key instanceof HTML.Tag)
			{
				AttributeSet eas = (AttributeSet)(as.getAttribute(key));
				if(eas != null)
				{
					val = eas.getAttribute(HTML.Attribute.CLASS);
					if(val != null)
					{
						return (String)val;
					}
				}
			}

		}
		return null;
	}

	/** Handles caret tracking and related events, such as displaying the current style
	  * of the text under the caret
	  */
	private void handleCaretPositionChange(CaretEvent ce)
	{
		int caretPos = ce.getDot();
		Element	element = htmlDoc.getCharacterElement(caretPos);
/*
---- TAG EXPLICATOR CODE -------------------------------------------
		javax.swing.text.ElementIterator ei = new javax.swing.text.ElementIterator(htmlDoc);
		Element ele;
		while((ele = ei.next()) != null)
		{
			System.out.println("ELEMENT : " + ele.getName());
		}
		System.out.println("ELEMENT:" + element.getName());
		Element elementParent = element.getParentElement();
		System.out.println("ATTRS:");
		AttributeSet attribs = elementParent.getAttributes();
		for(Enumeration eAttrs = attribs.getAttributeNames(); eAttrs.hasMoreElements();)
		{
			System.out.println("  " + eAttrs.nextElement().toString());
		}
		while(elementParent != null && !elementParent.getName().equals("body"))
		{
			String parentName = elementParent.getName();
			System.out.println("PARENT:" + parentName);
			System.out.println("ATTRS:");
			attribs = elementParent.getAttributes();
			for(Enumeration eAttr = attribs.getAttributeNames(); eAttr.hasMoreElements();)
			{
				System.out.println("  " + eAttr.nextElement().toString());
			}
			elementParent = elementParent.getParentElement();
		}
---- END -------------------------------------------
*/
		if(element == null)
		{
			return;
		}
		String style = null;
		Vector vcStyles = new Vector();
		while(element != null)
		{
			if(style == null)
			{
				style = findStyle(element);
			}
			vcStyles.add(element);
			element = element.getParentElement();
		}
		int stylefound = -1;
		if(style != null)
		{
			for(int i = 0; i < jcmbStyleSelector.getItemCount(); i++)
			{
				String in = (String)(jcmbStyleSelector.getItemAt(i));
				if(in.equalsIgnoreCase(style))
				{
					stylefound = i;
					break;
				}
			}
		}
		if(stylefound > -1)
		{
			Action ac = jcmbStyleSelector.getAction();
			ac.setEnabled(false);
			jcmbStyleSelector.setSelectedIndex(stylefound);
			ac.setEnabled(true);
		}
		else
		{
			jcmbStyleSelector.setSelectedIndex(0);
		}
	}

/* Inner Classes --------------------------------------------- */

	/** Class for implementing Undo as an autonomous action
	  */
	class UndoAction extends AbstractAction
	{
		public UndoAction()
		{
			super(Translatrix.getTranslationString("Undo"));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
				undoMngr.undo();
			}
			catch(CannotUndoException ex)
			{
				ex.printStackTrace();
			}
			updateUndoState();
			redoAction.updateRedoState();
		}

		protected void updateUndoState()
		{
			if(undoMngr.canUndo())
			{
				setEnabled(true);
				putValue(Action.NAME, undoMngr.getUndoPresentationName());
			}
			else
			{
				setEnabled(false);
				putValue(Action.NAME, Translatrix.getTranslationString("Undo"));
			}
		}
	}

	/** Class for implementing Redo as an autonomous action
	  */
	class RedoAction extends AbstractAction
	{
		public RedoAction()
		{
			super(Translatrix.getTranslationString("Redo"));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e)
		{
			try
			{
				undoMngr.redo();
			}
			catch(CannotUndoException ex)
			{
				ex.printStackTrace();
			}
			updateRedoState();
			undoAction.updateUndoState();
		}

		protected void updateRedoState()
		{
			if(undoMngr.canRedo())
			{
				setEnabled(true);
				putValue(Action.NAME, undoMngr.getRedoPresentationName());
			}
			else
			{
				setEnabled(false);
				putValue(Action.NAME, Translatrix.getTranslationString("Redo"));
			}
		}
	}

	/** Class for implementing the Undo listener to handle the Undo and Redo actions
	  */
	class CustomUndoableEditListener implements UndoableEditListener
	{
		public void undoableEditHappened(UndoableEditEvent uee)
		{
			undoMngr.addEdit(uee.getEdit());
			undoAction.updateUndoState();
			redoAction.updateRedoState();
		}
	}

}
