package ddap.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddap.ejb.entities.Configuration;

@Local
public interface ConfigurationManagerLocal {
	Collection<Configuration> getConfiguration();
	Configuration getConfigurationItem(String name);
}
