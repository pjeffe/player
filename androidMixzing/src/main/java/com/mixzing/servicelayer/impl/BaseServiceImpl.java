package com.mixzing.servicelayer.impl;


import com.mixzing.log.Logger;
import com.mixzing.servicelayer.BaseService;


public abstract class BaseServiceImpl implements BaseService {

	protected static final Logger lgr = Logger.getRootLogger();
	protected Logger getLogger() {
		return lgr;
	}
	
	public BaseServiceImpl() {
		
	}

	/* (non-Javadoc)
	 * @see com.mixzing.servicelayer.impl.BaseService#start()
	 */
	public void start() {
		
	}
	/* (non-Javadoc)
	 * @see com.mixzing.servicelayer.impl.BaseService#shutDown()
	 */
	public void shutDown() {
		
	}

}
