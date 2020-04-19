--------------------------------------------------------
-- Schema upgrade 0006
--------------------------------------------------------

INSERT INTO dd_action(id, name, description) VALUES (13, 'access-studies-from-institution', 'Acceder a estudios de una institución relacionada');

-- El rol 'Administrador' tiene permiso para realizar todas las acciones.
INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 13);


