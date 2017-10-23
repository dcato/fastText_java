package fasttext;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import fasttext.io.BufferedLineReader;
import fasttext.io.LineReader;
import fasttext.io.MappedByteBufferLineReader;
import fasttext.io.OnMemoryLineReader;

public class TestLineReaders {
	
	LineReader reader;
	
	@Before
	public void setUp() throws IOException {
	}

	@Test
	public void testOnMemoryLineReader() throws IOException {
		reader = new OnMemoryLineReader("LICENSE", "utf-8");
		testReader();
	}

	@Test
	public void testBufferedLineReader() throws IOException {
		reader = new BufferedLineReader("LICENSE", "utf-8");
		testReader();
	}

	@Test
	public void testMappedByteBufferLineReader() throws IOException {
		reader = new MappedByteBufferLineReader("LICENSE", "utf-8");
		testReader();
	}

	public void testReader() throws IOException {
		assertEquals("BSD License", reader.peekLine());
		assertEquals("BSD License", reader.readLine());
		assertEquals("For fastText software", reader.readLine());

		assertEquals("Copyright (c) 2016-present, Facebook, Inc. All rights reserved.", reader.peekLine());
		Iterator<String> tokens = reader.readLineTokens();
		assertEquals("Copyright", tokens.next());
		assertEquals("(c)", tokens.next());
		assertEquals("2016-present,", tokens.next());
		assertEquals("Facebook,", tokens.next());
		assertEquals("Inc.", tokens.next());
		assertEquals("All", tokens.next());
		assertEquals("rights", tokens.next());
		assertEquals("reserved.", tokens.next());
		assertFalse(tokens.hasNext());
		
		int i = 0;
		String line = reader.readLine();
		while (line != null) {
			i++;
			line = reader.readLine();
		}
		assertEquals(20, i);

		reader.rewind();
		assertEquals("BSD License", reader.readLine());
		reader.skipLine(12);
		assertEquals("THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND", reader.readLine());
	}
}
