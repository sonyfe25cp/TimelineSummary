/**
 * 
 */
package process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import summarize.Diversity;
import summarize.Diversity1;
import summarize.LSA;
import summarize.MMR;
import summarize.MMR2;
import summarize.TRM;
import summarize.TRM1;
import Jama.Matrix;

import compare.Centroid;

/**
 *
 */
public class SummaryRun extends TestCase{

	static Logger logger = LoggerFactory.getLogger(SummaryRun.class);
	
	private String eventName="D1001A";//事件名称
	private Map<String, Map<String, Double>> resultsMap = new HashMap<String, Map<String, Double>>();
	
	public void setEventName(String event){
		this.eventName = event;
	}
	public Map<String, Map<String, Double>> getResultsMap(){
		return resultsMap;
	}
	
	public void  testRun(){
		testAIL();
		testAIL_noeAging();
		testAIL_noLSA();
		testAllan();
		testCentroid();
		testRandom();
		System.out.println("----------------------------------------------------------");
		System.out.println("TaskName: "+eventName);
		for(Entry<String, Map<String, Double>> entry : resultsMap.entrySet()){
			String methodName = entry.getKey();
			System.out.println("methodName: "+methodName);
			Map<String, Double> results = entry.getValue();
			for(Entry<String, Double> result : results.entrySet()){
				String eva = result.getKey();
				double value = result.getValue();
				System.out.println(eva + " : "+value);
			}
		}
	}
	
	public void testAIL(){
		DAO doTrun=new DAO();
		doTrun.doTruncate();//删减last数据表
		List<String[]> list=new ArrayList<String[]>();
		Indexer4MyDoc i4m=new Indexer4MyDoc();
		Map<String,List<MyDoc>> map=i4m.readFromDB(eventName);//时间-文档 组合
		List<String> timeline=new ArrayList<>(map.keySet());
		Collections.sort(timeline);//按时间排序
		int i=1;
		Preprocess pre=new Preprocess();
		LSA lsa=new LSA();
		TRM1 trm=new TRM1();
		MMR2 mmr=new MMR2();
		Diversity1 div=new Diversity1();//计算cross-date diversity
		for(String time:timeline){
			logger.info("time : {}", time);
			if(i==1){
				List<DOC> tL=pre.catchTFIDF(time,map);
				List<String> sL=pre.sentences(pre.segSentence(tL,time,map));
				Map<String,Double> tfidfL=pre.globalTFIDF(tL);
				Map<String,Double> initEnergy=pre.initEnergy(tL, sL);
				Matrix matrixL=lsa.constructMatrix(tfidfL, sL);
				Matrix mL=lsa.calSVD(matrixL);
				Map<String,Double> trmMapLast=pre.sortD(trm.constructTRM(mL,sL));
				String[] summaryLast=mmr.summaryMMR(trmMapLast, pre.segSentence(tL),mL);////store
				list.add(summaryLast);
//				System.out.println(time);
//				for(int j=0;j<summaryLast.length;j++){
//					System.out.println(summaryLast[j]);
//				}
//				System.out.println();
				Last last=new Last();
				last.setId(i);
				last.setDate(time);
				last.setSentences(sL);
				last.setTermWeight(tfidfL);
				last.setEnergy(initEnergy);
				last.setSummary(summaryLast);
				DAO dao=new DAO();
				dao.insert(last);
				i++;
			}else{
				DAO dao=new DAO();
				Last last1=dao.selectByID(i-1);
				List<String> sL=last1.getSentencesList();
				Map<String,Double> tfidfL=last1.getTermWeightMap();
				String[] summaryLast=last1.getSummaryArray();
				List<DOC> tT=pre.catchTFIDF(time,map);
				List<String> sT=pre.sentences(pre.segSentence(tT,time,map));
				Map<String,Double> tfidfT=pre.globalTFIDF(tT);
				double[] kafang=pre.kafang(tT, sT, sL);//计算卡方
				Set<String> termSet=pre.termSet(tT);
				Map<String,Double> energy=pre.energy(termSet, kafang, tfidfL);//转化为能量值
				Map<String,Double> variation=pre.variation(energy, tfidfL);//得到变化率
				Map<String,Double> newWeight=pre.newWeight(tfidfT, variation);//赋予新权重
				Matrix matrixT=lsa.constructMatrix(newWeight, sT);
				Matrix mT=lsa.calSVD(matrixT);
				Map<String,Double> trmMapToday=pre.sortD(trm.constructTRM(mT,sT));
				div.calDiversity(trmMapToday, summaryLast, newWeight, tfidfL);
				String[] summaryToday=mmr.summaryMMR(trmMapToday,  pre.segSentence(tT),mT);
				list.add(summaryToday);
//				System.out.println(time);
//				for(int j=0;j<summaryToday.length;j++){
//					System.out.println(summaryToday[j]);
//				}
//				System.out.println();
				Last last=new Last();
				last.setId(i);
				last.setDate(time);
				last.setSentences(sT);
				last.setTermWeight(newWeight);
				last.setEnergy(energy);
				last.setSummary(summaryToday);
				dao.insert(last);
				i++;
			}
		}
		Evaluation e=new Evaluation();
		Map<String, Double> tmap = e.evalute(e.fun(e.manualTerms(eventName)), e.fun(e.autoTerms(list)));
		resultsMap.put("testAIL", tmap);
	}
	
