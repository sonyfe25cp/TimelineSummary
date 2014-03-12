import pojo.Sentence;
import service.SentenceService;


public class TestMybatisInsert {
	public static void main(String[] args) {
		SentenceService sentenceService = new SentenceService();
		Sentence s = new Sentence();
		s.setEventName("hh");
		s.setDocName("1");
		s.setSentenceContent("231");
		s.setSentenceId(1);
		sentenceService.insert(s);
	}

}
