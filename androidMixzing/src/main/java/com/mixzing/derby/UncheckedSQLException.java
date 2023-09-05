package com.mixzing.derby;

import java.sql.SQLException;

import com.mixzing.log.Logger;

public class UncheckedSQLException extends RuntimeException {

	private static Logger lgr = Logger.getRootLogger();

	public UncheckedSQLException(SQLException e) {
		super(e);
	}
	
	public UncheckedSQLException(SQLException e, String sql) {
		super(e);
		lgr.error(sql, e);
	}
}
