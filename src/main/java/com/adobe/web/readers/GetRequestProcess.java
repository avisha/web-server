package com.adobe.web.readers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import com.adobe.web.server.MalformedRequestException;
import com.adobe.web.utils.ResponseCodeParams;
import com.adobe.web.utils.WebServerConstants;

/**
 * This class is used for handling the get requests from client
 * 
 * @author avijain
 * 
 */
public class GetRequestProcess extends GenericHTTPRequestReader {

	public Hashtable<String, String> getFields;
	private static Logger logger = Logger.getLogger(GetRequestProcess.class
			.getName());

	/**
	 * It initializes the stream connected to client
	 * 
	 * @param bufferedByteInputStream
	 * @param bufferedByeOutputStream
	 * @param bufferedCharOutputStream
	 */
	public GetRequestProcess(BufferedInputStream bufferedByteInputStream,
			BufferedOutputStream bufferedByeOutputStream,
			BufferedWriter bufferedCharOutputStream) {

		super(bufferedByteInputStream, bufferedByeOutputStream,
				bufferedCharOutputStream);

	}

	/**
	 * this method will handle the get requests by processing headers and
	 * returning the responses
	 * 
	 * @param requestUri
	 * @throws IOException
	 * @throws MalformedRequestException
	 */
	public void handleGetRequest(String requestUri) throws IOException,
			MalformedRequestException {

		Hashtable<String, String> getHeaders = getAllHeaders();
		String decodedUri = decodeURI(requestUri);
		
		if (decodedUri == null) {
			logger.error("the decoded uri is null");
			return;
		}

		String resourcePath = requestedPath(decodedUri); // This is the absolute
															// path for the
															// resource
		File file = new File(resourcePath);
		if (file == null) {

			Reader.serverFormattedResponseToClient("404", "File Not Found",
					"the file you requested - " + decodedUri
							+ " does not exist on server" + "<hr>",
					charStreamOutput, outputStream);

			logger.error("the requested file doesnot exists on the server.Regret for inconvenience");

			return;
		}

		if (file.isDirectory()) {
			processDirectories(file, decodedUri);
			return;

		}
		BufferedInputStream resourceStream = null;
		resourceStream = processResourceAsStream(file);
		if (resourceStream == null) {
			return;
		}
		Reader.clientResponseWithBody("200", "OK", file, charStreamOutput,
				outputStream);

	}

	/**
	 * This method will find the resource path
	 * 
	 * @param requestUri
	 * @return String denoting the resource path
	 */
	public static String requestedPath(String requestUri) {
		String resourcePath = null;

		if (requestUri.equals("/")) {
			resourcePath = WebServerConstants.HOSTPATH + File.separator
					+ "index.html";
			return resourcePath;
		} else {

			String pathList[] = requestUri
					.split(WebServerConstants.URISeparator);

			StringBuffer outputResource = new StringBuffer(50);
			for (int i = 0; i < pathList.length; i++) {
				if (pathList[i] != null && pathList[i].length() > 0)
					outputResource.append(File.separator + pathList[i]);
			}
			if(outputResource!=null){
				
			
			resourcePath = WebServerConstants.HOSTPATH
					+ outputResource.toString();
			logger.info("resource path i.e file path is "+resourcePath);
			}

		}

		return resourcePath;

	}

	/**
	 * this will handle the get requests for directory
	 * 
	 * @param file
	 * @param requestUri
	 * @throws FileNotFoundException
	 */
	/**
	 * @param file
	 * @param requestUri
	 * @throws FileNotFoundException
	 */
	public static void processDirectories(File file, String requestUri)
			throws FileNotFoundException {

		File[] fileArray = file.listFiles();
		StringBuffer htmlLinks = new StringBuffer(100);
		for (int i = 0; i < fileArray.length; i++) {
			htmlLinks.append("<a href=\"" + requestUri
					+ WebServerConstants.URISeparator + fileArray[i].getName()
					+ "\">" + fileArray[i].getName() + "</a></BR>");
		}
		Reader.serverFormattedResponseToClient(
				"200",
				"OK",
				"The location you requested is a folder. Please follow links below to browse through the files .. <hr>"
						+ htmlLinks.toString(), charStreamOutput, outputStream);
		return;
	}

	/**
	 * this will process the file as stream so that it can be handled further
	 * 
	 * @param file
	 * @return file resource
	 * @throws FileNotFoundException
	 */
	public static BufferedInputStream processResourceAsStream(File file)
			throws FileNotFoundException {
		BufferedInputStream fileResource = null;
		try {
			fileResource = new BufferedInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e1) {
			Reader.serverFormattedResponseToClient(
					ResponseCodeParams.FILE_NOT_FOUND, "File Not Found",
					"the file you requested - " + file.getName()
							+ " does not exist on server" + "<hr>",
					charStreamOutput, outputStream);
			logger.info("file requested does not exist - " + file.getName());

		}

		return fileResource;
	}

}
