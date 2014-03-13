package process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import service.SentenceService;

public class RunResult {
	
	
	private SentenceService sentenceService = new SentenceService();
	
	private void run(){
		List<String> tasks = sentenceService.getEventTasks();
		List<Map<String, Map<String, Double>>> list = new ArrayList<Map<String, Map<String, Double>>>();
		for(String task : tasks){
			SummaryRun sr = new SummaryRun();
//			System.out.println(task);
			sr.setEventName(task);
			sr.testRun();
			Map<String, Map<String, Double>> resultsMap = sr.getResultsMap();
			list.add(resultsMap);
		}
		
		
		
	}

	public static void main(String[] args) {
		RunResult rr = new RunResult();
		rr.run();
	}
	
}