	public void testAIL_noeAging(){
		DAO doTrun=new DAO();
		doTrun.doTruncate();
		List<String[]> list=new ArrayList<String[]>();
		Indexer4MyDoc i4m=new Indexer4MyDoc();
		Map<String,List<MyDoc>> map=i4m.readFromDB(eventName);
		List<String> timeline=new ArrayList<String>(map.keySet());
		Collections.sort(timeline);
		int i=1;
		Preprocess pre=new Preprocess();
		LSA lsa=new LSA();
		TRM1 trm=new TRM1();
		MMR2 mmr=new MMR2();
		Diversity div=new Diversity();//计算cross-date diversity
		for(String time:timeline){
			if(i==1){
				List<DOC> tL=pre.catchTFIDF(time,map);
				List<String> sL=pre.sentences(pre.segSentence(tL,time,map));
				Map<String,Double> tfidfL=pre.globalTFIDF(tL);
				Map<String,Double> initEnergy=pre.initEnergy(tL, sL);
				Matrix matrixL=lsa.constructMatrix(tfidfL, sL);
				Matrix mL=lsa.calSVD(matrixL);
				Map<String,Double> trmMapLast=pre.sortD(trm.constructTRM(mL,sL));
				String[] summaryLast=mmr.summaryMMR(trmMapLast,pre.segSentence(tL),mL);////store
				list.add(summaryLast);
//				System.out.println(time);
//				for(int j=0;j<summaryLast.length;j++){
//					System.out.println(summaryLast[j]);
//				}
//				System.out.println();
				Last last=new Last();
				last.setId(i);
				last.setDate(time);
				last.setSentences(sL);
				last.setTermWeight(tfidfL);
				last.setEnergy(initEnergy);
				last.setSummary(summaryLast);
				DAO dao=new DAO();
				dao.insert(last);
				i++;
			}else{
				DAO dao=new DAO();
				Last last1=dao.selectByID(i-1);
				List<String> sL=last1.getSentencesList();
				Map<String,Double> tfidfL=last1.getTermWeightMap();
				String[] summaryLast=last1.getSummaryArray();
				List<DOC> tT=pre.catchTFIDF(time,map);
				List<String> sT=pre.sentences(pre.segSentence(tT,time,map));
				Map<String,Double> tfidfT=pre.globalTFIDF(tT);
				double[] kafang=pre.kafang(tT, sT, sL);//计算卡方
				Set<String> termSet=pre.termSet(tT);
				Map<String,Double> energy=pre.energy(termSet, kafang, tfidfL);//转化为能量值
				Matrix matrixT=lsa.constructMatrix(tfidfT, sT);
				Matrix mT=lsa.calSVD(matrixT);
				Map<String,Double> trmMapToday=pre.sortD(trm.constructTRM(mT,sT));
				div.calDiversity(trmMapToday, summaryLast, tfidfT, tfidfL);
				String[] summaryToday=mmr.summaryMMR(trmMapToday,pre.segSentence(tT),mT);
				list.add(summaryToday);
//				System.out.println(time);
//				for(int j=0;j<summaryToday.length;j++){
//					System.out.println(summaryToday[j]);
//				}
//				System.out.println();
				Last last=new Last();
				last.setId(i);
				last.setDate(time);
				last.setSentences(sT);
				last.setTermWeight(tfidfT);
				last.setEnergy(energy);
				last.setSummary(summaryToday);
				dao.insert(last);
				i++;
			}
		}
		Evaluation e=new Evaluation();
		Map<String, Double> tmap =  e.evalute(e.fun(e.manualTerms(eventName)), e.fun(e.autoTerms(list)));
		resultsMap.put("testAIL_noeAging", tmap);
	}
	public void testAIL_noLSA(){
		DAO doTrun=new DAO();
		doTrun.doTruncate();
		List<String[]> list=new ArrayList<String[]>();
		Indexer4MyDoc i4m=new Indexer4MyDoc();
		Map<String,List<MyDoc>> map=i4m.readFromDB(eventName);
		List<String> timeline=new ArrayList<String>(map.keySet());
		Collections.sort(timeline);
		int i=1;
		Preprocess pre=new Preprocess();
		TRM trm=new TRM();
		MMR mmr=new MMR();
		Diversity1 div=new Diversity1();//计算cross-date diversity
		for(String time:timeline){
			if(i==1){
				List<DOC> tL=pre.catchTFIDF(time,map);
				List<String> sL=pre.sentences(pre.segSentence(tL,time,map));
				Map<String,Double> tfidfL=pre.globalTFIDF(tL);
				Map<String,Double> initEnergy=pre.initEnergy(tL, sL);
				Map<String,Double> trmMapLast=pre.sortD(trm.constructTRM(sL,tfidfL));
				String[] summaryLast=mmr.summaryMMR(trmMapLast, tfidfL,pre.segSentence(tL));////store
				list.add(summaryLast);
//				System.out.println(time);
//				for(int j=0;j<summaryLast.length;j++){
//					System.out.println(summaryLast[j]);
//				}
//				System.out.println();
				Last last=new Last();
				last.setId(i);
				last.setDate(time);
				last.setSentences(sL);
				last.setTermWeight(tfidfL);
				last.setEnergy(initEnergy);
				last.setSummary(summaryLast);
				DAO dao=new DAO();
				dao.insert(last);
				i++;
			}else{
				DAO dao=new DAO();
				Last last1=dao.selectByID(i-1);
				List<String> sL=last1.getSentencesList();
				Map<String,Double> tfidfL=last1.getTermWeightMap();
				String[] summaryLast=last1.getSummaryArray();
				List<DOC> tT=pre.catchTFIDF(time,map);
				List<String> sT=pre.sentences(pre.segSentence(tT,time,map));
				Map<String,Double> tfidfT=pre.globalTFIDF(tT);
				double[] kafang=pre.kafang(tT, sT, sL);//计算卡方
				Set<String> termSet=pre.termSet(tT);
				Map<String,Double> energy=pre.energy(termSet, kafang, tfidfL);//转化为能量值
				Map<String,Double> variation=pre.variation(energy, tfidfL);//得到变化率
				Map<String,Double> newWeight=pre.newWeight(tfidfT, variation);//赋予新权重
				Map<String,Double> trmMapToday=pre.sortD(trm.constructTRM(sT,newWeight));
				div.calDiversity(trmMapToday, summaryLast, newWeight, tfidfL);
				String[] summaryToday=mmr.summaryMMR(trmMapToday,newWeight,  pre.segSentence(tT));
				list.add(summaryToday);
//				System.out.println(time);
//				for(int j=0;j<summaryToday.length;j++){
//					System.out.println(summaryToday[j]);
//				}
//				System.out.println();
				Last last=new Last();
				last.setId(i);
				last.setDate(time);
				last.setSentences(sT);
				last.setTermWeight(newWeight);
				last.setEnergy(energy);
				last.setSummary(summaryToday);
				dao.insert(last);
				i++;
			}
		}
		Evaluation e=new Evaluation();
		Map<String, Double> tmap =  e.evalute(e.fun(e.manualTerms(eventName)), e.fun(e.autoTerms(list)));
		resultsMap.put("testAIL_noLSA", tmap);
	}
	public void testAllan(){
		DAO doTrun=new DAO();
		doTrun.doTruncate();
		List<String[]> list=new ArrayList<String[]>();
		Indexer4MyDoc i4m=new Indexer4MyDoc();
		Map<String,List<MyDoc>> map=i4m.readFromDB(eventName);
		List<String> timeline=new ArrayList<String>(map.keySet());
		Collections.sort(timeline);
		int i=1;
		Preprocess pre=new Preprocess();
		TRM trm=new TRM();
		MMR mmr=new MMR();
		Diversity div=new Diversity();//计算cross-date diversity
		for(String time:timeline){
			if(i==1){
				List<DOC> tL=pre.catchTFIDF(time,map);
				List<String> sL=pre.sentences(pre.segSentence(tL,time,map));
				Map<String,Double> tfidfL=pre.globalTFIDF(tL);
				Map<String,Double> initEnergy=pre.initEnergy(tL, sL);
				Map<String,Double> trmMapLast=pre.sortD(trm.constructTRM(sL,tfidfL));
				String[] summaryLast=mmr.summaryMMR(trmMapLast, tfidfL,pre.segSentence(tL));////store
				list.add(summaryLast);
//				System.out.println(time);
//				for(int j=0;j<summaryLast.length;j++){
//					System.out.println(summaryLast[j]);
//				}
//				System.out.println();
				Last last=new Last();
				last.setId(i);
				last.setDate(time);
				last.setSentences(sL);
				last.setTermWeight(tfidfL);
				last.setEnergy(initEnergy);
				last.setSummary(summaryLast);
				DAO dao=new DAO();
				dao.insert(last);
				i++;
			}else{
				DAO dao=new DAO();
				Last last1=dao.selectByID(i-1);
				List<String> sL=last1.getSentencesList();
				Map<String,Double> tfidfL=last1.getTermWeightMap();
				String[] summaryLast=last1.getSummaryArray();
				List<DOC> tT=pre.catchTFIDF(time,map);
				List<String> sT=pre.sentences(pre.segSentence(tT,time,map));
				Map<String,Double> tfidfT=pre.globalTFIDF(tT);
				double[] kafang=pre.kafang(tT, sT, sL);//计算卡方
				Set<String> termSet=pre.termSet(tT);
				Map<String,Double> energy=pre.energy(termSet, kafang, tfidfL);//转化为能量值
				Map<String,Double> trmMapToday=pre.sortD(trm.constructTRM(sT,tfidfT));
				div.calDiversity(trmMapToday, summaryLast, tfidfT, tfidfL);
				String[] summaryToday=mmr.summaryMMR(trmMapToday,tfidfT,  pre.segSentence(tT));
				list.add(summaryToday);
//				System.out.println(time);
//				for(int j=0;j<summaryToday.length;j++){
//					System.out.println(summaryToday[j]);
//				}
//				System.out.println();
				Last last=new Last();
				last.setId(i);
				last.setDate(time);
				last.setSentences(sT);
				last.setTermWeight(tfidfT);
				last.setEnergy(energy);
				last.setSummary(summaryToday);
				dao.insert(last);
				i++;
			}
		}
		Evaluation e=new Evaluation();
		Map<String, Double> tmap =  e.evalute(e.fun(e.manualTerms(eventName)), e.fun(e.autoTerms(list)));
		resultsMap.put("testAllan", tmap);
	}
	
