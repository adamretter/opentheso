
--  !!!!!!! Attention !!!!!!!!!
--
-- pour le passage des anciennes versions vers la 4.1
-- il faut appliquer ce script à votre BDD actuelle,
-- il faut faire une sauvegarde avant toute opération
--
--  !!!!!!! Attention !!!!!!!!! 



-- 
-- ajout des nouvelles fonctions
--

--
--permet de change tout les identifian a id
--
CREATE OR REPLACE FUNCTION updateID_table() RETURNS VOID AS $$
BEGIN
    IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS
     WHERE COLUMN_NAME = 'id_alignement' AND TABLE_NAME = 'alignement') THEN
	Execute
		'ALTER TABLE alignement RENAME COLUMN id_alignement TO id;
		ALTER TABLE concept_group_label RENAME COLUMN idgrouplabel TO id;
		ALTER TABLE concept_group_label_historique RENAME COLUMN idgrouplabel TO id;
		ALTER TABLE note RENAME COLUMN id_note TO id;
		ALTER TABLE note_historique RENAME COLUMN id_note TO id;
		ALTER TABLE preferences RENAME COLUMN id_pref TO id;
		ALTER TABLE thesaurus_array RENAME COLUMN identifier TO facet_id;
		ALTER TABLE node_label RENAME COLUMN id TO facet_id;
		';
    END IF;
END;
$$ LANGUAGE plpgsql; 

CREATE OR REPLACE FUNCTION updateIDusers() RETURNS VOID AS $$
BEGIN
    IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS
     WHERE COLUMN_NAME = 'id' AND TABLE_NAME = 'users') THEN
	Execute
	'ALTER TABLE users RENAME COLUMN id TO id_user';
	end if;
end;
$$ LANGUAGE plpgsql;


--
--Permet de creé la fonction si n'exists pas alignement_format
--  
CREATE OR REPLACE FUNCTION addtype_Alignement_format() RETURNS VOID AS $$
BEGIN
    IF not exists(SELECT e.enumlabel from pg_type t, pg_enum e
		 where t.oid = e.enumtypid and t.typname = 'alignement_format') then 
		 execute 				
			'CREATE TYPE public.alignement_format AS ENUM
			   (''skos'',
			    ''json'',
			    ''xml'');
			ALTER TYPE public.alignement_format
			  OWNER TO opentheso;';
			  END IF;

END;
$$ LANGUAGE plpgsql; 

--
--Permet de creé la fonction si n'exists pas alignement_type_rqt
--
  CREATE OR REPLACE FUNCTION addtype_Alignement_type_rqt() RETURNS VOID AS $$
BEGIN
    IF not exists(SELECT e.enumlabel from pg_type t, pg_enum e
		 where t.oid = e.enumtypid and t.typname = 'alignement_type_rqt') then 
		 execute 				
			'CREATE TYPE public.alignement_type_rqt AS ENUM
			   (''SPARQL'',
			    ''REST'');
			ALTER TYPE public.alignement_type_rqt
			  OWNER TO opentheso;';
			  END IF;

END;
$$ LANGUAGE plpgsql;

--  
--Permet de creé la fonction si n'exists pas auth_method
--
  CREATE OR REPLACE FUNCTION addtype_auth_method() RETURNS VOID AS $$
BEGIN
    IF not exists(SELECT e.enumlabel from pg_type t, pg_enum e
		 where t.oid = e.enumtypid and t.typname = 'auth_method') then 
		 execute 				
			'CREATE TYPE public.auth_method AS ENUM
			   (''DB'',
			    ''LDAP'',
			    ''FILE'',
			    ''test'');
			ALTER TYPE public.auth_method
			  OWNER TO opentheso;';
			  END IF;

END;
$$ LANGUAGE plpgsql;

  
--
--Permet de creér la table alignement_source si n'exists pas
--
CREATE OR REPLACE FUNCTION create_table_aligenementSources() RETURNS VOID AS $$
BEGIN
    IF NOT EXISTS (SELECT table_name FROM information_schema.tables WHERE table_name = 'alignement_source') THEN

        execute 
		'CREATE TABLE alignement_source (
		    id_thesaurus character varying NOT NULL,
		    source character varying,
		    requete character varying,
		    type_rqt alignement_type_rqt NOT NULL,
		    alignement_format alignement_format NOT NULL,
		    id integer DEFAULT nextval(''alignement_source__id_seq''::regclass) NOT NULL,
			CONSTRAINT alignement_source_pkey PRIMARY KEY (id),
			CONSTRAINT alignement_source_id_thesaurus_source_key UNIQUE (id_thesaurus, source)
		);


		ALTER TABLE alignement_source OWNER TO opentheso;';

    END IF;
