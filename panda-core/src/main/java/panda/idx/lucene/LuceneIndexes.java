package panda.idx.lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import panda.idx.IndexException;
import panda.idx.Indexer;
import panda.idx.Indexes;


public class LuceneIndexes implements Indexes {
	public static final String DEFAULT = "default";
	
	protected Map<String, LuceneIndexer> indexes;

	public LuceneIndexes() {
		indexes = new HashMap<String, LuceneIndexer>();
	}

	protected Directory getDirectory(String name) {
		try {
			return FSDirectory.open(Paths.get(name));
		}
		catch (IOException e) {
			throw new IndexException("Failed to get lucene directory: " + name, e);
		}
	}
	
	protected Class<? extends Analyzer> getAnalyzerType(String name) {
		return StandardAnalyzer.class;
	}
	
	@Override
	public synchronized Indexer getIndexer() {
		return getIndexer(DEFAULT);
	}
	
	@Override
	public synchronized Indexer getIndexer(String name) {
		LuceneIndexer li = indexes.get(name);
		if (li == null) {
			li = new LuceneIndexer(name, getDirectory(name), getAnalyzerType(name));
			indexes.put(name, li);
		}
		return li;
	}

	protected synchronized void addIndexer(LuceneIndexer li) {
		indexes.put(li.name(), li);
	}

	@Override
	public synchronized void dropIndexer() {
		dropIndexer(DEFAULT);
	}
	
	@Override
	public synchronized void dropIndexer(String name) {
		LuceneIndexer li = indexes.remove(name);
		if (li == null) {
			return;
		}
		
		li.drop();
	}
	
	public synchronized void close() throws IOException {
		for (LuceneIndexer li : indexes.values()) {
			li.close();
		}
		indexes.clear();
	}
}
