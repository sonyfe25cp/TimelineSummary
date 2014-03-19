/**
 * 
 */
package summarize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangchangmin
 *
 */

//keywords
public class MMR {
	
	double lambda=0.7;
	public String[] summaryMMR(Map<String,Double> sentenceMap,Map<String,Double> termMap,Map<String,int[]> sentencePosMap){
		List<String> selectedSen=new ArrayList<String>();
		List<String> selectingSen=new ArrayList<String>();
		selectingSen.addAll(sentenceMap.keySet());
		selectedSen.add(selectingSen.get(0));
		selectingSen.remove(0);
		for(int i=0;i<sentenceMap.size()*0.05-1;i++){
			double[] mar=new double[selectingSen.size()];
			for(int j=0;j<selectingSen.size();j++){
				mar[j]=lambda*sentenceMap.get(selectingSen.get(j))
						-(1-lambda)*(marginal(selectingSen.get(j),selectedSen,termMap));
			}
			String s=selectingSen.get(maxPosition(mar));
			if(!s.contains("？"))
				selectedSen.add(s);//选取得分最高的句子加入摘要
			selectingSen.remove(maxPosition(mar));
		}
		return selectedSen.toArray(new String[0]);
	}
	
	//计算当前句子与已选句子相似度//之中的最大相似度
	public double marginal(String sentence,List<String> selectedSen,Map<String,Double> termMap){
		return calSimilarity(sentence,selectedSen,termMap);
	}

	// 计算两个句子的相似度
	public double calSimilarity(String sentence1, List<String> selectedSen,Map<String, Double> termMap) {
		double sameSum = 0.0, sum1 = 0.0, sum2 = 0.0;
		for(String term:termMap.keySet()){
			double t1=0.0,t2=0.0;
			if(sentence1.contains(term)){
				t1=termMap.get(term);
			}
			sum1 +=t1*t1;
			if(selectedSen.toString().contains(term)){
				t2=termMap.get(term);
			}
			sum2+=t2*t2;
			sameSum+=t1*t2;
		}
		double similarity = sameSum / (Math.sqrt(sum1) * Math.sqrt(sum2));
		return similarity;///selectedSen.size();
	}
	//计算数组最大值的位置下标
	public int maxPosition(double[] values){
		int max=0;
		double maximum=0.0;
		for(int i=0;i<values.length;i++){
			if(values[i]>maximum){
				maximum=values[i];
				max=i;
			}
		}
		return max;
	}
	//计算数组最大值
	public double max(double[] values){
		double maximum=0.0;
		for(int i=0;i<values.length;i++){
			if(values[i]>maximum)
				maximum=values[i];
		}
		return maximum;
	}
	//对句子按时间顺序重新排序
	public String[] reordering(List<String> sentences,Map<String,int[]> sentencePosMap){
		int[][] pos=new int[sentences.size()][2];
		for(int i=0;i<sentences.size();i++){
			pos[i]=sentencePosMap.get(sentences.get(i));
		}
		String[] array=sentences.toArray(new String[0]);
		sortByTime(array,pos);
		return array;
	}
	public void sortByTime(String[] sen,int[][] pos){
		String tempS;
		int[] tempV=new int[2];
		int boundary;
		int exchange=pos.length-1;
		while(exchange!=0){
			boundary=exchange;
			exchange=0;
			for(int i=0;i<boundary;i++){
				if(pos[i][0]>pos[i+1][0]||(pos[i][0]==pos[i+1][0]&&pos[i][1]>pos[i+1][1])){
					tempS=sen[i+1];tempV[0]=pos[i+1][0];tempV[1]=pos[i+1][1];
					sen[i+1]=sen[i];pos[i+1][0]=pos[i][0];pos[i+1][1]=pos[i][1];
					sen[i]=tempS;pos[i][0]=tempV[0];pos[i][1]=tempV[1];
					exchange=i;
				}
			}
		}
	}
	public double L1(double x){
		return 1.0/(1+Math.exp(x));
	}
	public double L2(double x){
		return Math.exp(x)/(1+Math.exp(x));
	}
}
