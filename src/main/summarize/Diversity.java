/**
 * 
 */
package summarize;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhangchangmin
 *
 */

//keywords
public class Diversity {

	double lambda=0.5;
	
	public void calDiversity(Map<String,Double> senMap,String[] sumLast,
			Map<String,Double> termTodayMap,Map<String,Double> termLastMap){
		List<String> senToday=new ArrayList<String>();
		senToday.addAll(senMap.keySet());
		StringBuffer sb=toStringBuffer(sumLast);
		double normLastV=normLast(sb,termLastMap);
		for(String sen:senToday){
			double sum1=0.0,sum2=0.0,sumSame=0.0;
			for(String term:termTodayMap.keySet()){
				double t1=0.0,t2=0.0;
				if(sen.contains(term))
					t1=termTodayMap.get(term);
				if(termLastMap.keySet().contains(term)&&sb.toString().contains(term))
					t2=termLastMap.get(term);
				sumSame+=t1*t2;
				sum1+=t1*t1;
			}
			double distance=sumSame/(Math.sqrt(sum1)*normLastV);
			double weight=lambda*L2(senMap.get(sen))+(1-lambda)*L1(distance);
			senMap.put(sen, weight);
		}
	}
	public Set<String> sameWord(Set<String> termToday,Set<String> termLast){
		Set<String> same=new LinkedHashSet<String>();
		for(String term: termToday){
			if(termLast.contains(term)){
				same.add(term);
			}
		}
		return same;
	}
	//涓婁竴涓憳瑕佺殑妯�
	public double normLast(StringBuffer sumLast,Map<String,Double> termLastMap){
		double normValue=0.0;
		for(String term:termLastMap.keySet()){
			if(sumLast.toString().contains(term)){
				normValue+=Math.pow(termLastMap.get(term), 2);
			}
		}
		return Math.sqrt(normValue);
	}
	public double L1(double x){
		return 1.0/(1+Math.exp(x));
	}
	public double L2(double x){
		return Math.exp(x)/(1+Math.exp(x));
	}
	public StringBuffer toStringBuffer(String[] sa){
		StringBuffer sb=new StringBuffer();
		for(String s:sa){
			sb.append(s);
		}
		return sb;
	}
}
