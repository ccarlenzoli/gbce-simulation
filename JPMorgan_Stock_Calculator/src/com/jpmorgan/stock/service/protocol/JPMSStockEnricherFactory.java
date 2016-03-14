package com.jpmorgan.stock.service.protocol;

import java.util.List;

import com.jpmorgan.stock.calculator.JPMSStockTradeCalculator;
import com.jpmorgan.stock.queue.JPMSQueue;

public interface JPMSStockEnricherFactory {

	List<JPMSAgent> createStockMarketEnrichers(JPMSQueue queue,JPMSStockTradeCalculator tradeCalculator );
	
}
