/**
 * 
 */
package process;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.RAMDirectory;

import edu.bit.dlde.utils.DLDEConfiguration;

/**
 * @author zhangchangmin
 * 
 */
public class Preprocess {
	double alpha = 0.118659;
	double beta = 0.145198;

	// 计算TFIDF值
	public List<DOC> catchTFIDF(String date, Map<String, List<MyDoc>> map) {
		Directory dir = new RAMDirectory();
		List<DOC> list = new ArrayList<DOC>();
		Indexer4MyDoc index = new Indexer4MyDoc();
		index.index4MyDoc(date, map, dir);
		try {
			IndexReader ir = IndexReader.open(dir, true);
			DOC doc = null;
			for (int i = 0; i < ir.numDocs(); i++) {
				doc = new DOC();
				TermFreqVector tfv = ir.getTermFreqVector(i, "body");
				Map<String, Integer> termsTF = filter(tfv.getTerms(),
						tfv.getTermFrequencies());
				String[] terms = termsTF.keySet().toArray(new String[0]);
				Integer[] TF = termsTF.values().toArray(new Integer[0]);// tfv.getTermFrequencies();
				int[] DF = new int[terms.length];
				double[] score = new double[terms.length];
				for (int j = 0; j < terms.length; j++) {
					DF[j] = ir.docFreq(new Term("body", terms[j]));
					score[j] = TF[j]
							* Math.log((ir.numDocs() + 1) / (DF[j] + 0.5))
							* 100;
				}
				doc.setDocId(ir.document(i).get("id"));
				doc.setDocName(ir.document(i).get("docName"));
				doc.setSentences(ir.document(i).get("body").toLowerCase());
				doc.setTime(timeInt(ir.document(i).get("docName")));
				doc.setTerms(terms);
				doc.setTF(TF);
				doc.setTFIDF(score);
				list.add(doc);
			}
			ir.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// 分句,并存储句子位置信息
	public Map<String, int[]> segSentence(List<DOC> list, String date,
			Map<String, List<MyDoc>> map) {
		// List<String> sentenceList=new ArrayList<String>();
		List<MyDoc> docList = map.get(date);
		Map<String, int[]> sentencePosMap = new LinkedHashMap<String, int[]>();
		for (int index = 0; index < list.size(); index++) {
			for (int i = 0; i < docList.size(); i++) {
				if (docList.get(i).getDocName()
						.equals(list.get(index).getDocName())) {
					for (int j = 0; j < docList.get(i).getSentencesOfDoc()
							.size(); j++) {
						if (!sentencePosMap.containsKey(docList.get(i)
								.getSentencesOfDoc().get(j)
								.getSentenceContent())) {
							int[] position = new int[3];
							position[0] = list.get(index).getTime();// 文档时间
							position[1] = j;// 句子序号
							position[2] = Integer.parseInt(list.get(index)
									.getDocId());// 文档id
							// System.out.println(docList.get(i).getSentencesOfDoc().get(j).getSentenceContent()+","+position[0]+","+position[1]+","+position[2]);
							sentencePosMap.put(docList.get(i)
									.getSentencesOfDoc().get(j)
									.getSentenceContent(), position);
						}
					}
				}
			}
		}
		return sentencePosMap;
	}

	// 分句,并存储句子位置信息
	public Map<String, int[]> segSentence(List<DOC> list) {
		Map<String, int[]> sentencePosMap = new LinkedHashMap<String, int[]>();
		for (int index = 0; index < list.size(); index++) {
			String[] paragraph = list.get(index).getSentences().split("\r\n");
			String s1 = new String();// char
			String s2;// for sentence
			int count = 0;// sentence number
			for (int i = 0; i < paragraph.length; i++) {
				int j = 0;
				int k = j;
				while (j < paragraph[i].length()) {
					s1 = String.valueOf(paragraph[i].charAt(j));
					if (s1.equals("…"))
						j = j + 1;// 因为……是由两个…组成的，一次只能得到一个…，所以要多做一步处理
					if (s1.equals("。") || s1.equals("？") || s1.equals("！")
							|| s1.equals("…")) {
						if ((j + 1) < paragraph[i].length()
								&& String.valueOf(paragraph[i].charAt(j + 1))
										.equals("”")) {
							j = j + 1;
						}
						s2 = new String(paragraph[i].substring(k, j + 1));// new
																			// sentence
						if (!sentencePosMap.containsKey(s2)) {
							int[] position = new int[3];
							position[0] = list.get(index).getTime();// 文档时间
							position[1] = count++;// 句子序号
							position[2] = Integer.parseInt(list.get(index)
									.getDocId());// 文档id
							// sentenceList.add(s2);
							sentencePosMap.put(s2, position);
						}
						k = j + 1;
					}
					j = j + 1;
				}
			}
		}
		return sentencePosMap;
	}

	// 获得句子内容
	public List<String> sentences(Map<String, int[]> sentencePosMap) {
		List<String> sentenceList = new ArrayList<String>();
		sentenceList.addAll(sentencePosMap.keySet());
		return sentenceList;
	}

	// calculate kafang
	public double[] kafang(List<DOC> tList, List<String> senList1,
			List<String> senList2) {
		Set<String> termSet = termSet(tList);// merge(tList1,tList2);
		int[][] senNum1 = statistic(termSet, senList1);
		int[][] senNum2 = statistic(termSet, senList2);
		double[] termkafang = new double[termSet.size()];
		for (int i = 0; i < termkafang.length; i++) {
			termkafang[i] = alpha
					* ((senNum1[i][0] + senNum1[i][1] + senNum2[i][0] + senNum2[i][1]) * Math
							.pow((senNum1[i][0] * senNum2[i][1] - senNum1[i][1]
									* senNum2[i][0]), 2))
					/ ((senNum1[i][0] + senNum1[i][1])
							* (senNum2[i][0] + senNum2[i][1])
							* (senNum1[i][0] + senNum2[i][0]) * (senNum1[i][1] + senNum2[i][1]));
		}
		return termkafang;
	}

	// calculate energy
	public Map<String, Double> energy(Set<String> termSet1,
			double[] termkafang, Map<String, Double> termEnergyMap2) {
		Map<String, Double> termEnergyMap = new LinkedHashMap<String, Double>();
		Iterator<String> iterator = termSet1.iterator();
		int index = 0;
		double energy = 0.0;
		double energyDecay = 0.0;
		while (iterator.hasNext()) {
			String s = iterator.next();
			if (termEnergyMap2.containsKey(s)) {
				energyDecay = termEnergyMap2.get(s) - beta;
				if (energyDecay < 0)
					energyDecay = 0.0;
				energy = (energyDecay + termkafang[index])
						/ (1 + energyDecay + termkafang[index]);
			} else {
				energy = termkafang[index] / (1 + termkafang[index]);
			}
			termEnergyMap.put(s, energy);
			index++;
		}
		return termEnergyMap;
	}

	// calculate variation
	public Map<String, Double> variation(Map<String, Double> termEnergyMap1,
			Map<String, Double> termEnergyMap2) {
		Map<String, Double> termVarMap = new LinkedHashMap<String, Double>();
		Set<String> termSet = termEnergyMap1.keySet();
		Iterator<String> iterator = termSet.iterator();
		double var;
		while (iterator.hasNext()) {
			String s = iterator.next();
			if (termEnergyMap2.keySet().contains(s)) {
				var = Math
						.abs((termEnergyMap1.get(s) - termEnergyMap2.get(s)) / 2);
			} else {
				var = Math.abs((termEnergyMap1.get(s)) / 2);
			}
			termVarMap.put(s, var);
		}
		return termVarMap;
	}

	// new weight
	public Map<String, Double> newWeight(Map<String, Double> tMap,
			Map<String, Double> termVarMap) {
		Set<String> keyset = tMap.keySet();
		Map<String, Double> newWeightMap = new LinkedHashMap<String, Double>();
		Map<String, Integer> sortedTMap = sortI(tMap);
		Map<String, Integer> sortedVarMap = sortI(termVarMap);
		Iterator<String> iterator = keyset.iterator();
		while (iterator.hasNext()) {
			String s = iterator.next();
			double m = (sortedTMap.get(s) - sortedVarMap.get(s))
					/ keyset.size();
			double newWeight = tMap.get(s) + termVarMap.get(s) * (1 + m);
			newWeightMap.put(s, newWeight);
		}
		return sortD(newWeightMap);
	}

	// 计算每个词在整个文档集中的总TFIDF得分:(tfi/tf总)*IDF
	public Map<String, Double> globalTFIDF(List<DOC> list) {
		Map<String, Double> tMap = new LinkedHashMap<String, Double>();
		double score;
		int total = totalNum(list);// 文档集中的单词总数
		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.get(i).getTerms().length; j++) {
				if (tMap.containsKey(list.get(i).getTerms()[j])) {
					score = tMap.get(list.get(i).getTerms()[j])
							+ list.get(i).getTFIDF()[j] / total;
				} else {
					score = list.get(i).getTFIDF()[j] / total;
				}
				tMap.put(list.get(i).getTerms()[j], score);
			}
		}
		return tMap;
	}

