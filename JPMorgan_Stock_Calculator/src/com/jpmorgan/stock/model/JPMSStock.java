package com.jpmorgan.stock.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

import com.jpmorgan.stock.calculator.JPMSStockTradeCalculator;
import com.jpmorgan.stock.calculator.JPMSStockTradeSynthAmount;

public class JPMSStock implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1894210189125088370L;
	
	private String symbol;
	private JPMSStockTypeEnum type;
	private BigDecimal lastDividend;
	private BigDecimal fixedDividendPercentage;
	private BigDecimal parValue;
	private BigDecimal tickerPrice;  //TODO is the Share Price? I consider the Stock Price when the dividend has been calculated!
	
	private long oldestTimeRegistered = 0;
	
	private long buyOccurences;
	private long sellOccurences;
	private BigDecimal stockSharesBuyValue;
	private BigDecimal stockTotalBuyValue;
	private BigDecimal stockSharesSellValue;
	private BigDecimal stockTotalSellValue;
	
	private BigDecimal stockLastPrice;
	private BigDecimal stockMeanValue;
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public JPMSStock withSymbol(String symbol) {
		this.symbol = symbol;
		return this;
	}
	public JPMSStockTypeEnum getType() {
		return type;
	}
	public void setType(JPMSStockTypeEnum type) {
		this.type = type;
	}
	public JPMSStock withType(JPMSStockTypeEnum type) {
		this.type = type;
		return this;
	}
	public BigDecimal getLastDividend() {
		return lastDividend;
	}
	public void setLastDividend(BigDecimal lastDividend) {
		this.lastDividend = lastDividend;
	}
	/**
	 *  Long can be accepted..NEVER FLOAT OR DOUBLE! BE CAREFULL!!
	 * @param lastDividend
	 * @return
	 */
	public JPMSStock withLastDividend(long lastDividend) {
		this.lastDividend = BigDecimal.valueOf(lastDividend);
		return this;
	}
	public JPMSStock withLastDividend(BigDecimal lastDividend) {
		this.lastDividend = lastDividend;
		return this;
	}
	
	public BigDecimal getTickerPrice() {
		return tickerPrice;
	}
	public void setTickerPrice(BigDecimal tickerPrice) {
		this.tickerPrice = tickerPrice;
	}
	public JPMSStock withTickerPrice(BigDecimal tickerPrice) {
		this.tickerPrice = tickerPrice;
		return this;
	}
	
	public BigDecimal getFixedDividendPercentage() {
		return fixedDividendPercentage;
	}
	
	/**
	 * 
	 * @param fixedDividendPercentage  Based on 100 (i.e. 2% --> 2)
	 */
	public void setFixedDividendPercentage(BigDecimal fixedDividendPercentage) {
		this.fixedDividendPercentage = fixedDividendPercentage;
	}
	
	/**
	 * 
	 * @param fixedDividendPercentage Based on 100 (i.e. 2% --> 2)
	 * @return
	 */
	public JPMSStock withFixedDividendPercentage(BigDecimal fixedDividendPercentage) {
		this.fixedDividendPercentage = fixedDividendPercentage;
		return this;
	}
	/**
	 * Integer can be accepted..NEVER FLOAT OR DOUBLE! BE CAREFULL!!
	 * @param fixedDividendPercentage Based on 100 (i.e. 2% --> 2)
	 * @return
	 */
	public JPMSStock withFixedDividendPercentage(int fixedDividendPercentage) {
		this.fixedDividendPercentage = BigDecimal.valueOf(fixedDividendPercentage);
		return this;
	}
	public BigDecimal getParValue() {
		return parValue;
	}
	public void setParValue(BigDecimal parValue) {
		this.parValue = parValue;
	}
	public JPMSStock withParValue(BigDecimal parValue) {
		this.parValue = parValue;
		return this;
	}
	/**
	 * Integer can be accepted..NEVER FLOAT OR DOUBLE! BE CAREFULL!!
	 * @param parValue
	 * @return
	 */
	public JPMSStock withParValue(int parValue) {
		this.parValue = BigDecimal.valueOf(parValue);
		return this;
	}
	
	

	public BigDecimal getStockMeanValue() {
		return stockMeanValue;
	}
	
	
	/**
	 * TESTING PURPOSE!
	 * @param lastStockValue
	 * @return
	 */
	public JPMSStock withLastStockValue(BigDecimal lastStockValue) {
		this.stockMeanValue = lastStockValue;
		return this;
	}
	

	
	public void updateForTrades(JPMSStockTradeSynthAmount tradeAmount, BigDecimal stockMeanValue,long  oldestTime) {
		
		oldestTimeRegistered = oldestTime;
		buyOccurences = tradeAmount.getBuyOccurences();
		sellOccurences = tradeAmount.getSellOccurences();
		stockSharesBuyValue = tradeAmount.getSharesBuy();
		stockTotalBuyValue = tradeAmount.getTotalBuy();
		stockSharesSellValue = tradeAmount.getSharesSell();
		stockTotalSellValue = tradeAmount.getTotalSell();
		
		stockLastPrice = tradeAmount.getStockLastPrice();
		
		this.stockMeanValue = stockMeanValue;
		
		

		
	}
	

	
	public String getCurrentStatusDescription() {
		
		NumberFormat nrFormatter = NumberFormat.getInstance(); //TODO Use the same for price and shares...Split? Rules?
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("Stock: ");
		buffer.append(symbol);
		buffer.append("\n");
		
		buffer.append("TICKER PRICE: ");	
		buffer.append(nrFormatter.format(tickerPrice)); 		
		buffer.append("\n");
		buffer.append("PAR VALUE: ");	
		buffer.append(nrFormatter.format(parValue)); 		
		buffer.append("\n");	
		buffer.append("LAST DIVIDEND: ");	
		buffer.append(nrFormatter.format(lastDividend)); 		
		buffer.append("\n");
		if ( fixedDividendPercentage != null){
			buffer.append("FIXED DIVIDENT PERCENTAGE: ");	
			buffer.append(nrFormatter.format(fixedDividendPercentage)); 		
			buffer.append("%\n");
		}
		
		buffer.append("DIVIDEND YIELD on Ticker: ");	
		double div = getDividendYieldOnTicker();
		if ( div < 0.){
			buffer.append("N/A");
		} else {
			buffer.append(nrFormatter.format(div*100.)); 
		}
		buffer.append("%\n");
		
		
		buffer.append("P/E Ratio on Ticker: ");	
		double pe = getPERatioOnTicker();
		if ( pe < 0.){
			buffer.append("N/A");
		} else {
			buffer.append(nrFormatter.format(pe)); 
		}
		buffer.append("\n");
		
		buffer.append("DIVIDEND YIELD on Current Mean: ");	
		div = getDividendYieldOnMean();
		if ( div < 0.){
			buffer.append("N/A");
		} else {
			buffer.append(nrFormatter.format(div*100.)); 
		}
		buffer.append("%\n");
		
		
		buffer.append("P/E Ratio on Current Mean: ");	
		pe = getPERatioOnMean();
		if ( pe < 0.){
			buffer.append("N/A");
		} else {
			buffer.append(nrFormatter.format(pe)); 
		}
		buffer.append("\n");
		
		long r = System.currentTimeMillis()-oldestTimeRegistered;
		//long rs = r / 1000;
		long ms = r / 60000;
		long rrs = (r - ms * 60000) / 1000;
		
		buffer.append("TRADES (Based on operations occurred in the last ");
		if ( ms > 1){
			buffer.append(String.valueOf(ms));
			buffer.append(" minutes");
		} else if ( ms == 1){
			buffer.append(" one minute");
		}
		
		if ( rrs > 1){
			if ( ms > 0)
				buffer.append(" and ");
			buffer.append(String.valueOf(rrs));
			buffer.append(" seconds");
		} else if (rrs == 1) {
			if ( ms > 0)
				buffer.append(" and ");
			buffer.append("one second");				
		}	

		buffer.append(")\n");
		
		buffer.append("BUY:");
		buffer.append("\n");
		
		buffer.append("OCCURENCES: ");	
		buffer.append(String.valueOf(buyOccurences));
		buffer.append("\n");
		buffer.append("SHARES: ");		
		buffer.append(nrFormatter.format(stockSharesBuyValue)); 
		buffer.append("\n");
		buffer.append("AMOUNT VOLUME: ");		
		buffer.append(nrFormatter.format(stockTotalBuyValue)); 
		buffer.append("\n");
		
		buffer.append("SELL: ");
		buffer.append("\n");
		
		buffer.append("OCCURENCES: ");	
		buffer.append(String.valueOf(sellOccurences));
		buffer.append("\n");
		
		buffer.append("SHARES: ");		
		buffer.append(nrFormatter.format(stockSharesSellValue)); 
		buffer.append("\n");
		buffer.append("AMOUNT VOLUME: ");		
		buffer.append(nrFormatter.format(stockTotalSellValue)); 
		buffer.append("\n");
		buffer.append("\n");

		buffer.append("LAST PRICE: ");		
		buffer.append(nrFormatter.format(stockLastPrice)); 
		buffer.append("\n");
		
		buffer.append("MEAN PRICE: ");		
		buffer.append(nrFormatter.format(stockMeanValue)); 
		buffer.append("\n");
		
		
		return buffer.toString();
	}
	
	private double getDividend() {
		if ( type == JPMSStockTypeEnum.COMMON){
			return lastDividend.doubleValue();  
		} else {
			return parValue.doubleValue() * fixedDividendPercentage.doubleValue()/(100.);
		}
	}
	
	/**
	 * Calculate without using specific rules for scaling...since I don't know if is supposed to do as in other accounting fields (business accounting for instance)
	 * @return
	 */
	private double getDividendYieldOnTicker() {
		if ( tickerPrice.doubleValue() < JPMSStockTradeCalculator.EPS){
			return -1.;
		}
		return getDividend()/tickerPrice.doubleValue();
	}

	/**
	 * Calculate without using specific rules for scaling...since I don't know if is supposed to do as in other accounting fields (business accounting for instance)
	 * @return
	 */
	private double getPERatioOnTicker() {
		double div = getDividend();
		if ( div < JPMSStockTradeCalculator.EPS){
			return -1.;
		}
		return tickerPrice.doubleValue()/div;
	}
	
	/**
	 * Calculate without using specific rules for scaling...since I don't know if is supposed to do as in other accounting fields (business accounting for instance)
	 * @return
	 */
	private double getDividendYieldOnMean() {
		BigDecimal smv = stockMeanValue; //For Concurrent Reason!
		if ( smv.doubleValue() < JPMSStockTradeCalculator.EPS){
			return -1.;
		}
		return getDividend()/smv.doubleValue();
	}

	/**
	 * Calculate without using specific rules for scaling...since I don't know if is supposed to do as in other accounting fields (business accounting for instance)
	 * @return
	 */
	private double getPERatioOnMean() {
		double div = getDividend();
		if ( div < JPMSStockTradeCalculator.EPS){
			return -1.;
		}
		return stockMeanValue.doubleValue()/div;
	}

	
	
	

}
