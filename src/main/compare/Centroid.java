/**
 * 
 */
package compare;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import process.Preprocess;

/**
 * @author zhangchangmin
 *
 */
public class Centroid {

	Preprocess pre=new Preprocess();
	
	public Map<String,Double> centroid(List<String> sentences,Map<String,Double> termMap){
		Map<String,Double> sentenceWeight=new LinkedHashMap<String,Double>();
		Map<String,Double> centroidMap=selectCentroid(termMap);
		double centroidVal=centroidValue(centroidMap);
		for(String sen:sentences){
			double sum1=0.0,sumSame=0.0;
			for(String term:termMap.keySet()){
//			for(String term:centroidMap.keySet()){
				if(sen.contains(term)&&centroidMap.keySet().contains(term)){
					sumSame+=Math.pow(centroidMap.get(term), 2);
				}
				if(sen.contains(term)){
					sum1+=Math.pow(termMap.get(term), 2);
				}
			}
			double weight=sumSame/(Math.sqrt(sum1)*centroidVal);
			sentenceWeight.put(sen, weight);
		}
		return sentenceWeight;
	}
	public Map<String,Double> selectCentroid(Map<String,Double> termMap){
		Map<String,Double> sortedTerm=pre.sortD(termMap);
		Map<String,Double> centroid=new LinkedHashMap<String,Double>();
		Iterator<String> iter=sortedTerm.keySet().iterator();
		int count=0;
		while(count<sortedTerm.size()*0.2){
			String s=iter.next();
			centroid.put(s, sortedTerm.get(s));
			count++;
		}
		return centroid;
	}
	public double centroidValue(Map<String,Double> termMap){
		double sum=0.0;
		for(String term:termMap.keySet()){
			sum+=Math.pow(termMap.get(term), 2);
		}
		return sum;
	}
	
}
