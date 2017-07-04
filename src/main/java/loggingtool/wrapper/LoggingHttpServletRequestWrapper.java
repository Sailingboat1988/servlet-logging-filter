package loggingtool.wrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

public class LoggingHttpServletRequestWrapper extends HttpServletRequestWrapper {

	private final Charset UTF_8 = Charset.forName("UTF-8");
	private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

	private static final String METHOD_POST = "POST";

//	private byte[] content;

//	private final Map<String, String[]> parameterMap;

	private final HttpServletRequest delegate;

	public LoggingHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		this.delegate = request;
	}

	public Map<String, String> getHeaders() {
		Map<String, String> headers = new HashMap(0);
		Enumeration<String> headerNames = getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			if (headerName != null) {
				headers.put(headerName, getHeader(headerName));
			}
		}
		return headers;
	}

	public Map<String, String> getParameters() {
		Map<String, String> parameters = new HashMap<String, String>();
		if (getParameterMap() != null) {
			for (Entry<String, String[]> entry : getParameterMap().entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue().length>0?entry.getValue()[0]:"[EMPTY]";
				parameters.put(key, value);
			}
		}
		return parameters;
	}

	public boolean isFormPost() {
		String contentType = getContentType();
		return (contentType != null && contentType.contains(FORM_CONTENT_TYPE) && METHOD_POST.equalsIgnoreCase(getMethod()));
	}
}
