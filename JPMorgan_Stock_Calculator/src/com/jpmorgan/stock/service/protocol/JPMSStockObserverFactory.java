package com.jpmorgan.stock.service.protocol;

import com.jpmorgan.stock.market.JPMSStockMarket;

public interface JPMSStockObserverFactory {

	JPMSAgent createStockMarketObserver(JPMSStockMarket stockMarket);
	
}
