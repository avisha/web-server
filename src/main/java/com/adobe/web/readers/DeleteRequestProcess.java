package com.adobe.web.readers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import com.adobe.web.client.WebClient;
import com.adobe.web.server.MalformedRequestException;
import com.adobe.web.utils.WebServerConstants;

public class DeleteRequestProcess extends GenericHTTPRequestReader  {
	 public DeleteRequestProcess(BufferedInputStream bufferedByteInputStream,BufferedOutputStream bufferedByeOutputStream,BufferedWriter bufferedCharOutputStream){
			
		 super(bufferedByteInputStream,bufferedByeOutputStream,bufferedCharOutputStream);
		 System.out.println("in get handler constructor");


			}
	 
	 public static void handleDeleteRequest(String requestUri) throws IOException, MalformedRequestException{
		 
	 
	 Hashtable<String, String> getHeaders=getAllHeaders();
		String decodedUri=decodeURI(requestUri);
		System.out.println("decoded uri"+decodedUri);
		if(decodedUri==null){
			return;
		}
		if(decodedUri.equals(WebServerConstants.URISeparator)){
			
			Reader.serverFormattedResponseToClient("404","Resource not found","the file you requested - " + decodedUri
					+ " does not exist on server" + "<hr>",charStreamOutput,outputStream);
				
		}
		String resourcePath=requestedPath(decodedUri );
		System.out.println("resourcePath=="+resourcePath);
		File file=new File(resourcePath);
		System.out.println("file length is =="+ file.length());
		if(file==null){
			System.out.println("file length"+file.length());
			Reader.serverFormattedResponseToClient("404", "File Not Found","the file you requested - " + decodedUri
					+ " does not exist on server" + "<hr>", charStreamOutput, outputStream);
	
			//logger.error("the requested file doesnot exists"
				//	);
			
			return;
		}
	
	 
	 
	 if(file.isDirectory()){
		 
		 return;
	 }
	 
	 if(file.delete()){
		 
		 System.out.println("file is deleted");
		 return;
		 
	 }
	
		 
		 
	 }
	 
	 
	 
	 
	 public static String requestedPath(String requestUri){
			String resourcePath=null;
			StringBuffer outputResource=new StringBuffer(50);
			
			if(requestUri.equals("/")){
				resourcePath=WebServerConstants.HOSTPATH+ File.separator+"index.html";
				return resourcePath;
			}
			else{
				
			
				String pathList[]=requestUri.split("/");
				for(int i=0;i<pathList.length && pathList[i]!=null && pathList[i].length()>0;i++){
				outputResource.append(File.separator+pathList[i]);	
					
				}
				resourcePath=WebServerConstants.HOSTPATH+outputResource.toString();
				
				
			}	
			
			return resourcePath;
				
			
		}

}
