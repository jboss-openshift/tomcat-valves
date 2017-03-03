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

<<<<<<< HEAD
=======
import java.io.File;
>>>>>>> 072f30d9f06453300f18cacabc85145381a340fd
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.catalina.AccessLog;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
<<<<<<< HEAD
=======
import org.apache.catalina.valves.AccessLogValve;
>>>>>>> 072f30d9f06453300f18cacabc85145381a340fd
import org.apache.catalina.valves.Constants;
import org.apache.catalina.valves.ValveBase;
import org.apache.coyote.RequestInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;

/**
 * @author <a href="mailto:rmartine@redhat.com">Ricardo Martinelli</a>
 */
public class StdoutAccessLogValve extends ValveBase implements AccessLog {

<<<<<<< HEAD
    private static final Log log = LogFactory.getLog(StdoutAccessLogValve.class);
=======
    private static final Log log = LogFactory.getLog(AccessLogValve.class);
>>>>>>> 072f30d9f06453300f18cacabc85145381a340fd

    public StdoutAccessLogValve() {
        super(true);
    }

<<<<<<< HEAD
    protected static final String info =
        "org.apache.catalina.valves.StdoutAccessLogValve/2.2";
=======
    private volatile String dateStamp = "";

    protected static final String info =
        "org.apache.catalina.valves.AccessLogValve/2.2";
>>>>>>> 072f30d9f06453300f18cacabc85145381a340fd

    protected boolean enabled = true;

    protected String pattern = null;

    private boolean buffered = true;

    protected PrintWriter writer = null;

    protected SimpleDateFormat fileDateFormatter = null;

    private static final int globalCacheSize = 300;

    private static final int localCacheSize = 60;

<<<<<<< HEAD
=======
    protected File currentLogFile = null;

>>>>>>> 072f30d9f06453300f18cacabc85145381a340fd
    protected static class DateFormatCache {

        protected class Cache {

            private static final String cLFFormat = "dd/MMM/yyyy:HH:mm:ss Z";

            private long previousSeconds = Long.MIN_VALUE;
            private String previousFormat = "";

            private long first = Long.MIN_VALUE;
            private long last = Long.MIN_VALUE;
            private int offset = 0;
            private final Date currentDate = new Date();

            protected final String cache[];
            private SimpleDateFormat formatter;
            private boolean isCLF = false;

            private Cache parent = null;

            private Cache(Cache parent) {
                this(null, parent);
            }

            private Cache(String format, Cache parent) {
                this(format, null, parent);
            }

            private Cache(String format, Locale loc, Cache parent) {
                cache = new String[cacheSize];
                for (int i = 0; i < cacheSize; i++) {
                    cache[i] = null;
                }
                if (loc == null) {
                    loc = cacheDefaultLocale;
                }
                if (format == null) {
                    isCLF = true;
                    format = cLFFormat;
                    formatter = new SimpleDateFormat(format, Locale.US);
                } else {
                    formatter = new SimpleDateFormat(format, loc);
                }
                formatter.setTimeZone(TimeZone.getDefault());
                this.parent = parent;
            }

            private String getFormatInternal(long time) {

                long seconds = time / 1000;

                if (seconds == previousSeconds) {
                    return previousFormat;
                }

                previousSeconds = seconds;
                int index = (offset + (int)(seconds - first)) % cacheSize;
                if (index < 0) {
                    index += cacheSize;
                }
                if (seconds >= first && seconds <= last) {
                    if (cache[index] != null) {
                        previousFormat = cache[index];
                        return previousFormat;
                    }

                } else if (seconds >= last + cacheSize || seconds <= first - cacheSize) {
                    first = seconds;
                    last = first + cacheSize - 1;
                    index = 0;
                    offset = 0;
                    for (int i = 1; i < cacheSize; i++) {
                        cache[i] = null;
                    }
                } else if (seconds > last) {
                    for (int i = 1; i < seconds - last; i++) {
                        cache[(index + cacheSize - i) % cacheSize] = null;
                    }
                    first = seconds - (cacheSize - 1);
                    last = seconds;
                    offset = (index + 1) % cacheSize;
                } else if (seconds < first) {
                    for (int i = 1; i < first - seconds; i++) {
                        cache[(index + i) % cacheSize] = null;
                    }
                    first = seconds;
                    last = seconds + (cacheSize - 1);
                    offset = index;
                }

                if (parent != null) {
                    synchronized(parent) {
                        previousFormat = parent.getFormatInternal(time);
                    }
                } else {
                    currentDate.setTime(time);
                    previousFormat = formatter.format(currentDate);
                    if (isCLF) {
                        StringBuilder current = new StringBuilder(32);
                        current.append('[');
                        current.append(previousFormat);
                        current.append(']');
                        previousFormat = current.toString();
                    }
                }
                cache[index] = previousFormat;
                return previousFormat;
            }
        }

