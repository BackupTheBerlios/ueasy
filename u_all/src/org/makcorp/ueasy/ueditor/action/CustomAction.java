/*
GNU Lesser General Public License

CustomAction
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

package org.makcorp.ueasy.ueditor.action;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;

import org.makcorp.ueasy.ueditor.UEditor;
import org.makcorp.ueasy.ueditor.component.*;

import org.makcorp.ueasy.util.Translatrix;

/** Class for implementing custom HTML insertion actions
*/
public class CustomAction extends StyledEditorKit.StyledTextAction
{
	protected UEditor parentEkit;
	private   HTML.Tag htmlTag;
	private   Hashtable htmlAttribs;

	public CustomAction(UEditor ekit, HTML.Tag inTag, Hashtable attribs)
	{
		super("");
		parentEkit  = ekit;
		htmlTag     = inTag;
		htmlAttribs = attribs;
	}

	public CustomAction(UEditor ekit, HTML.Tag inTag)
	{
		this(ekit, inTag, new Hashtable());
	}

	public void actionPerformed(ActionEvent ae)
	{
		JTextPane parentTextPane = parentEkit.getTextPane();
		String selText = parentTextPane.getSelectedText();
		int textLength = -1;
		if(selText != null)
		{
			textLength = selText.length();
		}
		if(selText == null || textLength < 1)
		{
			SimpleInfoDialog sidWarn = new SimpleInfoDialog(parentEkit.getFrame(), "", true, Translatrix.getTranslationString("ErrorNoTextSelected"), SimpleInfoDialog.ERROR);
		}
		else
		{
			int caretOffset = parentTextPane.getSelectionStart();
			int internalTextLength = selText.length();
			String currentAnchor = "";
			// Somewhat ham-fisted code to obtain the first HREF in the selected text,
			// which (if found) is passed to the URL HREF request dialog.
			SimpleAttributeSet sasText = null;
			for(int i = caretOffset; i < caretOffset + internalTextLength; i++)
			{
				parentTextPane.select(i, i + 1);
				sasText = new SimpleAttributeSet(parentTextPane.getCharacterAttributes());
				Enumeration attribEntries1 = sasText.getAttributeNames();
				while(attribEntries1.hasMoreElements() && currentAnchor.equals(""))
				{
					Object entryKey   = attribEntries1.nextElement();
					Object entryValue = sasText.getAttribute(entryKey);
					if(entryKey.toString().equals(HTML.Tag.A.toString()))
					{
						if(entryValue instanceof SimpleAttributeSet)
						{
							Enumeration subAttributes = ((SimpleAttributeSet)entryValue).getAttributeNames();
							while(subAttributes.hasMoreElements() && currentAnchor.equals(""))
							{
								Object subKey = subAttributes.nextElement();
								if(subKey.toString().toLowerCase().equals("href"))
								{
									currentAnchor = ((SimpleAttributeSet)entryValue).getAttribute(subKey).toString();
									break;
								}
							}
						}
					}
				}
				if(!currentAnchor.equals("")) { break; }
			}
			parentTextPane.select(caretOffset, caretOffset + internalTextLength);
			SimpleAttributeSet sasTag  = new SimpleAttributeSet();
			SimpleAttributeSet sasAttr = new SimpleAttributeSet();
			if(htmlTag.toString().equals(HTML.Tag.A.toString()))
			{
				if(!htmlAttribs.containsKey("href"))
				{
					UserInputDialog uidInput = new UserInputDialog(parentEkit.getFrame(), Translatrix.getTranslationString("AnchorDialogTitle"), true, "href", currentAnchor);
					String newAnchor = uidInput.getInputText();
					if(newAnchor != null)
					{
						htmlAttribs.put("href", newAnchor);
					}
					else
					{
						parentEkit.repaint();
						return;
					}
				}
			}
			else if(htmlTag.toString().equals(HTML.Tag.FONT.toString()))
			{
				if(!htmlAttribs.containsKey("face"))
				{
					FontSelectorDialog fsdInput = new FontSelectorDialog(parentEkit.getFrame(), Translatrix.getTranslationString("FontDialogTitle"), true, "face", parentTextPane.getSelectedText());
					String newFace = fsdInput.getFontName();
					if(newFace != null)
					{
						htmlAttribs.put("face", newFace);
					}
					else
					{
						parentEkit.repaint();
						return;
					}
				}
			}
			if(htmlAttribs.size() > 0)
			{
				Enumeration attribEntries = htmlAttribs.keys();
				while(attribEntries.hasMoreElements())
				{
					Object entryKey   = attribEntries.nextElement();
					Object entryValue = htmlAttribs.get(entryKey);
					sasAttr.addAttribute(entryKey, entryValue);
					htmlAttribs.remove(entryKey);
				}
			}
			sasTag.addAttribute(htmlTag, sasAttr);
			parentTextPane.setCharacterAttributes(sasTag, false);
			parentEkit.refreshOnUpdate();
			parentTextPane.select(caretOffset, caretOffset + internalTextLength);
		}
	}

}