END;
$$ LANGUAGE plpgsql;




--
-- permet de supprimer une table 
-- avec condition si elle n'existe ou pas
CREATE OR REPLACE FUNCTION delete_table(TEXT) RETURNS VOID AS $$
DECLARE
 tableName ALIAS FOR $1;

BEGIN
    IF EXISTS (SELECT table_name FROM information_schema.tables WHERE table_name = tableName) THEN

        execute 'drop table ' || tableName ;

    END IF;
END;
$$ LANGUAGE plpgsql;



-- permet de adjuter une colonne dans une table si n'exists pas
-- exp :  select updatecolumn_table('term','contributor','integer')
-- (table, colonne, type) 

CREATE OR REPLACE FUNCTION updateColumn_table(TEXT, TEXT, TEXT) RETURNS VOID AS $$
DECLARE
 tableName1 ALIAS FOR $1;
 columnName1 ALIAS for $2;
 typeVar ALIAS FOR $3;

BEGIN
    IF not EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS
     WHERE COLUMN_NAME = columnName1 AND TABLE_NAME = tableName1) THEN
	Execute
	'Alter TABLE '||tableName1||' ADD COLUMN ' || columnName1|| ' ' || typeVar||'  default false;';
    END IF;
END;
$$ LANGUAGE plpgsql; 




-- Table: public.user_role
-- DROP TABLE public.user_role;
-- permet de créer la table user_role qui n'existait pas avant la version 4.8
CREATE OR REPLACE FUNCTION createUser_role() RETURNS VOID AS $$
DECLARE 

