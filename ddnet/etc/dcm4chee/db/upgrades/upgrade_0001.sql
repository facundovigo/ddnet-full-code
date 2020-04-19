--------------------------------------------------------
-- Schema upgrade 0001
--------------------------------------------------------

ALTER TABLE dd_study ADD COLUMN report_body text NULL;

INSERT INTO dd_config (name, value) VALUES ('report.templates.source-path', '/home/rodrigo/all/projects/dev/diagnosticodigital/ddnet/doc/report-templates');
