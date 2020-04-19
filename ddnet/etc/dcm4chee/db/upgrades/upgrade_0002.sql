--------------------------------------------------------
-- Schema upgrade 0002
--------------------------------------------------------

ALTER TABLE dd_user_study DROP CONSTRAINT dd_user_study_pk;
ALTER TABLE dd_user_study ADD COLUMN id bigserial NOT NULL;
ALTER TABLE dd_user_study ADD CONSTRAINT dd_user_study_pk PRIMARY KEY (id);
ALTER TABLE dd_user_study ADD CONSTRAINT dd_user_study_uq1 UNIQUE (user_id, study_id);