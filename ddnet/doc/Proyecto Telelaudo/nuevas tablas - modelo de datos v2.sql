-- Table: dd_archivos_adjuntos

-- DROP TABLE dd_archivos_adjuntos;

CREATE TABLE dd_archivos_adjuntos
(
  id_archivos_adjuntos bigserial NOT NULL,
  "Fecha_archivos_adjuntos" character varying(8),
  "Hs_archivos_adjuntos" character varying(8),
  "Nombre_archivo_adjunto" character varying(20),
  id_study bigserial NOT NULL,
  CONSTRAINT dd_archivos_adjuntos_pkey PRIMARY KEY (id_archivos_adjuntos),
  CONSTRAINT dd_archivos_adjuntos_id_study_fkey FOREIGN KEY (id_study)
      REFERENCES dd_study (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE dd_archivos_adjuntos OWNER TO postgres;

-------------------------------------------------------

-- Table: dd_asignacion_estudio

-- DROP TABLE dd_asignacion_estudio;

CREATE TABLE dd_asignacion_estudio
(
  id_asignacion_estudio bigserial NOT NULL,
  id_study_fk bigint NOT NULL,
  fecha_asignacion_estudio character varying(8) NOT NULL,
  hs_asignacion_estudio character varying(8) NOT NULL,
  id_medico_fk bigserial NOT NULL,
  estado_asignacion_estudio integer NOT NULL DEFAULT 0,
  CONSTRAINT dd_asignacion_estudio_pkey PRIMARY KEY (id_asignacion_estudio),
  CONSTRAINT dd_asignacion_estudio_id_medico_fk_fkey FOREIGN KEY (id_medico_fk)
      REFERENCES dd_medicos (id_medico) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT dd_asignacion_estudio_id_study_fk_fkey FOREIGN KEY (id_study_fk)
      REFERENCES dd_study (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE dd_asignacion_estudio OWNER TO postgres;


----------------------------------------------------------------------

-- Table: dd_centro

-- DROP TABLE dd_centro;

CREATE TABLE dd_centro
(
  id_centro bigserial NOT NULL,
  "Nom_Fantasia_Centro" character varying(20),
  "Direccion_centro" character varying(40),
  "Localidad_centro" character varying(20),
  "Mail1_centro" character varying(50),
  "Mail2_centro" character varying(50),
  "Telefono1_centro" character varying(30),
  "Telefono2_centro" character varying(30),
  "Skype_centro" character varying(30),
  "Contactos_centro" character varying(50),
  "Observacion_centro" text,
  "Id_institucion_fk" bigserial NOT NULL,
  CONSTRAINT "dd_centro_Id_institucion_fk_fkey" FOREIGN KEY ("Id_institucion_fk")
      REFERENCES dd_institution (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE dd_centro OWNER TO postgres;


---------------------------------------------------------------------

-- Table: dd_datos_clinicos

-- DROP TABLE dd_datos_clinicos;

CREATE TABLE dd_datos_clinicos
(
  id_datos_clinicos bigserial NOT NULL,
  id_study_datos_clinicos bigserial NOT NULL,
  "Prioridad_datos_clinicos" integer NOT NULL DEFAULT 0, -- 0 = Sin prioridad...
  "Cte_oral_datos_clinicos" boolean,
  "Cte_ev_datos_clinicos" boolean,
  "Observaciones_datos_clinicos" text,
  CONSTRAINT dd_datos_clinicos_pkey PRIMARY KEY (id_datos_clinicos),
  CONSTRAINT dd_datos_clinicos_id_study_datos_clinicos_fkey FOREIGN KEY (id_study_datos_clinicos)
      REFERENCES dd_study (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE dd_datos_clinicos OWNER TO postgres;
COMMENT ON COLUMN dd_datos_clinicos."Prioridad_datos_clinicos" IS '0 = Sin prioridad
1 = Preferente
2 = Urgente';



-----------------------------------------------------------------------

-- Table: dd_estados

-- DROP TABLE dd_estados;

CREATE TABLE dd_estados
(
  id_estado bigserial NOT NULL,
  tipo_estado integer,
  "Descripcion_estado" character varying(15),
  CONSTRAINT dd_estados_pkey PRIMARY KEY (id_estado)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE dd_estados OWNER TO postgres;


------------------------------------------------------------------------

-- Table: dd_medicos

-- DROP TABLE dd_medicos;

CREATE TABLE dd_medicos
(
  id_medico bigserial NOT NULL,
  "Direccion_medico" character varying(50),
  "Telefono_1_medico" character varying(30) NOT NULL,
  "Telefono_2_medico" character varying(30),
  "Mail_medico" character varying(50) NOT NULL,
  "Skype_medico" character varying(50),
  "Titulo_medico" character varying(20),
  "Matricula_medico" character varying(10),
  "Firma_medico" character varying(50), -- Ruta de la firma digital en JPG
  "User_medico_fk" bigserial NOT NULL,
  CONSTRAINT dd_medicos_pkey PRIMARY KEY (id_medico),
  CONSTRAINT "dd_medicos_User_medico_fk_fkey" FOREIGN KEY ("User_medico_fk")
      REFERENCES dd_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE dd_medicos OWNER TO postgres;
COMMENT ON COLUMN dd_medicos."Firma_medico" IS 'Ruta de la firma digital en JPG';


----------------------------------------------------------------------------

-- Table: dd_estudios_eliminados

-- DROP TABLE dd_estudios_eliminados;

CREATE TABLE dd_estudios_eliminados
(
  id_estudios_eliminados bigserial NOT NULL,
  id_study_fk bigserial NOT NULL,
  id_user_fk bigserial NOT NULL,
  pat_id character varying(20) NOT NULL,
  nombre_paciente_eliminado text NOT NULL,
  modalidad_eliminado character varying(10) NOT NULL,
  institucion_eliminado text NOT NULL,
  fecha_eliminado character varying(8) NOT NULL,
  hora_eliminado character varying(8) NOT NULL,
  filepath_eliminado text,
  estado_estudio_eliminado integer NOT NULL,
  CONSTRAINT dd_estudios_eliminados_pkey PRIMARY KEY (id_estudios_eliminados),
  CONSTRAINT dd_estudios_eliminados_id_study_fk_fkey FOREIGN KEY (id_study_fk)
      REFERENCES dd_study (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT dd_estudios_eliminados_id_user_fk_fkey FOREIGN KEY (id_user_fk)
      REFERENCES dd_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE dd_estudios_eliminados OWNER TO postgres;