/**
 * 
 */
package summarize;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Jama.Matrix;

/**
 * @author zhangchangmin
 * 
 */

// LSA
public class Diversity1 {

	double lambda = 0.3;
	LSA lsa = new LSA();
	TRM1 trm = new TRM1();

	public void calDiversity(Map<String, Double> senMap, String[] sumLast,
			Map<String, Double> termTodayMap, Map<String, Double> termLastMap) {
		StringBuffer sb = toStringBuffer(sumLast);
		List<String> senToday = new ArrayList<String>();
		senToday.addAll(senMap.keySet());
		// 语义
		Matrix matrix = lsa.calSVD(constructMatrix(termLastMap, termTodayMap,
				sumLast, senToday));
		double[] Last = centroid(matrix, sumLast.length);
		for (int i = 0; i < senToday.size(); i++) {
			double[] senI = matrix.getMatrix(0, matrix.getRowDimension() - 1,
					i + 1, i + 1).getColumnPackedCopy();
			double distance = trm.calSimilarity(Last, senI);
			double weight = lambda * L2(senMap.get(senToday.get(i)))
					+ (1 - lambda) * L1(distance);
			senMap.put(senToday.get(i), weight);
		}
	}

	public Matrix constructMatrix(Map<String, Double> termMapL,
			Map<String, Double> termMapT, String[] sumL, List<String> sentences) {
		List<String> mergedTerm = merge(termMapT.keySet(), termMapL.keySet());
		int row = mergedTerm.size();
		int column = sumL.length + sentences.size();
		// System.out.println(row+"\t"+column);
		Matrix matrix = new Matrix(row, column);
		for (int i = 0; i < row; i++) {
			// System.out.print(i+mergedTerm.get(i));
			for (int k = 0; k < sumL.length; k++) {
				if (sumL[k].contains(mergedTerm.get(i))
						&& termMapL.keySet().contains(mergedTerm.get(i))) {
					matrix.set(i, k, termMapL.get(mergedTerm.get(i)));
				}
			}
			for (int j = 0; j < sentences.size(); j++) {
				if (sentences.get(j).contains(mergedTerm.get(i))
						&& termMapT.keySet().contains(mergedTerm.get(i))) {
					matrix.set(i, j + sumL.length,
							termMapT.get(mergedTerm.get(i)));
				}
			}
		}
		return matrix;
	}

	public List<String> merge(Set<String> termToday, Set<String> termLast) {
		Set<String> mergedTerm = new LinkedHashSet<String>();
		mergedTerm.addAll(termToday);
		mergedTerm.addAll(termLast);
		List<String> mergedList = new ArrayList<String>();
		mergedList.addAll(mergedTerm);
		return mergedList;
	}

	public StringBuffer toStringBuffer(String[] sa) {
		StringBuffer sb = new StringBuffer();
		for (String s : sa) {
			sb.append(s);
		}
		return sb;
	}

	public double L1(double x) {
		return 1.0 / (1 + Math.exp(x));
	}

	public double L2(double x) {
		return Math.exp(x) / (1 + Math.exp(x));
	}

	// 上一个摘要的质心
	public double[] centroid(Matrix matrix, int sumLength) {
		int row = matrix.getRowDimension();
		double[] cent = new double[row];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < sumLength; j++) {
				cent[i] += matrix.get(i, j);
			}
			cent[i] = cent[i] / sumLength;
		}
		return cent;
	}
}
