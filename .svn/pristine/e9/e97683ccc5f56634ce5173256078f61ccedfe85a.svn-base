--------------------------------------------------------
-- Schema upgrade 0008
--------------------------------------------------------

INSERT INTO dd_config (name, value) VALUES ('dcm4che.utility.path', '/home/rodrigo/all/projects/dev/diagnosticodigital/tools/dcm4che-2.0.28');

CREATE TABLE dd_user_property
(
  user_id bigserial NOT NULL,
  name text NOT NULL,
  value text NOT NULL,
  CONSTRAINT dd_user_property_pk PRIMARY KEY (user_id, name),
  CONSTRAINT dd_user_property_user_fk FOREIGN KEY (user_id)
      REFERENCES dd_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