        private int cacheSize = 0;

        private final Locale cacheDefaultLocale;
        private final DateFormatCache parent;
        protected final Cache cLFCache;
        private final HashMap<String, Cache> formatCache = new HashMap<String, Cache>();

        protected DateFormatCache(int size, Locale loc, DateFormatCache parent) {
            cacheSize = size;
            cacheDefaultLocale = loc;
            this.parent = parent;
            Cache parentCache = null;
            if (parent != null) {
                synchronized(parent) {
                    parentCache = parent.getCache(null, null);
                }
            }
            cLFCache = new Cache(parentCache);
        }

        private Cache getCache(String format, Locale loc) {
            Cache cache;
            if (format == null) {
                cache = cLFCache;
            } else {
                cache = formatCache.get(format);
                if (cache == null) {
                    Cache parentCache = null;
                    if (parent != null) {
                        synchronized(parent) {
                            parentCache = parent.getCache(format, loc);
                        }
                    }
                    cache = new Cache(format, loc, parentCache);
                    formatCache.put(format, cache);
                }
            }
            return cache;
        }

        public String getFormat(long time) {
            return cLFCache.getFormatInternal(time);
        }

        public String getFormat(String format, Locale loc, long time) {
            return getCache(format, loc).getFormatInternal(time);
        }
    }

    private static final DateFormatCache globalDateCache =
            new DateFormatCache(globalCacheSize, Locale.getDefault(), null);

    private static final ThreadLocal<DateFormatCache> localDateCache =
            new ThreadLocal<DateFormatCache>() {
        @Override
        protected DateFormatCache initialValue() {
            return new DateFormatCache(localCacheSize, Locale.getDefault(), globalDateCache);
        }
    };

    private static final ThreadLocal<Date> localDate = new ThreadLocal<Date>() {
        @Override
        protected Date initialValue() {
            return new Date();
        }
    };

    private static enum FormatType {
        CLF, SEC, MSEC, MSEC_FRAC, SDF
    }

    private boolean resolveHosts = false;

    private boolean checkExists = false;

    protected String condition = null;

    protected String conditionIf = null;

    protected String localeName = Locale.getDefault().toString();

    protected Locale locale = Locale.getDefault();

    protected String encoding = null;

    protected AccessLogElement[] logElements = null;

    protected boolean requestAttributesEnabled = false;

    public boolean getEnabled() {
        return enabled;
    }

    public void setRequestAttributesEnabled(boolean requestAttributesEnabled) {
        this.requestAttributesEnabled = requestAttributesEnabled;
    }

