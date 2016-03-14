package com.jpmorgan.stock.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jpmorgan.stock.calculator.JPMSStockTradeCalculator;
import com.jpmorgan.stock.market.JPMSStockMarket;
import com.jpmorgan.stock.queue.JPMSQueue;
import com.jpmorgan.stock.service.protocol.JPMSAgent;
import com.jpmorgan.stock.service.protocol.JPMSStockEnricherFactory;
import com.jpmorgan.stock.service.protocol.JPMSStockObserverFactory;
import com.jpmorgan.stock.service.protocol.JPMSStockProvider;

public class JPMSStockMarketManager {

	private final static long DEFAULT_SLEEP_TIME = 100;
	private final static long DEFAULT_WAIT_SLEEP_TIME = 100;

	
	/**
	 * 
	 * 
	 * 
	 * @param stockProvider  
	 * @param enricherSessionNames cannot be null
	 * @param testTimeoutMs  if positive stops after the milliseconds time requested
	 * @param waitTimeout if positive after milliseconds time out reached exit process
	 */
	public static void doRunManager(JPMSStockProvider stockProvider,JPMSStockObserverFactory observerFactory,JPMSStockEnricherFactory enricherFactory,int deQueuerThreads,long testTimeoutMs,long waitTimeoutMs) {
		
		System.out.println("MANAGER STARTED at "+new Date());
		
		//TODO Parametrize
		long sleepTime = DEFAULT_SLEEP_TIME;
		long waitSleepTime = DEFAULT_WAIT_SLEEP_TIME;
		
		
		if (enricherFactory == null ){
			System.err.println("No Enricher Factory Found. Exit!");
		} else {
			
			//Define MAIN QUEUE alias the COLLECTOR of each trade!
			JPMSQueue queue = new JPMSQueue();
			JPMSStockMarket stockMarket = new JPMSStockMarket(stockProvider);

			//Start Calculator
			JPMSStockTradeCalculator tradeCalculator = new JPMSStockTradeCalculator(stockMarket,deQueuerThreads, queue);			
			tradeCalculator.start("MANAGER-TRADE-SESSION");
			
			
			//Start Enrichers
			List<JPMSAgent> enrichers = new ArrayList<JPMSAgent>(enricherFactory.createStockMarketEnrichers(queue, tradeCalculator));			
			int progressive = 0;
			for ( JPMSAgent agent : enrichers){
				//Test data needed for run 
				agent.start("ENRICHER-"+progressive++);
			}
			
			//START Observer
			JPMSAgent stockMarketObserver = observerFactory != null ? observerFactory.createStockMarketObserver(stockMarket) : null;
			if ( stockMarketObserver != null) {
				stockMarketObserver.start("STOCK-OBSERVER");
			}
			
					
			//AND NOW START PLAYING!
			long start = System.currentTimeMillis();
			while ( true){
								
				try {					
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				//If testTimeoutsMs is not positive continue until external kill
				if ( testTimeoutMs > 0 && (System.currentTimeMillis()-start) > testTimeoutMs)
					break;
				
			}
			
			
			System.out.println("MANAGER PREPARE TO STOP at "+new Date());			
			long startStopProcess = System.currentTimeMillis();
			
			tradeCalculator.stop();
			for ( JPMSAgent enricher : enrichers){
				enricher.stop();
			}
			
			stockMarketObserver.stop();
			
			//Test all Threads are stopped and exit
			//TODO no time out...you must kill in case!
			while ( true){
				
				boolean allDone = !tradeCalculator.isAlive();
				if ( allDone){
					for ( JPMSAgent enricher : enrichers){
						allDone &= !enricher.isAlive();
					}
				}
				
				if ( stockMarketObserver != null){
					allDone &=  !stockMarketObserver.isAlive();
				}
				
				if ( !allDone ){
					try {
						Thread.sleep(waitSleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					break;
				}
				
				if ( waitTimeoutMs > 0 && (System.currentTimeMillis()-startStopProcess) > waitTimeoutMs){
					System.err.println("FORCE EXIT FOR WAIT TIMEOUT REACHED at " + new Date());
					break;
				}
			}
			
			System.out.println("MANAGER FINISHED at "+new Date());

	
		}
	}
	
	



}
