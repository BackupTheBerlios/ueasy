/*
GNU Lesser General Public License

SimpleInfoDialog
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

package org.makcorp.ueasy.ueditor.component;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.makcorp.ueasy.util.Translatrix;

/** Class for providing a dialog that lets the user specify values for tag attributes.
  */
public class SimpleInfoDialog extends JDialog
{

	public static final int ERROR    = JOptionPane.ERROR_MESSAGE;
	public static final int INFO     = JOptionPane.INFORMATION_MESSAGE;
	public static final int WARNING  = JOptionPane.WARNING_MESSAGE;
	public static final int QUESTION = JOptionPane.QUESTION_MESSAGE;
	public static final int PLAIN    = JOptionPane.PLAIN_MESSAGE;

	private JOptionPane jOptionPane;

	public SimpleInfoDialog(Frame parent, String title, boolean bModal, String message, int type)
	{
		super(parent, title, bModal);
		final Object[] buttonLabels = { Translatrix.getTranslationString("DialogClose") };
		jOptionPane = new JOptionPane(message, type, JOptionPane.DEFAULT_OPTION, null, buttonLabels, buttonLabels[0]);

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
					setVisible(false);
				}
			}
		});

		this.pack();
		int centerX = (int)(((parent.getSize().getWidth()  / 2) + parent.getLocation().getX()) - (this.getSize().getWidth()  / 2));
		int centerY = (int)(((parent.getSize().getHeight() / 2) + parent.getLocation().getY()) - (this.getSize().getHeight() / 2));
		if(centerX < 0) { centerX = 0; }
		if(centerY < 0) { centerY = 0; }
		this.setLocation(centerX, centerY);
		this.show();
	}

	public SimpleInfoDialog(Frame parent, String title, boolean bModal, String message)
	{
		this(parent, title, bModal, message, WARNING);
	}
}
