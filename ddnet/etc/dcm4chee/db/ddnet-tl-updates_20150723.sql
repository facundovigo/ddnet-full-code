ALTER TABLE dd_password_restore
  ADD COLUMN restore_host_requested character varying(30) NOT NULL;

INSERT INTO dd_action VALUES
	(default, 'modify-clinical-data', 'Modificar Datos Clínicos de Estudio');

INSERT INTO dd_role_action VALUES(
	(SELECT id FROM dd_role WHERE name = 'Administrador'),
	(SELECT id FROM dd_action WHERE name = 'modify-clinical-data')
);
