/**
 * 
 */
package process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangchangmin
 *
 */
public class Last {

	private int id;
	private String date;
	private List<String> sentences=new ArrayList<String>();
	private Map<String,Double> termWeight=new LinkedHashMap<String,Double>();
	private String[] summary;
	private Map<String,Double> energy=new LinkedHashMap<String,Double>();
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
	public List<String> getSentencesList(){
		return this.sentences;
	}
	
	public String getSentences(){
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<sentences.size();i++){
			sb.append(sentences.get(i)).append("\t");
		}
		return sb.toString();
	}
	
	public void setSentences(List<String> sentences){
		this.sentences=sentences;
	}
	
	public void setSentences(String s){
		String[] sa=s.split("\t");
		for(int i=0;i<sa.length;i++){
			sentences.add(sa[i]);
		}
	}
	
	public Map<String,Double> getTermWeightMap(){
		return this.termWeight;
	}
	
	public String getTermWeight(){
		StringBuffer sb=new StringBuffer();
		Iterator<String> iter=termWeight.keySet().iterator();
		while(iter.hasNext()){
			String s=iter.next();
			double d=termWeight.get(s);
			sb.append(s).append(":").append(d).append(";");
		}
		return sb.toString();
	}
	
	public void setTermWeight(Map<String,Double> termWeight){
		this.termWeight=termWeight;
	}
	
	public void setTermWeight(String s){
		String[] sa=s.split(";");
		for(int i=0;i<sa.length;i++){
			String[] sc=sa[i].split(":");
			int length=sc.length;
			termWeight.put(sc[0], Double.parseDouble(sc[length-1]));
		}
	}
	
	/**
	 * @return the summary
	 */
	public String[] getSummaryArray(){
		return this.summary;
	}
	
	public String getSummary() {
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<summary.length;i++){
			sb.append(summary[i]).append("\t");
		}
		return sb.toString();
	}
	/**
	 * @param summary the summary to set
	 */
	public void setSummary(String[] summary){
		this.summary=summary;
	}
	
	public void setSummary(String s) {
		summary=s.split("\t");
	}
	
	public Map<String,Double> getEnergyMap(){
		return this.energy;
	}
	
	public String getEnergy(){
		StringBuffer sb=new StringBuffer();
		Iterator<String> iter=energy.keySet().iterator();
		while(iter.hasNext()){
			String s=iter.next();
			double d=energy.get(s);
			sb.append(s).append(":").append(d).append(";");
		}
		return sb.toString();
	}
	
	public void setEnergy(Map<String,Double> energy){
		this.energy=energy;
	}
	
	public void setEnergy(String s){
		String[] sa=s.split(";");
		for(int i=0;i<sa.length;i++){
			String[] sc=sa[i].split(":");
			int length=sc.length;
			energy.put(sc[0], Double.parseDouble(sc[length-1]));
		}
	}
}
