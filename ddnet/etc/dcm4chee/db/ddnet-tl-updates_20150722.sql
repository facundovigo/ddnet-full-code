CREATE TABLE public.dd_password_restore
(
   id bigserial, 
   user_id bigint NOT NULL, 
   restore_key character varying(100) NOT NULL, 
   restore_done boolean NOT NULL DEFAULT false, 
   restore_times integer NOT NULL DEFAULT 0, 
   restore_init_date character varying(20), 
   restore_limit_date character varying(20) NOT NULL, 
   PRIMARY KEY (id), 
   FOREIGN KEY (user_id) REFERENCES dd_user (id) ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITH (
  OIDS = FALSE
)
;
ALTER TABLE public.dd_password_restore
  OWNER TO postgres;

ALTER TABLE dd_envio_correo
   ALTER COLUMN study_id DROP NOT NULL;

ALTER TABLE dd_envio_correo
  ADD COLUMN restore_uuid character varying(100);

INSERT INTO study(pk, study_iuid, num_series, num_instances, availability, study_status)
VALUES(0, '0', 0, 0, 0, 0);

