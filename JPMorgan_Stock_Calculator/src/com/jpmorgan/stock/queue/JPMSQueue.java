package com.jpmorgan.stock.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.jpmorgan.stock.model.JPMSStockTrade;

public class JPMSQueue {

	/**
	 * FIFO. 
	 * 
	 * TODO Check performance and in case implements a custom one
	 */
	private BlockingQueue<JPMSStockTrade> tradeQueue = new LinkedBlockingQueue<JPMSStockTrade>();
	
	
	public boolean addStockTrade(JPMSStockTrade trade) {
		if ( tradeQueue.remainingCapacity() == Integer.MAX_VALUE){
			return tradeQueue.add(trade);		
		} else {
			try {
				tradeQueue.put(trade);
				return true;
			} catch ( InterruptedException e){
				e.printStackTrace();
				return false;
			}
		}
	}
	
	public JPMSStockTrade takeNext() {
		if ( tradeQueue.isEmpty()){
			return null;
		}
		return tradeQueue.poll();		
	}

	public int getQueueSize() {		
		return tradeQueue.size();
	}
	
	
}
