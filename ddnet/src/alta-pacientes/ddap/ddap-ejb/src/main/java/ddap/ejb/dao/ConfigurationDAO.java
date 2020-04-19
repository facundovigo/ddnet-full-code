package ddap.ejb.dao;

import javax.ejb.Stateless;

import ddap.ejb.entities.Configuration;
import ddap.ejb.util.data.AbstractDAO;

@Stateless
public class ConfigurationDAO extends AbstractDAO<Long, Configuration> {
	public ConfigurationDAO() {
		super(Configuration.class);
	}
}
