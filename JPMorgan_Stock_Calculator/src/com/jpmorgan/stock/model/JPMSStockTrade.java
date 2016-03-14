package com.jpmorgan.stock.model;

import java.io.Serializable;
import java.math.BigDecimal;


public class JPMSStockTrade implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3811858040447411297L;
	
	private JPMSStock stock;  
	private String stockSymbol;
	private long timestamp = System.currentTimeMillis(); //In any case I set the current Time
	private int timeZone = 0; //0 means GMT, 1 GMT --> +1 , -1 --> GMT -1   //Default is GMT
	private BigDecimal shares; //TODO long is enough?
	private BigDecimal price;
	private JPMSStockTradeTypeEnum tradeType;
	
	public JPMSStock getStock() {
		return stock;
	}
	public void setStock(JPMSStock stock) {
		this.stock = stock;
		if ( stock != null){
			stockSymbol = stock.getSymbol();
		} else {
			stockSymbol = null;
		}
	}
	public JPMSStockTrade withStock(JPMSStock stock) {
		setStock(stock);
		return this;
	}
	public String getStockSymbol() {
		return stockSymbol;
	}
	public void setStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
	}
	public JPMSStockTrade withStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
		return this;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public JPMSStockTrade withTimestamp(long timestamp) {
		this.timestamp = timestamp;
		return this;
	}
	public int getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(int timeZone) {
		this.timeZone = timeZone;
	}
	public JPMSStockTrade withTimeZone(int timeZone) {
		this.timeZone = timeZone;
		return this;
	}
	
	public BigDecimal getShares() {
		return shares;
	}
	public void setShares(BigDecimal shares) {
		this.shares = shares;
	}
	public JPMSStockTrade withShares(BigDecimal shares) {
		this.shares = shares;
		return this;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public JPMSStockTrade withPrice(BigDecimal price) {
		this.price = price;
		return this;
	}
	public JPMSStockTradeTypeEnum getTradeType() {
		return tradeType;
	}
	public void setTradeType(JPMSStockTradeTypeEnum tradeType) {
		this.tradeType = tradeType;
	}
	public JPMSStockTrade withTradeType(JPMSStockTradeTypeEnum tradeType) {
		this.tradeType = tradeType;
		return this;
	}
	
	
	
	

	
	
	
}
