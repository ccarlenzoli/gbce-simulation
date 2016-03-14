package com.jpmorgan.stock.model;

import java.io.Serializable;

public enum JPMSStockTradeTypeEnum implements Serializable {

	
	BUY{
		@Override
		public String getDescription() {
			return "Buy";
		}
	},
	SELL{
		@Override
		public String getDescription() {
			return "Sell";
		}
		
	};
	
	public abstract String getDescription();
}