    public boolean getRequestAttributesEnabled() {
        return requestAttributesEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getInfo() {
        return (info);
    }

    public String getPattern() {
        return (this.pattern);
    }

    public void setPattern(String pattern) {
        if (pattern == null) {
            this.pattern = "";
        } else if (pattern.equals(Constants.AccessLog.COMMON_ALIAS)) {
            this.pattern = Constants.AccessLog.COMMON_PATTERN;
        } else if (pattern.equals(Constants.AccessLog.COMBINED_ALIAS)) {
            this.pattern = Constants.AccessLog.COMBINED_PATTERN;
        } else {
            this.pattern = pattern;
        }
        logElements = createLogElements();
    }

    public boolean isCheckExists() {
        return checkExists;
    }

    public void setCheckExists(boolean checkExists) {
        this.checkExists = checkExists;
    }

    public boolean isBuffered() {
        return buffered;
    }

    public void setBuffered(boolean buffered) {
        this.buffered = buffered;
    }

    @Deprecated
    public void setResolveHosts(boolean resolveHosts) {
        this.resolveHosts = resolveHosts;
    }

    @Deprecated
    public boolean isResolveHosts() {
        return resolveHosts;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getConditionUnless() {
        return getCondition();
    }

    public void setConditionUnless(String condition) {
        setCondition(condition);
    }

    public String getConditionIf() {
        return conditionIf;
    }

    public void setConditionIf(String condition) {
        this.conditionIf = condition;
    }

    public String getLocale() {
        return localeName;
    }

    public void setLocale(String localeName) {
        this.localeName = localeName;
        locale = findLocale(localeName, locale);
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        if (encoding != null && encoding.length() > 0) {
            this.encoding = encoding;
        } else {
            this.encoding = null;
        }
    }

    @Override
    public synchronized void backgroundProcess() {
        if (getState().isAvailable() && getEnabled() && writer != null &&
                buffered) {
            writer.flush();
        }
    }

    @Override
    public void invoke(Request request, Response response) throws IOException,
            ServletException {
        getNext().invoke(request, response);
    }

    public void log(Request request, Response response, long time) {
        if (!getState().isAvailable() || !getEnabled() || logElements == null
                || condition != null
                && null != request.getRequest().getAttribute(condition)
                || conditionIf != null
                && null == request.getRequest().getAttribute(conditionIf)) {
            return;
        }

        long start = request.getCoyoteRequest().getStartTime();
        Date date = getDate(start + time);

        StringBuilder result = new StringBuilder(128);

        for (int i = 0; i < logElements.length; i++) {
            logElements[i].addElement(result, date, request, response, time);
        }

        log(result.toString());
    }

    public void log(String message) {
    	System.out.println(message);
    }

    private static Date getDate(long systime) {
        Date date = localDate.get();
        date.setTime(systime);
        return date;
    }

    protected static Locale findLocale(String name, Locale fallback) {
        if (name == null || name.isEmpty()) {
            return Locale.getDefault();
        } else {
            for (Locale l: Locale.getAvailableLocales()) {
                if (name.equals(l.toString())) {
                    return(l);
                }
            }
        }
        log.error(sm.getString("accessLogValve.invalidLocale", name));
        return fallback;
    }

    @Override
    protected synchronized void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
    }

    @Override
    protected synchronized void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
    }

