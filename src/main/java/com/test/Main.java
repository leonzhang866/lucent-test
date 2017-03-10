package com.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * Created by yiche on 17/3/8.
 */
public class Main {

	public static void main(String[] args) throws IOException, ParseException {

		StandardAnalyzer analyzer = new StandardAnalyzer();
		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter w = new IndexWriter(index, config);

		long startTime = new Date().getTime();

		addDoc(w, "Lucene in Action", "193398817");
		addDoc(w, "Lucene for Dummies", "55320055Z");
		addDoc(w, "Managing Gigabytes", "55063554A");
		addDoc(w, "The Art of Computer Science", "9900333X");

		// indexWriter.optimize();
		w.close();
		long endTime = new Date().getTime();

		System.out.println("It took " + (endTime - startTime)
				+ " milliseconds to create an index");

		String querystr = "lucene";

		startTime = new Date().getTime();
		Query q = new QueryParser("name", analyzer).parse(querystr);
		IndexReader indexReader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(indexReader);

		int hitsPerPage = 10;
		TopScoreDocCollector collector = TopScoreDocCollector
				.create(hitsPerPage);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		System.out.println("Found " + hits.length + " hits.");
		endTime = new Date().getTime();
		System.out.println("It took " + (endTime - startTime)
				+ " milliseconds to search an index");

		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println((i + 1) + ". " + d.get("name") + "\t"
					+ d.get("isbn"));
		}
		indexReader.close();
	}

	private static void addDoc(IndexWriter iw, String name, String isbn)
			throws IOException {
		Document document = new Document();
		document.add(new TextField("name", name, Store.YES));
		document.add(new StringField("isbn", isbn, Store.YES));
		iw.addDocument(document);
	}
}
