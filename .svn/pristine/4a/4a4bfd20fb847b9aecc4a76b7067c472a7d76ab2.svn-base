CREATE OR REPLACE FUNCTION create_user(first_name text, last_name text, 
	login text, password_clear text, 
	institution_id bigint, institution_role_id bigint, modalities text)
RETURNS void AS '
    INSERT INTO dd_user (first_name, last_name, login, password, has_password_expired, 
	is_administrator, deleted) 
    VALUES ($1, $2, $3, md5($4), false, false, false);
    
    INSERT INTO dd_user_institution (user_id, institution_id, role_id)
    VALUES (CURRVAL(''dd_user_id_seq''), $5, $6);

    INSERT INTO dd_user_modality (user_id, modality_id)
	SELECT CURRVAL(''dd_user_id_seq''), m.id
	FROM dd_modality m
	WHERE m.name = ANY(string_to_array(replace($7, '' '', ''''), '',''));	
' LANGUAGE SQL;

