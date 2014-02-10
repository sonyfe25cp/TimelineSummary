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
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import pojo.Sentence;
import service.SentenceService;

/**
 * @author zhangchangmin
 *
 */
public class Indexer4MyDoc {

	public void index4MyDoc(String date,Map<String,List<MyDoc>> map,Directory dir){
		//<date,List<MyDoc>>
		List<MyDoc> docList=map.get(date);
		IndexWriter iw = null;
		try{
			StandardAnalyzer analyzer=new StandardAnalyzer(Version.LUCENE_35);
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_35,
					analyzer);
			iw = new IndexWriter(dir, conf);
			int count = 0;
			Document document = null;
			for(MyDoc myDoc:docList){
				document = new Document();
				Field uniqueField = new Field("id", count + "", Store.YES,
						Index.NOT_ANALYZED);
				Field docNameField=new Field("docName",myDoc.getDocName(),Store.YES,Index.NOT_ANALYZED);
				Field dateField = new Field("date", date, Store.YES,
						Index.NOT_ANALYZED);// 显示用
				StringBuilder sb=new StringBuilder();
				List<Sentence> sentence=myDoc.getSentencesOfDoc();
				for(Sentence sen:sentence){
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
				if(count %100==0)
					iw.commit();
			}
			iw.commit();
			iw.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
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
	public Map<String,List<MyDoc>> readFromDB(String eventName){
		SentenceService sentenceService=new SentenceService();
		List<Sentence> sentenceList=sentenceService.getSentenceByByEventName(eventName);
		//docName
		Set<String> docNames=new HashSet<String>();
		for(Sentence sen:sentenceList){
			docNames.add(sen.getDocName());
		}
		//<docName,MyDoc>
		Map<String,MyDoc> nameDocMap=new HashMap<String,MyDoc>();
		Iterator<String> iter=docNames.iterator();
		while(iter.hasNext()){
			String docName=iter.next();
			String date=getDate(docName);
			String time=getTime(docName);
			List<Sentence> sentencesOfDoc=getSentencesByDocName(docName,sentenceList);
			MyDoc myDoc=new MyDoc();
			myDoc.setDocName(docName);
			myDoc.setDate(date);
			myDoc.setTime(time);
			myDoc.setSentencesOfDoc(sentencesOfDoc);
			nameDocMap.put(docName, myDoc);
		}
		//date
		Set<String> dates=new HashSet<String>();
		Iterator<String> iter1=docNames.iterator();
		while(iter1.hasNext()){
			dates.add(getDate(iter1.next()));
		}
		List<String> dateList=new ArrayList<String>(dates);
		Collections.sort(dateList);
		
		//<date,List<MyDoc>>
		Map<String,List<MyDoc>> sameDateDocMap=new HashMap<String,List<MyDoc>>();
		for(String date:dateList){
//			System.out.println(date);
			List<MyDoc> docSameDate=new ArrayList<MyDoc>();
			Iterator<String> iter2=docNames.iterator();
			while(iter2.hasNext()){
				String dateName=iter2.next();
				if(getDate(dateName).equals(date)){
					docSameDate.add(nameDocMap.get(dateName));
				}
			}
			sameDateDocMap.put(date, docSameDate);
		}
		return sameDateDocMap;
	}
	
	public List<Sentence> getSentencesByDocName(String docName,List<Sentence> sentences){
		List<Sentence> sentenceOfDoc=new ArrayList<Sentence>();
		for(Sentence sen:sentences){
			if(sen.getDocName().equals(docName)){
				sentenceOfDoc.add(sen);
			}
		}
		return sentenceOfDoc;
	}
	
	public String getDate(String docName){
		int length=docName.length();
		String date=docName.substring(length-13, length-5);
		return date;
	}
	public String getTime(String docName){
		int length=docName.length();
		String time=docName.substring(length-4, length);
		return time;
	}
}
