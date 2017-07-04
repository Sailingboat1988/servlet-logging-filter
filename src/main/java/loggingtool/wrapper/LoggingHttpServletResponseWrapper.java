package loggingtool.wrapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


public class LoggingHttpServletResponseWrapper extends HttpServletResponseWrapper {

	private final Charset UTF_8 = Charset.forName("UTF-8");
	private final LoggingServletOutpuStream loggingServletOutpuStream = new LoggingServletOutpuStream();

	private final HttpServletResponse delegate;

	private int httpStatus = 200;

	public LoggingHttpServletResponseWrapper(HttpServletResponse response) {
		super(response);
		delegate = response;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return loggingServletOutpuStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
//		return new PrintWriter(loggingServletOutpuStream.baos);
		String responseEncoding = delegate.getCharacterEncoding();
		return new ResponsePrintWriter(loggingServletOutpuStream.baos, responseEncoding != null ? responseEncoding : UTF_8.name());
	}

	public Map<String, String> getHeaders() {
		Map<String, String> headers = new HashMap(0);
		for (String headerName : getHeaderNames()) {
			headers.put(headerName, getHeader(headerName));
		}
		return headers;
	}

	public String getContent() {
		try {
			String responseEncoding = delegate.getCharacterEncoding();
			return loggingServletOutpuStream.baos.toString(responseEncoding != null ? responseEncoding : UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			return "[UNSUPPORTED ENCODING]";
		}
	}

	public byte[] getContentAsBytes() {
		return loggingServletOutpuStream.baos.toByteArray();
	}

	@Override
	public void reset() {
		super.reset();
		httpStatus = 200;
	}

	@Override
	public void sendError(int sc) throws IOException {
		httpStatus = sc;
		super.sendError(sc);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		httpStatus = sc;
		super.sendError(sc, msg);
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		httpStatus = super.SC_FOUND;
		super.sendRedirect(location);
	}

	@Override
	public void setStatus(int sc) {
		httpStatus = sc;
		super.setStatus(sc);
	}

	public int getStatus() {
		return httpStatus;
	}

	private class LoggingServletOutpuStream extends ServletOutputStream {

		private ByteArrayOutputStream baos = new ByteArrayOutputStream();

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setWriteListener(WriteListener writeListener) {
		}

		@Override
		public void write(int b) throws IOException {
			baos.write(b);
		}

		@Override
		public void write(byte[] b) throws IOException {
			baos.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			baos.write(b, off, len);
		}
	}

	private class ResponsePrintWriter extends PrintWriter {
		private ResponsePrintWriter(OutputStream buf, String characterEncoding) throws UnsupportedEncodingException {
			super(new OutputStreamWriter(buf, characterEncoding));
		}
		@Override
		public void write(char buf[], int off, int len) {
			super.write(buf, off, len);
			super.flush();
		}
		@Override
		public void write(String s, int off, int len) {
			super.write(s, off, len);
			super.flush();
		}
		@Override
		public void write(int c) {
			super.write(c);
			super.flush();
		}
	}
}
