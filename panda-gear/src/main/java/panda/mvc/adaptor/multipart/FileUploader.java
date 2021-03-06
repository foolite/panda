package panda.mvc.adaptor.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import panda.io.MimeTypes;
import panda.io.SizeLimitExceededException;
import panda.io.Streams;
import panda.io.stream.LimitedInputStream;
import panda.lang.time.DateTimes;
import panda.net.http.MultipartStream;
import panda.net.http.MultipartStream.ItemInputStream;
import panda.net.http.MultipartStream.ProgressListener;
import panda.net.http.MultipartStream.ProgressNotifier;
import panda.net.http.ParameterParser;
import panda.servlet.HttpServlets;

/**
 * <p>
 * High level API for processing file uploads.
 * </p>
 * <p>
 * This class handles multiple files per single HTML widget, sent using <code>multipart/mixed</code>
 * encoding type, as specified by <a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>. 
 * </p>
 * <p>
 * How the data for individual parts is stored is determined by the factory used to create them; a
 * given part may be in memory, on disk, or somewhere else.
 * </p>
 * 
 */
public class FileUploader {

	// ----------------------------------------------------- Manifest constants
	/**
	 * Content-disposition value for form data.
	 */
	public static final String FORM_DATA = "form-data";

	/**
	 * Content-disposition value for file attachment.
	 */
	public static final String ATTACHMENT = "attachment";

	/**
	 * unlimited size: -1
	 */
	public static final long UNLIMITED_SIZE = -1;

	// ----------------------------------------------------------- Data members

	/**
	 * The maximum size permitted for the complete request, as opposed to {@link #fileSizeMax}. A
	 * value of -1 indicates no maximum.
	 */
	private long bodySizeMax = UNLIMITED_SIZE;

	/**
	 * The maximum size permitted for a single uploaded file, as opposed to {@link #bodySizeMax}. A
	 * value of -1 indicates no maximum.
	 */
	private long fileSizeMax = UNLIMITED_SIZE;

	/**
	 * The timeout of drain a request.
	 * default: 20s
	 */
	private long drainTimeout = 20 * DateTimes.MS_SECOND;

	/**
	 * The content encoding to use when reading part headers.
	 */
	private String headerEncoding;

	/**
	 * The progress listener.
	 */
	private MultipartStream.ProgressListener listener;

	/**
	 * Returns the maximum allowed size of a complete request, as opposed to
	 * {@link #getFileSizeMax()}.
	 * 
	 * @return The maximum allowed size, in bytes. The default value of -1 indicates, that there is
	 *         no limit.
	 * @see #setBodySizeMax(long)
	 */
	public long getBodySizeMax() {
		return bodySizeMax;
	}

	/**
	 * Sets the maximum allowed size of a complete request, as opposed to
	 * {@link #setFileSizeMax(long)}.
	 * 
	 * @param sizeMax The maximum allowed size, in bytes. The default value of -1 indicates, that
	 *            there is no limit.
	 * @see #getBodySizeMax()
	 */
	public void setBodySizeMax(long sizeMax) {
		this.bodySizeMax = sizeMax;
	}

	/**
	 * Returns the maximum allowed size of a single uploaded file, as opposed to
	 * {@link #getBodySizeMax()}.
	 * 
	 * @see #setFileSizeMax(long)
	 * @return Maximum size of a single uploaded file.
	 */
	public long getFileSizeMax() {
		return fileSizeMax;
	}

	/**
	 * Sets the maximum allowed size of a single uploaded file, as opposed to {@link #getBodySizeMax()}.
	 * 
	 * @see #getFileSizeMax()
	 * @param fileSizeMax Maximum size of a single uploaded file.
	 */
	public void setFileSizeMax(long fileSizeMax) {
		this.fileSizeMax = fileSizeMax;
	}

	/**
	 * @return the drainTimeout
	 */
	public long getDrainTimeout() {
		return drainTimeout;
	}

	/**
	 * @param drainTimeout the drainTimeout to set
	 */
	public void setDrainTimeout(long drainTimeout) {
		this.drainTimeout = drainTimeout;
	}

