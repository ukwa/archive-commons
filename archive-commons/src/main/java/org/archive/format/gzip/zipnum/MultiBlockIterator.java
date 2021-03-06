package org.archive.format.gzip.zipnum;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import org.archive.util.binsearch.SeekableLineReader;
import org.archive.util.iterator.AbstractPeekableIterator;
import org.archive.util.iterator.CloseableIteratorUtil;

/**
 * @author brad, ilya
 *
 */
public class MultiBlockIterator extends AbstractPeekableIterator<String> {
	private static final Logger LOGGER = Logger.getLogger(
			MultiBlockIterator.class.getName());

	private SeekableLineReader currLoader = null;
	private Iterator<SeekableLineReader> blockItr = null;

	/**
	 * @param blocks which should be fetched and unzipped, one after another
	 */
	public MultiBlockIterator(Iterator<SeekableLineReader> blockItr) {
//		if (LOGGER.isLoggable(Level.INFO)) {
//			LOGGER.info("Iterating over " + blocks.size() + " blocks");
//		}
		this.blockItr = blockItr;
	}
	
	@Override
	public String getNextInner() {
				
		try {		
			while (true) {
				if (currLoader == null) {
					if (blockItr.hasNext()) {
						currLoader = blockItr.next();
					} else {
						return null;
					}
				}
				
				// attempt to read the next line from this:
				String next = currLoader.readLine();
				
				if (next != null) {
					return next;
				}
		
				//currLoader.close();
				currLoader = null;
			}
		} catch (IOException io) {
			LOGGER.warning(io.toString());
			return null;
		}
	}

	@Override
	public void close() throws IOException {
		if (currLoader != null) {
			currLoader.close();
			currLoader = null;
		}
		
		if (blockItr != null) {
			CloseableIteratorUtil.attemptClose(blockItr);
			blockItr = null;
		}
	}
}
