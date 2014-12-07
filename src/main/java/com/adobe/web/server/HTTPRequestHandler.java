package com.adobe.web.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.adobe.web.client.WebClient;
import com.adobe.web.utils.WebServerConstants;

/**
 * this class will handle the incoming request for client a pool of thread is
 * created When a request arrives the request is queued in the requestQueue and
 * then it is served by one of the threads in the threadPool
 * 
 * @author avijain
 * 
 */
public class HTTPRequestHandler {

	private static Thread[] httpWebServerThread = new Thread[WebServerConstants.THREAD_POOL_SIZE];
	private static Logger logger = Logger.getLogger(HTTPRequestHandler.class
			.getName());
	private static boolean[] hasRequest = new boolean[WebServerConstants.THREAD_POOL_SIZE];
	private static long[] lastSeenAt = new long[WebServerConstants.THREAD_POOL_SIZE];
	private static Queue<Socket> httpRequestQueue = new LinkedList<Socket>();
	public ServerSocket serverSocket;
	private static int requestArrived = 0;
	private static Integer requestServed = 0;

	public class HTTPRequestSchedulerThread implements Runnable {

		@SuppressWarnings("deprecation")
		public void run() {
			long current;
			try {

				while (true) {
					current = System.currentTimeMillis();
					for (int i = 0; i < WebServerConstants.THREAD_POOL_SIZE; i++) {
						if (hasRequest[i]
								&& (current - lastSeenAt[i]) > WebServerConstants.requestTimeOut) {
							httpWebServerThread[i].stop(new Exception());
						}
					}
					Thread.sleep(10);
				}
			} catch (InterruptedException e) {
				logger.error("an error has occured while it is interrupted"
						+ e.getMessage());
			}
		}
	}

	public HTTPRequestHandler() {

	}

	/**
	 * This class will assign the request from the requestQueue to be handled by
	 * the WebClient
	 * 
	 */
	public class HTTPSchedulerThread implements Runnable {
		private int threadID;
		Socket socket;

		public HTTPSchedulerThread(int i) {
			threadID = i;

		}

		@Override
		public void run() {

			while (true) {
				try {

					synchronized (httpRequestQueue) {
						while (httpRequestQueue.isEmpty())
							httpRequestQueue.wait();
						socket = httpRequestQueue.poll();
						System.out.println("EXCEUOR"+Thread.currentThread().getName());
						WebClient client = new WebClient(socket);
						client.handleClient();
						httpRequestQueue.notify();
					}

					socket.setSoTimeout(WebServerConstants.requestTimeOut);
					hasRequest[threadID] = true;
					
					socket.setSoTimeout(WebServerConstants.requestTimeOut);
				

						try {
							lastSeenAt[threadID] = System.currentTimeMillis();

							synchronized (requestServed) {
								requestServed++;

							}

						} catch (Exception e) {

							hasRequest[threadID] = false;
							Thread.interrupted();
						}

						socket.close();
					}
				catch (IOException e) {
					}

				catch (InterruptedException e) {

					logger.error("the error has occured" + e.getMessage());
				}
			}
		}
	}

	/**
	 * This method will start the server at the mentioned port and will throw an
	 * exception if the server cannot be started.
	 */
	public void serverStart() {
		try {
			serverSocket = new ServerSocket(WebServerConstants.PORT);
			logger.info("Starting server on port " + WebServerConstants.PORT);
		} catch (Exception e) {
			logger.info("Could not start server on port: "
					+ WebServerConstants.PORT + "The port number :"
					+ WebServerConstants.PORT
					+ "may be already in use.Try with other port");
			System.exit(0);
		}
		logger.trace("Server is started on the port " + WebServerConstants.PORT);

		// Initializing the threads in the threadPool

		for (int i = 0; i < WebServerConstants.THREAD_POOL_SIZE; i++) {
			httpWebServerThread[i] = new Thread(new HTTPSchedulerThread(i));
			httpWebServerThread[i].start();
		}
		// starting the schduler thread that is scheduling the threads in the
		// threadPool

		Thread httpRequestSchedulerThread = new Thread(
				new HTTPRequestSchedulerThread());
		httpRequestSchedulerThread.start();

		while (true) {
			try {
				Socket socket = serverSocket.accept();
				requestArrived++;

				synchronized (httpRequestQueue) {
					while (httpRequestQueue.size() >= WebServerConstants.QUEUE_SIZE)
						httpRequestQueue.wait();
					httpRequestQueue.add(socket);
					httpRequestQueue.notify();
				}

			} catch (Exception e) {
				if (serverSocket.isClosed())
					System.out.println("Server Stopped");
				// Stop();
				return;
				// System.exit(0);
			}
		}
	}

	/**
	 * this will stop the server and all the threads in the thread pool are
	 * stopped
	 * 
	 */
	public void serverStop() {
		for (int i = 0; i < WebServerConstants.THREAD_POOL_SIZE; i++) {
			httpWebServerThread[i].stop();
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			logger.error("error in closing the socket" + e.getMessage());
		}
	}
}
