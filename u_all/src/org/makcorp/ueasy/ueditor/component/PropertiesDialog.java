/*
GNU Lesser General Public License

PropertiesDialog
Copyright (C) 2002  Howard A Kistler

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

package org.makcorp.ueasy.ueditor.component;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Iterator;
//import java.util.LinkedHashMap;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.makcorp.ueasy.util.Translatrix;

/** Class for providing a dialog that lets the user specify values for tag attributes
  */
public class PropertiesDialog extends JDialog
{
	private JOptionPane jOptionPane;
	private Hashtable htInputFields;

/* Sorry, LinkedHashMap is 1.4 only!
	private LinkedHashMap lhmInputFields;

	public PropertiesDialog(Frame parent, LinkedHashMap fields, String title, boolean bModal)
	{
		super(parent, title, bModal);
		lhmInputFields = new LinkedHashMap();
		final Object[] buttonLabels = { Translatrix.getTranslationString("DialogAccept"), Translatrix.getTranslationString("DialogCancel") };
		Object[] panelContents = new Object[(fields.size() * 2)];
		int objectCount = 0;
		for(Iterator iter = fields.keySet().iterator(); iter.hasNext();)
		{
			String fieldName = iter.next().toString();
			String fieldType = fields.get(fieldName).toString();
			Object fieldComponent;
			if(fieldType.equals("text"))
			{
				fieldComponent = new JTextField(3);
			}
			else if(fieldType.equals("bool"))
			{
				fieldComponent = new JCheckBox();
			}
			else
			{
				fieldComponent = new JTextField(3);
			}
			lhmInputFields.put(fieldName, fieldComponent);
			panelContents[objectCount]     = fieldName; // Translatrix.getTranslationString(fieldName);
			panelContents[objectCount + 1] = fieldComponent;
			objectCount += 2;
		}
		jOptionPane = new JOptionPane(panelContents, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, buttonLabels, buttonLabels[0]);

		setContentPane(jOptionPane);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we)
			{
				jOptionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
			}
		});

		jOptionPane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e)
			{
				String prop = e.getPropertyName();
				if(isVisible() 
					&& (e.getSource() == jOptionPane)
					&& (prop.equals(JOptionPane.VALUE_PROPERTY) || prop.equals(JOptionPane.INPUT_VALUE_PROPERTY)))
				{
					Object value = jOptionPane.getValue();
					if (value == JOptionPane.UNINITIALIZED_VALUE)
					{
						return;
					}
					jOptionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
					if(value.equals(buttonLabels[0]))
					{
						setVisible(false);
					}
					else
					{
						setVisible(false);
					}
				}
			}
		});
		this.pack();
	}
*/

	public PropertiesDialog(Frame parent, String[] fields, String[] types, String title, boolean bModal)
	{
		super(parent, title, bModal);
		htInputFields = new Hashtable();
		final Object[] buttonLabels = { Translatrix.getTranslationString("DialogAccept"), Translatrix.getTranslationString("DialogCancel") };
		Object[] panelContents = new Object[(fields.length * 2)];
		int objectCount = 0;
		for(int iter = 0; iter < fields.length; iter++)
		{
			String fieldName = fields[iter];
			String fieldType = types[iter];
			Object fieldComponent;
			if(fieldType.equals("text"))
			{
				fieldComponent = new JTextField(3);
			}
			else if(fieldType.equals("bool"))
			{
				fieldComponent = new JCheckBox();
			}
			else
			{
				fieldComponent = new JTextField(3);
			}
			htInputFields.put(fieldName, fieldComponent);
			panelContents[objectCount]     = fieldName; // Translatrix.getTranslationString(fieldName);
			panelContents[objectCount + 1] = fieldComponent;
			objectCount += 2;
		}
		jOptionPane = new JOptionPane(panelContents, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, buttonLabels, buttonLabels[0]);

		setContentPane(jOptionPane);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we)
			{
				jOptionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
			}
		});

		jOptionPane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e)
			{
				String prop = e.getPropertyName();
				if(isVisible() 
					&& (e.getSource() == jOptionPane)
					&& (prop.equals(JOptionPane.VALUE_PROPERTY) || prop.equals(JOptionPane.INPUT_VALUE_PROPERTY)))
				{
					Object value = jOptionPane.getValue();
					if (value == JOptionPane.UNINITIALIZED_VALUE)
					{
						return;
					}
					jOptionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
					if(value.equals(buttonLabels[0]))
					{
						setVisible(false);
					}
					else
					{
						setVisible(false);
					}
				}
			}
		});
		this.pack();
	}

	public String getFieldValue(String fieldName)
	{
		Object dataField = htInputFields.get(fieldName);
		if(dataField instanceof JTextField)
		{
			return ((JTextField)dataField).getText();
		}
		else if(dataField instanceof JCheckBox)
		{
			if(((JCheckBox)dataField).isSelected())
			{
				return "true";
			}
			else
			{
				return "false";
			}
		}
		else
		{
			return (String)null;
		}
	}

}

