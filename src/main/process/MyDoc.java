/**
 * 
 */
package process;

import java.util.List;

import pojo.Sentence;

/**
 * @author zhangchangmin
 *
 */
public class MyDoc {

	private String docName;
	private String date;
	private String time;
	private List<Sentence> sentencesOfDoc;
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
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}
	/**
	 * @return the sentencesOfDoc
	 */
	public List<Sentence> getSentencesOfDoc() {
		return sentencesOfDoc;
	}
	/**
	 * @param sentencesOfDoc the sentencesOfDoc to set
	 */
	public void setSentencesOfDoc(List<Sentence> sentencesOfDoc) {
		this.sentencesOfDoc = sentencesOfDoc;
	}
	
}
