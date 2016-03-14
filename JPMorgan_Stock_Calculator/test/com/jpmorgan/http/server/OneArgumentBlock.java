package com.jpmorgan.http.server;

public interface OneArgumentBlock<RETURN_VALUE,ARGUMENT_VALUE> {

	public RETURN_VALUE execute(ARGUMENT_VALUE argument);
}
