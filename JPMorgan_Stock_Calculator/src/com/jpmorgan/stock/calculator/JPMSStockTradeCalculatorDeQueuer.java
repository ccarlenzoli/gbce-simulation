package com.jpmorgan.stock.calculator;

import java.util.Date;

import com.jpmorgan.stock.model.JPMSStockTrade;

class JPMSStockTradeCalculatorDeQueuer {

	private final JPMSStockTradeCalculator calculator;
		
	private Thread runningThread;
	private boolean alive;
	private boolean interrupt;
	
	
	public JPMSStockTradeCalculatorDeQueuer(JPMSStockTradeCalculator calculator) {
		this.calculator = calculator;
	}
	
	
	/**
	 * Perform all operations needed to manage Trades
	 * @param processName
	 */
	public void start(String processName){
		System.out.println("DE QUEUER "+processName+" STARTING at "+new Date());
		runningThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				alive = true;
				while ( !interrupt){
					doStep();
					
					try {
						Thread.sleep(1);
					} catch ( InterruptedException e){}
				}
				
				System.out.println("DE QUEUER "+runningThread.getName()+" FINISHED at "+new Date());

				alive = false;
				interrupt = false;
				runningThread = null;
				
			}

	
		});
		runningThread.setName(processName);
		//runningThread.setDaemon(true); Wait for stop process
		runningThread.start();
		
		
		
	}
	
	public void stop() {
		if ( runningThread != null){
			System.out.println("CALCULATOR "+runningThread.getName()+" REQUEST TO STOP at "+new Date());
			interrupt = true;
		}
		
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	private void doStep() {
		
		JPMSStockTrade trade = calculator.getQueue().takeNext();
		if ( trade != null){
			
			calculator.addStockTrade(trade);
			
			
			
		}
		
	}
	
}
