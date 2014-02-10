/**
 * 
 */
package summarize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhangchangmin
 *
 */
//plsa
public class TRM2 {

	//构建TRM
	public Map<String,Double> constructTRM(List<String> sentences,Map<String,Double> termMap,Map<String,Integer> w){
		double[] simArray=new double[((int)Math.pow(sentences.size(), 2)-sentences.size())/2];
		Map<String,Double> sentenceSimMap=new LinkedHashMap<String,Double>();
		int count=0;
		for(int i=0;i<sentences.size();i++){
			for(int j=i+1;j<sentences.size();j++){
				simArray[count++]=calSimilarity(sentences.get(i),sentences.get(j),termMap,w);
			}
		}
		for(int i=0;i<sentences.size();i++){
			double sumSim=0.0;
			for(int j=0;j<sentences.size();j++){
				if(j!=i&&j>i){
					sumSim +=simArray[i*(2*sentences.size()-i-1)/2+j-i-1];
				}
				else if(j!=i&&j<i){
					sumSim +=simArray[j*(2*sentences.size()-j-1)/2+i-j-1];
				}
			}
			sentenceSimMap.put(sentences.get(i), sumSim/sentences.size());
		}
		return sentenceSimMap;
	}
	//计算两个句子的相似度
	public double calSimilarity(String sentence1,String sentence2,Map<String,Double> termMap,Map<String,Integer> w){
		Map<String,Double> words1=new LinkedHashMap<String,Double>();
		Map<String,Double> words2=new LinkedHashMap<String,Double>();
		double sameSum=0.0, sum1=0.0, sum2=0.0;
		for(String term:termMap.keySet()){
			if(sentence1.toLowerCase().contains(term)){
				words1.put(term,termMap.get(term));
				sum1+=Math.pow(termMap.get(term), 2);
			}
			if(sentence2.toLowerCase().contains(term)){
				words2.put(term,termMap.get(term));
				sum2+=Math.pow(termMap.get(term), 2);
			}
			if(sentence1.toLowerCase().contains(term)&&sentence2.toLowerCase().contains(term)){
				sameSum+=Math.pow(termMap.get(term), 2);
			}
		}
		semantic(words1,words2,sameSum,w);
		double similarity=sameSum/(Math.sqrt(sum1)*Math.sqrt(sum2));
		return similarity;
	}
	public void semantic(Map<String,Double> words1,Map<String,Double> words2,double sameSum,Map<String,Integer> w){
		List<String> wordList1=new ArrayList<String>();
		wordList1.addAll(words1.keySet());
		List<String> wordList2=new ArrayList<String>();
		wordList2.addAll(words2.keySet());
		for(int i=0;i<wordList1.size();i++){
			for(int j=0;j<wordList2.size();j++){
				if(w.get(wordList1.get(i))==w.get(wordList2.get(j))){
					sameSum+=words1.get(wordList1.get(i))*words2.get(wordList2.get(j));
				}
			}
		}
	}
}
