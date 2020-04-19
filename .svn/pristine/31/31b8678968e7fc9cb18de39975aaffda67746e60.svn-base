			--------------------------------------
			-- 	Carga inicial de datos	    --
			--------------------------------------

	-- Usuario 'admin' creado por defecto --

	INSERT INTO dd_user VALUES (DEFAULT, 'System', 'Admin', 'admin', md5('sergio'), FALSE, TRUE, FALSE);

--	***********************************************		--

	-- Roles por defecto --
	
	INSERT INTO dd_role VALUES (DEFAULT, 'Administrador');
	INSERT INTO dd_role VALUES (DEFAULT, 'Medico Administrador');
	INSERT INTO dd_role VALUES (DEFAULT, 'Medico');
	INSERT INTO dd_role VALUES (DEFAULT, 'Centro');
	INSERT INTO dd_role VALUES (DEFAULT, 'Tecnico');
	INSERT INTO dd_role VALUES (DEFAULT, 'Paciente');

--	***********************************************		--

	-- Incorporar los estudios existentes --

	INSERT INTO study(pk, study_iuid, num_series, num_instances, availability, study_status)
		VALUES(0, '0', 0, 0, 0, 0);

	INSERT INTO dd_study 
		SELECT pk, FALSE
		FROM study;

--	***********************************************		--

	-- Modalidades --

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
	INSERT INTO dd_modality (id, name) VALUES (DEFAULT, 'MG');
	INSERT INTO dd_modality (id, name) VALUES (DEFAULT, 'DXA');

--	***********************************************		--

	-- Configuración general --

	INSERT INTO dd_config (name, value) VALUES ('actions.study.simple-view.url', 'http://${HOST}:8080/oviyam/oviyam?studyUID=${STUDYID}&seriesUID=${SERIEID}');
	INSERT INTO dd_config (name, value) VALUES ('actions.study.advanced-view.url', 'http://${HOST}:8080/oviyam2/viewer.html?studyUID=${STUDYID}&serverName=PACS');
	INSERT INTO dd_config (name, value) VALUES ('report.templates.source-path', 'c:/ddnet/report-templates');
	INSERT INTO dd_config (name, value) VALUES ('dcm4chee.server.default.path', 'c:/DCM4chee/dcm4chee-2.18.1-psql/server/default');
	INSERT INTO dd_config (name, value) VALUES ('study.uploaded-files.path', 'c:/ddnet/study-files');
	INSERT INTO dd_config (name, value) VALUES ('report.signatures.source-path', 'c:/ddnet/signatures');
	INSERT INTO dd_config (name, value) VALUES ('report.logos.source-path', 'c:/ddnet/logos');
	INSERT INTO dd_config (name, value) VALUES ('report.saved-reports.path', 'c:/ddnet/saved-reports');
	INSERT INTO dd_config (name, value) VALUES ('dcm4che.utility.path', 'c:/ddnet/apps/dcm4che-2.0.28');
	INSERT INTO dd_config (name, value) VALUES ('download.tools.path', 'c:/ddnet/apps/descargas');
	INSERT INTO dd_config (name, value) VALUES ('study.media.lossy-transfer-syntax', '1.2.840.10008.1.2.4.51');
	INSERT INTO dd_config (name, value) VALUES ('study.uploaded-orden-medica.path', 'c:/ddnet/orden-medica');
	INSERT INTO dd_config (name, value) VALUES ('statistics-xls-path', 'c:/ddnet/statistics-xls');
	INSERT INTO dd_config (name, value) VALUES ('worklist-pdf-path', 'c:/ddnet/wl-pdf');

--	***********************************************		--

	-- Acciones/Roles --

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
	INSERT INTO dd_action(id, name, description) VALUES (12, 'access-to-abm', 
		'Acceder al ABM del programa');
	INSERT INTO dd_action(id, name, description) VALUES (13, 'assign-study-to', 
		'Asignar estudio para diagnóstico');
	INSERT INTO dd_action(id, name, description) VALUES (14, 'access-studies-from-institution', 
		'Acceder a estudios de una institución relacionada');
	INSERT INTO dd_action(id, name, description) VALUES (15, 'request-study-images', 
		'Obtener imágenes de un estudio');
	INSERT INTO dd_action(id, name, description) VALUES (16, 'set-doctor-on-call', 
		'Definir el Médico que está de guardia');
	INSERT INTO dd_action(id, name, description) VALUES (17, 'declare-clinical-data', 
		'Petición de Datos Clínicos');
	INSERT INTO dd_action(id, name, description) VALUES (18, 'modify-clinical-data', 
		'Modificar Datos Clínicos de Estudio');