    protected interface AccessLogElement {
        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time);

    }

    protected static class ThreadNameElement implements AccessLogElement {
        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            RequestInfo info = request.getCoyoteRequest().getRequestProcessor();
            if(info != null) {
                buf.append(info.getWorkerThreadName());
            } else {
                buf.append("-");
            }
        }
    }

    protected static class LocalAddrElement implements AccessLogElement {

        private static final String LOCAL_ADDR_VALUE;

        static {
            String init;
            try {
                init = InetAddress.getLocalHost().getHostAddress();
            } catch (Throwable e) {
                ExceptionUtils.handleThrowable(e);
                init = "127.0.0.1";
            }
            LOCAL_ADDR_VALUE = init;
        }

        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            buf.append(LOCAL_ADDR_VALUE);
        }
    }

    protected class RemoteAddrElement implements AccessLogElement {
        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            if (requestAttributesEnabled) {
                Object addr = request.getAttribute(REMOTE_ADDR_ATTRIBUTE);
                if (addr == null) {
                    buf.append(request.getRemoteAddr());
                } else {
                    buf.append(addr);
                }
            } else {
                buf.append(request.getRemoteAddr());
            }
        }
    }

    protected class HostElement implements AccessLogElement {
        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            String value = null;
            if (requestAttributesEnabled) {
                Object host = request.getAttribute(REMOTE_HOST_ATTRIBUTE);
                if (host != null) {
                    value = host.toString();
                }
            }
            if (value == null || value.length() == 0) {
                value = request.getRemoteHost();
            }
            if (value == null || value.length() == 0) {
                value = "-";
            }
            buf.append(value);
        }
    }

    protected static class LogicalUserNameElement implements AccessLogElement {
        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            buf.append('-');
        }
    }

    protected class ProtocolElement implements AccessLogElement {
        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            if (requestAttributesEnabled) {
                Object proto = request.getAttribute(PROTOCOL_ATTRIBUTE);
                if (proto == null) {
                    buf.append(request.getProtocol());
                } else {
                    buf.append(proto);
                }
            } else {
                buf.append(request.getProtocol());
            }
        }
    }

    protected static class UserElement implements AccessLogElement {
        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            if (request != null) {
                String value = request.getRemoteUser();
                if (value != null) {
                    buf.append(value);
                } else {
                    buf.append('-');
                }
            } else {
                buf.append('-');
            }
        }
    }

    protected class DateAndTimeElement implements AccessLogElement {

        private static final String requestStartPrefix = "begin";

        private static final String responseEndPrefix = "end";

        private static final String prefixSeparator = ":";

        private static final String secFormat = "sec";

        private static final String msecFormat = "msec";

        private static final String msecFractionFormat = "msec_frac";

        private static final String msecPattern = "{#}";
        private static final String trippleMsecPattern =
            msecPattern + msecPattern + msecPattern;

        private String format = null;
        private boolean usesBegin = false;
        private FormatType type = FormatType.CLF;
        private boolean usesMsecs = false;

        protected DateAndTimeElement() {
            this(null);
        }

        private String tidyFormat(String format) {
            boolean escape = false;
            StringBuilder result = new StringBuilder();
            int len = format.length();
            char x;
            for (int i = 0; i < len; i++) {
                x = format.charAt(i);
                if (escape || x != 'S') {
                    result.append(x);
                } else {
                    result.append(msecPattern);
                    usesMsecs = true;
                }
                if (x == '\'') {
                    escape = !escape;
                }
            }
            return result.toString();
        }

        protected DateAndTimeElement(String header) {
            format = header;
            if (format != null) {
                if (format.equals(requestStartPrefix)) {
                    usesBegin = true;
                    format = "";
                } else if (format.startsWith(requestStartPrefix + prefixSeparator)) {
                    usesBegin = true;
                    format = format.substring(6);
                } else if (format.equals(responseEndPrefix)) {
                    usesBegin = false;
                    format = "";
                } else if (format.startsWith(responseEndPrefix + prefixSeparator)) {
                    usesBegin = false;
                    format = format.substring(4);
                }
                if (format.length() == 0) {
                    type = FormatType.CLF;
                } else if (format.equals(secFormat)) {
                    type = FormatType.SEC;
                } else if (format.equals(msecFormat)) {
                    type = FormatType.MSEC;
                } else if (format.equals(msecFractionFormat)) {
                    type = FormatType.MSEC_FRAC;
                } else {
                    type = FormatType.SDF;
                    format = tidyFormat(format);
                }
            }
        }

        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            long timestamp = date.getTime();
            long frac;
            if (usesBegin) {
                timestamp -= time;
            }
            switch (type) {
            case CLF:
                buf.append(localDateCache.get().getFormat(timestamp));
                break;
            case SEC:
                buf.append(timestamp / 1000);
                break;
            case MSEC:
                buf.append(timestamp);
                break;
            case MSEC_FRAC:
                frac = timestamp % 1000;
                if (frac < 100) {
                    if (frac < 10) {
                        buf.append('0');
                        buf.append('0');
                    } else {
                        buf.append('0');
                    }
                }
                buf.append(frac);
                break;
            case SDF:
                String temp = localDateCache.get().getFormat(format, locale, timestamp);
                if (usesMsecs) {
                    frac = timestamp % 1000;
                    StringBuilder trippleMsec = new StringBuilder(4);
                    if (frac < 100) {
                        if (frac < 10) {
                            trippleMsec.append('0');
                            trippleMsec.append('0');
                        } else {
                            trippleMsec.append('0');
                        }
                    }
                    trippleMsec.append(frac);
                    temp = temp.replace(trippleMsecPattern, trippleMsec);
                    temp = temp.replace(msecPattern, Long.toString(frac));
                }
                buf.append(temp);
                break;
            }
        }
    }

    protected static class RequestElement implements AccessLogElement {
        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            if (request != null) {
                String method = request.getMethod();
                if (method == null) {
                    // No method means no request line
                    buf.append('-');
                } else {
                    buf.append(request.getMethod());
                    buf.append(' ');
                    buf.append(request.getRequestURI());
                    if (request.getQueryString() != null) {
                        buf.append('?');
                        buf.append(request.getQueryString());
                    }
                    buf.append(' ');
                    buf.append(request.getProtocol());
                }
            } else {
                buf.append('-');
            }
        }
    }

    protected static class HttpStatusCodeElement implements AccessLogElement {
        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            if (response != null) {
                buf.append(response.getStatus());
            } else {
                buf.append('-');
            }
        }
    }

    protected class LocalPortElement implements AccessLogElement {
        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            if (requestAttributesEnabled) {
                Object port = request.getAttribute(SERVER_PORT_ATTRIBUTE);
                if (port == null) {
                    buf.append(request.getServerPort());
                } else {
                    buf.append(port);
                }
            } else {
                buf.append(request.getServerPort());
            }
        }
    }

    protected static class ByteSentElement implements AccessLogElement {
        private final boolean conversion;

        public ByteSentElement(boolean conversion) {
            this.conversion = conversion;
        }

        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            long length = response.getBytesWritten(false);
            if (length <= 0) {
                Object start = request.getAttribute(
                        Globals.SENDFILE_FILE_START_ATTR);
                if (start instanceof Long) {
                    Object end = request.getAttribute(
                            Globals.SENDFILE_FILE_END_ATTR);
                    if (end instanceof Long) {
                        length = ((Long) end).longValue() -
                                ((Long) start).longValue();
                    }
                }
            }
            if (length <= 0 && conversion) {
                buf.append('-');
            } else {
                buf.append(length);
            }
        }
    }

    protected static class MethodElement implements AccessLogElement {
        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            if (request != null) {
                buf.append(request.getMethod());
            }
        }
    }

    protected static class ElapsedTimeElement implements AccessLogElement {
        private final boolean millis;

        public ElapsedTimeElement(boolean millis) {
            this.millis = millis;
        }

        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            if (millis) {
                buf.append(time);
            } else {
                buf.append(time / 1000);
                buf.append('.');
                int remains = (int) (time % 1000);
                buf.append(remains / 100);
                remains = remains % 100;
                buf.append(remains / 10);
                buf.append(remains % 10);
            }
        }
    }

    protected static class FirstByteTimeElement implements AccessLogElement {
        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            long commitTime = response.getCoyoteResponse().getCommitTime();
            if (commitTime == -1) {
                buf.append('-');
            } else {
                long delta =
                        commitTime - request.getCoyoteRequest().getStartTime();
                buf.append(Long.toString(delta));
            }
        }
    }

    protected static class QueryElement implements AccessLogElement {
        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            String query = null;
            if (request != null) {
                query = request.getQueryString();
            }
            if (query != null) {
                buf.append('?');
                buf.append(query);
            }
        }
    }

    protected static class SessionIdElement implements AccessLogElement {
        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            if (request == null) {
                buf.append('-');
            } else {
                Session session = request.getSessionInternal(false);
                if (session == null) {
                    buf.append('-');
                } else {
                    buf.append(session.getIdInternal());
                }
            }
        }
    }

    protected static class RequestURIElement implements AccessLogElement {
        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            if (request != null) {
                buf.append(request.getRequestURI());
            } else {
                buf.append('-');
            }
        }
    }

    protected static class LocalServerNameElement implements AccessLogElement {
        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            buf.append(request.getServerName());
        }
    }

    protected static class StringElement implements AccessLogElement {
        private final String str;

        public StringElement(String str) {
            this.str = str;
        }

        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            buf.append(str);
        }
    }

    protected static class HeaderElement implements AccessLogElement {
        private final String header;

        public HeaderElement(String header) {
            this.header = header;
        }

        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            Enumeration<String> iter = request.getHeaders(header);
            if (iter.hasMoreElements()) {
                buf.append(iter.nextElement());
                while (iter.hasMoreElements()) {
                    buf.append(',').append(iter.nextElement());
                }
                return;
            }
            buf.append('-');
        }
    }

    protected static class CookieElement implements AccessLogElement {
        private final String header;

        public CookieElement(String header) {
            this.header = header;
        }

        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            String value = "-";
            Cookie[] c = request.getCookies();
            if (c != null) {
                for (int i = 0; i < c.length; i++) {
                    if (header.equals(c[i].getName())) {
                        value = c[i].getValue();
                        break;
                    }
                }
            }
            buf.append(value);
        }
    }

    protected static class ResponseHeaderElement implements AccessLogElement {
        private final String header;

        public ResponseHeaderElement(String header) {
            this.header = header;
        }

        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            if (null != response) {
                Iterator<String> iter = response.getHeaders(header).iterator();
                if (iter.hasNext()) {
                    buf.append(iter.next());
                    while (iter.hasNext()) {
                        buf.append(',').append(iter.next());
                    }
                    return;
                }
            }
            buf.append('-');
        }
    }

    protected static class RequestAttributeElement implements AccessLogElement {
        private final String header;

        public RequestAttributeElement(String header) {
            this.header = header;
        }

        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            Object value = null;
            if (request != null) {
                value = request.getAttribute(header);
            } else {
                value = "??";
            }
            if (value != null) {
                if (value instanceof String) {
                    buf.append((String) value);
                } else {
                    buf.append(value.toString());
                }
            } else {
                buf.append('-');
            }
        }
    }

    protected static class SessionAttributeElement implements AccessLogElement {
        private final String header;

        public SessionAttributeElement(String header) {
            this.header = header;
        }

        public void addElement(StringBuilder buf, Date date, Request request,
                Response response, long time) {
            Object value = null;
            if (null != request) {
                HttpSession sess = request.getSession(false);
                if (null != sess) {
                    value = sess.getAttribute(header);
                }
            } else {
                value = "??";
            }
            if (value != null) {
                if (value instanceof String) {
                    buf.append((String) value);
                } else {
                    buf.append(value.toString());
                }
            } else {
                buf.append('-');
            }
        }
    }


    protected AccessLogElement[] createLogElements() {
        List<AccessLogElement> list = new ArrayList<AccessLogElement>();
        boolean replace = false;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < pattern.length(); i++) {
            char ch = pattern.charAt(i);
            if (replace) {
                if ('{' == ch) {
                    StringBuilder name = new StringBuilder();
                    int j = i + 1;
                    for (; j < pattern.length() && '}' != pattern.charAt(j); j++) {
                        name.append(pattern.charAt(j));
                    }
                    if (j + 1 < pattern.length()) {
                        j++;
                        list.add(createAccessLogElement(name.toString(),
                                pattern.charAt(j)));
                        i = j;
                    } else {
                        list.add(createAccessLogElement(ch));
                    }
                } else {
                    list.add(createAccessLogElement(ch));
                }
                replace = false;
            } else if (ch == '%') {
                replace = true;
                list.add(new StringElement(buf.toString()));
                buf = new StringBuilder();
            } else {
                buf.append(ch);
            }
        }
        if (buf.length() > 0) {
            list.add(new StringElement(buf.toString()));
        }
        return list.toArray(new AccessLogElement[0]);
    }

    protected AccessLogElement createAccessLogElement(String header, char pattern) {
        switch (pattern) {
        case 'i':
            return new HeaderElement(header);
        case 'c':
            return new CookieElement(header);
        case 'o':
            return new ResponseHeaderElement(header);
        case 'r':
            return new RequestAttributeElement(header);
        case 's':
            return new SessionAttributeElement(header);
        case 't':
            return new DateAndTimeElement(header);
        default:
            return new StringElement("???");
        }
    }

    protected AccessLogElement createAccessLogElement(char pattern) {
        switch (pattern) {
        case 'a':
            return new RemoteAddrElement();
        case 'A':
            return new LocalAddrElement();
        case 'b':
            return new ByteSentElement(true);
        case 'B':
            return new ByteSentElement(false);
        case 'D':
            return new ElapsedTimeElement(true);
        case 'F':
            return new FirstByteTimeElement();
        case 'h':
            return new HostElement();
        case 'H':
            return new ProtocolElement();
        case 'l':
            return new LogicalUserNameElement();
        case 'm':
            return new MethodElement();
        case 'p':
            return new LocalPortElement();
        case 'q':
            return new QueryElement();
        case 'r':
            return new RequestElement();
        case 's':
            return new HttpStatusCodeElement();
        case 'S':
            return new SessionIdElement();
        case 't':
            return new DateAndTimeElement();
        case 'T':
            return new ElapsedTimeElement(false);
        case 'u':
            return new UserElement();
        case 'U':
            return new RequestURIElement();
        case 'v':
            return new LocalServerNameElement();
        case 'I':
            return new ThreadNameElement();
        default:
            return new StringElement("???" + pattern + "???");
        }
    }

}