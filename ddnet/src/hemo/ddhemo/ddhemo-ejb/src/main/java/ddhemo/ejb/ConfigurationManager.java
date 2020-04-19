package ddhemo.ejb;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import ddhemo.ejb.dao.ConfigurationDAO;
import ddhemo.ejb.entities.Configuration;

/**
 * Session Bean implementation class ConfigurationManager
 */
@Stateless(mappedName = "ejb/ddhemo/ConfigurationManager")
@LocalBean
public class ConfigurationManager implements ConfigurationManagerLocal {

	@EJB
	private ConfigurationDAO configurationDAO;

	public Collection<Configuration> getConfiguration() {
		return configurationDAO.getAll();
	}

	public Configuration getConfigurationItem(String name) {
		Collection<Configuration> config = configurationDAO.getAll();
		for(Configuration c : config)
			if (c.getName().equals(name))
				return c;		
		return null;
	}
}
