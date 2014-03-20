/**
 * 
 */
package process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pojo.Sentence;
import service.SentenceService;

/**
 * @author zhangchangmin
 * 
 */
/**
 * @author coder
 *
 */
public class Indexer4MyDoc {
	static Logger logger = LoggerFactory.getLogger(Indexer4MyDoc.class);

	public void index4MyDoc(String date, Map<String, List<MyDoc>> map,
			Directory dir) {
		// <date,List<MyDoc>>
		List<MyDoc> docList = map.get(date);
		IndexWriter iw = null;
		try {
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_35,
					analyzer);
			iw = new IndexWriter(dir, conf);
			int count = 0;
			Document document = null;
			for (MyDoc myDoc : docList) {
				document = new Document();
				Field uniqueField = new Field("id", count + "", Store.YES,
						Index.NOT_ANALYZED);
				Field docNameField = new Field("docName", myDoc.getDocName(),
						Store.YES, Index.NOT_ANALYZED);
				Field dateField = new Field("date", date, Store.YES,
						Index.NOT_ANALYZED);// 显示用
				StringBuilder sb = new StringBuilder();
				List<Sentence> sentence = myDoc.getSentencesOfDoc();
				for (Sentence sen : sentence) {
					sb.append(sen.getSentenceContent());
				}
				Field contentField = new Field("body", sb.toString(),
						Store.YES, Index.ANALYZED, TermVector.YES);
				document.add(uniqueField);
				document.add(docNameField);
				document.add(contentField);
				document.add(dateField);
				iw.addDocument(document);
				count++;
				if (count % 100 == 0)
					iw.commit();
			}
			iw.commit();
			iw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (iw != null) {
				try {
					iw.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将同一个eventName的文档按照时间分组
	 * @param eventName
	 * @return
	 */
	public Map<String, List<MyDoc>> readFromDB(String eventName) {
		SentenceService sentenceService = new SentenceService();
		List<Sentence> sentenceList = sentenceService.getSentenceByByEventName(eventName);
		// docName
		Set<String> docNames = new HashSet<String>();//不同的文件名
		for (Sentence sen : sentenceList) {
			docNames.add(sen.getDocName());
		}
		// <docName,MyDoc>
		Map<String, MyDoc> nameDocMap = new HashMap<String, MyDoc>();//按照文件名存放
		// date
		Set<String> dates = new HashSet<String>();
		for (String docName : docNames) {
			String date = getDate(docName);
			dates.add(date);
			String time = getTime(docName);
			List<Sentence> sentencesOfDoc = getSentencesByDocName(docName, sentenceList);
			MyDoc myDoc = new MyDoc();
			myDoc.setDocName(docName);
			myDoc.setDate(date);
			myDoc.setTime(time);
			myDoc.setSentencesOfDoc(sentencesOfDoc);
			nameDocMap.put(docName, myDoc);
		}
		List<String> dateList = new ArrayList<String>(dates);
		Collections.sort(dateList);

		// <date,List<MyDoc>>
		Map<String, List<MyDoc>> sameDateDocMap = new HashMap<String, List<MyDoc>>();
		for (String date : dateList) {
			List<MyDoc> docSameDate = new ArrayList<MyDoc>();
			for (String dateName : docNames) {
				if (getDate(dateName).equals(date)) {
					docSameDate.add(nameDocMap.get(dateName));
				}
			}
			sameDateDocMap.put(date, docSameDate);
		}
		return sameDateDocMap;
	}

	public List<Sentence> getSentencesByDocName(String docName,
			List<Sentence> sentences) {
		List<Sentence> sentenceOfDoc = new ArrayList<Sentence>();
		for (Sentence sen : sentences) {
			if (sen.getDocName().equals(docName)) {
				sentenceOfDoc.add(sen);
			}
		}
		return sentenceOfDoc;
	}

	public String getDate(String docName) {
		int length = docName.length();
		String date = docName.substring(length - 13, length - 5);
		return date;
	}

	public String getTime(String docName) {
		int length = docName.length();
		String time = docName.substring(length - 4, length);
		return time;
	}
}
