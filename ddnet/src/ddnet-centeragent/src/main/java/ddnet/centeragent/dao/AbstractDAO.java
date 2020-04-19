package ddnet.centeragent.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public abstract class AbstractDAO {
	
	private static final String PERSISTENCE_UNIT_NAME = "PACS";
	private static EntityManagerFactory factory = null;
	
	static {
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	}
	
	public static EntityManager getEntityManager() {
		return factory.createEntityManager();
	}
}
