package fasttext.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class OnMemoryLineReader extends LineReader {

	private static HashMap<String, List<String>> cache = new HashMap<String, List<String>>();
	private List<String> lines_;
	private int index_ = 0;

	public OnMemoryLineReader(String filename, String charsetName) throws IOException, UnsupportedEncodingException {
		synchronized (cache) {
			lines_ = cache.get(filename);
			if (lines_ == null) {
				lines_ = FileUtils.readLines(new File(filename), Charset.forName(charsetName));
				cache.put(filename, lines_);
			}
		}
	}

	public OnMemoryLineReader(InputStream inputStream, String charsetName) throws UnsupportedEncodingException {
		try {
			lines_ = IOUtils.readLines(inputStream, charset_);
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
			return StringUtils.split(line, " \r\t\n\u000B\u0085\u2028\u2029\f\0");
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void rewind() throws IOException {
		index_ = 0;
	}
}