	public void testCentroid(){
		DAO doTrun=new DAO();
		doTrun.doTruncate();
		List<String[]> list=new ArrayList<String[]>();
		Indexer4MyDoc i4m=new Indexer4MyDoc();
		Map<String,List<MyDoc>> map=i4m.readFromDB(eventName);
		List<String> timeline=new ArrayList<String>(map.keySet());
		Collections.sort(timeline);
		int i=1;
		Preprocess pre=new Preprocess();
		LSA lsa=new LSA();
		Centroid centroid=new Centroid();
		MMR2 mmr=new MMR2();
		Diversity1 div=new Diversity1();//计算cross-date diversity
		for(String time:timeline){
			if(i==1){
				List<DOC> tL=pre.catchTFIDF(time,map);
				List<String> sL=pre.sentences(pre.segSentence(tL,time,map));
				Map<String,Double> tfidfL=pre.globalTFIDF(tL);
				Map<String,Double> initEnergy=pre.initEnergy(tL, sL);
				Matrix matrixL=lsa.constructMatrix(tfidfL, sL);
				Matrix mL=lsa.calSVD(matrixL);
				Map<String,Double> trmMapLast=pre.sortD(centroid.centroid(sL,tfidfL));
				String[] summaryLast=mmr.summaryMMR(trmMapLast, pre.segSentence(tL),mL);////store
				list.add(summaryLast);
//				System.out.println(time);
//				for(int j=0;j<summaryLast.length;j++){
//					System.out.println(summaryLast[j]);
//				}
//				System.out.println();
				Last last=new Last();
				last.setId(i);
				last.setDate(time);
				last.setSentences(sL);
				last.setTermWeight(tfidfL);
				last.setEnergy(initEnergy);
				last.setSummary(summaryLast);
				DAO dao=new DAO();
				dao.insert(last);
				i++;
			}else{
				DAO dao=new DAO();
				Last last1=dao.selectByID(i-1);
				List<String> sL=last1.getSentencesList();
				Map<String,Double> tfidfL=last1.getTermWeightMap();
				String[] summaryLast=last1.getSummaryArray();
				List<DOC> tT=pre.catchTFIDF(time,map);
				List<String> sT=pre.sentences(pre.segSentence(tT,time,map));
				Map<String,Double> tfidfT=pre.globalTFIDF(tT);
				double[] kafang=pre.kafang(tT, sT, sL);//计算卡方
				Set<String> termSet=pre.termSet(tT);
				Map<String,Double> energy=pre.energy(termSet, kafang, tfidfL);//转化为能量值
				Matrix matrixT=lsa.constructMatrix(tfidfT, sT);
				Matrix mT=lsa.calSVD(matrixT);
				Map<String,Double> trmMapToday=pre.sortD(centroid.centroid(sT, tfidfT));
				div.calDiversity(trmMapToday, summaryLast, tfidfT, tfidfL);
				String[] summaryToday=mmr.summaryMMR(trmMapToday,  pre.segSentence(tT),mT);
				list.add(summaryToday);
//				System.out.println(time);
//				for(int j=0;j<summaryToday.length;j++){
//					System.out.println(summaryToday[j]);
//				}
//				System.out.println();
				Last last=new Last();
				last.setId(i);
				last.setDate(time);
				last.setSentences(sT);
				last.setTermWeight(tfidfT);
				last.setEnergy(energy);
				last.setSummary(summaryToday);
				dao.insert(last);
				i++;
			}
		}
		Evaluation e=new Evaluation();
		Map<String, Double> tmap =  e.evalute(e.fun(e.manualTerms(eventName)), e.fun(e.autoTerms(list)));
		resultsMap.put("testCentroid", tmap);
	}
	public void testRandom(){
		DAO doTrun=new DAO();
		doTrun.doTruncate();
		List<String[]> list=new ArrayList<String[]>();
		Indexer4MyDoc i4m=new Indexer4MyDoc();
		Map<String,List<MyDoc>> map=i4m.readFromDB(eventName);
		List<String> timeline=new ArrayList<String>(map.keySet());
		Collections.sort(timeline);
		int i=1;
		Preprocess pre=new Preprocess();
		for(String time:timeline){
			
				List<DOC> tL=pre.catchTFIDF(time,map);
				List<String> sL=pre.sentences(pre.segSentence(tL,time,map));
				List<String> sum=new ArrayList<String>();
				Random rd=new Random();
				for(int j=0;j<sL.size()*0.05-1;j++){
					int p=rd.nextInt(sL.size());
					if(!(sL.get(p).endsWith("？")||sL.get(p).endsWith("？”"))){
						sum.add(sL.get(p));
					}
				}
				String[] summaryLast=sum.toArray(new String[0]);
				list.add(summaryLast);
//				System.out.println(time);
//				for(int j=0;j<summaryLast.length;j++){
//					System.out.println(summaryLast[j]);
//				}
//				System.out.println();
				Last last=new Last();
				last.setId(i);
				last.setDate(time);
				last.setSummary(summaryLast);
				DAO dao=new DAO();
				dao.insert(last);
				i++;
			
		}
		Evaluation e=new Evaluation();
		Map<String, Double> tmap = e.evalute(e.fun(e.manualTerms(eventName)), e.fun(e.autoTerms(list)));
		resultsMap.put("testRandom", tmap);
	}
//	public void test1(){
//		double r=0.4692;
//		double p=0.4868;
//		System.out.println(2*p*r/(p+r));
//	}
//	public void test2(){
//		String s="aaa20120101.0024";
//		int length=s.length();
//		String ss=s.substring(length-4, length);
//		System.out.println(ss);
//	}
//	public static void main(String[] args){
//		SummaryRun sr=new SummaryRun();
//		sr.run();
//	}
}
