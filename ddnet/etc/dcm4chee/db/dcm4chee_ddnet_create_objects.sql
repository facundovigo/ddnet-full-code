/* **************************************** */
/* Script de creación de objetos para DDNET */
/* **************************************** */

CREATE TABLE dd_user
(
  id serial8 NOT NULL,
  first_name text NOT NULL,
  last_name text NOT NULL,
  login text NOT NULL,
  password text NOT NULL,
  has_password_expired boolean NOT NULL,
  is_administrator boolean NOT NULL,  
  deleted boolean NOT NULL,
  CONSTRAINT dd_user_pk PRIMARY KEY (id)
)
WITH (OIDS=FALSE);

CREATE TABLE dd_institution
(
  id serial8 NOT NULL,
  name text NOT NULL,
  related_ae_title text NOT NULL,
  adm_enabled boolean NOT NULL,
  deleted boolean NOT NULL,  
  CONSTRAINT dd_institution_pk PRIMARY KEY (id)
)
WITH (OIDS=FALSE);

CREATE TABLE dd_action
(
  id serial8 NOT NULL,
  name text NOT NULL,
  description text NOT NULL,
  CONSTRAINT dd_action_pk PRIMARY KEY (id)
)
WITH (OIDS=FALSE);

CREATE TABLE dd_role
(
  id serial8 NOT NULL,
  name text NOT NULL,
  CONSTRAINT dd_role_pk PRIMARY KEY (id)
)
WITH (OIDS=FALSE);

