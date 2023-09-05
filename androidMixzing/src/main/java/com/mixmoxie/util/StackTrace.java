package com.mixmoxie.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTrace {
	public static String getStackTrace()
	{
		return getStackTrace(true);
	}

	public static String getStackTrace(boolean header)
	{
		String trace = getStackTrace(new Throwable());
		return header ? "\nAt:\n" + trace : trace;
	}

	public static String getStackTrace(Throwable e)
	{
		StringWriter w = new StringWriter();
		e.printStackTrace(new PrintWriter(w));
		return w.toString();
	}
}
