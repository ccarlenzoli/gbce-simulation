package com.jpmorgan.stock.service.impl;

import java.io.IOException;

import com.jpmorgan.http.server.HttpServer;
import com.jpmorgan.http.server.OneArgumentBlock;
import com.jpmorgan.stock.market.JPMSStockMarket;
import com.jpmorgan.stock.service.protocol.JPMSAgent;
import com.jpmorgan.stock.service.protocol.JPMSStockObserverFactory;

public class JPMSHttpServerFactory implements JPMSStockObserverFactory {

	private final static String PRE_DESCRIPTION_HTML_PAGE = "<html><head></head><body><form action=''>";
	private final static String POST_DESCRIPTION_HTML_PAGE = "<br/><input type='submit' value='Refresh'></form></body></html>";
	
	private final int httpPort;
	
	public JPMSHttpServerFactory(int httpPort) {
		this.httpPort = httpPort;
	}
	
	
	@Override
	public JPMSAgent createStockMarketObserver(final JPMSStockMarket stockMarket) {
		
		HttpServer server;
		try {
			server = new HttpServer(httpPort,new OneArgumentBlock<String, String>() {
				
				@Override
				public String execute(String queryString) {
					if ( queryString.startsWith("/stockMarketStatus")){
						
						return getStandardHtmlResponsePageWithDescription(stockMarket.getCurrentStatusDescription());
						
					}
					return "REQUEST NOT UNDERSTOOD";
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

        return server;
    }


	private String getStandardHtmlResponsePageWithDescription(String description) {
		StringBuffer buffer = new StringBuffer();
		//FAST CODE FOR ANSWER
		buffer.append(PRE_DESCRIPTION_HTML_PAGE);
		buffer.append(description.replace("\n", "<br/>"));
		buffer.append(POST_DESCRIPTION_HTML_PAGE);
		
		return buffer.toString();
	}

}
