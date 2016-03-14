package com.jpmorgan.stock.service.protocol;

public interface JPMSAgent {

	
	public String getJPMSAgentDescription();
	
	public boolean isAlive();
	
	public void start(String processName);
	
	public void stop();
}