--	***********************************************		--

	-- El rol 'Administrador' tiene permiso para realizar todas las acciones --

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
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 15);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 16);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 17);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Administrador'), 18);

	-- Roles para 'Medico Administrador' --

	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico Administrador'), 1);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico Administrador'), 4);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico Administrador'), 5);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico Administrador'), 6);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico Administrador'), 7);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico Administrador'), 8);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico Administrador'), 9);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico Administrador'), 12);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico Administrador'), 13);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico Administrador'), 14);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico Administrador'), 15);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico Administrador'), 16);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico Administrador'), 17);

	-- Roles para 'Medico' --

	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico'), 4);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico'), 5);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico'), 7);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico'), 9);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Medico'), 14);

	-- Roles para 'Centro' --

	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Centro'), 5);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Centro'), 6);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Centro'), 7);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Centro'), 8);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Centro'), 9);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Centro'), 13);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Centro'), 14);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Centro'), 17);

	-- Roles para 'Tecnico' --

	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Tecnico'), 1);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Tecnico'), 3);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Tecnico'), 5);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Tecnico'), 6);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Tecnico'), 7);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Tecnico'), 10);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Tecnico'), 11);

	-- Roles para 'Usuario' --

	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Paciente'), 1);
	INSERT INTO dd_role_action (role_id, action_id) VALUES ((SELECT id FROM dd_role WHERE name = 'Paciente'), 3);

--	***********************************************		--

	-- Modalidades para 'admin' --

	INSERT INTO dd_user_modality VALUES (default, (SELECT id FROM dd_user WHERE "login" = 'admin'), 1);
	INSERT INTO dd_user_modality VALUES (default, (SELECT id FROM dd_user WHERE "login" = 'admin'), 2);
	INSERT INTO dd_user_modality VALUES (default, (SELECT id FROM dd_user WHERE "login" = 'admin'), 3);
	INSERT INTO dd_user_modality VALUES (default, (SELECT id FROM dd_user WHERE "login" = 'admin'), 4);
	INSERT INTO dd_user_modality VALUES (default, (SELECT id FROM dd_user WHERE "login" = 'admin'), 5);
	INSERT INTO dd_user_modality VALUES (default, (SELECT id FROM dd_user WHERE "login" = 'admin'), 6);
	INSERT INTO dd_user_modality VALUES (default, (SELECT id FROM dd_user WHERE "login" = 'admin'), 7);
	INSERT INTO dd_user_modality VALUES (default, (SELECT id FROM dd_user WHERE "login" = 'admin'), 8);
	INSERT INTO dd_user_modality VALUES (default, (SELECT id FROM dd_user WHERE "login" = 'admin'), 9);
	INSERT INTO dd_user_modality VALUES (default, (SELECT id FROM dd_user WHERE "login" = 'admin'), 10);
	INSERT INTO dd_user_modality VALUES (default, (SELECT id FROM dd_user WHERE "login" = 'admin'), 11);
	INSERT INTO dd_user_modality VALUES (default, (SELECT id FROM dd_user WHERE "login" = 'admin'), 12);
	INSERT INTO dd_user_modality VALUES (default, (SELECT id FROM dd_user WHERE "login" = 'admin'), 13);

--	***********************************************		--

	ALTER TABLE files
	  DROP CONSTRAINT files_instance_fk_fkey;

	ALTER TABLE instance
	  DROP CONSTRAINT instance_series_fk_fkey;

	ALTER TABLE series
	  DROP CONSTRAINT series_study_fk_fkey;

	ALTER TABLE series_req
	  DROP CONSTRAINT series_req_series_fk_fkey;

	ALTER TABLE study
	  DROP CONSTRAINT study_patient_fk_fkey;

	ALTER TABLE study_on_fs
	  DROP CONSTRAINT study_on_fs_study_fk_fkey;

	ALTER TABLE files
	  ADD FOREIGN KEY (instance_fk) REFERENCES instance (pk) ON UPDATE NO ACTION ON DELETE CASCADE;

	ALTER TABLE instance
	  ADD FOREIGN KEY (series_fk) REFERENCES series (pk) ON UPDATE NO ACTION ON DELETE CASCADE;

	ALTER TABLE series
	  ADD FOREIGN KEY (study_fk) REFERENCES study (pk) ON UPDATE NO ACTION ON DELETE CASCADE;

	ALTER TABLE series_req
	  ADD FOREIGN KEY (series_fk) REFERENCES series (pk) ON UPDATE NO ACTION ON DELETE CASCADE;

	ALTER TABLE study
	  ADD FOREIGN KEY (patient_fk) REFERENCES patient (pk) ON UPDATE NO ACTION ON DELETE CASCADE;

	ALTER TABLE study_on_fs
	  ADD FOREIGN KEY (study_fk) REFERENCES study (pk) ON UPDATE NO ACTION ON DELETE CASCADE;


