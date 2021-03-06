package com.adobe.web.readers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import com.adobe.web.utils.WebServerConstants;

/**
 * This class will process the headers and store them in the hashtable
 * 
 * @author avijain
 * 
 */
public class HeaderHandler {

	/**
	 * this class will read each line and determine if the message body is
	 * reached terminated by two CRLF's oderwise it will append each line to the
	 * String buffer
	 * 
	 * @param clientInput
	 * @return - a string containing all the header fields
	 * @throws IOException
	 */
	public static String headerReader(InputStream clientInput)
			throws IOException {

		StringBuffer clientHeaders = new StringBuffer(100);

		int character;
		int final_state = 4;
		int intermediate_state = 0;
		while (intermediate_state != final_state) {

			if ((character = clientInput.read()) > -1) {

				switch ((char) character) {

				case '\r':
					if (intermediate_state == 0) {
						intermediate_state = 1;
					} else if (intermediate_state == 2) {
						intermediate_state = 3;
					}
					break;
				case '\n':
					if (intermediate_state == 1) {
						intermediate_state = 2;
					} else if (intermediate_state == 3) {
						intermediate_state = 4;
					}
					break;
				default:
					intermediate_state = 0;
				}

				clientHeaders.append((char) character);
			}
		}
		if (intermediate_state != final_state) {

		}

		clientHeaders.deleteCharAt(clientHeaders.length() - 1);
		clientHeaders.deleteCharAt(clientHeaders.length() - 1);

		return clientHeaders.toString();

	}

	/**
	 * this method will create a Hashtable with the key value as header name and
	 * value as header value
	 * 
	 * @param clientHeaders
	 * @return -a hashtable with header name and header value as the key value
	 *         pair
	 */
	public static Hashtable<String, String> getclientHeadersinHashTable(
			String clientHeaders) {

		if (clientHeaders == null) {
			return null;
		}
		Hashtable<String, String> headerTable = new Hashtable<String, String>(
				50);
		// splitting the string with the regex as "\r\n"

		String[] headerList = clientHeaders.split(WebServerConstants.CRLF);

		int length = headerList.length;
		for (int i = 0; i < length && headerList[i] != null
				&& headerList[i].length() > 0; i++) {

			String property[] = getPropertyAndValue(headerList[i]);
			if (property != null)

			{
				// if the header name is already present then the second header
				// value is appended to the name
				if (headerTable.containsKey(property[0])) {
					String value = headerTable.get(property[0]) + ","
							+ property[1];

					headerTable.put(property[0], value);

				} else {
					headerTable.put(property[0], property[1]);

				}

			}
		}

		return headerTable;

	}

	/**
	 * This is basically used for spliiting the multipart header
	 * 
	 * @param header
	 * @return
	 */
	public static String[] getPropertyAndValue(String header) {

		header = header.replaceAll("\\s+", " ");
		String[] strArr = header.split(":", 2);

		if (strArr[0] != null && strArr[1] != null) {
			strArr[0] = strArr[0].trim();
			strArr[1] = strArr[1].trim();
			if (strArr[0].length() != 0 && strArr[1].length() != 0)
				return strArr;
		}
		return null;
	}

}
