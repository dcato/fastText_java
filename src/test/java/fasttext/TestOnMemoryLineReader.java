package fasttext;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import fasttext.io.OnMemoryLineReader;

public class TestOnMemoryLineReader {
	
	OnMemoryLineReader reader;
	
	@Before
	public void setUp() throws IOException {
		reader = new OnMemoryLineReader("LICENSE", "utf-8");
	}

	@Test
	public void testReadLine() throws IOException {
		assertEquals("BSD License", reader.readLine());
		assertEquals("", reader.readLine());
		assertEquals("For fastText software", reader.readLine());

		int i = 0;
		String line = reader.readLine();
		while (line != null) {
			i++;
			line = reader.readLine();
		}
		assertEquals(27, i);
	}

	@Test
	public void testReadLineTokens() throws IOException {
		Iterator<String> tokens = reader.readLineTokens2();
		assertEquals("BSD", tokens.next());
		assertEquals("License", tokens.next());
		assertFalse(tokens.hasNext());
	}

	@Test
	public void testRewind() throws IOException {
		assertEquals("BSD License", reader.readLine());
		reader.rewind();
		assertEquals("BSD License", reader.readLine());
	}
}
