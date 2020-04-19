package ddnet.centeragent;

import ddnet.centeragent.dao.StudyDAO;
import ddnet.centeragent.entities.Study;

public class App {
	public static void main(String[] args) {
		StudyDAO studyDAO = new StudyDAO();
		System.out.println("Estudios sin enviar sus tags:");
		for(Study s : studyDAO.getStudiesUnsentData())
			System.out.println("\t" + s.getId());
	}
}