	/**
	 * Retrieves the character encoding used when reading the headers of an individual part. When
	 * not specified, or <code>null</code>, the request encoding is used. If that is also not
	 * specified, or <code>null</code>, the platform default encoding is used.
	 * 
	 * @return The encoding used to read part headers.
	 */
	public String getHeaderEncoding() {
		return headerEncoding;
	}

	/**
	 * Specifies the character encoding to be used when reading the headers of individual part. When
	 * not specified, or <code>null</code>, the request encoding is used. If that is also not
	 * specified, or <code>null</code>, the platform default encoding is used.
	 * 
	 * @param encoding The encoding used to read part headers.
	 */
	public void setHeaderEncoding(String encoding) {
		headerEncoding = encoding;
	}

	// --------------------------------------------------------- Public methods

	/**
	 * Processes an <a href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a> compliant
	 * <code>multipart/form-data</code> stream.
	 * 
	 * @param req The context for the request to be parsed.
	 * @return An iterator to instances of <code>FileItemStream</code> parsed from the request, in
	 *         the order that they were transmitted.
	 * @throws IOException An I/O error occurred. This may be a network error while communicating
	 *             with the client or a problem while storing the uploaded content.
	 */
	public FileItemIterator getItemIterator(HttpServletRequest req) throws IOException {
		return new FileItemIteratorImpl(req);
	}

	// ------------------------------------------------------ Protected methods

	/**
	 * Retrieves the boundary from the <code>Content-type</code> header.
	 * 
	 * @param contentType The value of the content type header from which to extract the boundary
	 *            value.
	 * @return The boundary, as a byte array.
	 */
	protected byte[] getBoundary(String contentType) {
		return HttpServlets.getBoundaryBytes(contentType);
	}

	/**
	 * Retrieves the file name from the <code>Content-disposition</code> header.
	 * 
	 * @param headers The HTTP headers object.
	 * @return The file name for the current <code>encapsulation</code>.
	 */
	protected String getFileName(FileItemHeaders headers) {
		return getFileName(headers.getString(FileItemHeaders.CONTENT_DISPOSITION));
	}

	/**
	 * Returns the given content-disposition headers file name.
	 * 
	 * @param pContentDisposition The content-disposition headers value.
	 * @return The file name
	 */
	private String getFileName(String pContentDisposition) {
		String fileName = null;
		if (pContentDisposition != null) {
			String cdl = pContentDisposition.toLowerCase(Locale.ENGLISH);
			if (cdl.startsWith(FORM_DATA) || cdl.startsWith(ATTACHMENT)) {
				ParameterParser parser = new ParameterParser(true);
				// Parameter parser can handle null input
				fileName = parser.get(pContentDisposition, "filename", ';');
				if (fileName != null) {
					fileName = fileName.trim();
				}
			}
		}
		return fileName;
	}

	/**
	 * Retrieves the field name from the <code>Content-disposition</code> header.
	 * 
	 * @param headers A <code>Map</code> containing the HTTP request headers.
	 * @return The field name for the current <code>encapsulation</code>.
	 */
	protected String getFieldName(FileItemHeaders headers) {
		return getFieldName(headers.getString(FileItemHeaders.CONTENT_DISPOSITION));
	}

	/**
	 * Returns the field name, which is given by the content-disposition header.
	 * 
	 * @param pContentDisposition The content-dispositions header value.
	 * @return The field jake
	 */
	private String getFieldName(String pContentDisposition) {
		String fieldName = null;
		if (pContentDisposition != null && pContentDisposition.toLowerCase(Locale.ENGLISH).startsWith(FORM_DATA)) {
			ParameterParser parser = new ParameterParser(true);
			// Parameter parser can handle null input
			fieldName = parser.get(pContentDisposition, "name", ';');
			if (fieldName != null) {
				fieldName = fieldName.trim();
			}
		}
		return fieldName;
	}

