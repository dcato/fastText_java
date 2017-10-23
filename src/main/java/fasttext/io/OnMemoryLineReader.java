package fasttext.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OnMemoryLineReader extends LineReader {

	private static HashMap<String, List<String>> cache = new HashMap<String, List<String>>();
	private List<String> lines_;
	private int index_ = 0;

	public OnMemoryLineReader(String filename, String charsetName) throws IOException, UnsupportedEncodingException {
		synchronized (cache) {
			lines_ = cache.get(filename);
			if (lines_ == null) {
				lines_ = new ArrayList<String>();
				readAllLines(new BufferedReader(new InputStreamReader(new FileInputStream(filename), charsetName)), lines_);
				cache.put(filename, lines_);
			}
		}
	}

	public OnMemoryLineReader(InputStream inputStream, String charsetName) throws UnsupportedEncodingException {
		try {
			lines_ = new ArrayList<String>();
			readAllLines(new BufferedReader(new InputStreamReader(inputStream, charsetName)), lines_);
		} catch (IOException e) {
			// TODO temporary hack.
			e.printStackTrace();
			throw new UnsupportedEncodingException(e.toString());
		}
	}

	private void readAllLines(BufferedReader reader, List<String> lines) throws IOException {
		try {
			for (;;) {
				String line = reader.readLine();
				if (line == null)
					break;
				lines_.add(line);
			}
		} finally {
			reader.close();
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
	protected String readLineInternal() throws IOException {
		while (index_ < lines_.size()) {
			String lineString = lines_.get(index_++);
			if (! lineString.isEmpty() && ! lineString.startsWith("#"))
				return lineString;
		}
		return null;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		throw new UnsupportedOperationException("read() not supported on OnMemoryLineReader.");
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void rewind() throws IOException {
		index_ = 0;
	}
}
