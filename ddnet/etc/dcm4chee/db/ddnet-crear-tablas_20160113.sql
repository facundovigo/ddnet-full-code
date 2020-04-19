----------------------------------------------------------------------------------
--				CREAR LAS TABLAS DDNET				--
----------------------------------------------------------------------------------

	-- usuarios del ddnet --

	CREATE TABLE dd_user
	(
	  id bigserial NOT NULL,
	  first_name text NOT NULL,
	  last_name text NOT NULL,
	  login text NOT NULL,
	  password text NOT NULL,
	  has_password_expired boolean NOT NULL,
	  is_administrator boolean NOT NULL,
	  deleted boolean NOT NULL,
	  is_patient boolean DEFAULT false,
	  CONSTRAINT dd_user_pk PRIMARY KEY (id)
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_user
	  OWNER TO postgres;

--	***********************************************		--

	-- estudios del ddnet --

	CREATE TABLE dd_study
	(
	  id bigserial NOT NULL,
	  is_reported boolean NOT NULL,
	  report_body text,
	  estado_incidencia integer NOT NULL DEFAULT 0,
	  img_downloaded boolean DEFAULT false,
	  archivos_adjuntos boolean DEFAULT false,
	  has_orden_medica boolean DEFAULT false,
	  CONSTRAINT dd_study_pk PRIMARY KEY (id),
	  CONSTRAINT dd_study_id_fkey FOREIGN KEY (id)
	      REFERENCES study (pk) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_study
	  OWNER TO postgres;

--	***********************************************		--

	-- configuraciones necesarias para ddnet --

	CREATE TABLE dd_config
	(
	  name text NOT NULL,
	  value text,
	  CONSTRAINT dd_config_pk PRIMARY KEY (name)
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_config
	  OWNER TO postgres;

--	***********************************************		--

	-- instituciones que trabajan con ddnet --

	CREATE TABLE dd_institution
	(
	  id bigserial NOT NULL,
	  name text NOT NULL,
	  related_ae_title text NOT NULL,
	  adm_enabled boolean NOT NULL,
	  deleted boolean NOT NULL,
	  CONSTRAINT dd_institution_pk PRIMARY KEY (id)
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_institution
	  OWNER TO postgres;

--	***********************************************		--

	-- roles para los usuarios ddnet --

	CREATE TABLE dd_role
	(
	  id bigserial NOT NULL,
	  name text NOT NULL,
	  CONSTRAINT dd_role_pk PRIMARY KEY (id)
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_role
	  OWNER TO postgres;

--	***********************************************		--

	-- modalidades de estudio con las que trabaja ddnet --

	CREATE TABLE dd_modality
	(
	  id bigserial NOT NULL,
	  name text NOT NULL,
	  CONSTRAINT dd_modality_pk PRIMARY KEY (id)
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_modality
	  OWNER TO postgres;

--	***********************************************		--

	-- acciones a realizar ddnet --

	CREATE TABLE dd_action
	(
	  id bigserial NOT NULL,
	  name text NOT NULL,
	  description text NOT NULL,
	  CONSTRAINT dd_action_pk PRIMARY KEY (id)
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_action
	  OWNER TO postgres;

--	***********************************************		--

	-- vincular un usuario con su rol e instituciones respectivas --

	CREATE TABLE dd_user_institution
	(
	  id bigserial NOT NULL,
	  user_id bigserial NOT NULL,
	  institution_id bigserial NOT NULL,
	  role_id bigserial NOT NULL,
	  CONSTRAINT dd_user_inst_pk PRIMARY KEY (id),
	  CONSTRAINT dd_user_inst_inst_fk FOREIGN KEY (institution_id)
	      REFERENCES dd_institution (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE NO ACTION,
	  CONSTRAINT dd_user_inst_role_fk FOREIGN KEY (role_id)
	      REFERENCES dd_role (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE NO ACTION,
	  CONSTRAINT dd_user_inst_user_fk FOREIGN KEY (user_id)
	      REFERENCES dd_user (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE,
	  CONSTRAINT dd_user_inst_uq UNIQUE (user_id, institution_id, role_id)
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_user_institution
	  OWNER TO postgres;

--	***********************************************		--

	-- modalidades con que trabaja cada usuario ddnet --

	CREATE TABLE dd_user_modality
	(
	  id bigserial NOT NULL,
	  user_id bigserial NOT NULL,
	  modality_id bigserial NOT NULL,
	  CONSTRAINT dd_user_modality_pkey PRIMARY KEY (id),
	  CONSTRAINT dd_user_modality_modality_fk FOREIGN KEY (modality_id)
	      REFERENCES dd_modality (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE NO ACTION,
	  CONSTRAINT dd_user_modality_user_id_fkey FOREIGN KEY (user_id)
	      REFERENCES dd_user (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_user_modality
	  OWNER TO postgres;

--	***********************************************		--

	-- estudios asignados a usuario ddnet --

	CREATE TABLE dd_user_study
	(
	  id bigserial NOT NULL,
	  user_id bigserial NOT NULL,
	  study_id bigserial NOT NULL,
	  CONSTRAINT dd_user_study_pk PRIMARY KEY (id),
	  CONSTRAINT dd_user_study_study_fk FOREIGN KEY (study_id)
	      REFERENCES dd_study (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE,
	  CONSTRAINT dd_user_study_user_id_fkey FOREIGN KEY (user_id)
	      REFERENCES dd_user (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE,
	  CONSTRAINT dd_user_study_uq1 UNIQUE (user_id, study_id)
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_user_study
	  OWNER TO postgres;

--	***********************************************		--

	-- acciones permitidas según el rol de usuario ddnet --

	CREATE TABLE dd_role_action
	(
	  role_id bigserial NOT NULL,
	  action_id bigserial NOT NULL,
	  CONSTRAINT dd_role_action_pk PRIMARY KEY (role_id, action_id),
	  CONSTRAINT dd_role_action_action_fk FOREIGN KEY (action_id)
	      REFERENCES dd_action (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE NO ACTION,
	  CONSTRAINT dd_role_action_role_fk FOREIGN KEY (role_id)
	      REFERENCES dd_role (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE NO ACTION
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_role_action
	  OWNER TO postgres;

--	***********************************************		--
	
	-- perfil de usuario ddnet --

	CREATE TABLE dd_user_profile
	(
	  id bigserial NOT NULL,
	  user_id bigint NOT NULL,
	  user_firstname character varying(100),
	  user_lastname character varying(100) NOT NULL,
	  user_fancyname character varying(10),
	  user_email character varying(100) NOT NULL,
	  user_phone1 character varying(20),
	  user_phone2 character varying(20),
	  user_address character varying(100),
	  user_location character varying(50),
	  user_province character varying(50),
	  user_skype character varying(50),
	  user_title character varying(50),
	  user_mn character varying(20),
	  user_mp character varying(20),
	  user_addinfo text,
	  CONSTRAINT dd_user_profile_pkey PRIMARY KEY (id),
	  CONSTRAINT dd_user_profile_user_id_fkey FOREIGN KEY (user_id)
	      REFERENCES dd_user (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_user_profile
	  OWNER TO postgres;

--	***********************************************		--

	-- estado de los estudios ddnet --

	CREATE TABLE dd_datos_clinicos
	(
	  id_datos_clinicos bigserial NOT NULL,
	  id_study_datos_clinicos bigserial NOT NULL,
	  prioridad_datos_clinicos integer NOT NULL DEFAULT 0,
	  cte_oral_datos_clinicos boolean DEFAULT FALSE,
	  cte_ev_datos_clinicos boolean DEFAULT FALSE,
	  observaciones_datos_clinicos text,
	  user_datos_clinicos character varying(100),
	  CONSTRAINT dd_datos_clinicos_pkey PRIMARY KEY (id_datos_clinicos),
	  CONSTRAINT dd_datos_clinicos_id_study_datos_clinicos_fkey FOREIGN KEY (id_study_datos_clinicos)
	      REFERENCES dd_study (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_datos_clinicos
	  OWNER TO postgres;

--	***********************************************		--

	-- mensajes referentes a un estudio ddnet --

	CREATE TABLE dd_incidencias
	(
	  id_incidencia bigserial NOT NULL,
	  study_id_incidencia bigint NOT NULL,
	  mensaje_incidencia text NOT NULL,
	  fecha_mensaje_incidencia character varying(20) NOT NULL,
	  usuario_incidencia character varying(50),
	  estado_incidencia integer NOT NULL DEFAULT 0,
	  inicio_incidencia character varying(20),
	  resolucion_incidencia character varying(20),
	  incidencia_resuelta bigint,
	  CONSTRAINT dd_incidencias_pkey PRIMARY KEY (id_incidencia),
	  CONSTRAINT dd_incidencias_study_id_incidencia_fkey FOREIGN KEY (study_id_incidencia)
	      REFERENCES dd_study (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_incidencias
	  OWNER TO postgres;

--	***********************************************		--

	-- log de acciones sobre estudio ddnet --

	CREATE TABLE dd_study_log
	(
	  id bigserial NOT NULL,
	  study_id bigserial NOT NULL,
	  usuario_accion character varying(50) NOT NULL,
	  tiempo character varying(10),
	  accion character varying(100) NOT NULL,
	  parametros character varying(100),
	  fecha_hora character varying(20),
	  CONSTRAINT dd_study_log_pkey PRIMARY KEY (id),
	  CONSTRAINT dd_study_log_study_id_fkey FOREIGN KEY (study_id)
	      REFERENCES dd_study (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_study_log
	  OWNER TO postgres;

--	***********************************************		--

	-- informes realizados sobre estudios ddnet --

	CREATE TABLE dd_informes
	(
	  id_informe bigserial NOT NULL,
	  study_id bigint NOT NULL,
	  usuario_informe character varying(100) NOT NULL,
	  estado_informe integer NOT NULL,
	  cant_informes integer DEFAULT 1,
	  is_teaching_file boolean DEFAULT false,
	  segunda_lectura boolean DEFAULT false,
	  comprobar_caso boolean DEFAULT false,
	  comprobar_caso_estado integer NOT NULL DEFAULT 0,
	  emergencia_medica boolean DEFAULT false,
	  fecha_ultima_modificacion character varying(20),
	  fecha_firma_informe character varying(20),
	  CONSTRAINT dd_informes_pkey PRIMARY KEY (id_informe),
	  CONSTRAINT dd_informes_study_id_fkey FOREIGN KEY (study_id)
	      REFERENCES dd_study (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_informes
	  OWNER TO postgres;

--	***********************************************		--

	-- comprobar informe realizado sobre estudio ddnet --

	CREATE TABLE dd_comprobar_caso
	(
	  id_cc bigserial NOT NULL,
	  informe_id bigserial NOT NULL,
	  estado_cc integer NOT NULL,
	  fecha_cc character varying(20),
	  usuario_cc character varying(100) NOT NULL,
	  observacion_cc text NOT NULL,
	  CONSTRAINT dd_comprobar_caso_pkey PRIMARY KEY (id_cc),
	  CONSTRAINT dd_comprobar_caso_informe_id_fkey FOREIGN KEY (informe_id)
	      REFERENCES dd_informes (id_informe) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_comprobar_caso
	  OWNER TO postgres;

--	***********************************************		--

	-- segunda opinión informe realizado sobre estudio ddnet --

	CREATE TABLE dd_segunda_lectura
	(
	  id bigserial NOT NULL,
	  informe_id bigserial NOT NULL,
	  usuario_informe character varying(100) NOT NULL,
	  grado_discrepancia integer NOT NULL,
	  usuario_discrepante character varying(100) NOT NULL,
	  mensaje text,
	  fecha_segunda_lectura character varying(20),
	  CONSTRAINT dd_segunda_lectura_pkey PRIMARY KEY (id),
	  CONSTRAINT dd_segunda_lectura_informe_id_fkey FOREIGN KEY (informe_id)
	      REFERENCES dd_informes (id_informe) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_segunda_lectura
	  OWNER TO postgres;

--	***********************************************		--

	-- estudios que se han borrado ddnet --

	CREATE TABLE dd_estudios_eliminados
	(
	  id_estudios_eliminados bigserial NOT NULL,
	  study_id bigserial NOT NULL,
	  user_eliminado character varying(30) NOT NULL,
	  pat_id_eliminado character varying(30) NOT NULL,
	  paciente_eliminado character varying(200) NOT NULL,
	  modalidad_eliminado character varying(10) NOT NULL,
	  institucion_eliminado character varying(200) NOT NULL,
	  fecha_eliminado character varying(20) NOT NULL,
	  imgpath_eliminado text,
	  filepath_eliminado text,
	  reportpath_eliminado text,
	  ordenmedicapath_eliminado text,
	  estado_estudio_eliminado integer NOT NULL DEFAULT 0,
	  CONSTRAINT dd_estudios_eliminados_pkey PRIMARY KEY (id_estudios_eliminados)
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_estudios_eliminados
	  OWNER TO postgres;

--	***********************************************		--

	-- control de correos a enviar --

	CREATE TABLE dd_envio_correo
	(
	  id bigserial NOT NULL,
	  study_id bigint,
	  user_id bigint NOT NULL,
	  mail_destino character varying(200) NOT NULL,
	  mail_asunto integer NOT NULL,
	  mail_envio boolean NOT NULL DEFAULT false,
	  mail_llego boolean NOT NULL DEFAULT false,
	  mail_fecha_registro character varying(20),
	  mail_fecha_envio character varying(20),
	  mail_detalle_error text,
	  restore_uuid character varying(100),
	  mail_reenvios integer DEFAULT 0,
	  CONSTRAINT dd_envio_correo_pkey PRIMARY KEY (id),
	  CONSTRAINT dd_envio_correo_study_id_fkey FOREIGN KEY (study_id)
	      REFERENCES dd_study (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE,
	  CONSTRAINT dd_envio_correo_user_id_fkey FOREIGN KEY (user_id)
	      REFERENCES dd_user (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_envio_correo
	  OWNER TO postgres;

--	***********************************************		--

	-- pedidos restaurar contraseña usuario ddnet --

	CREATE TABLE dd_password_restore
	(
	  id bigserial NOT NULL,
	  user_id bigint NOT NULL,
	  restore_key character varying(100) NOT NULL,
	  restore_done boolean NOT NULL DEFAULT false,
	  restore_times integer NOT NULL DEFAULT 0,
	  restore_init_date character varying(20),
	  restore_limit_date character varying(20) NOT NULL,
	  restore_host_requested character varying(30) NOT NULL,
	  CONSTRAINT dd_password_restore_pkey PRIMARY KEY (id),
	  CONSTRAINT dd_password_restore_user_id_fkey FOREIGN KEY (user_id)
	      REFERENCES dd_user (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_password_restore
	  OWNER TO postgres;

--	***********************************************		--

	-- propiedades userAgent usuario ddnet --

	CREATE TABLE dd_user_property
	(
	  id bigserial NOT NULL,
	  user_id bigint NOT NULL,
	  hostname character varying(50) NOT NULL,
	  port character varying(10) NOT NULL,
	  aet character varying(100) NOT NULL,
	  calling_aet character varying(100) NOT NULL,
	  CONSTRAINT dd_user_property1_pkey PRIMARY KEY (id),
	  CONSTRAINT dd_user_property1_user_id_fkey FOREIGN KEY (user_id)
	      REFERENCES dd_user (id) MATCH SIMPLE
	      ON UPDATE NO ACTION ON DELETE CASCADE
	)
	WITH (
	  OIDS=FALSE
	);
	ALTER TABLE dd_user_property
	  OWNER TO postgres;

--	***********************************************		--

	-- crear trigger tabla study --

	CREATE OR REPLACE FUNCTION dd_study_catchup_function() RETURNS trigger AS $dd_study_catchup_function$
	    BEGIN
		INSERT INTO dd_study VALUES (NEW.pk, FALSE);
		RETURN NEW;
	    END;
	$dd_study_catchup_function$ LANGUAGE plpgsql;

	CREATE TRIGGER dd_study_catchup_trigger AFTER INSERT ON study 
		FOR EACH ROW EXECUTE PROCEDURE dd_study_catchup_function();

--	***********************************************		--