	/**
	 * <p>
	 * Parses the <code>header-part</code> and returns as key/value pairs.
	 * <p>
	 * If there are multiple headers of the same names, the name will map to a comma-separated list
	 * containing the values.
	 * 
	 * @param headerPart The <code>header-part</code> of the current <code>encapsulation</code>.
	 * @return A <code>Map</code> containing the parsed HTTP request headers.
	 */
	protected FileItemHeaders getParsedHeaders(String headerPart) {
		final int len = headerPart.length();
		FileItemHeaders headers = newFileItemHeaders();
		int start = 0;
		for (;;) {
			int end = parseEndOfLine(headerPart, start);
			if (start == end) {
				break;
			}
			StringBuilder header = new StringBuilder(headerPart.substring(start, end));
			start = end + 2;
			while (start < len) {
				int nonWs = start;
				while (nonWs < len) {
					char c = headerPart.charAt(nonWs);
					if (c != ' ' && c != '\t') {
						break;
					}
					++nonWs;
				}
				if (nonWs == start) {
					break;
				}
				// Continuation line found
				end = parseEndOfLine(headerPart, nonWs);
				header.append(" ").append(headerPart.substring(nonWs, end));
				start = end + 2;
			}
			parseHeaderLine(headers, header.toString());
		}
		return headers;
	}

	/**
	 * Creates a new instance of {@link FileItemHeaders}.
	 * 
	 * @return The new instance.
	 */
	protected FileItemHeaders newFileItemHeaders() {
		return new FileItemHeaders();
	}

	/**
	 * Skips bytes until the end of the current line.
	 * 
	 * @param headerPart The headers, which are being parsed.
	 * @param end Index of the last byte, which has yet been processed.
	 * @return Index of the \r\n sequence, which indicates end of line.
	 */
	private int parseEndOfLine(String headerPart, int end) {
		int index = end;
		for (;;) {
			int offset = headerPart.indexOf('\r', index);
			if (offset == -1 || offset + 1 >= headerPart.length()) {
				throw new IllegalStateException("Expected headers to be terminated by an empty line.");
			}
			if (headerPart.charAt(offset + 1) == '\n') {
				return offset;
			}
			index = offset + 1;
		}
	}

	/**
	 * Reads the next header line.
	 * 
	 * @param headers String with all headers.
	 * @param header Map where to store the current header.
	 */
	private void parseHeaderLine(FileItemHeaders headers, String header) {
		final int colonOffset = header.indexOf(':');
		if (colonOffset == -1) {
			// This header line is malformed, skip it.
			return;
		}
		String headerName = header.substring(0, colonOffset).trim();
		String headerValue = header.substring(header.indexOf(':') + 1).trim();
		headers.addString(headerName, headerValue);
	}

	private class FileItemIteratorImpl implements FileItemIterator {

		/**
		 * Default implementation of {@link FileItemStream}.
		 */
		class FileItemStreamImpl implements FileItemStream {

			/**
			 * The file items content type.
			 */
			private final String contentType;

			/**
			 * The file items field name.
			 */
			private final String fieldName;

			/**
			 * The file items file name.
			 */
			private final String name;

			/**
			 * Whether the file item is a form field.
			 */
			private final boolean formField;

			/**
			 * The file items input stream.
			 */
			private final InputStream stream;

			/**
			 * The headers, if any.
			 */
			private FileItemHeaders headers;

			/**
			 * Creates a new instance.
			 * 
			 * @param pName The items file name, or null.
			 * @param pFieldName The items field name.
			 * @param pContentType The items content type, or null.
			 * @param pFormField Whether the item is a form field.
			 * @param pContentLength The items content length, if known, or -1
			 * @throws IOException Creating the file item failed.
			 */
			FileItemStreamImpl(String pName, String pFieldName, String pContentType, boolean pFormField,
					long pContentLength) throws IOException {
				name = pName;
				fieldName = pFieldName;
				contentType = pContentType;
				formField = pFormField;
				final ItemInputStream itemStream = multi.newInputStream();
				InputStream istream = itemStream;
				if (fileSizeMax != -1) {
					if (pContentLength != -1 && pContentLength > fileSizeMax) {
						sizeExceeded(fileSizeMax, pContentLength);
					}
					istream = new LimitedInputStream(istream, fileSizeMax) {
						@Override
						protected void raiseError(long pSizeMax, long pCount) throws IOException {
							pCount += Streams.safeDrain(in, drainTimeout);
							itemStream.close(true);
							sizeExceeded(pSizeMax, pCount);
						}
					};
				}
				stream = istream;
			}

