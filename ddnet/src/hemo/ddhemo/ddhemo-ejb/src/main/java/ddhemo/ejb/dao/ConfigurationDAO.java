package ddhemo.ejb.dao;

import javax.ejb.Stateless;

import ddhemo.ejb.entities.Configuration;
import ddhemo.ejb.util.data.AbstractDAO;

@Stateless
public class ConfigurationDAO extends AbstractDAO<Long, Configuration> {
	public ConfigurationDAO() {
		super(Configuration.class);
	}
}
