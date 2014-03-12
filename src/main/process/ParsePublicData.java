package process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pojo.Sentence;
import service.SentenceService;

public class ParsePublicData {

	public static void main(String[] args) {
		ParsePublicData ppd = new ParsePublicData();
		ppd.run();
	}

	private static String folderPath = "/Users/omar/data/Timeline17/Data";
	
	private SentenceService sentenceService = new SentenceService();
	
	private void run(){
		List<Sentence> sentences = parse(new File(folderPath));
		for(Sentence sentence : sentences){
			sentenceService.insert(sentence);
		}
		
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
				}else if( t.getName().equals("timelines")){
					
				}
			}
		}
		return sentences;
	}
	
}