	public Map<String, Double> global(Map<String, Double> termMap,
			List<String> sentenceList) {
		int[][] containOrNot = statistic(termMap.keySet(), sentenceList);
		Iterator<String> iter = termMap.keySet().iterator();
		int i = 0;
		while (iter.hasNext()) {
			String s = iter.next();
			double d = termMap.get(s)
					* Math.log((double) (containOrNot[i++][0])
							/ sentenceList.size());
			termMap.put(s, d);
		}
		return termMap;
	}

	// 计算文档集中的单词总数
	public int totalNum(List<DOC> list) {
		int total = 0;
		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.get(i).getTF().length; j++) {
				total += list.get(i).getTF()[j];
			}
		}
		return total;
	}

	// 统计文档集中不重复的单词集
	public Set<String> termSet(List<DOC> tList) {
		Set<String> termSet = new LinkedHashSet<String>();
		for (int i = 0; i < tList.size(); i++) {
			for (int j = 0; j < tList.get(i).getTerms().length; j++) {
				termSet.add(tList.get(i).getTerms()[j]);
			}
		}
		return termSet;
	}

	// 统计包含和不包含一个词的句子个数
	public int[][] statistic(Set<String> termSet, List<String> senList) {
		int[][] senNum = new int[termSet.size()][2];
		int num = 0;
		for(String s : termSet) {
			for (int i = 0; i < senList.size(); i++) {
				if (senList.get(i).toLowerCase().contains(s)) {
					senNum[num][0]++;// sentences number containing the word
				} else
					senNum[num][1]++;// sentences number do not containing the
										// word
			}
			num++;
		}
		return senNum;
	}

	public Map<String, Integer> sortI(Map<String, Double> map) {
		String[] terms = new String[map.size()];
		double[] values = new double[map.size()];
		Set<Map.Entry<String, Double>> set = map.entrySet();
		Iterator<Map.Entry<String, Double>> iterator = set.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			Map.Entry<String, Double> entry = iterator.next();
			terms[index] = entry.getKey();
			values[index] = entry.getValue();
			index++;
		}
		exchangeSort(terms, values);
		Map<String, Integer> sortedTerms = new LinkedHashMap<String, Integer>();
		for (int i = 0; i < terms.length; i++) {
			sortedTerms.put(terms[i], i + 1);
		}
		return sortedTerms;
	}

	// 排序之后原来的序列并没有变，而是返回的新申请内存的map
	public Map<String, Double> sortD(Map<String, Double> map) {
		String[] terms = new String[map.size()];
		double[] values = new double[map.size()];
		Set<Map.Entry<String, Double>> set = map.entrySet();
		Iterator<Map.Entry<String, Double>> iterator = set.iterator();
		int index = 0;
		while (iterator.hasNext()) {
			Map.Entry<String, Double> entry = iterator.next();
			terms[index] = entry.getKey();
			values[index] = entry.getValue();
			index++;
		}
		exchangeSort(terms, values);
		Map<String, Double> sortedTerms = new LinkedHashMap<String, Double>();
		for (int i = 0; i < terms.length; i++) {
			sortedTerms.put(terms[i], values[i]);
			// System.out.println(terms[i]+"\t"+values[i]);
		}
		return sortedTerms;
	}

	// 倒排
	public void exchangeSort(String[] terms, double[] values) {
		String tempT;
		double tempV;
		int boundary;
		int exchange = terms.length - 1;
		while (exchange != 0) {
			boundary = exchange;
			exchange = 0;
			for (int i = 0; i < boundary; i++) {
				if (values[i] < values[i + 1]) {
					tempT = terms[i + 1];
					tempV = values[i + 1];
					terms[i + 1] = terms[i];
					values[i + 1] = values[i];
					terms[i] = tempT;
					values[i] = tempV;
					exchange = i;
				}
			}
		}
	}

	public int timeInt(String time) {
		int length = time.length();
		return Integer.parseInt(time.substring(length - 4, length));
	}

	public Map<String, Integer> filter(String[] terms, int[] tf) {
		Map<String, Integer> termTF = new LinkedHashMap<String, Integer>();
		for (int i = 0; i < terms.length; i++) {
			if (terms[i].length() > 1) {
				termTF.put(terms[i], tf[i]);
			}
		}
		return termTF;
	}

	public Map<Integer, DOC> TFIDFMap(List<DOC> list) {
		Map<Integer, DOC> map = new LinkedHashMap<Integer, DOC>();
		for (DOC doc : list) {
			map.put(Integer.parseInt(doc.getDocId()), doc);
		}
		return map;
	}

	public Map<String, Double> initEnergy(List<DOC> tList, List<String> senList1) {
		Set<String> termSet = termSet(tList);
		List<String> termList = new ArrayList<String>();
		termList.addAll(termSet);
		int[][] senNum1 = statistic(termSet, senList1);
		int[][] senNum2 = statistic(termSet.size(), senList1.size());
		// double[] termkafang=new double[termSet.size()];
		Map<String, Double> initEnergyMap = new LinkedHashMap<String, Double>();
		for (int i = 0; i < termSet.size(); i++) {
			double termkafang = alpha
					* ((senNum1[i][0] + senNum1[i][1] + senNum2[i][0] + senNum2[i][1]) * Math
							.pow((senNum1[i][0] * senNum2[i][1] - senNum1[i][1]
									* senNum2[i][0]), 2))
					/ ((senNum1[i][0] + senNum1[i][1])
							* (senNum2[i][0] + senNum2[i][1])
							* (senNum1[i][0] + senNum2[i][0]) * (senNum1[i][1] + senNum2[i][1]));
			initEnergyMap.put(termList.get(i), termkafang / (1 + termkafang));
		}
		return initEnergyMap;
	}

	public int[][] statistic(int m, int n) {
		int[][] senNum = new int[m][2];
		for (int i = 0; i < m; i++) {
			senNum[i][0] = 0;
			senNum[i][1] = n;
		}
		return senNum;
	}
}
