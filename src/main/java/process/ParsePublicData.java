package process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pojo.Sentence;
import service.SentenceService;

public class ParsePublicData {

	public static void main(String[] args) {
		ParsePublicData ppd = new ParsePublicData();
		ppd.run();
		ppd.runLabel();
	}

	private static String folderPath = "/Users/omar/data/Timeline17/Data";
	
	private SentenceService sentenceService = new SentenceService();
	
	private void run(){
		List<Sentence> sentences = parse(new File(folderPath));
		int count = 0; 
		for(Sentence sentence : sentences){
			sentenceService.insert(sentence);
			count ++;
			if(count %200 == 0){
				System.out.println("run ~~ "+ count);
			}
		}
	}
	private void runLabel(){
		List<Sentence> sentences = parseLabel(new File(folderPath));
		int count = 0; 
		for(Sentence sentence : sentences){
			sentenceService.insert(sentence);
			count ++;
			if(count %200 == 0){
				System.out.println("runLabel ~~ "+ count);
			}
		}
	}
	Pattern datePattern = Pattern.compile("^\\d+-\\d+-\\d+$");
	private boolean matchDate(String line){
		Matcher m = datePattern.matcher(line);
		if(m.find()){
			return true;
		}else{
			return false;
		}
	}
	private List<Sentence> parseLabel(File folder){
		List<Sentence> sentences = new ArrayList<>();
		for(File tmpFile : folder.listFiles()){
			String eventName = tmpFile.getName();
			for(File t : tmpFile.listFiles()){
				if( t.getName().equals("timelines")){
					for(File label : t.listFiles()){
						BufferedReader br;
						try {
							br = new BufferedReader(new FileReader(label));
							String line =null;
							line = br.readLine();
						
							String date = null;
							while(line != null){
								boolean isDate = matchDate(line);
								if(isDate){
									date = line;
									continue;
								}
								if(line.contains("-------------------------")){
									continue;
								}
								Sentence sentence = new Sentence();
								sentence.setIsSummary("yes");
								sentence.setPublishDate(date);
								sentence.setDocName(label.getName());
								sentence.setEventName(eventName);
								sentence.setSentenceContent(line);
								sentences.add(sentence);
							}
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return sentences;
	}
		
	private List<Sentence> parse(File folder){
		List<Sentence> sentences = new ArrayList<>();
		for(File tmpFile : folder.listFiles()){
			String eventName = tmpFile.getName();
			for(File t : tmpFile.listFiles()){
				if(t.getName().equals("InputDocs")){
					for(File dateFolder : t.listFiles()){
						String date = dateFolder.getName();
						if(date.equals(".DS_Store")){
							continue;
						}
						for(File file : dateFolder.listFiles()){
							try {
								BufferedReader br = new BufferedReader(new FileReader(file));
								String line = br.readLine();
								int index = 1;
								List<Sentence> tmpList = new ArrayList<>();
								while(line != null){
									Sentence sentence = new Sentence();
									sentence.setEventName(eventName);
									sentence.setDocName(file.getName());
									sentence.setPublishDate(date);
									sentence.setSentenceId(index);
									sentence.setSentenceContent(line);
									tmpList.add(sentence);
									index ++;
									line = br.readLine();
								}
								for(Sentence sentence : tmpList){
									sentence.setTotal(index);
								}
								sentences.addAll(tmpList);
								br.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		return sentences;
	}
	
}