			protected void sizeExceeded(long pSizeMax, long pCount) throws IOException {
				FileSizeLimitExceededException e = new FileSizeLimitExceededException(String.format(
					"The field %s exceeds it's maximum permitted size of %s bytes.", fieldName,
					Long.valueOf(pSizeMax)), pCount, pSizeMax);
				e.setFieldName(fieldName);
				e.setFileName(name);
				throw e;
			}
			
			/**
			 * Returns the items content type, or null.
			 * 
			 * @return Content type, if known, or null.
			 */
			public String getContentType() {
				return contentType;
			}

			/**
			 * Returns the items field name.
			 * 
			 * @return Field name.
			 */
			public String getFieldName() {
				return fieldName;
			}

			/**
			 * Returns the items file name.
			 * 
			 * @return File name, if known, or null.
			 */
			public String getName() {
				return name;
			}

			/**
			 * Returns, whether this is a form field.
			 * 
			 * @return True, if the item is a form field, otherwise false.
			 */
			public boolean isFormField() {
				return formField;
			}

			/**
			 * Returns an input stream, which may be used to read the items contents.
			 * 
			 * @return Opened input stream.
			 * @throws IOException An I/O error occurred.
			 */
			public InputStream openStream() throws IOException {
				return stream;
			}

			/**
			 * Closes the file item.
			 * 
			 * @throws IOException An I/O error occurred.
			 */
			void close() throws IOException {
				stream.close();
			}

			/**
			 * Returns the file item headers.
			 * 
			 * @return The items header object
			 */
			public FileItemHeaders getHeaders() {
				return headers;
			}

			/**
			 * Sets the file item headers.
			 * 
			 * @param pHeaders The items header object
			 */
			public void setHeaders(FileItemHeaders pHeaders) {
				headers = pHeaders;
			}

		}

		/**
		 * The multi part stream to process.
		 */
		private final MultipartStream multi;

		/**
		 * The notifier, which used for triggering the {@link ProgressListener}.
		 */
		private final ProgressNotifier notifier;

		/**
		 * The boundary, which separates the various parts.
		 */
		private final byte[] boundary;

		/**
		 * The item, which we currently process.
		 */
		private FileItemStreamImpl currentItem;

		/**
		 * The current items field name.
		 */
		private String currentFieldName;

		/**
		 * Whether we are currently skipping the preamble.
		 */
		private boolean skipPreamble;

		/**
		 * Whether the current item may still be read.
		 */
		private boolean itemValid;

		/**
		 * Whether we have seen the end of the file.
		 */
		private boolean eof;

		/**
		 * Creates a new instance.
		 * 
		 * @param req The request context.
		 * @throws IOException An I/O error occurred.
		 */
		FileItemIteratorImpl(final HttpServletRequest req) throws IOException {
			InputStream input = req.getInputStream();

			final long requestSize = HttpServlets.getContentLength(req);

			if (bodySizeMax >= 0) {
				if (requestSize != -1 && requestSize > bodySizeMax) {
					sizeExceeded(bodySizeMax, requestSize);
				}
				input = new LimitedInputStream(input, bodySizeMax) {
					@Override
					protected void raiseError(long pSizeMax, long pCount) throws IOException {
						pCount += Streams.safeDrain(in, drainTimeout);
						sizeExceeded(pSizeMax, pCount);
					}
				};
			}

			String charEncoding = headerEncoding;
			if (charEncoding == null) {
				charEncoding = HttpServlets.getEncoding(req);
			}

			boundary = getBoundary(req.getContentType());
			if (boundary == null) {
				throw new InvalidContentTypeException("the request was rejected because no multipart boundary was found");
			}

			notifier = new ProgressNotifier(listener, requestSize);
			try {
				multi = new MultipartStream(input, boundary, notifier);
			}
			catch (IllegalArgumentException iae) {
				throw new InvalidContentTypeException(String.format("The boundary specified in the %s header is too long",
					FileItemHeaders.CONTENT_TYPE), iae);
			}
			multi.setHeaderEncoding(charEncoding);

			skipPreamble = true;
			findNextItem();
		}

