package ddhemo.ejb;

import java.util.Collection;

import javax.ejb.Local;

import ddhemo.ejb.entities.Institution;

@Local
public interface InstitutionManagerLocal {
	Collection<Institution> getAll();
	Institution findById(Long id);
}
