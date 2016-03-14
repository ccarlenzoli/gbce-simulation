package com.jpmorgan.stock.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.jpmorgan.stock.model.JPMSStock;
import com.jpmorgan.stock.model.JPMSStockTypeEnum;
import com.jpmorgan.stock.service.protocol.JPMSStockProvider;

public class JPMSMemoryStockProvider implements JPMSStockProvider {

	private List<JPMSStock> stocks = new ArrayList<>();
	private Iterator<JPMSStock> stocksIterator;
	
	
	
	public JPMSMemoryStockProvider() {
		initializeStocks();
	}
	
	private void initializeStocks() {
		//TODO Load Stocks on Market. Read from external Resources...DB..
		stocks.add(new JPMSStock()
								.withSymbol("TEA")
								.withType(JPMSStockTypeEnum.COMMON)
								.withLastDividend(0L)
								.withParValue(100)
								.withTickerPrice(BigDecimal.valueOf(99.79))
								.withLastStockValue(BigDecimal.valueOf(99.79)));
		stocks.add(new JPMSStock()
								.withSymbol("POP")
								.withType(JPMSStockTypeEnum.COMMON)
								.withLastDividend(8L)
								.withParValue(100)
								.withTickerPrice(BigDecimal.valueOf(95.56))
								.withLastStockValue(BigDecimal.valueOf(95.56)));
		stocks.add(new JPMSStock()
								.withSymbol("ALE")
								.withType(JPMSStockTypeEnum.COMMON)
								.withLastDividend(23L)
								.withParValue(60)
								.withTickerPrice(BigDecimal.valueOf(63.23))
								.withLastStockValue(BigDecimal.valueOf(63.23)));
		stocks.add(new JPMSStock()
								.withSymbol("GIN")
								.withType(JPMSStockTypeEnum.PREFERRED)
								.withLastDividend(8L)
								.withFixedDividendPercentage(2)
								.withParValue(100)
								.withTickerPrice(BigDecimal.valueOf(102.34))
								.withLastStockValue(BigDecimal.valueOf(102.34)));
		stocks.add(new JPMSStock()
								.withSymbol("JOE")
								.withType(JPMSStockTypeEnum.COMMON)
								.withLastDividend(13L)
								.withParValue(250)
								.withTickerPrice(BigDecimal.valueOf(250.12))
								.withLastStockValue(BigDecimal.valueOf(250.12)));
		
	}

	
	private Iterator<JPMSStock> getIterator() {
		if ( stocksIterator == null) {
			stocksIterator = stocks.iterator();
		}
		return stocksIterator;
	}
	
	@Override
	public boolean hasMoreStock() {
		
		return getIterator().hasNext();
	}
	
	@Override
	public JPMSStock takeStock() {
		
		return hasMoreStock() ? getIterator().next() : null;
	}
}
