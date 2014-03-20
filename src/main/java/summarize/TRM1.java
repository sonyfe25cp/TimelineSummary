/**
 * 
 */
package summarize;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Jama.Matrix;

/**
 * @author zhangchangmin
 * 
 */
// LSA
public class TRM1 {

	public Map<String, Double> constructTRM(Matrix matrix,
			List<String> sentences) {
		int senLength = matrix.getColumnDimension();
		int wordLength = matrix.getRowDimension();
		double[] simArray = new double[((int) Math.pow(senLength, 2) - senLength) / 2];
		Map<String, Double> sentenceSimMap = new LinkedHashMap<String, Double>();
		int count = 0;
		for (int i = 0; i < senLength; i++) {
			for (int j = i + 1; j < senLength; j++) {
				simArray[count++] = calSimilarity(
						matrix.getMatrix(0, wordLength - 1, i, i)
								.getColumnPackedCopy(),
						matrix.getMatrix(0, wordLength - 1, j, j)
								.getColumnPackedCopy());
			}
		}
		for (int i = 0; i < senLength; i++) {
			double sumSim = 0.0;
			for (int j = 0; j < senLength; j++) {
				if (j != i && j > i) {
					sumSim += simArray[i * (2 * senLength - i - 1) / 2 + j - i - 1];
				} else if (j != i && j < i) {
					sumSim += simArray[j * (2 * senLength - j - 1) / 2 + i - j - 1];
				}
			}
			sentenceSimMap.put(sentences.get(i), sumSim / sentences.size());
		}
		return sentenceSimMap;
	}

	public double calSimilarity(double[] s1, double[] s2) {
		double multiSum = 0.0, s1Sum = 0.0, s2Sum = 0.0;
		for (int i = 0; i < s1.length; i++) {
			multiSum += s1[i] * s2[i];
			s1Sum += s1[i] * s1[i];
			s2Sum += s2[i] * s2[i];
		}
		return multiSum / (Math.sqrt(s1Sum) * Math.sqrt(s2Sum));
	}
}
