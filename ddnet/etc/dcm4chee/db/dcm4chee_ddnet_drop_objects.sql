/* ******************************************* */
/* Script de eliminación de objetos para DDNET */
/* ******************************************* */

DROP TRIGGER dd_study_catchup_trigger ON study; 

DROP FUNCTION dd_study_catchup_function();

DROP FUNCTION create_user(first_name text, last_name text, 
	login text, password_clear text, 
	institution_id bigint, institution_role_id bigint, modalities text);

DROP TABLE dd_segunda_lectura;

DROP TABLE dd_comprobar_caso;

DROP TABLE dd_informes;

DROP TABLE dd_user_institution;

DROP TABLE dd_user_study;

DROP TABLE dd_user_modality;

DROP TABLE dd_role_action;

DROP TABLE dd_institution;

DROP TABLE dd_user_property;

DROP TABLE dd_user;

DROP TABLE dd_action;

DROP TABLE dd_role;

DROP TABLE dd_study;

DROP TABLE dd_modality;

DROP TABLE dd_config;

DROP TABLE dd_datos_clinicos;

DROP TABLE dd_incidencias;

DROP TABLE dd_medicos;

DROP TABLE dd_study_log;

