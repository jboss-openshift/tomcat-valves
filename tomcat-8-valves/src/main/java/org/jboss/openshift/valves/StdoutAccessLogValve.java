package org.jboss.openshift.valves;

import java.io.CharArrayWriter;

import org.apache.catalina.valves.AccessLogValve;

public class StdoutAccessLogValve extends AccessLogValve {
	
	@Override
	public boolean isRotatable() {
		return false;
	}

	@Override
	public void rotate() {
		// Do nothing
	}

	@Override
	public synchronized boolean rotate(String newFileName) {
		return true;
	}
	
	@Override
	public void log(CharArrayWriter message) {
		System.out.println(message);
	}

}
