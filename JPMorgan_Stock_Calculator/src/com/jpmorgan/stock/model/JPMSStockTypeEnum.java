package com.jpmorgan.stock.model;

import java.io.Serializable;

public enum JPMSStockTypeEnum implements Serializable {

	COMMON{
		@Override
		public String getDescription() {
			return "Common";
		}
	},
	PREFERRED{
		@Override
		public String getDescription() {
			return "Preferred";
		}
		
	};
	
	public abstract String getDescription();
}
