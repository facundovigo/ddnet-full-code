package ddnet.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddnet.ejb.entities.Configuration;

@Local
public interface ConfigurationManagerLocal {
	Collection<Configuration> getConfiguration();
	Configuration getConfigurationItem(String name);
}
