/**
 *  Copyright 2017 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package org.jboss.openshift.valves;

import java.io.CharArrayWriter;

import org.apache.catalina.valves.AbstractAccessLogValve;

import java.util.logging.Logger;

/**
 * @author <a href="mailto:rmartine@redhat.com">Ricardo Martinelli</a>
 */
public class StdoutAccessLogValve extends AbstractAccessLogValve {

	Logger log = Logger.getLogger(this.getClass().getName());

	@Override
	protected void log(CharArrayWriter message) {
		System.out.println(message);
	}
	

}
