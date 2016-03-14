package com.jpmorgan.http.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.jpmorgan.stock.service.impl.JPMSAbstractAgent;
/**
 * Basic Implementation of an HTTP Server
 * 
 * @author ccarlenzoli
 *
 */
public class HttpServer extends  JPMSAbstractAgent {

	
    private ServerSocket mainSocket;
    private OneArgumentBlock<String, String> requestHandler;

	   
    public HttpServer(int port,OneArgumentBlock<String, String>requestHandler) throws IOException {
    	mainSocket = new ServerSocket(port);
    	this.requestHandler = requestHandler;
    }
    
  
    
    @Override
    protected void doPrepareForRun() {
    	try {
			mainSocket.setSoTimeout(100);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
    	
    }
	
    @Override
    protected void doStep() {
    	try {
            Socket socket = mainSocket.accept();
            if ( socket != null){
            	HttpRequestHandler requestThread = new HttpRequestHandler(socket,requestHandler);
            	requestThread.start("HTTP-REQ-HANDLER-"+Thread.currentThread().getName() + "-" + System.currentTimeMillis());
            }
        }
        catch (IOException e) {
            //System.exit(1);
        }
    	
    }
    
    @Override
    protected void doPostRun() {
    
    	
    }
    
	protected boolean mustSleepBetweenSteps() {
		return false;
	}

	@Override
	public String getJPMSAgentDescription() {
		
		return "HTTP Server";
	}
	
  
  

}
