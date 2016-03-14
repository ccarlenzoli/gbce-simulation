package com.jpmorgan.stock.service.impl;

import java.util.Date;

import com.jpmorgan.stock.service.protocol.JPMSAgent;

public abstract class JPMSAbstractAgent implements JPMSAgent{
	
	private Thread runningThread;
	private boolean alive;
	private boolean interrupt;
	
	
	/**
	 * Perform all operations needed to manage Trades
	 * @param processName
	 */
	@Override
	public void start(String processName){
		
		System.out.println("ENRICHER "+processName+" STARTING at "+new Date());
		runningThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				alive = true;
				doPrepareForRun();
				while ( !interrupt){
					doStep();
					if ( mustSleepBetweenSteps()){
						try {
							Thread.sleep(10);
						} catch ( InterruptedException e){}
					}
				}
				
				System.out.println(getJPMSAgentDescription()+" "+runningThread.getName()+" FINISHED at "+new Date());

				alive = false;
				interrupt = false;
				runningThread = null;
				doPostRun();
				
			}



	
		});
		runningThread.setName(processName);
		//runningThread.setDaemon(true); Wait for stop process
		runningThread.start();
		
		
		
	}

	@Override
	public void stop() {
		
		
		if ( runningThread != null){
			System.out.println(getJPMSAgentDescription()+" "+runningThread.getName()+" REQUEST TO STOP at "+new Date());
			interrupt = true;
		}
		
	}
	
	@Override
	public boolean isAlive() {
		return alive;
	}
	
	protected boolean mustSleepBetweenSteps() {
		return true;
	}
	
	/**
	 * Do preparing jobs
	 */
	protected abstract void doPrepareForRun();
	
	/**
	 * Do the real job!
	 */
	protected abstract void doStep();

	/**
	 * Do post run jobs
	 */
	protected abstract void doPostRun();


}