CREATE TABLE dd_role_action
(
  role_id serial8 NOT NULL,
  action_id serial8 NOT NULL,
  CONSTRAINT dd_role_action_pk PRIMARY KEY (role_id, action_id),
  CONSTRAINT dd_role_action_role_fk FOREIGN KEY (role_id)
      REFERENCES dd_role (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT dd_role_action_action_fk FOREIGN KEY (action_id)
      REFERENCES dd_action (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (OIDS=FALSE);

CREATE TABLE dd_user_institution
(
  id serial8 NOT NULL,
  user_id serial8 NOT NULL,
  institution_id serial8 NOT NULL,
  role_id serial8 NOT NULL,  
  CONSTRAINT dd_user_inst_pk PRIMARY KEY (id),
  CONSTRAINT dd_user_inst_uq UNIQUE (user_id, institution_id, role_id),
  CONSTRAINT dd_user_inst_user_fk FOREIGN KEY (user_id)
      REFERENCES dd_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT dd_user_inst_inst_fk FOREIGN KEY (institution_id)
      REFERENCES dd_institution (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT dd_user_inst_role_fk FOREIGN KEY (role_id)
      REFERENCES dd_role (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (OIDS=FALSE);

CREATE TABLE dd_study
(
  id serial8 NOT NULL,
  is_reported boolean NOT NULL,
  report_body text,
  notes text,
  estado_incidencia integer NOT NULL DEFAULT 0,
  fecha_ultima_incidencia character varying(16),
  img_downloaded boolean DEFAULT false,
  asignado boolean NOT NULL DEFAULT false,
  archivos_adjuntos boolean NOT NULL DEFAULT false,
  CONSTRAINT dd_study_pk PRIMARY KEY (id),
  CONSTRAINT dd_study_study_fk FOREIGN KEY (id)
      REFERENCES study (pk) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (OIDS=FALSE);

CREATE TABLE dd_user_study
(
  user_id bigserial NOT NULL,
  study_id bigserial NOT NULL,
  id bigserial NOT NULL,
  CONSTRAINT dd_user_study_pk PRIMARY KEY (id),
  CONSTRAINT dd_user_study_study_fk FOREIGN KEY (study_id)
      REFERENCES dd_study (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT dd_user_study_user_fk FOREIGN KEY (user_id)
      REFERENCES dd_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT dd_user_study_uq1 UNIQUE (user_id, study_id)
)
WITH (OIDS=FALSE);

CREATE TABLE dd_modality
(
  id serial8 NOT NULL,
  name text NOT NULL,
  CONSTRAINT dd_modality_pk PRIMARY KEY (id)
)
WITH (OIDS=FALSE);

CREATE TABLE dd_user_modality
(
  user_id serial8 NOT NULL,
  modality_id serial8 NOT NULL,
  CONSTRAINT dd_user_modality_pk PRIMARY KEY (user_id, modality_id),
  CONSTRAINT dd_user_modality_user_fk FOREIGN KEY (user_id)
      REFERENCES dd_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT dd_user_modality_modality_fk FOREIGN KEY (modality_id)
      REFERENCES dd_modality (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION      
)
WITH (OIDS=FALSE);

CREATE TABLE dd_config
(
  name text NOT NULL,
  value text NULL,
  CONSTRAINT dd_config_pk PRIMARY KEY (name)
)
WITH (OIDS=FALSE);

CREATE TABLE dd_user_property
(
  "user_id" bigserial NOT NULL,
  "name" text NOT NULL,
  "value" text NOT NULL,
  CONSTRAINT dd_user_property_pk PRIMARY KEY (user_id, name),
  CONSTRAINT dd_user_property_user_fk FOREIGN KEY (user_id)
      REFERENCES dd_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

CREATE TABLE dd_datos_clinicos
(
  id_datos_clinicos bigserial NOT NULL,
  id_study_datos_clinicos bigserial NOT NULL,
  prioridad_datos_clinicos integer NOT NULL DEFAULT 0,
  cte_oral_datos_clinicos boolean,
  cte_ev_datos_clinicos boolean,
  observaciones_datos_clinicos text,
  CONSTRAINT dd_datos_clinicos_pkey PRIMARY KEY (id_datos_clinicos),
  CONSTRAINT dd_datos_clinicos_id_study_datos_clinicos_fkey FOREIGN KEY (id_study_datos_clinicos)
      REFERENCES dd_study (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
COMMENT ON COLUMN dd_datos_clinicos.prioridad_datos_clinicos IS '0 = Sin prioridad
1 = Preferente
2 = Urgente';

CREATE TABLE dd_incidencias
(
  id_incidencia bigserial NOT NULL,
  study_id bigserial NOT NULL,
  fecha_incidencia character varying(16),
  mensaje_incidencia text NOT NULL,
  fecha_mensaje character varying(16),
  estado_incidencia integer NOT NULL DEFAULT 0,
  usuario_incidencia character varying(50),
  CONSTRAINT dd_incidencias_pkey PRIMARY KEY (id_incidencia)
)
WITH (
  OIDS=FALSE
);

CREATE TABLE dd_medicos
(
  id_medico bigserial NOT NULL,
  nombre_abrev_medico character varying(4) NOT NULL,
  direccion_medico character varying(50),
  telefono_1_medico character varying(30) NOT NULL,
  telefono_2_medico character varying(30),
  mail_medico character varying(50) NOT NULL,
  skype_medico character varying(50),
  titulo_medico character varying(20),
  matricula_medico character varying(50),
  firma_medico character varying(50), -- Ruta de la firma digital en JPG
  user_medico_fk bigint NOT NULL,
  CONSTRAINT dd_medicos_pkey PRIMARY KEY (id_medico),
  CONSTRAINT dd_medicos_user_medico_fk_fkey FOREIGN KEY (user_medico_fk)
      REFERENCES dd_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
COMMENT ON COLUMN dd_medicos.firma_medico IS 'Ruta de la firma digital en JPG';

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
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

CREATE TABLE dd_informes
(
	id_informe bigint NOT NULL,
	study_id bigint NOT NULL,
	usuario_informe character varying(100) NOT NULL,	--modificar este campo
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
	CONSTRAINT dd_informes_id_study_fk_fkey FOREIGN KEY (study_id)
	REFERENCES dd_study (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION
	-- eliminar Foreign key "user_id"
)
WITH (
	OIDS=FALSE
);

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
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);


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
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

CREATE OR REPLACE FUNCTION dd_study_catchup_function() RETURNS trigger AS $dd_study_catchup_function$
    BEGIN
	INSERT INTO dd_study VALUES (NEW.pk, FALSE);
        RETURN NEW;
    END;
$dd_study_catchup_function$ LANGUAGE plpgsql;

CREATE TRIGGER dd_study_catchup_trigger AFTER INSERT ON study 
	FOR EACH ROW EXECUTE PROCEDURE dd_study_catchup_function();

-------------------------------------
-- Carga inicial de datos
-------------------------------------

-- Usuario 'admin' creado por defecto, con su clave vencida.
INSERT INTO dd_user VALUES (DEFAULT, 'System', 'Admin', 'admin', md5('admin'), TRUE, TRUE, FALSE);

-- Roles por defecto.
INSERT INTO dd_role VALUES (DEFAULT, 'Administrador');

-- Acciones.
INSERT INTO dd_action(id, name, description) VALUES (1, 'simple-study-visualization', 'Visualización simple de un estudio');
INSERT INTO dd_action(id, name, description) VALUES (2, 'advanced-study-visualization', 'Visualización avanzada de un estudio');
INSERT INTO dd_action(id, name, description) VALUES (3, 'view-study-report', 'Ver informe de estudio');
INSERT INTO dd_action(id, name, description) VALUES (4, 'edit-study-report', 'Editar informe de estudio');
INSERT INTO dd_action(id, name, description) VALUES (5, 'view-study-files', 'Ver los archivos asociados a un estudio');
INSERT INTO dd_action(id, name, description) VALUES (6, 'upload-study-file', 'Subir un archivo y asociarlo a un estudio');
INSERT INTO dd_action(id, name, description) VALUES (7, 'download-study-file', 'Descargar un archivo asociado a un estudio');
INSERT INTO dd_action(id, name, description) VALUES (8, 'delete-study-file', 'Eliminar un archivo asociado a un estudio');
INSERT INTO dd_action(id, name, description) VALUES (9, 'access-study-dicomdir', 'Obtener archivo DICOMDIR e imágenes de un estudio');
INSERT INTO dd_action(id, name, description) VALUES (10, 'view-study-notes', 'Ver notas del estudio');
INSERT INTO dd_action(id, name, description) VALUES (11, 'edit-study-notes', 'Editar las notas del estudio');
INSERT INTO dd_action(id, name, description) VALUES (12, 'assign-study-to', 'Asignar estudio para diagnóstico');
INSERT INTO dd_action(id, name, description) VALUES (13, 'access-studies-from-institution', 'Acceder a estudios de una institución relacionada');
INSERT INTO dd_action(id, name, description) VALUES (14, 'request-study-images', 'Obtener imágenes de un estudio');

-- El rol 'Administrador' tiene permiso para realizar todas las acciones.
INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 1);
INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 2);
INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 3);
INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 4);
INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 5);
INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 6);
INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 7);
INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 8);
INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 9);
INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 10);
INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 11);
INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 12);
INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 13);
INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 14);

