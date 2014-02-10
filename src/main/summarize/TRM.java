/**
 * 
 */
package summarize;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangchangmin
 *
 */
public class TRM {

	public Map<String,Double> constructTRM(List<String> sentences,Map<String,Double> termMap){
		double[] simArray=new double[((int)Math.pow(sentences.size(), 2)-sentences.size())/2];
		Map<String,Double> sentenceSimMap=new LinkedHashMap<String,Double>();
		int count=0;
		for(int i=0;i<sentences.size();i++){
			for(int j=i+1;j<sentences.size();j++){
				simArray[count++]=calSimilarity(sentences.get(i),sentences.get(j),termMap);
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
	public double calSimilarity(String sentence1,String sentence2,Map<String,Double> termMap){
		double sameSum=0.0, sum1=0.0, sum2=0.0;
		for(String term:termMap.keySet()){
			if(sentence1.toLowerCase().contains(term)){
				sum1+=Math.pow(termMap.get(term), 2);
			}
			if(sentence2.toLowerCase().contains(term)){
				sum2+=Math.pow(termMap.get(term), 2);
			}
			if(sentence1.toLowerCase().contains(term)&&sentence2.toLowerCase().contains(term)){
				sameSum+=Math.pow(termMap.get(term), 2);
			}
		}
		double similarity=sameSum/(Math.sqrt(sum1)*Math.sqrt(sum2));
		return similarity;
	}
}
