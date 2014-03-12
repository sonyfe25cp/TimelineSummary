package service;

import java.util.List;

import mapper.SentenceMapper;
import pojo.Sentence;

public class SentenceService extends Service{


	private SentenceMapper sentenceMapper = session.getMapper(SentenceMapper.class) ;
	
	public Sentence getFirstSenceOfDoc(String docName){
		Sentence s = sentenceMapper.getSentenceBySentenceIdAndDocName(1, docName);
		return s;
	}
	
	public List<Sentence> getAllSentences(){
		return sentenceMapper.getAllSentences();
	}
	
	public List<Sentence> getSentenceByByEventName(String eventName){
		return sentenceMapper.getSentencesByEventName(eventName);
	}
	
	public List<Sentence> getSummary(String eventName,String isSummary){
		return sentenceMapper.getSummary(eventName, isSummary);
	}
	
	public void insert(Sentence sentence){
		sentenceMapper.insert(sentence);
		commit();
	}
	
	public SentenceMapper getSentenceMapper() {
		return sentenceMapper;
	}
	public void setSentenceMapper(SentenceMapper sentenceMapper) {
		this.sentenceMapper = sentenceMapper;
	}
}