		private void sizeExceeded(long pSizeMax, long pCount) throws IOException {
			throw new SizeLimitExceededException(String.format(
				"the request was rejected because its size (%s) exceeds the configured maximum (%s)",
				Long.valueOf(pCount), Long.valueOf(pSizeMax)), pCount, pSizeMax);
		}

		/**
		 * Called for finding the next item, if any.
		 * 
		 * @return True, if an next item was found, otherwise false.
		 * @throws IOException An I/O error occurred.
		 */
		private boolean findNextItem() throws IOException {
			if (eof) {
				return false;
			}
			if (currentItem != null) {
				currentItem.close();
				currentItem = null;
			}
			for (;;) {
				boolean nextPart;
				if (skipPreamble) {
					nextPart = multi.skipPreamble();
				}
				else {
					nextPart = multi.readBoundary();
				}
				if (!nextPart) {
					if (currentFieldName == null) {
						// Outer multipart terminated -> No more data
						eof = true;
						return false;
					}
					// Inner multipart terminated -> Return to parsing the outer
					multi.setBoundary(boundary);
					currentFieldName = null;
					continue;
				}
				FileItemHeaders headers = getParsedHeaders(multi.readHeaders());
				if (currentFieldName == null) {
					// We're parsing the outer multipart
					String fieldName = getFieldName(headers);
					if (fieldName != null) {
						String subContentType = headers.getString(FileItemHeaders.CONTENT_TYPE);
						if (subContentType != null
								&& subContentType.toLowerCase(Locale.ENGLISH).startsWith(MimeTypes.MULTIPART_MIXED)) {
							currentFieldName = fieldName;
							// Multiple files associated with this field name
							byte[] subBoundary = getBoundary(subContentType);
							multi.setBoundary(subBoundary);
							skipPreamble = true;
							continue;
						}
						String fileName = getFileName(headers);
						currentItem = new FileItemStreamImpl(fileName, fieldName, headers.getString(FileItemHeaders.CONTENT_TYPE),
							fileName == null, getContentLength(headers));
						currentItem.setHeaders(headers);
						notifier.noteItem();
						itemValid = true;
						return true;
					}
				}
				else {
					String fileName = getFileName(headers);
					if (fileName != null) {
						currentItem = new FileItemStreamImpl(fileName, currentFieldName,
							headers.getString(FileItemHeaders.CONTENT_TYPE), false, getContentLength(headers));
						currentItem.setHeaders(headers);
						notifier.noteItem();
						itemValid = true;
						return true;
					}
				}
				multi.discardBodyData();
			}
		}

		private long getContentLength(FileItemHeaders headers) {
			try {
				return Long.parseLong(headers.getString(FileItemHeaders.CONTENT_LENGTH));
			}
			catch (Exception e) {
				return -1;
			}
		}

		/**
		 * Returns, whether another instance of {@link FileItemStream} is available.
		 * 
		 * @throws IOException Reading the file item failed.
		 * @return True, if one or more additional file items are available, otherwise false.
		 */
		public boolean hasNext() throws IOException {
			if (eof) {
				return false;
			}
			if (itemValid) {
				return true;
			}
			return findNextItem();
		}

		/**
		 * Returns the next available {@link FileItemStream}.
		 * 
		 * @throws java.util.NoSuchElementException No more items are available. Use
		 *             {@link #hasNext()} to prevent this exception.
		 * @throws IOException Reading the file item failed.
		 * @return FileItemStream instance, which provides access to the next file item.
		 */
		public FileItemStream next() throws IOException {
			if (eof || (!itemValid && !hasNext())) {
				throw new NoSuchElementException();
			}
			itemValid = false;
			return currentItem;
		}
	}

	/**
	 * Returns the progress listener.
	 * 
	 * @return The progress listener, if any, or null.
	 */
	public ProgressListener getProgressListener() {
		return listener;
	}

	/**
	 * Sets the progress listener.
	 * 
	 * @param pListener The progress listener, if any. Defaults to null.
	 */
	public void setProgressListener(ProgressListener pListener) {
		listener = pListener;
	}

}
