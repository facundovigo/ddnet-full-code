--------------------------------------------------------
-- Schema upgrade 0003
--------------------------------------------------------

ALTER TABLE dd_study ADD COLUMN notes text NULL;

-- Acciones/Roles/Seguridad
DELETE FROM dd_role_action;
DELETE FROM dd_action;

ALTER TABLE dd_action ADD COLUMN description text NOT NULL;

INSERT INTO dd_action(id, name, description) VALUES (1, 'simple-study-visualization', 
	'Visualización simple de un estudio');
INSERT INTO dd_action(id, name, description) VALUES (2, 'advanced-study-visualization', 
	'Visualización avanzada de un estudio');
INSERT INTO dd_action(id, name, description) VALUES (3, 'view-study-report', 
	'Ver informe de estudio');
INSERT INTO dd_action(id, name, description) VALUES (4, 'edit-study-report', 
	'Editar informe de estudio');
INSERT INTO dd_action(id, name, description) VALUES (5, 'view-study-files', 
	'Ver los archivos asociados a un estudio');
INSERT INTO dd_action(id, name, description) VALUES (6, 'upload-study-file', 
	'Subir un archivo y asociarlo a un estudio');
INSERT INTO dd_action(id, name, description) VALUES (7, 'download-study-file', 
	'Descargar un archivo asociado a un estudio');
INSERT INTO dd_action(id, name, description) VALUES (8, 'delete-study-file', 
	'Eliminar un archivo asociado a un estudio');
INSERT INTO dd_action(id, name, description) VALUES (9, 'access-study-dicomdir', 
	'Obtener archivo DICOMDIR e imágenes de un estudio');
INSERT INTO dd_action(id, name, description) VALUES (10, 'view-study-notes', 
	'Ver notas del estudio');
INSERT INTO dd_action(id, name, description) VALUES (11, 'edit-study-notes', 
	'Editar las notas del estudio');

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

