package fasttext.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

public class OnMemoryLineReader extends LineReader {

	private static HashMap<String, List<String>> cache = new HashMap<String, List<String>>();
	private List<String> lines_;
	private int index_ = 0;

	private String lineDelimitingRegex_ = " |\r|\t|\\v|\f|\0";
	private Splitter splitter = Splitter.on(CharMatcher.anyOf(" \r\t\n\u000B\u0085\u2028\u2029\f\0"));

	public OnMemoryLineReader(String filename, String charsetName) throws IOException, UnsupportedEncodingException {
		synchronized (cache) {
			lines_ = cache.get(filename);
			if (lines_ == null) {
				lines_ = Files.readLines(new File(filename), Charset.forName(charsetName));
				cache.put(filename, lines_);
			}
		}
	}

	public OnMemoryLineReader(InputStream inputStream, String charsetName) throws UnsupportedEncodingException {
		try {
			lines_ = CharStreams.readLines(new InputStreamReader(inputStream, charset_));
		} catch (IOException e) {
			// TODO temporary hack.
			e.printStackTrace();
			throw new UnsupportedEncodingException(e.toString());
		}
	}

	@Override
	public long skipLine(long n) throws IOException {
		if (n < 0L) {
			throw new IllegalArgumentException("skip value is negative");
		}
		String line;
		long currentLine = 0;
		long readLine = 0;
		synchronized (lock) {
			while (currentLine < n && (line = this.readLine()) != null) {
				readLine++;
				if (line == null || line.isEmpty() || line.startsWith("#")) {
					continue;
				}
				currentLine++;
			}
			return readLine;
		}
	}

	@Override
	public String readLine() throws IOException {
		if(index_ < lines_.size())
			return lines_.get(index_++);
		else
			return null;
	}

	@Override
	public String[] readLineTokens() throws IOException {
		String line = readLine();
		if (line == null)
			return null;
		else
			return line.split(lineDelimitingRegex_, -1);
	}

	public Iterable<String> readLineTokens2() throws IOException {
		String line = readLine();
		if (line == null)
			return null;
		else
			return splitter.split(line);
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		// TODO temporary hack.
		return 0;
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void rewind() throws IOException {
		index_ = 0;
	}
}
