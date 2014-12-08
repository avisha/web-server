=== Webserver 1.0 ===
Author: Avisha Jain

Webserver 1.0 is a simple HTTP webserver implementation in java.   

== Description ==

Current version of the Webserver support handling the HTTP rfc specified requests namely GET and POST request
 

== Prerequisites ==

Java Runtime Environment(JRE) 7
- The application has been run and found to have to issues with JRE 7.

== Features ==

Support handling the HTTP rfc specified requests namely GET and POST request

Server maintains a request queue which can be bounded to have an upper limit to capacity. Thread pooling has been implemented to handle multiple requests with multiple threads. 
Keep-alive connection is not yet implemented.It is kept for further extension.

this server handles multipart/form-data requests.
The Webserver supports get and post requests. Since a server can decide on its own to handle a particular request, given below is the server behaviour to different requests:
	1. For "general" GET requests the Server looks for the specified file present at the document root, acting as a simple file server.  Also,if the requested uri asks for directory then all the files in the directory are listed on a page
	
	3. For any "general" POST request the server reads the post data parses it on the basis of specified encoding. But the data is ignored and the requested url is served same as a GET request.
	Also the server handles any multipart/form-data requests containing multiple file uploads.Rest can be extended further
	
	== Usage ==

To simply run this server 
->click run.bat file in windows

Java command to run the server is 
java -cp webserver.jar com.adobe.web.server.HttpWebServerRun

The server will look for a config.properties and log.properties in the current directory where the jar is run. You can copy the example configuration file to use configure the srever 

Example Config file:
###########################################

HostName=localhost
Port=9191
ThreadPoolSize=10
HostPath=C:\\Users\\avijain\\web-server\\src\\test\\resources\\testFiles
UploadPath=directory
requestQueueMaxSize=10000
requestTimeOut=1000
readTimeOut=1000

###########################################

Guide to Configuration:
HostName-the server ip
Port=the port on which it is run
ThreadPoolSize-the number of threads in the pool
requestQueueMaxSize-the request queue size
HostPath-the root path
UploadPath-the directory where file is to be uploaded


Example log.properties file:
###########################################

# Root logger option
log4j.rootLogger=INFO, file

# Direct log messages to a log file

log4j.appender.file=org.apache.log4j.RollingFileAppender
log4j.appender.file.File=C:\\Users\\avijain\\web-server\\loging.log
log4j.appender.file.MaxFileSize=1MB
log4j.appender.file.MaxBackupIndex=1
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} - %m%n
###########################################
# Set root logger level to DEBUG and its only appender to A1.
log4j.rootLogger=trace,A2,A1
log4j.logger.org.apache.http.wire=info,A2
log4j.logger.org.apache.http.headers=info,A2

# A1 is set to be a ConsoleAppender.
log4j.appender.A1=org.apache.log4j.ConsoleAppender
#log4j.appender.A1.Target=System.out
log4j.appender.A2=org.apache.log4j.RollingFileAppender
log4j.appender.A2.File=logs//server.log

# A1 uses PatternLayout.
log4j.appender.A1.layout=org.apache.log4j.PatternLayout
log4j.appender.A1.layout.ConversionPattern="%L %r [%t] %-5p %c - %m%n"


log4j.appender.A2.layout=org.apache.log4j.PatternLayout
log4j.appender.A2.layout.ConversionPattern="%L %r [%t] %-5p %c - %m%n"


== Dependencies ==

The Webserver code uses log4j for its logging requirements. The used version is log4j-1.2.17. 
The required dependencies have been packages within the jar file shipped, so they do not need to be installed along with.

== Limitations ==
Webserver currently does not allows you to add/change any request handling logic. 
Due to limitations of current implementaion of Javadoc, the javadocs could not be created for classes which use generics.
the webserver doesnot suppport PUT,HEAD and DELETE request
Also keep alive functionality is no yet implemented


== Sources ==

The server has been built upon the Java socket implementation. The protocol standards have been adopted as per specified in the standard RFC documentation.
Standard code for KMP has been adapted for  for byte search from an implementation  available at http://tekmarathon.wordpress.com/2013/05/14/algorithm-to-find-substring-in-a-string-kmp-algorithm/.
Maven was been used for project management and packaging.



	
	
	
	
