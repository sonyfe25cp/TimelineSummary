/**
 * 
 */
package summarize;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Jama.Matrix;

/**
 * @author zhangchangmin
 *
 */

//calSVD
public class MMR2 {

	double lambda=0.7;
	public String[] summaryMMR(Map<String,Double> sentenceMap,
			Map<String,int[]> sentencePosMap,Matrix matrix){
		Map<String,Integer> selectedSen=new LinkedHashMap<String,Integer>();
		List<String> selectingSen=new ArrayList<String>();
		selectingSen.addAll(sentenceMap.keySet());
		selectedSen.put(selectingSen.get(0),0);
		selectingSen.remove(0);
		for(int i=0;i<sentenceMap.size()*0.05-1;i++){
			double[] mar=new double[selectingSen.size()];
			for(int j=0;j<selectingSen.size();j++){
				mar[j]=lambda*sentenceMap.get(selectingSen.get(j))
						-(1-lambda)*(marginal(matrix,selectingSen.get(j),selectedSen,sentenceMap));
			}
			String s=selectingSen.get(maxPosition(mar));
			int numth=0;
			if(!s.contains("？")){
				numth=findNumth(s,sentenceMap);
				selectedSen.put(s,numth);//选取得分最高的句子加入摘要
			}
			selectingSen.remove(maxPosition(mar));
		}
		List<String> sentences=new ArrayList<String>();
		sentences.addAll(selectedSen.keySet());
		return sentences.toArray(new String[0]);
	}
	//计算当前句子与已选句子相似度//之中的最大相似度
	public double marginal(Matrix matrix,String sentence,Map<String,Integer> selectedSen,Map<String,Double> sentenceMap){
		int numth=findNumth(sentence,sentenceMap);
		double[] sim=new double[selectedSen.size()];
		Iterator<String> iter=selectedSen.keySet().iterator();
		int i=0;
		while(iter.hasNext()){
			int num=selectedSen.get(iter.next());
			sim[i++]=calSimilarity(matrix,numth,num);
		}
		return max(sim);
	}
	public double calSimilarity(Matrix matrix,int numth,int num){
		int row=matrix.getRowDimension();
		double[] numthM=matrix.getMatrix(0, row-1, numth, numth).getColumnPackedCopy();
		double[] numM=matrix.getMatrix(0, row-1, num, num).getColumnPackedCopy();
		double multiSum=0.0,s1Sum=0.0,s2Sum=0.0;
		for(int i=0;i<row;i++){
				multiSum+=numthM[i]*numM[i];
				s1Sum=numthM[i]*numthM[i];
				s2Sum=numM[i]*numM[i];
		}
		return multiSum/(Math.sqrt(s1Sum)*Math.sqrt(s2Sum));
	}
	public int findNumth(String sen,Map<String,Double> sentenceMap){
		Iterator<String> iter=sentenceMap.keySet().iterator();
		int numth=0;
		while(iter.hasNext()){
			String s=iter.next();
			if(s.equals(sen)){
				break;
			}
			numth++;
		}
		return numth;
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
}
