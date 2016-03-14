package com.jpmorgan.http.server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

public class HttpRequestHandler {

	
	private static final String HTML_TEXT_MIME = "text/html";
	private OneArgumentBlock<String, String> requestHandler;
	private final Socket socket;
	private Thread runningThread;
	

		
	public HttpRequestHandler(Socket socket, OneArgumentBlock<String, String> requestHandler) {
		this.socket = socket;
		this.requestHandler = requestHandler;
	}
	    
	/**
	 * TODO Take from Internet. It needs to be written in a better way 
	 * @param out
	 * @param code
	 * @param contentType
	 * @param contentLength
	 * @param lastModifiedDate
	 * @throws IOException
	 */
    private static void sendHttpHeader(OutputStream out, int code, String contentType, long contentLength, Date lastModifiedDate) throws IOException {
        out.write(("HTTP/1.0 " + code + " OK\r\n" + 
                   "Date: " + new Date().toString() + "\r\n" +
                   "Server: JPMorgan HTTP Server/1.0\r\n" +
                   "Content-Type: " + contentType + "\r\n" +
                   "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" +
                   ((contentLength != -1) ? "Content-Length: " + contentLength + "\r\n" : "") +
                   "Last-modified: " + lastModifiedDate.toString() + "\r\n" +
                   "\r\n").getBytes());
    }
	    
    private static void sendTextResponse(OutputStream out, int code, String message) throws IOException {
    	sendHttpHeader(out, code, HTML_TEXT_MIME, message.length(), new Date());
        out.write(message.getBytes("UTF-8"));
        out.flush();
        out.close();
    }
    
    
	/**
	 * Perform all operations needed to manage Trades
	 * @param processName
	 */
	public void start(String processName){
		System.out.println(processName+" STARTING at "+new Date());
		runningThread = new Thread(new Runnable() {
			
			@Override
			public void run() {

				runRequestThread();
		
				System.out.println(runningThread.getName()+" FINISHED at "+new Date());
				runningThread = null;
				
			}

	
		});
		runningThread.setName(processName);
		//runningThread.setDaemon(true); Wait for stop process
		runningThread.start();
		
		
		
	}
	    
    private void runRequestThread() {
      
        try {
            socket.setSoTimeout(30000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStream out = new BufferedOutputStream(socket.getOutputStream());
            
            String request = in.readLine();
            if (request == null || !request.startsWith("GET ") || !(request.endsWith(" HTTP/1.0") || request.endsWith("HTTP/1.1"))) {
                // Invalid request type (no "GET")
            	sendTextResponse(out, 500, "Invalid Method.");
                return;
            }   
            StringTokenizer tokenizer = new StringTokenizer(request);
            //METH
            tokenizer.nextToken();
            
            String message = null;
            if( tokenizer.hasMoreElements() ){
            	message = tokenizer.nextToken();
            }
        	if ( message == null){
        		message = "";
        	}
        	
        	sendTextResponse(out, 200, requestHandler == null ? "NO HANDLER DEFINED FOR QUERY: " + message : requestHandler.execute(message));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
		
		 
  
  

}
