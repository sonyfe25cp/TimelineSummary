/**
 * 
 */
package process;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import pojo.Sentence;
import service.SentenceService;

/**
 * @author zhangchangmin
 *
 */
public class Evaluation {
	
	private double recall;
	private double precise;
	private double fValue;

	
	Preprocess pre=new Preprocess();
	public StringBuffer manualTerms(String eventName){
		SentenceService sentenceService=new SentenceService();
		List<Sentence> sentenceList=sentenceService.getSummary(eventName, "true");
		StringBuffer sb=new StringBuffer();
		for(Sentence sen:sentenceList){
			sb.append(sen.getSentenceContent());
		}
		return sb;
	}
	public StringBuffer autoTerms(List<String[]> summary){
		StringBuffer sb=new StringBuffer();
		for(String[] s:summary){
			for(int i=0;i<s.length;i++){
				sb.append(s[i]);
			}
		}
		return sb;
	}
	public Set<String> fun(StringBuffer sb){
		Set<String> termSet=new LinkedHashSet<String>();
		try{
			Directory dir=new RAMDirectory();
			IndexWriter iw = null;
			StandardAnalyzer analyzer=new StandardAnalyzer(Version.LUCENE_35);
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_35,
					analyzer);
			iw = new IndexWriter(dir, conf);
			Document doc= new Document();
			Field contentField = new Field("body", sb.toString(),
					Store.YES, Index.ANALYZED, TermVector.YES);
			doc.add(contentField);
			iw.addDocument(doc);
			iw.commit();
			iw.close();
			
			IndexReader ir = IndexReader.open(dir, true);
			for(int i=0;i<ir.numDocs();i++){
				TermFreqVector tfv=ir.getTermFreqVector(i, "body");
				Map<String,Integer> termsTF=pre.filter(tfv.getTerms(),tfv.getTermFrequencies());
				String[] terms=termsTF.keySet().toArray(new String[0]);
				for(int j=0;j<terms.length;j++){
					termSet.add(terms[j]);
				}
			}
			ir.close();
			dir.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return termSet;
	}
	public Map<String, Double> evalute(Set<String> termManual,Set<String> termAuto){
		Iterator<String> iter=termAuto.iterator();
		double i=0.0;
		while(iter.hasNext()){
			String s=iter.next();
			if(termManual.contains(s)){
				i=i+1;
			}
		}
		recall=i/termManual.size();
		precise=i/termAuto.size();
		fValue=(2*recall*precise)/(recall+precise);
//		System.out.println(termManual.size()+"\t"+termAuto.size());
//		System.out.println("召回率="+recall);
//		System.out.println("精确率="+precise);
//		System.out.println("F值="+fValue);
		Map<String, Double> map = new HashMap<String, Double>();
		map.put("recall", recall);
		map.put("precise", precise);
		map.put("fvalue", fValue);
		return map;
		
	}
	
}
