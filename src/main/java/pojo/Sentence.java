package pojo;

public class Sentence {

	private int id;
	private int sentenceId;//句子id
	private String sentenceContent;//句子内容
	private String docName;//文件名
	private String eventName;//事件名
	private String isSummary;//是否是摘要
	private String publishDate;//发布时间
	private int total;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the sentence_id
	 */
	public int getSentenceId() {
		return sentenceId;
	}
	/**
	 * @param sentence_id the sentence_id to set
	 */
	public void setSentenceId(int sentenceId) {
		this.sentenceId = sentenceId;
	}
	/**
	 * @return the sentence
	 */
	public String getSentenceContent() {
		return sentenceContent;
	}
	/**
	 * @param sentence the sentence to set
	 */
	public void setSentenceContent(String sentenceContent) {
		this.sentenceContent = sentenceContent;
	}
	/**
	 * @return the docname
	 */
	public String getDocName() {
		return docName;
	}
	/**
	 * @param docname the docname to set
	 */
	public void setDocName(String docName) {
		this.docName = docName;
	}
	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}
	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	/**
	 * @return the isSummary
	 */
	public String getIsSummary() {
		return isSummary;
	}
	/**
	 * @param isSummary the isSummary to set
	 */
	public void setIsSummary(String isSummary) {
		this.isSummary = isSummary;
	}
	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(int total) {
		this.total = total;
	}
	public String getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	
}
