package com.jpmorgan.stock.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.jpmorgan.stock.calculator.JPMSStockTradeCalculator;
import com.jpmorgan.stock.queue.JPMSQueue;
import com.jpmorgan.stock.service.model.impl.JPMSStockTradeEnricher;
import com.jpmorgan.stock.service.protocol.JPMSAgent;
import com.jpmorgan.stock.service.protocol.JPMSStockEnricherFactory;

public class JPMSRandomStockEnricherFactory implements JPMSStockEnricherFactory{
	
	
	private final int numberOfInitialSessions;
	
	public JPMSRandomStockEnricherFactory(int numberOfInitialSessions) {
		this.numberOfInitialSessions = numberOfInitialSessions; 
	}
	
	@Override
	public List<JPMSAgent> createStockMarketEnrichers(JPMSQueue queue,JPMSStockTradeCalculator tradeCalculator ) {
		List<JPMSAgent> enrichers = new ArrayList<JPMSAgent>();			
		for ( int i = 0 ; i < numberOfInitialSessions ; i++){
			//Test data needed for run 
			enrichers.add(JPMSStockTradeEnricher.create(queue,tradeCalculator.getTradesAmount()));
		}
		return enrichers;
	}

}
