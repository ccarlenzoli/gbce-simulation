package com.jpmorgan.stock.service.model.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jpmorgan.stock.calculator.JPMSStockTradeSynthAmount;
import com.jpmorgan.stock.model.JPMSStockTrade;
import com.jpmorgan.stock.model.JPMSStockTradeTypeEnum;
import com.jpmorgan.stock.queue.JPMSQueue;
import com.jpmorgan.stock.service.impl.JPMSAbstractAgent;


public class JPMSStockTradeEnricher extends JPMSAbstractAgent {

	

	private Map<String,JPMSStockTradeSynthAmount> tradesAmount;
	private List<String> stockSymbols;
	private JPMSQueue stockQueue;
	

	

	public static JPMSStockTradeEnricher create(JPMSQueue stockQueue,Map<String,JPMSStockTradeSynthAmount> tradesAmount) {
		JPMSStockTradeEnricher enricher = new JPMSStockTradeEnricher(stockQueue,tradesAmount);
		return enricher;
	}
	
	public JPMSStockTradeEnricher(JPMSQueue stockQueue,Map<String,JPMSStockTradeSynthAmount> tradesAmount) {
		this.stockQueue = stockQueue;
		this.tradesAmount = tradesAmount;
		stockSymbols = new ArrayList<String>(tradesAmount.keySet());
	}
	
	
	private int getRandomNumberFromTo(int min, int max){
		return min+(int)Math.floor(Math.random() * (1+max-min));
	}
	
	@Override
	public String getJPMSAgentDescription() {
		
		return "ENRICHER";
	}
	
	
	@Override
	protected void doPrepareForRun() {

	}
	
	@Override
	protected void doStep() {
		//Random values
		String stockSymbol = stockSymbols.get(getRandomNumberFromTo(0, stockSymbols.size()-1));
		int tradTypeR = getRandomNumberFromTo(0, 1);
		JPMSStockTradeTypeEnum tradeType = null;
		JPMSStockTradeSynthAmount tradeAmount = tradesAmount.get(stockSymbol);
		BigDecimal cPrice = tradeAmount.getStock().getStockMeanValue(); //TEST Purpose
//		cPrice = tradeAmount.getStock().getTickerPrice();
		double delta = 0.01;
		BigDecimal nPrice = null;
		BigDecimal shares = BigDecimal.valueOf(getRandomNumberFromTo(1, 10));
//		shares = BigDecimal.ONE;
		if ( tradTypeR == 0){
			nPrice = cPrice.add(BigDecimal.valueOf(delta));
			tradeType = JPMSStockTradeTypeEnum.BUY;
		} else {
			nPrice = cPrice.subtract(BigDecimal.valueOf(delta));
			if ( nPrice.compareTo(BigDecimal.ONE) < 0){//Avoid to go under a limit...
				nPrice = BigDecimal.ONE;
			}
			tradeType = JPMSStockTradeTypeEnum.SELL;			
		}
		
		
		//Time could be avoided...
		stockQueue.addStockTrade(new JPMSStockTrade().withStockSymbol(stockSymbol).withTradeType(tradeType).withPrice(nPrice).withShares(shares).withTimestamp(System.currentTimeMillis()));
		
	}
	
	@Override
	protected void doPostRun() {
	
	}
	
	


	
}
