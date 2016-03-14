package com.jpmorgan.stock.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jpmorgan.stock.calculator.JPMSStockTradeCalculator;
import com.jpmorgan.stock.model.JPMSStock;
import com.jpmorgan.stock.service.protocol.JPMSStockProvider;

public class JPMSStockMarket {

	private final static String STOCK_SEPARATOR = "--------------------------------------------------";
	
	private Map<String, JPMSStock> stockMap = new HashMap<String, JPMSStock>();
	
	public JPMSStockMarket(JPMSStockProvider stockProvider) {
		
		while ( stockProvider.hasMoreStock()){
			addStock(stockProvider.takeStock());
		}
	}

	private void addStock(JPMSStock stock){
		stockMap.put(stock.getSymbol(), stock);
	}
	
	public Collection<JPMSStock> getAllStocks() {
		
		return stockMap.values();
	}
	
	public JPMSStock getStockForSymbol(String stockSymbol) {
		return stockMap.get(stockSymbol);
	}
	

	public Map<String, BigDecimal> getStockSymbolsAndCurrentPrice() {
		Map<String,BigDecimal> stockSymbolsAndCurrentPrice = new HashMap<String, BigDecimal>();
		for ( JPMSStock stock : stockMap.values()){
			stockSymbolsAndCurrentPrice.put(stock.getSymbol(), stock.getStockMeanValue()); //TODO Scaling?..rules for scaling?
		}
		return stockSymbolsAndCurrentPrice;
	}

	public String getCurrentStatusDescription() {
		StringBuffer buffer = new StringBuffer();
		
		List<String> keys = new ArrayList<String>(stockMap.keySet());
		Collections.sort(keys);
		double geoMean = 1.;
		int counter = 0;
		buffer.append(getStockSeparator());
		buffer.append("\n");
		buffer.append(getStockSeparator());
		buffer.append("\n");
		for ( String symb : keys){
			JPMSStock stock = stockMap.get(symb);
			buffer.append(stock.getCurrentStatusDescription());
			buffer.append("\n");
			buffer.append(getStockSeparator());
			buffer.append("\n");
			double mv = stock.getStockMeanValue().doubleValue();
			if ( mv > JPMSStockTradeCalculator.EPS){
				geoMean *= mv;
				counter++;
			}
			
		}
		buffer.append(getStockSeparator());
		buffer.append("\n");
		
		
		if ( counter == 0){
			buffer.append("UNABLE TO CALCULATE GBCE All Share Index");
		} else {
			buffer.append("GBCE All Share Index: " + Math.pow(geoMean, 1./counter));
		}
		buffer.append("\n");
		
		buffer.append(getStockSeparator());
		buffer.append("\n");
		buffer.append(getStockSeparator());
		buffer.append("\n");
		
		
		return buffer.toString();
	}

	private String getStockSeparator() {
		
		return STOCK_SEPARATOR;
	}




	
}
