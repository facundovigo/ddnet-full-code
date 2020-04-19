package ddap.ejb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="ap_practicas")
@Entity
public class Practica implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="id_practicas")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="prestacion_practicas")
	private String prestacion;
	
	@Column(name="region_practicas")
	private int region;
	
	@Column(name="nombre_practicas")
	private String nombre;
	
	@Column(name="nombre_interno_practicas")
	private String interno;
	
	@Column(name="nombre_abrev_practicas")
	private String abreviado;
	
	@Column(name="servicio_practicas")
	private String servicio;
	
	@Column(name="especialidad_practicas")
	private String especialidad;
	
	@Column(name="tipo_practicas")
	private String tipo;
	
	@Column(name="requiere_informe_practicas")
	private boolean requiereInforme;
	
	@Column(name="region_informe_practicas")
	private String regionInforme;
	
	@Column(name="modalidad_practicas")
	private String modalidad;
	
	@Column(name="valor_practicas")
	private float valor;
	
	@Column(name="valor_emergencia_practicas")
	private float emergencyValue;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPrestacion() {
		return prestacion;
	}

	public void setPrestacion(String prestacion) {
		this.prestacion = prestacion;
	}

	public int getRegion() {
		return region;
	}

	public void setRegion(int region) {
		this.region = region;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getInterno() {
		return interno;
	}

	public void setInterno(String interno) {
		this.interno = interno;
	}

	public String getAbreviado() {
		return abreviado;
	}

	public void setAbreviado(String abreviado) {
		this.abreviado = abreviado;
	}

	public String getServicio() {
		return servicio;
	}

	public void setServicio(String servicio) {
		this.servicio = servicio;
	}

	public String getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public boolean isRequiereInforme() {
		return requiereInforme;
	}

	public void setRequiereInforme(boolean requiereInforme) {
		this.requiereInforme = requiereInforme;
	}

	public String getRegionInforme() {
		return regionInforme;
	}

	public void setRegionInforme(String regionInforme) {
		this.regionInforme = regionInforme;
	}
	
	public String getModalidad() {
		return modalidad;
	}

	public void setModalidad(String modalidad) {
		this.modalidad = modalidad;
	}
	
	public float getValor() {
		return valor;
	}

	public void setValor(float valor) {
		this.valor = valor;
	}

	public float getEmergencyValue() {
		return emergencyValue;
	}

	public void setEmergencyValue(float emergencyValue) {
		this.emergencyValue = emergencyValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Practica other = (Practica) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s", nombre);
	}

}
