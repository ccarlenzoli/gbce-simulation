package com.jpmorgan.stock.service.protocol;

import com.jpmorgan.stock.model.JPMSStock;

public interface JPMSStockProvider {

	
	/**
	 * Get a Stock or null if finished
	 * @return
	 */
	JPMSStock takeStock();
	
	/**
	 * 
	 * @return true if has still stock
	 */
	boolean hasMoreStock();
	
	
}
