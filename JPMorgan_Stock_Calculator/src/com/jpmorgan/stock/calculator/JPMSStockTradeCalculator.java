package com.jpmorgan.stock.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

import com.jpmorgan.stock.market.JPMSStockMarket;
import com.jpmorgan.stock.model.JPMSStock;
import com.jpmorgan.stock.model.JPMSStockTrade;
import com.jpmorgan.stock.queue.JPMSQueue;

/**
 * This class read Trades Queue and calculate all Prices and Indexes depending on Data 
 * It Keeps track of last 15 Minutes data
 * 
 * @author claudiocarlenzoli
 *
 */
public class JPMSStockTradeCalculator {


	public final static double EPS = 0.0001; //Floating Point ISSUE 
	private final static int START_DE_QUEUER_THREAD = 5;
	private final static long MAX_DELAY = 15 * 60 * 1000L; //15 Minutes
	/**
	 * Right now single Queue
	 */
	private final JPMSQueue queue;
	private final JPMSStockMarket stockMarket;
	private final Map<String, ConcurrentSkipListSet<JPMSStockTrade>> tradesBySymbolMap;
	
	private Map<String,JPMSStockTradeSynthAmount> tradesAmount = new HashMap<String, JPMSStockTradeSynthAmount>();

	
	private List<JPMSStockTradeCalculatorDeQueuer> deQueuers = new ArrayList<JPMSStockTradeCalculatorDeQueuer>();

	
	
	
	private Thread runningThread;
	private boolean alive;
	private boolean interrupt;
	
	private int queuerThreadSize = 0;
	private int queueSize = 0;

	
	public JPMSStockTradeCalculator(JPMSStockMarket stockMarket,JPMSQueue queue) {
		this(stockMarket,0,queue);
	}

	public JPMSStockTradeCalculator(JPMSStockMarket stockMarket,int queuerThreadSize,JPMSQueue queue) {
		this.stockMarket = stockMarket;
		this.queue = queue;
		this.queuerThreadSize = queuerThreadSize;
		
		tradesBySymbolMap = new HashMap<String, ConcurrentSkipListSet<JPMSStockTrade>>();
		//Init the concurrent map avoiding to use concurrency on adding new keys!
		for ( JPMSStock stock : stockMarket.getAllStocks()){
			String symb = stock.getSymbol();
			tradesBySymbolMap.put(symb, new ConcurrentSkipListSet<JPMSStockTrade>(
					new Comparator<JPMSStockTrade>() {
						@Override
						public int compare(JPMSStockTrade o1, JPMSStockTrade o2) {
							if ( o1.getTimestamp() == o2.getTimestamp()){
								return 0;
							}
							return o1.getTimestamp() < o2.getTimestamp() ? -1 : 1;
						}
					}));
			tradesAmount.put(symb, new JPMSStockTradeSynthAmount(stock));
			
		}
		
	}
	
	/**
	 * Perform all operations needed to manage Trades
	 * @param processName
	 */
	public void start(String processName){
		
		int rQueuerThreadSize = queuerThreadSize;
		if ( rQueuerThreadSize <= 0){
			rQueuerThreadSize = START_DE_QUEUER_THREAD;
		}
		System.out.println("CALCULATOR "+processName+" STARTING at "+new Date());
		for ( int i = 0; i < rQueuerThreadSize; i++){
			JPMSStockTradeCalculatorDeQueuer deQueuer = new JPMSStockTradeCalculatorDeQueuer(this);
			deQueuers.add(deQueuer);
			deQueuer.start("DE-QUEUER-"+i);
		}
		
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
				
				//Check al dequers
				while (true){
					boolean allDone = true;
					for (JPMSStockTradeCalculatorDeQueuer deQueuer : deQueuers){
						allDone &= !deQueuer.isAlive();
					}
					
					if ( allDone ){
						break;
					} else {
						try {
							Thread.sleep(10);
						} catch ( InterruptedException e){}
					}
						
				}
				
				System.out.println("CALCULATOR "+runningThread.getName()+" FINISHED at "+new Date());
				

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
			for (JPMSStockTradeCalculatorDeQueuer deQueuer : deQueuers){
				deQueuer.stop();
			}
			interrupt = true;
		}
		
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	private void doStep() {
		
		int cQSize = queue.getQueueSize();
		
		if ( cQSize > 10 && cQSize > queueSize) {
			System.out.println("WARNING QUEUE SIZE GROWING: " + cQSize);
		}
		
		long startRefValue = System.currentTimeMillis()-MAX_DELAY;
		//Check if there is some "old" values
		for ( Map.Entry<String,ConcurrentSkipListSet<JPMSStockTrade>> entry : tradesBySymbolMap.entrySet()){
			
			ConcurrentSkipListSet<JPMSStockTrade> set = entry.getValue();
			String stockSymbol = entry.getKey();
			
			long oldestTime = -1;
			List<JPMSStockTrade> coll = new ArrayList<JPMSStockTrade>();
			for ( JPMSStockTrade trade : set){
				oldestTime = trade.getTimestamp();
				if ( oldestTime < startRefValue){
					coll.add(trade);
				} else {
					break;
				}	
			}
			
			
			//Update counters
			JPMSStockTradeSynthAmount tradeAmount = tradesAmount.get(stockSymbol);
			
			if( !coll.isEmpty()){
				tradeAmount.maxRangeTimeReached();
			}
			
			set.removeAll(coll);
			for ( JPMSStockTrade trade : coll){
				tradeAmount.update(trade,null,false,false);
			}
			
			//Update Stock Market
			JPMSStock stock = stockMarket.getStockForSymbol(stockSymbol);
			if ( stock != null){ //Useless...for the current example...but can be useful for future improvements!
				
				BigDecimal den = tradeAmount.getSharesBuy().add(tradeAmount.getSharesSell());
				BigDecimal stockMeanValue = null;
				if ( den.doubleValue() > JPMSStockTradeCalculator.EPS){
					stockMeanValue = tradeAmount.getTotalBuy().add(tradeAmount.getTotalSell()).divide(den,RoundingMode.HALF_UP); //TODO Scaling...Rules...
					stock.updateForTrades(tradeAmount,stockMeanValue,tradeAmount.isMaxRangeTimeReached() ? startRefValue : oldestTime);
				}
				
			}
			
		}

		queueSize = cQSize;
		
	}
	
	public Map<String, JPMSStockTradeSynthAmount> getTradesAmount() {
		return tradesAmount;
	}
	
	/**
	 * Used by {@link JPMSStockTradeCalculatorDeQueuer}
	 * @return
	 */
	JPMSQueue getQueue() {
		return queue;
	}

	/**
	 * Used by {@link JPMSStockTradeCalculatorDeQueuer}
	 * 
	 * @param trade
	 */
	 void addStockTrade(JPMSStockTrade trade) {
		 
		 String stockSymbol = trade.getStockSymbol();
		 ConcurrentSkipListSet<JPMSStockTrade> trades = tradesBySymbolMap.get(stockSymbol);
		 trades.add(trade);		
		 
		 //Update counters
		 JPMSStockTradeSynthAmount tradeAmount = tradesAmount.get(stockSymbol);
		 tradeAmount.update(trade,trades,true,true);

	 }
	
}
