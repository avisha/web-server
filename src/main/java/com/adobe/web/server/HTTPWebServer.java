package com.adobe.web.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.adobe.web.utils.WebServerConstants;

/**
 * 
 * This class is the main class for starting the server
 * 
 * @author avijain
 */

public class HTTPWebServer {

	private static Logger logger = Logger.getLogger(HTTPWebServer.class
			.getName());

	private static Thread handlerThread;

	/**
	 * This method will read properties from config file This will initialize
	 * the server properties
	 * 
	 * @param userProperties
	 * @throws IOException
	 */
	public void serverInitialiation(File userProperties)
			throws IOException {

		logger.info("Initializing server properties by reading from properties file");

		Properties properties = new Properties();
		InputStream inputStream = new FileInputStream(userProperties);
		properties.load(inputStream);
		WebServerConstants.PORT = Integer.parseInt(properties
				.getProperty("Port"));
		WebServerConstants.HOSTNAME = properties.getProperty("HostName");
		WebServerConstants.UPLOAD_PATH = properties.getProperty("UploadPath");
		WebServerConstants.HOSTPATH = properties.getProperty("HostPath");
		WebServerConstants.THREAD_POOL_SIZE = Integer.parseInt(properties
				.getProperty("ThreadPoolSize"));

	}

	/**
	 * This method will start the server
	 * 
	 * @throws InterruptedException
	 */
	public void serverStart() throws InterruptedException {

		Handler thread = new Handler();
		handlerThread = new Thread(thread);
		handlerThread.start();

		logger.trace("Server is started on the port " + WebServerConstants.PORT);
	}

	/**
	 * this method will stop the server
	 * 
	 * @throws InterruptedException
	 */
	public  void serverStop() throws InterruptedException {

		handlerThread.interrupt();
		handlerThread.join();

	}

	/**
	 * this is the main method which proceeds with server properties
	 * initialization,then server start and the server can be stopped by
	 * pressing a key
	 * 
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String args[]) {

		PropertyConfigurator.configure("log4j.properties");
		HTTPWebServer webServer=new HTTPWebServer();
		try {
			webServer.serverInitialiation(new File("config.properties"));
				
		} catch (FileNotFoundException fileNotFoundException) {
			logger.error("properties file could not be found"
					+ fileNotFoundException.getMessage());
			return;
		} catch (IOException ioException) {
			logger.error("properties file reading is causing an error"
					+ ioException.getMessage());
			return;
		}
		try {
			webServer.serverStart();
		} catch (InterruptedException interruptedException) {

			logger.error("thread is interrupted"
					+ interruptedException.getMessage());
			return;

		}
		logger.info("enter e for stopping the server");
		try {
			while ((char) System.in.read() != 'e')
				;
		} catch (IOException e) {
			logger.error("key pressed cant be read");

		}
		try {
			webServer.serverStop();
		} catch (InterruptedException e) {
			logger.error("server could not be stopped properly .. exiting thread");
			return;
		}

	}

}
