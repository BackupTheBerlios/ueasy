/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.aml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This implementation uses the local files system.
 * The classpath should contains the ".iaml" and ".laml" files
 *  - iaml files are international AML file, containing keywords for label and menu
 *  - laml files are localized files, containing local keywords translations
 * The goal of this class is to get both iaml and laml to produce a single aml file.
 * naming convention: the laml file for org.mycorp.docbook in France's french should be named "org.mygroup.docbook.fr_FR.laml"
 */
public class LocalAmlFactory extends DefaultHandler implements AmlFactory
{
	/** Logging utility */
	Logger log = Logger.getRootLogger();
	
	public static String PLUGINS_DIRECTORY = "plugins";
	
	// auxiliary variables for XML parsing
	private String category = "";
	private String workingFile;
	
	public AmlString getAmlString (String mode, String lang) throws Exception
	{
		// Get the IAML file
		
		String iaml = "";
		String file = PLUGINS_DIRECTORY+System.getProperties().getProperty("file.separator")+mode+".iaml";
		log.info("Reading "+file);
		try {
			FileInputStream fin =  new FileInputStream(file);
			BufferedReader myInput = new BufferedReader (new InputStreamReader(fin));
			String line;
			while ((line = myInput.readLine()) != null) {
				iaml += line;
			}
		}
		catch (FileNotFoundException e)
		{
			log.error ("Error : "+file+" not found", e);
			throw e;
		}
		catch (IOException e)
		{
			log.error ("Error while reading "+file, e);
			throw e;
		}
		log.debug (iaml);
		this.workingFile = iaml;

		// Parse the localization file to localize the IAML file into an AML file
		
		file = PLUGINS_DIRECTORY+System.getProperties().getProperty("file.separator")+mode+"."+lang+".laml";
		log.info("Reading "+file);
		InputSource is;
		try {
			is = new InputSource(new FileReader(new File(file)));
		}
		catch (FileNotFoundException e)
		{
			log.error ("Error : "+file+" not found", e);
			throw e;
		}
		catch (IOException e)
		{
			log.error ("Error while reading "+file, e);
			throw e;
		}
		
		XMLReader parser;
		try {
			// try to load Apache's parser
			parser = XMLReaderFactory.createXMLReader("org.apache.crimson.parser.XMLReaderImpl");
			parser.setContentHandler(this);
			log.info("Parsing laml file with Apache's parser");
			parser.parse (is);
		}
		catch (SAXException e1) {
			try {
				// if apache's XML parser is not available, load the default XML parser
				log.info("Trying with default parser");
				parser = XMLReaderFactory.createXMLReader();
				parser.setContentHandler(this);
				log.info("Parsing laml file with default parser");
				parser.parse (is);
			}
			catch (SAXException e2) {
				// no XML parser available
				log.error("no XML parser available or error in the XML or no AML file found");
				throw e2;
			}
			catch (java.io.IOException e) {
				log.error("Parsing LAML file", e);
				throw e;
			}
		}
		catch (java.io.IOException e) {
			log.error("Parsing LAML file", e);
			throw e;
		}
		
		return new AmlString(workingFile);
	}

	public void startElement(String uri, String name, String qName, Attributes atts)
	{
		if (name.equals("laml")) return;
		
		if (name.equals("category"))
			category = atts.getValue ("name");

		if (name.equals("tr") && !category.equals(""))
		{
			// replaces the international attributes of the working file by the associated localized attributes
			String key = atts.getValue("key");
			String value = atts.getValue("value");
			String search = category+"=\""+key+"\"";
			String replace = category+"=\""+value+"\"";
			
			if(workingFile.matches(".*"+search+".*"))
			{
				workingFile = workingFile.replaceAll(search, replace);
			}
			else
			{
				// if there is no 'label', the key is supposed to be the label
				workingFile = workingFile.replaceAll("key=\""+key+"\"", "key=\""+key+"\" label=\""+value+"\"");
			}
		}
	}
}
