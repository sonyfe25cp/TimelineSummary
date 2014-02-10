/**
 * 
 */
package process;

/**
 * @author zhangchangmin
 *
 */
public class DOC {

	private String docId;
	private String docName;
	private String sentences;
	private int time;
	private String[] terms;
	private Integer[] TF;
	private int[] DF;
	private double[] TFIDF;
	/**
	 * @return the docId
	 */
	public String getDocId() {
		return docId;
	}
	/**
	 * @param docId the docId to set
	 */
	public void setDocId(String docId) {
		this.docId = docId;
	}
	
	/**
	 * @return the docName
	 */
	public String getDocName() {
		return docName;
	}
	/**
	 * @param docName the docName to set
	 */
	public void setDocName(String docName) {
		this.docName = docName;
	}
	
	/**
	 * @return the sentences
	 */
	public String getSentences() {
		return sentences;
	}
	/**
	 * @param sentences the sentences to set
	 */
	public void setSentences(String sentences) {
		this.sentences = sentences;
	}
	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}
	/**
	 * @return the terms
	 */
	public String[] getTerms() {
		return terms;
	}
	/**
	 * @param terms the terms to set
	 */
	public void setTerms(String[] terms) {
		this.terms = terms;
	}
	/**
	 * @return the tF
	 */
	public Integer[] getTF() {
		return TF;
	}
	/**
	 * @param tF the tF to set
	 */
	public void setTF(Integer[] tF) {
		TF = tF;
	}
	/**
	 * @return the dF
	 */
	public int[] getDF() {
		return DF;
	}
	/**
	 * @param dF the dF to set
	 */
	public void setDF(int[] dF) {
		DF = dF;
	}
	/**
	 * @return the tFIDF
	 */
	public double[] getTFIDF() {
		return TFIDF;
	}
	/**
	 * @param tFIDF the tFIDF to set
	 */
	public void setTFIDF(double[] tFIDF) {
		TFIDF = tFIDF;
	}
}
