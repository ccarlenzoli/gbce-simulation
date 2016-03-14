package com.jpmorgan.stock.calculator;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.ConcurrentSkipListSet;

import com.jpmorgan.stock.model.JPMSStock;
import com.jpmorgan.stock.model.JPMSStockTrade;
import com.jpmorgan.stock.model.JPMSStockTradeTypeEnum;

public class JPMSStockTradeSynthAmount {
	
	private final static long DEFAULT_GLOBAL_RECALCULATE_DELAY = 5*60*1000L;  //5 Minutes
	
	
	private long lastGlobalRecalculateTime = System.currentTimeMillis();
	
	private long buyOccurences;
	private long sellOccurences;
	
	private BigDecimal sharesBuy;
	private BigDecimal totalBuy;
	private BigDecimal sharesSell;
	private BigDecimal totalSell;
	
	private BigDecimal stockLastPrice;
	
	
	private JPMSStock stock;
	
	
	private boolean maxRangeTimeReached;

	
	public JPMSStockTradeSynthAmount(JPMSStock stock) {
		this.stockLastPrice = stock.getStockMeanValue();
		this.stock = stock;
		initializeCounters();
	}
	
	public void maxRangeTimeReached() {
		maxRangeTimeReached = true;
	}
	
	private void initializeCounters() {
		buyOccurences = 0;
		sellOccurences = 0;
		sharesBuy = BigDecimal.ZERO;
		totalBuy = BigDecimal.ZERO;
		sharesSell = BigDecimal.ZERO;
		totalSell = BigDecimal.ZERO;
	}
	
	public synchronized void update(JPMSStockTrade trade, ConcurrentSkipListSet<JPMSStockTrade> activeTrades, boolean add,boolean updateStockLastPrice) {
		
		if  (add){

			long now= System.currentTimeMillis();
			if ( activeTrades != null && now-lastGlobalRecalculateTime > DEFAULT_GLOBAL_RECALCULATE_DELAY){
				initializeCounters();
				for ( JPMSStockTrade tradeActive :activeTrades ){
					update(tradeActive, null, true,false);
				}
				lastGlobalRecalculateTime = now;
				System.out.println("TRADE AMOUNT FOR STOCK: " + trade.getStockSymbol()+ " RESET COUNTERS at " + new Date());
			} else {
				if ( trade.getTradeType() == JPMSStockTradeTypeEnum.BUY){
					buyOccurences++;
					sharesBuy = sharesBuy.add(trade.getShares());
					totalBuy = totalBuy.add(trade.getShares().multiply(trade.getPrice()));
				} else {
					sellOccurences++;
					sharesSell = sharesSell.add(trade.getShares());
					totalSell = totalSell.add(trade.getShares().multiply(trade.getPrice()));					
				}
			}
			
			if ( updateStockLastPrice){
				stockLastPrice = trade.getPrice();
			}
			
		} else {
			if ( trade.getTradeType() == JPMSStockTradeTypeEnum.BUY){
				buyOccurences--;
				sharesBuy = sharesBuy.subtract(trade.getShares());
				totalBuy = totalBuy.subtract(trade.getShares().multiply(trade.getPrice()));
			} else {
				sellOccurences--;
				sharesSell = sharesSell.subtract(trade.getShares());
				totalSell = totalSell.subtract(trade.getShares().multiply(trade.getPrice()));					
			}
		}
		
		

		
	}
	
	public long getBuyOccurences() {
		return buyOccurences;
	}
	
	public long getSellOccurences() {
		return sellOccurences;
	}


	public BigDecimal getSharesBuy() {
		return sharesBuy;
	}



	public BigDecimal getTotalBuy() {
		return totalBuy;
	}



	public BigDecimal getSharesSell() {
		return sharesSell;
	}



	public BigDecimal getTotalSell() {
		return totalSell;
	}
	
	public BigDecimal getStockLastPrice() {
		return stockLastPrice;
	}
	
	public boolean isMaxRangeTimeReached() {
		return maxRangeTimeReached;
	}


	public JPMSStock getStock() {
		return stock;
	}


	
	
}