-- Incorporar los estudios existentes.
INSERT INTO dd_study 
	SELECT pk, FALSE
	FROM study;

-- Modalidades
INSERT INTO dd_modality (id, name) VALUES (DEFAULT, 'CR');
INSERT INTO dd_modality (id, name) VALUES (DEFAULT, 'DX');
INSERT INTO dd_modality (id, name) VALUES (DEFAULT, 'CT');
INSERT INTO dd_modality (id, name) VALUES (DEFAULT, 'SC');
INSERT INTO dd_modality (id, name) VALUES (DEFAULT, 'US');
INSERT INTO dd_modality (id, name) VALUES (DEFAULT, 'MR');
INSERT INTO dd_modality (id, name) VALUES (DEFAULT, 'NM');
INSERT INTO dd_modality (id, name) VALUES (DEFAULT, 'PX');
INSERT INTO dd_modality (id, name) VALUES (DEFAULT, 'XA');
INSERT INTO dd_modality (id, name) VALUES (DEFAULT, 'RF');
INSERT INTO dd_modality (id, name) VALUES (DEFAULT, 'OT');	

-- Configuración general 
INSERT INTO dd_config (name, value) VALUES ('actions.study.simple-view.url', 'http://190.210.189.236:8080/oviyam/oviyam?studyUID=${STUDYID}');
INSERT INTO dd_config (name, value) VALUES ('actions.study.advanced-view.url', 'http://190.210.189.236:8080/weasis-pacs-connector/viewer.jnlp?studyUID=${STUDYID}');
INSERT INTO dd_config (name, value) VALUES ('report.templates.source-path', 'c:\\a-definir');
INSERT INTO dd_config (name, value) VALUES ('study.uploaded-files.path', 'c:\\a-definir2');
INSERT INTO dd_config (name, value) VALUES ('dcm4chee.server.default.path', 'c:\\a-definir3');
INSERT INTO dd_config (name, value) VALUES ('report.signatures.source-path', 'c:\\a-definir4');
INSERT INTO dd_config (name, value) VALUES ('report.logos.source-path', 'c:\\a-definir5');
INSERT INTO dd_config (name, value) VALUES ('report.saved-reports.path', 'c:\\a-definir6');
INSERT INTO dd_config (name, value) VALUES ('dcm4che.utility.path', 'c:\\a-definir7');
INSERT INTO dd_config (name, value) VALUES ('study.media.lossy-transfer-syntax', '1.2.840.10008.1.2.4.51');

