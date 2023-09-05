package com.mixzing.util;

import java.util.ArrayList;
import java.util.List;

import com.mixzing.external.android.KeyValuePair;
import com.mixzing.log.Logger;

public class Parameters {
	private static final Logger log = Logger.getRootLogger();
	private List<KeyValuePair> params;

	public Parameters(List<KeyValuePair> params) {
		this.params = params == null ? new ArrayList<KeyValuePair>(0) : params;
	}

	public String getParameter(String key) {
		for (KeyValuePair kvp : params) {
//			if (Logger.IS_DEBUG_ENABLED)
//				log.debug("Parameters.getParameter: checking: " + kvp);
			if (kvp.getKey().equals(key)) {
				return kvp.getValue();
			}
		}
		return null;
	}

	public List<KeyValuePair> getParameters() {
		return params;
	}
}
