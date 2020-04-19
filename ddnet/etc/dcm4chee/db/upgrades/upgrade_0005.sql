--------------------------------------------------------
-- Schema upgrade 0005
--------------------------------------------------------

INSERT INTO dd_action(id, name, description) VALUES (12, 'assign-study-to', 
	'Asignar estudio para diagnóstico');
-- El rol 'Administrador' tiene permiso para realizar todas las acciones.
INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 12);



