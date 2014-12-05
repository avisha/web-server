package com.adobe.web.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import com.adobe.web.client.WebClient;
import com.adobe.web.utils.WebServerConstants;


/**
 * This class listens to the request and forward the client request to WebClient
 * @author avijain
 *
 */
public class Handler implements Runnable {
	 private static Logger logger = Logger.getLogger(Handler.class.getName());
	ExecutorService threadPool;

	public Handler() {
		threadPool = ThreadPoolProvider.createThreadPool();
	}

	@Override
	public void run() {

		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket();
			serverSocket
					.bind((WebServerConstants.HOSTNAME != null) ? new InetSocketAddress(
							WebServerConstants.HOSTNAME,
							WebServerConstants.PORT) : new InetSocketAddress(
							WebServerConstants.PORT),
							WebServerConstants.QUEUE_SIZE);

		} catch (IOException e) {
			logger.error("the server socket cannot be created"+e.getMessage());

		}
		try	{
serverSocket.setSoTimeout(WebServerConstants.ServerSocketSoTimeout);
	} catch (SocketException e) {
		logger.fatal("timeout for server socket could not be set - \r\n"
				+ e.getMessage() + "\r\n" + e.toString());
	try {
			serverSocket.close();
		} catch (IOException e1) {
			logger.error("could not cloase server socket - " + e.getMessage());
		}
		return;
	}


		while (true) {
			
			Socket client = null;
			try {
				if(serverSocket.isClosed()){
				return;
			}
				
				client = serverSocket.accept();	
				

				WebClient request = new WebClient(client);
				this.threadPool.execute(request);

			} catch (SocketException e) {
			
						logger.error("client so timeout could not be set .. so closing client connection - "
								+ e.getMessage());
				try {
					client.close();
				} catch (Exception e1) {
					 logger.error("client could not be closed properly");
					
				}
			} catch (SocketTimeoutException e) {

				if (Thread.interrupted()) {

					try {
						serverSocket.close();
					} catch (IOException e1) {
						 logger.warn("serversocket.accept error occured - \r\n"
						+ e1.getMessage() + "\r\n" + e1.toString());
					}

					ThreadPoolProvider
							.shutdownAndAwaitTermination(this.threadPool);
					logger.info("exiting listener thread");
					return;
				}
				 logger.trace("server is idle .. no incoming request ..");
			} catch (IOException e) {
				logger.warn("  serversocket.accept error occured - \r\n"
				 + e.getMessage() + "\r\n" + e.toString());
			} 
			

		}
	}

}
