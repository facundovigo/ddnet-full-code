--------------------------------------------------------
-- Schema upgrade 0007
--------------------------------------------------------

INSERT INTO dd_action(id, name, description) VALUES (14, 'request-study-images', 'Obtener imágenes de un estudio');

-- El rol 'Administrador' tiene permiso para realizar todas las acciones.
INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 14);


