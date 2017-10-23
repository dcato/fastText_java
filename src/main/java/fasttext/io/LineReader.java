package fasttext.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;

public abstract class LineReader extends Reader {

	protected InputStream inputStream_ = null;
	protected File file_ = null;
	protected Charset charset_ = null;

	protected String peekedLine_ = null;

	private String lineDelimitingRegex_ = " |\r|\t|\\v|\f|\0";


	protected LineReader() {
		super();
	}

	protected LineReader(Object lock) {
		super(lock);
	}

	public LineReader(String filename, String charsetName) throws IOException, UnsupportedEncodingException {
		this();
		this.file_ = new File(filename);
		this.charset_ = Charset.forName(charsetName);
	}

	public LineReader(InputStream inputStream, String charsetName) throws UnsupportedEncodingException {
		this();
		this.inputStream_ = inputStream;
		this.charset_ = Charset.forName(charsetName);
	}

	public String peekLine() throws IOException {
		if (peekedLine_ != null)
			return peekedLine_;
		peekedLine_ = readLineInternal();
		return peekedLine_;
	}
	
	public String readLine() throws IOException {
		if (peekedLine_ != null) {
			String result = peekedLine_;
			peekedLine_ = null;
			return result;
		}
		return readLineInternal();
	}
	
	public Iterator<String> readLineTokens() throws IOException {
		String line = readLine();
		if (line == null)
			return null;
		else {
			Scanner scanner = new Scanner(line);
			scanner.useDelimiter(lineDelimitingRegex_);
			return scanner;
		}
	}

	/**
	 * Skips lines.
	 * 
	 * @param n
	 *            The number of lines to skip
	 * @return The number of lines actually skipped
	 * @exception IOException
	 *                If an I/O error occurs
	 * @exception IllegalArgumentException
	 *                If <code>n</code> is negative.
	 */
	public abstract long skipLine(long n) throws IOException;
	
	protected abstract String readLineInternal() throws IOException;

	public abstract void rewind() throws IOException;
}
