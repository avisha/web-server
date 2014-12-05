package com.adobe.web.readers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Hashtable;




import org.apache.log4j.Logger;

import com.adobe.web.server.MalformedRequestException;
import com.adobe.web.utils.WebServerConstants;

/**
 * This class is used as a base class for handling HTTP requests
 * @author avijain
 *
 */
public class GenericHTTPRequestReader {

	private static Logger logger = Logger
			.getLogger(GenericHTTPRequestReader.class.getName());
	public static BufferedInputStream inputStream;
	public static BufferedOutputStream outputStream;
	public static BufferedWriter charStreamOutput;

	/**
	 * this is the constructor for intializing the streams connected to client
	 * @param bufferedByteInputStream
	 * @param bufferedByeOutputStream
	 * @param bufferedCharOutputStream
	 */
	public GenericHTTPRequestReader(
			BufferedInputStream bufferedByteInputStream,
			BufferedOutputStream bufferedByeOutputStream,
			BufferedWriter bufferedCharOutputStream) {
		inputStream = bufferedByteInputStream;
		outputStream = bufferedByeOutputStream;
		charStreamOutput = bufferedCharOutputStream;

	}

	/**
	 * this method is used for handling the headers
	 * @return a hashtable of headers
	 * @throws IOException
	 */
	public static Hashtable<String, String> getAllHeaders() throws IOException,MalformedRequestException{

		String requestHeaders = HeaderHandler.headerReader(inputStream);
		Hashtable<String, String> headersList = HeaderHandler
				.getclientHeadersinHashTable(requestHeaders);

		return headersList;

	}

	/**
	 * this  method is used for decoding the request URI 
	 * @param requestURI
	 * @return null if the uri cannot be decoded
	 * @throws IOException
	 */
	public static String decodeURI(String requestURI) throws IOException {
		String decodedURI = null;
		try {
			decodedURI = URLDecoder.decode(requestURI,
					WebServerConstants.ENCODING);
		} catch (UnsupportedEncodingException e2) {
			 logger.error("request uri could not decoded . " +e2.getMessage());
			Reader.serverFormattedResponseToClient("500",
					"Internal Server Error", "url could not be decoded",
					charStreamOutput, outputStream);
		}

		return decodedURI;

	}

}
