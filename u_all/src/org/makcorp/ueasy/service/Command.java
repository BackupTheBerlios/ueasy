/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.service;

public class Command
{
	public final static int NONE   = 0;
	public final static int TEST   = 1;
	public final static int RELOAD = 2;
	public final static int STOP   = 3;

	
	private int command;
	
	public Command()
	{
		command = Command.NONE;
	}

	public void add (String s)
	{
		command = Command.NONE;
		
		if(s.equals("test"))		command = Command.TEST;
		else if (s.equals("reload"))	command = Command.RELOAD;
		else if (s.equals("stop"))	command = Command.STOP;
	}

	public void reset()
	{
		command = Command.NONE;
	}

	public boolean accepts()
	{
		return command != Command.STOP;
	}

	public int intValue()
	{
		return command;
	}
}
