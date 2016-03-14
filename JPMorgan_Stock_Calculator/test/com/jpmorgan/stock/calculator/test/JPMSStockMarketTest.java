package com.jpmorgan.stock.calculator.test;

import com.jpmorgan.stock.manager.JPMSStockMarketManager;
import com.jpmorgan.stock.service.impl.JPMSHttpServerFactory;
import com.jpmorgan.stock.service.impl.JPMSMemoryStockProvider;
import com.jpmorgan.stock.service.impl.JPMSRandomStockEnricherFactory;


public class JPMSStockMarketTest {

	/**
	 * Inject Providers and Factories...ONE MAY USE Spring or any other Service-Provider Framework
	 * @param args
	 */
	public static void main(String[] args) {
		
		JPMSStockMarketManager.doRunManager(
				new JPMSMemoryStockProvider(),
				new JPMSHttpServerFactory(9090),
				new JPMSRandomStockEnricherFactory(2), 
				3,
				100*60*1000L,2000L);
	}
	

}