Begin
	IF NOT EXISTs (SELECT * from information_schema.tables  where table_name = 'user_role') then
		execute 'CREATE TABLE user_role 
		(
		  id_user integer NOT NULL,
		  id_role integer NOT NULL,
		  id_thesaurus character varying  DEFAULT ''''::character varying,
		  id_group character varying,
		  CONSTRAINT user_role_pkey PRIMARY KEY (id_user, id_role, id_thesaurus))
		WITH (
		  OIDS=FALSE
		);
		ALTER TABLE public.user_role
		  OWNER TO opentheso;


		insert into user_role (id_user, id_role)
		 select id, id_role from users;


		Alter Table users drop id_role;';

		END IF;
END;
$$ LANGUAGE plpgsql;


--permet d'ajouter une sequence si elle n'existe pas

CREATE OR REPLACE FUNCTION ajouter_sequence(TEXT) RETURNS VOID AS $$
DECLARE
 nom_sequence ALIAS FOR $1;

BEGIN
    IF NOT EXISTS (SELECT * FROM information_schema.sequences WHERE sequence_name = nom_sequence) THEN

        execute 'CREATE sequence ' || nom_sequence || '
              INCREMENT 1
		  MINVALUE 1
		  MAXVALUE 9223372036854775807
		  START 1
		  CACHE 1;
		ALTER TABLE '||nom_sequence||'
		  OWNER TO opentheso;
		';

    END IF;
END;
$$ LANGUAGE plpgsql;


-- permet de supprimer une sequence si elle existe

CREATE OR REPLACE FUNCTION delete_sequence(TEXT) RETURNS VOID AS $$
DECLARE
 nom_sequence ALIAS FOR $1;

BEGIN
    IF EXISTS (SELECT * FROM information_schema.sequences WHERE sequence_name = nom_sequence) THEN
        execute 'Drop sequence ' || nom_sequence;
    END IF;
END;
$$ LANGUAGE plpgsql; 


-- permet d'effacer une fonction
CREATE OR REPLACE FUNCTION delete_fonction(TEXT, TEXT) RETURNS VOID AS $$
DECLARE
 nom_fonction ALIAS FOR $1;
 type_function ALIAS for $2;

BEGIN
    IF EXISTS (SELECT proargtypes FROM pg_proc  WHERE proname = nom_fonction) THEN
        execute 'Drop function ' || nom_fonction||'('||type_function||')';
    END IF;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION delete_fonction(TEXT, TEXT, TEXT) RETURNS VOID AS $$
DECLARE
 nom_fonction ALIAS FOR $1;
 type_function ALIAS for $2;
 type_function2 ALIAS for $3;
BEGIN
    IF EXISTS (SELECT proargtypes FROM pg_proc  WHERE proname = nom_fonction) THEN
        execute 'Drop function ' || nom_fonction||'('||type_function||','||type_function2||')';
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION delete_fonction1(TEXT, TEXT, TEXT) RETURNS VOID AS $$
DECLARE
 nom_fonction ALIAS FOR $1;
 type_function ALIAS for $2;
 type_function2 ALIAS for $3;
BEGIN
    IF EXISTS (SELECT proargtypes FROM pg_proc  WHERE proname = nom_fonction) THEN
        execute 'Drop function ' || nom_fonction||'('||type_function||','||type_function2||','||type_function2||')';
    END IF;
END;
$$ LANGUAGE plpgsql;
--
--ajoute des colonnes creator et contributor
--
--drop function updateColumnTerm(TEXT);
CREATE OR REPLACE FUNCTION updateColumnTerm(TEXT) RETURNS VOID AS $$
DECLARE
tableName1 ALIAS for $1;
BEGIN
    IF not exists(SELECT *FROM INFORMATION_SCHEMA.COLUMNS
     WHERE COLUMN_NAME = 'creator' AND TABLE_NAME = tableName1) THEN

	EXECUTE
		'ALTER TABLE term ADD COLUMN contributor integer;
		ALTER TABLE term ADD COLUMN creator integer;';
     ELSE
     EXECUTE
		'
		ALTER TABLE term ALTER COLUMN creator TYPE integer USING (creator::integer);
		ALTER TABLE term ALTER COLUMN contributor TYPE integer USING (creator::integer);';
		END IF;
END;
$$ LANGUAGE plpgsql;

--
--permet de suprimer une colonne s'exists
--select('nom_table','colonne')
--
CREATE OR REPLACE FUNCTION delete_column(TEXT, TEXT) RETURNS VOID AS $$
DECLARE
 nom_table ALIAS FOR $1;
 nom_column ALIAS for $2;

BEGIN
    IF EXISTS (SELECT *FROM INFORMATION_SCHEMA.COLUMNS
     WHERE COLUMN_NAME = nom_column AND TABLE_NAME = nom_table) THEN
     EXECUTE 
	'alter table '|| nom_table || ' drop column '||nom_column||';';
    END IF;
END;
$$ LANGUAGE plpgsql;


--
-- Permet de changer  les id de les tables
-- 
CREATE OR REPLACE FUNCTION updatesequencesTH() RETURNS VOID AS $$
declare 
id int;
BEGIN
	IF EXISTS (SELECT * FROM term where term.id is not null) THEN

	select max(term.id) from term into id ; 
	id= id+2;
	Execute
		'ALTER TABLE ONLY term_historique ALTER COLUMN id SET DEFAULT nextval(''term_historique__id_seq''::regclass);
		ALTER SEQUENCE term_historique__id_seq RESTART WITH '|| id||';';
		else
		Execute
		'ALTER TABLE ONLY term_historique ALTER COLUMN id SET DEFAULT nextval(''term_historique__id_seq''::regclass);
		ALTER SEQUENCE term_historique__id_seq RESTART WITH 1;';
		END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION updatesequencesCH() RETURNS VOID AS $$
declare 
id int;
BEGIN
		IF EXISTS (SELECT * FROM concept where concept.id is not null) THEN
	select max(concept.id) from concept into id ; 
	id= id+2;
	Execute
		'ALTER TABLE ONLY concept_historique ALTER COLUMN id SET DEFAULT nextval(''concept_historique__id_seq''::regclass);
		ALTER SEQUENCE concept_historique__id_seq RESTART WITH '|| id||';';
	ELSE 
	execute	
	'ALTER TABLE ONLY concept_historique ALTER COLUMN id SET DEFAULT nextval(''concept_historique__id_seq''::regclass);
		ALTER SEQUENCE concept_historique__id_seq RESTART WITH 1;';
	END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION updatesequencesCGLH() RETURNS VOID AS $$
declare 
id int;
BEGIN
	IF EXISTS (SELECT * FROM concept_group_label where concept_group_label.id is not null) THEN
	select max(concept_group_label.id) from concept_group_label into id ; 
	id= id+2;
	Execute
		'ALTER TABLE ONLY concept_group_label_historique ALTER COLUMN id SET DEFAULT nextval(''concept_group_label_historique__id_seq''::regclass);
		ALTER SEQUENCE concept_group_label_historique__id_seq RESTART WITH '|| id||';';
		else 
		Execute
		'ALTER TABLE ONLY concept_group_label_historique ALTER COLUMN id SET DEFAULT nextval(''concept_group_label_historique__id_seq''::regclass);
		ALTER SEQUENCE concept_group_label_historique__id_seq RESTART WITH 1;';
		END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION updatesequencesCGH() RETURNS VOID AS $$
declare 
id int;
BEGIN
	IF EXISTS (SELECT * FROM concept_group where concept_group.id is not null) THEN
	select max(concept_group.id) from concept_group into id ; 
	id= id+2;
	Execute
		'ALTER TABLE ONLY concept_group_historique ALTER COLUMN id SET DEFAULT nextval(''concept_group_historique__id_seq''::regclass);
		ALTER SEQUENCE concept_group_historique__id_seq RESTART WITH '|| id||';';
		else
		Execute
		'ALTER TABLE ONLY concept_group_historique ALTER COLUMN id SET DEFAULT nextval(''concept_group_historique__id_seq''::regclass);
		ALTER SEQUENCE concept_group_historique__id_seq RESTART WITH 1;';
		END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION updatesequencesNH() RETURNS VOID AS $$
declare 
id int;
BEGIN
	IF EXISTS (SELECT * FROM note where note.id is not null) THEN
	select max(note.id) from note into id ; 
	id= id+2;
	Execute
		'ALTER TABLE ONLY note_historique ALTER COLUMN id SET DEFAULT nextval(''note_historique__id_seq''::regclass);
		ALTER SEQUENCE note_historique__id_seq RESTART WITH '|| id||';';
	
	else
	Execute
		'ALTER TABLE ONLY note_historique ALTER COLUMN id SET DEFAULT nextval(''note_historique__id_seq''::regclass);
		ALTER SEQUENCE note_historique__id_seq RESTART WITH 1;';
		END IF;
END;
$$ LANGUAGE plpgsql;
-- 
-- fin des fonctions
-- 




--
-- mises à jour 
--



-- création ou mise à jour des séquences 

SELECT ajouter_sequence('alignement_source__id_seq');
SELECT ajouter_sequence('concept_group_historique__id_seq');
SELECT ajouter_sequence('concept_group_label_historique__id_seq');
SELECT ajouter_sequence('concept_historique__id_seq');
SELECT ajouter_sequence('note_historique__id_seq');
SELECT ajouter_sequence('pref__id_seq');
SELECT ajouter_sequence('role_id_seq');
SELECT ajouter_sequence('term_historique__id_seq');
SELECT createUser_role();
SELECT updateColumnTerm('term');
SELECT updateColumn_table('users','passtomodify','boolean');
select updateID_table();
SELECT updateIDusers();
SELECT updatesequencesNH();
SELECT updatesequencesCGH();
SELECT updatesequencesCGLH();
SELECT updatesequencesCH();
SELECT updatesequencesTH();
-- Creation de les types pour alignement_source

select addtype_Alignement_format();

-- Type: public.alignement_type_rqt


select addtype_Alignement_type_rqt();

-- Type: public.auth_method

select addtype_auth_method();
SELECT create_table_aligenementSources();


-- delete sequences
SELECT delete_sequence('user_username_seq');
SELECT delete_sequence('editorial_note__id_seq');
SELECT delete_sequence('definition_note__id_seq');
SELECT delete_sequence('history_note__id_seq');



ALTER TABLE pref__id_seq OWNER TO opentheso;
ALTER TABLE role_id_seq OWNER TO opentheso;
ALTER TABLE ONLY roles ALTER COLUMN id SET DEFAULT nextval('role_id_seq'::regclass);


--
-- création et mise à jour des tables
--
delete from roles;
INSERT INTO roles (id, name, description) VALUES (1, 'superAdmin', 'Super Administrateur pour tout gérer tout thésaurus et tout utilisateur');
INSERT INTO roles (id, name, description) VALUES (2, 'admin', 'administrateur par thésaurus ou plus');
INSERT INTO roles (id, name, description) VALUES (3, 'user', 'utilisateur par thésaurus ou plus');
INSERT INTO roles (id, name, description) VALUES (4, 'traducteur', 'traducteur par thésaurus ou plus');
INSERT INTO roles (id, name, description) VALUES (5, 'images', 'gestion des images par thésaurus ou plus');
 



DROP TABLE preferences;

CREATE TABLE preferences
(
  id_pref integer NOT NULL DEFAULT nextval('pref__id_seq'::regclass),
  id_thesaurus character varying NOT NULL,
  source_lang character varying(3),
  nb_alert_cdt integer,
  alert_cdt boolean,
  CONSTRAINT preferences_pkey PRIMARY KEY (id_pref),
  CONSTRAINT preferences_id_thesaurus_key UNIQUE (id_thesaurus)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE preferences
  OWNER TO opentheso;
  
--mise a jour de la table concept_group
SELECT delete_column('concept_group','idparentgroup');
SELECT delete_column('concept_group','idconcept');


-- Mise à jour de la table de types d'alignement
DROP TABLE alignement_type;

CREATE TABLE alignement_type
(
  id integer NOT NULL,
  label text NOT NULL,
  isocode text NOT NULL,
  label_skos character varying,
  CONSTRAINT alignment_type_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE alignement_type
  OWNER TO opentheso;

INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (1, 'Equivalence exacte', '=EQ', 'exactMatch');
INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (2, 'Equivalence inexacte', '~EQ', 'closeMatch');
INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (3, 'Equivalence générique', 'EQB', 'broadMatch');
INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (4, 'Equivalence associative', 'EQR', 'relatedMatch');
INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (5, 'Equivalence spécifique', 'EQS', 'narrowMatch');


-- Mise à jour de la Function: unaccent_string(text)

DROP FUNCTION unaccent_string(text);

CREATE OR REPLACE FUNCTION unaccent_string(text)
  RETURNS text AS
$BODY$
DECLARE
input_string text := $1;
BEGIN

input_string := translate(input_string, 'âãäåāăąÁÂÃÄÅĀĂĄ', 'aaaaaaaaaaaaaaa');
input_string := translate(input_string, 'èééêëēĕėęěĒĔĖĘĚ', 'eeeeeeeeeeeeeee');
input_string := translate(input_string, 'ìíîïìĩīĭÌÍÎÏÌĨĪĬ', 'iiiiiiiiiiiiiiii');
input_string := translate(input_string, 'óôõöōŏőÒÓÔÕÖŌŎŐ', 'ooooooooooooooo');
input_string := translate(input_string, 'ùúûüũūŭůÙÚÛÜŨŪŬŮ', 'uuuuuuuuuuuuuuuu');
input_string := translate(input_string, '-_/()', '     ');

return input_string;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION unaccent_string(text)
  OWNER TO opentheso;



  
--mise à jour de la table note_type
DROP TABLE note_type;

CREATE TABLE note_type
(
  code text NOT NULL,
  isterm boolean NOT NULL,
  isconcept boolean NOT NULL,
  CONSTRAINT pk_note_type PRIMARY KEY (code),
  CONSTRAINT chk_not_false_values CHECK (NOT (isterm = false AND isconcept = false))
)
WITH (
  OIDS=FALSE
);
ALTER TABLE note_type
  OWNER TO opentheso;

INSERT INTO note_type (code, isterm, isconcept) VALUES ('customNote', false, true);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('definition', true, false);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('editorialNote', true, false);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('historyNote', true, true);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('scopeNote', false, true);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('note', false, true);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('example', true, false);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('changeNote', true, false);

--
--Delete toutes les function
--
select delete_fonction ('updatesequencesch','');
select delete_fonction ('updatesequencesth','');
select delete_fonction ('updatesequencesnh','');
select delete_fonction ('updatesequencescgh','');
select delete_fonction ('updatesequencescglh','');
select delete_fonction ('updateidusers','');
select delete_fonction ('updateid_table','');
select delete_fonction ('updatecolumnterm','TEXT');
select delete_fonction ('delete_table','TEXT');
select delete_fonction ('delete_sequence','TEXT');
select delete_fonction ('createuser_role','');
select delete_fonction ('create_table_aligenementsources','');
select delete_fonction ('ajouter_sequence','TEXT');
select delete_fonction ('addtype_auth_method','');
select delete_fonction ('addtype_alignement_type_rqt','');
select delete_fonction ('addtype_alignement_format','');
select delete_fonction1 ('updatecolumn_table','TEXT','TEXT');
select delete_fonction ('delete_column','TEXT','TEXT');
select delete_fonction ('delete_fonction','TEXT','TEXT');
select delete_fonction1 ('delete_fonction','TEXT','TEXT');
select delete_fonction1 ('delete_fonction1','TEXT','TEXT');










  

 










 

