package com.adobe.web.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;



import org.apache.log4j.Logger;

import com.adobe.web.utils.WebServerConstants;

/**
 * this class will create a poll of thread to handle the requests
 * @author avijain
 *
 */
public class ThreadPoolProvider {
	
	private static int shutdownTime=60;
	private static Logger logger=Logger.getLogger(ThreadPoolProvider.class.getName());
	
	public static ExecutorService createThreadPool()
	{
		ThreadPoolExecutor threadPool=(ThreadPoolExecutor)Executors.newFixedThreadPool(WebServerConstants.THREAD_POOL_SIZE);
		return threadPool;
		
	}
	public static void shutdownAndAwaitTermination(ExecutorService pool) {
		   pool.shutdown(); // Disable new tasks from being submitted
		   try {
		     
		     if (!pool.awaitTermination(shutdownTime, TimeUnit.SECONDS)) {
		       pool.shutdownNow(); // Cancel currently executing tasks
		       // Wait a while for tasks to respond to being cancelled
		       if (!pool.awaitTermination(shutdownTime, TimeUnit.SECONDS))
		           System.err.println("Pool did not terminate");
		     }
		   } catch (InterruptedException ie) {
			   logger.error("the thread creation is interrupted");
		    
		     pool.shutdownNow();
		     // Preserve interrupt status
		     Thread.currentThread().interrupt();
		   }
		 }
		 
	
	

}
