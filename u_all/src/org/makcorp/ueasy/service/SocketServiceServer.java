/**
 *  uEasy (http://ueasy.berlios.de)
 *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)
 *  This program is distributed under the terms of the GNU General Public License
 *  See the whole license text in the "LICENSE" file.
 */

package org.makcorp.ueasy.service;

import java.net.ServerSocket;
import java.io.IOException;
import org.apache.log4j.Logger;
import java.util.Properties;
import java.io.FileInputStream;

/*
 * This ServiceServer is based on socket, you can stop/reload/test the services by sending signals to the socket.
 * You should carefully choose one of its extensions knowing which security scheme you want for the service server.
 * Use org.makcorp.ueasy.service.SocketServiceClient to communicate with it.
 * TODO: By now it can only handle one Service, it would be nice if it were able to handle several Services.
 */

abstract class SocketServiceServer extends Thread implements ServiceServer
{
	public static String PROPERTIES_FILE="socketserviceserver.properties";
	protected Properties p = null;
	
	private int port;
	private Service service;
	private Command command;
	
	/** logging utility */
	Logger log = Logger.getRootLogger();
	
	public SocketServiceServer ()
	{
		super("ServiceServer");
		p = new Properties();
		try {
			p.load(new FileInputStream(PROPERTIES_FILE));
		}
		catch (java.io.FileNotFoundException e)
		{
			log.fatal ("File "+PROPERTIES_FILE+" not found.", e);
			System.exit(1);
		}
		catch (java.io.IOException e)
		{
			log.fatal ("I/O error while reading "+PROPERTIES_FILE, e);
			System.exit(1);
		}
		String portString = p.getProperty ("port");
		try {
			this.port = Integer.parseInt(portString);
		}
		catch (NumberFormatException e)
		{
			log.fatal ("Wrong port format:"+port, e);
			System.exit(1);
		}
		this.initializeAuth();
		log.info ("SocketServiceServer initialized on port"+port);
	}

	public void addService(Service service)
	{
		this.service = service;
	}

	public void run()
	{
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
		}
		catch (IOException e)
		{
			log.fatal ("Could not listen on port: "+port, e);
			System.exit(1);
		}

		command = new Command();
		while (command.accepts())
		{
			log.debug ("ACCEPTS");
			try {
				new SocketServiceServerThread(serverSocket.accept(), command, this.getAuth()).start();
			}
			catch (IOException e)
			{
				log.error ("accepting socket", e);
			}
			
			switch (command.intValue())
			{
				case Command.TEST:
					service.serviceTest();
					break;
				case Command.RELOAD:
					service.serviceReload();
					break;
				case Command.STOP:
					service.serviceStop();
			}
		}
		try {
			serverSocket.close();
		}
		catch (IOException e)
		{
			log.error ("closing socket", e);
		}
	}

	/** Authentication operations on start-up, usually storing passwords */
	abstract void initializeAuth();
	/** get an instance of the authentication object which can be used to authenticate users */
	abstract Auth getAuth();
}
