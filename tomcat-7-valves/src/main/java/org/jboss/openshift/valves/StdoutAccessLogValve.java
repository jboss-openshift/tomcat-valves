package org.jboss.openshift.valves;

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
	public void log(String message) {
		System.out.println(message);
	}
	
}