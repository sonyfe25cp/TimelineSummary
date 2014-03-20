/**
 * 
 */
package summarize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

/**
 * @author zhangchangmin
 * 
 */
public class LSA {

	// 构建词-句子矩阵
	public Matrix constructMatrix(Map<String, Double> termMap,
			List<String> sentences) {
		int m = termMap.size();
		int n = sentences.size();
		Matrix matrix = new Matrix(m, n);
		List<String> terms = new ArrayList<String>();
		terms.addAll(termMap.keySet());
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				if (sentences.get(j).contains(terms.get(i))) {
					matrix.set(i, j, termMap.get(terms.get(i)));
				}
			}
		}
		return matrix;
	}

	public Matrix calSVD(Matrix matrix) {
		SingularValueDecomposition svd = new SingularValueDecomposition(matrix);
		int d = svd.rank() * 8 / 10;
		Matrix u = svd.getU().getMatrix(0, matrix.getRowDimension() - 1, 0, d);// 左矩阵
		Matrix s = svd.getS().getMatrix(0, d, 0, d);// 中间矩阵
		Matrix v = svd.getV().getMatrix(0, matrix.getColumnDimension() - 1, 0,
				d);// 右矩阵
		Matrix matrix_new = u.times(s).times(v.transpose());
		return matrix_new;
	}

	MMR mmr = new MMR();

	public String[] summaryLSA(Matrix V, List<String> sentences) {
		List<String> summary = new ArrayList<String>();
		int n = V.getColumnDimension();
		for (int i = 0; i < sentences.size() * 0.05; i++) {
			for (int j = 0; j < sentences.size(); j++) {
				double[] column = V.getMatrix(0, n - 1, j, j)
						.getColumnPackedCopy();
				int pos = mmr.maxPosition(column);
				if (pos == i)
					summary.add(sentences.get(j));
			}
		}
		return summary.toArray(new String[0]);
	}

	public void print(Matrix matrix) {
		int m = matrix.getRowDimension();
		int n = matrix.getColumnDimension();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(matrix.get(i, j) + "\t");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		LSA lsa = new LSA();
		Matrix m = new Matrix(5, 2, 2.0);
		double[][] vals = { { 1., 2., 3 }, { 4., 5., 6. }, { 7., 8., 10. } };
		Matrix A = new Matrix(vals);
		Matrix newA = lsa.calSVD(A);
		lsa.print(A);
		lsa.print(newA);

		// Matrix m=new Matrix(2,2);
		// m.set(0, 0, 1);
		// m.set(0, 1, 2);
		// m.set(1, 0, 3);
		// m.set(1, 1, 4);
		// double[] column=m.getMatrix(0, 1, 0, 0).getColumnPackedCopy();
		// System.out.println(column[0]+" "+column[1]);
	}
}
