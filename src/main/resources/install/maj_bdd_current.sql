
--  !!!!!!! Attention !!!!!!!!!
--
-- pour le passage des anciennes versions vers la 4.2.0
-- il faut appliquer ce script à votre BDD actuelle,
-- il faut faire une sauvegarde avant toute opération
--
--  !!!!!!! Attention !!!!!!!!! 

-- version=4.2.2
-- date : 12/01/2017
--
-- n'oubliez pas de définir le role suivant votre installation 
--
SET ROLE = opentheso;
-- 
-- ajout des nouvelles fonctions
--

-- avertissement, il faut faire attention, si le script ne passe pas,
-- c'est peut être que les contraintes que nous avons ajouté montrent des doublons interdits
-- il faut alors les identifier et les supprimer 
-- voici une requête d'exemple pour repérer les doublons 

--
-- 
-- select notetypecode, id_thesaurus, id_term,lexicalvalue, lang, count(*) from note 
-- where id_term !='' group by notetypecode, lexicalvalue, id_thesaurus, id_term, lang having count(*)>1;

-- select notetypecode, id_thesaurus, id_concept,lexicalvalue, lang, count(*) from note 
-- where id_concept !='' group by notetypecode, lexicalvalue, id_thesaurus, id_concept, lang having count(*)>1;




--
--permet de change tous les identifiants à id
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

--
--
--

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
--Permet de créer la table gps
--

-- DROP FUNCTION public.table_gps();

CREATE OR REPLACE FUNCTION public.table_gps() RETURNS void AS $$
BEGIN
    IF NOT EXISTS (SELECT table_name FROM information_schema.tables WHERE table_name = 'gps') THEN

        execute 
		'CREATE TABLE gps (
		id_concept character varying,
		id_theso character varying,
		latitude float,
		longitude float,
		CONSTRAINT gps_pkey PRIMARY KEY (id_concept)
		);'
	;

    END IF;
END;

$$LANGUAGE plpgsql VOLATILE;


--
--Permet de creé la fonction si elle n'existe pas (alignement_format)
--  
CREATE OR REPLACE FUNCTION addtype_Alignement_format() RETURNS VOID AS $$
BEGIN
    IF not exists(SELECT e.enumlabel from pg_type t, pg_enum e
		 where t.oid = e.enumtypid and t.typname = 'alignement_format') then 
		 execute 				
			'CREATE TYPE public.alignement_format AS ENUM
			   (''skos'',
			    ''json'',
			    ''xml'');';
			  END IF;

END;
$$ LANGUAGE plpgsql; 

--
--Permet de creé la fonction si elle n'existe pas (alignement_type_rqt)
--

  CREATE OR REPLACE FUNCTION addtype_Alignement_type_rqt() RETURNS VOID AS $$
BEGIN
    IF not exists(SELECT e.enumlabel from pg_type t, pg_enum e
		 where t.oid = e.enumtypid and t.typname = 'alignement_type_rqt') then 
		 execute 				
			'CREATE TYPE public.alignement_type_rqt AS ENUM
			   (''SPARQL'',
			    ''REST'');';
			  END IF;

END;
$$ LANGUAGE plpgsql;

--  
--Permet de creé la fonction si elle n'existe pas (auth_method)
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
			    ''test'');';
			  END IF;

END;
$$ LANGUAGE plpgsql;

  
--
--Permet de creér la table alignement_source si elle n'existe pas
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
                INSERT INTO alignement_source (id_thesaurus, source, requete, type_rqt, alignement_format) VALUES (''1'', ''wikipedia'', ''https://##lang##.wikipedia.org/w/api.php?action=query&list=search&srwhat=text&format=xml&srsearch=##value##&srnamespace=0"'', ''REST'', ''xml'');
                INSERT INTO alignement_source (id_thesaurus, source, requete, type_rqt, alignement_format) VALUES (''1'', ''Pactols'', ''http://pactols.frantiq.fr/opentheso/webresources/rest/skos/concept/value=##value##&lang=##lang##&th=TH_1'', ''REST'', ''skos'');
                INSERT INTO alignement_source (id_thesaurus, source, requete, type_rqt, alignement_format) VALUES (''1'', ''bnf'', ''PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
                PREFIX xml: <http://www.w3.org/XML/1998/namespace>
                SELECT ?instrument ?prop ?value where {
                  <http://data.bnf.fr/ark:/12148/cb119367821> skos:narrower+ ?instrument.
                  ?instrument ?prop ?value.
                  FILTER( (regex(?prop,skos:prefLabel) || regex(?prop,skos:altLabel))  && regex(?value, ##value##,"i") ) 
                    filter(lang(?value) =##lang##)
                } LIMIT 20'', ''SPARQL'', ''skos'');';

    END IF;
END;
$$ LANGUAGE plpgsql;


--
--Permet de adjuter une nouvelle constraint a la table users
--

create or replace function adjuteconstraintuser() returns void as $$
begin
	if not exists (SELECT * from information_schema.table_constraints where table_name = 'users' and constraint_type = 'UNIQUE'
	and constraint_name ='users_mail_key1') then 
	execute
	'ALTER TABLE ONLY users
	  ADD CONSTRAINT users_mail_key1 UNIQUE 
	  (mail);';
  end if;
  end;
  $$LANGUAGE plpgsql;

--
--Permet de créer la table d'info
--

CREATE OR REPLACE FUNCTION create_table_info() RETURNS VOID AS $$
BEGIN
    IF NOT EXISTS (SELECT table_name FROM information_schema.tables WHERE table_name = 'info') THEN
        execute 
		'CREATE TABLE info (
		    version_Opentheso character varying,
		    version_Bdd character varying
		);';
    END IF;
END;
$$ LANGUAGE plpgsql;




CREATE OR REPLACE FUNCTION info_donnes() RETURNS VOID AS $$
BEGIN
    IF NOT EXISTS (SELECT * FROM info ) THEN

        execute 
		'insert into info  values (''0.0.0'', ''xyz'');';
    END IF;
END;
$$ LANGUAGE plpgsql;


--
-- permet de supprimer une table 
-- avec condition si elle existe ou pas

CREATE OR REPLACE FUNCTION delete_table(TEXT) RETURNS VOID AS $$
DECLARE
 tableName ALIAS FOR $1;

BEGIN
    IF EXISTS (SELECT table_name FROM information_schema.tables WHERE table_name = tableName) THEN

        execute 'drop table ' || tableName ;

    END IF;
END;
$$ LANGUAGE plpgsql;

-- permet d'ajouter une colonne dans une table 
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

		insert into user_role (id_user, id_role)
		 SELECT id, id_role from users;


		Alter Table users drop id_role;';

		END IF;
END;
$$ LANGUAGE plpgsql;

--
-- permet d'ajouter la table user_historique
--

CREATE OR REPLACE FUNCTION create_table_users_historique() RETURNS VOID AS $$
BEGIN
    IF NOT EXISTS (SELECT table_name FROM information_schema.tables WHERE table_name = 'users_historique') THEN

        execute 
		'CREATE TABLE users_historique (
		    id_user integer NOT NULL,
		    username character varying,
		    created timestamp(6) with time zone NOT NULL DEFAULT now(),
		    modified timestamp(6) with time zone NOT NULL DEFAULT now(),
                    delete timestamp(6) with time zone,
			CONSTRAINT users_historique_pkey PRIMARY KEY (id_user))
		WITH (
		  OIDS=FALSE
		);
                INSERT INTO users_historique (id_user, username)
                SELECT id_user, username from users;

                ';

    END IF;
END;
$$ LANGUAGE plpgsql;

--
--permet d'ajouter la table thesaurus_alignement_source
--

CREATE OR REPLACE FUNCTION create_table_thesaurus_alignement_source() RETURNS VOID AS $$
BEGIN
    IF NOT EXISTS (SELECT table_name FROM information_schema.tables WHERE table_name = 'thesaurus_alignement_source') THEN

        execute 
		'CREATE TABLE public.thesaurus_alignement_source
		(
		  id_thesaurus character varying NOT NULL,
		  id_alignement_source integer NOT NULL,
		  CONSTRAINT thesaurus_alignement_source_pkey PRIMARY KEY (id_thesaurus, id_alignement_source)
		)
		WITH (
		  OIDS=FALSE
		);';

    END IF;
END;
$$ LANGUAGE plpgsql;

--
--permet de changer la column de alignemet source
--

CREATE OR REPLACE FUNCTION updateColumn_alignement_source() RETURNS VOID AS $$
BEGIN
    IF  EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS
     WHERE COLUMN_NAME = 'id_thesaurus' AND TABLE_NAME = 'alignement_source') THEN
	Execute
	'Alter TABLE alignement_source DROP COLUMN  id_thesaurus;
	 Alter TABLE alignement_source ADD COLUMN id_user integer;';
    END IF;
END;
$$ LANGUAGE plpgsql; 

--
--Permet d'ajouter la column description dans alignement source
--

CREATE OR REPLACE FUNCTION ajoutercolumn_alignement_source() RETURNS VOID AS $$
BEGIN
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS
     WHERE COLUMN_NAME = 'description' AND TABLE_NAME = 'alignement_source') THEN
	Execute
	'
	 Alter TABLE alignement_source ADD COLUMN description character varying;';
    END IF;
END;
$$ LANGUAGE plpgsql; 

--
--
--
CREATE OR REPLACE FUNCTION ajoutercolumn_alignement() RETURNS VOID AS $$
BEGIN
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS
     WHERE COLUMN_NAME = 'id_alignement_source' AND TABLE_NAME = 'alignement') THEN
	Execute
	'
	 Alter TABLE alignement ADD COLUMN id_alignement_source integer;
	 ALTER TABLE ONLY alignement ADD CONSTRAINT alignement_uri_target_internal_id_thesaurus_internal_id_con_key 
	 UNIQUE (uri_target, internal_id_thesaurus, internal_id_concept);

';
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
		  CACHE 1;';

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
--permet de suprimer une colonne 
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
-- Permet de changer  les sequences de les tables-- term_historique
-- 

CREATE OR REPLACE FUNCTION updatesequencesTH() RETURNS VOID AS $$
declare 
id int;
BEGIN
	IF EXISTS (SELECT * FROM term where term.id is not null) THEN

	SELECT max(term.id) from term into id ; 
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

--
-- Permet de changer  les sequences de les tables-- users
-- 

CREATE OR REPLACE FUNCTION updatesequences_user() RETURNS VOID AS $$
declare 
id int;
BEGIN
	IF EXISTS (SELECT * FROM users where id_user is not null) THEN

	SELECT max(users.id_user) from users into id ; 
	id= id+2;
	Execute
		'
		ALTER SEQUENCE user__id_seq RESTART WITH '|| id||';';
	else
	Execute
		'
		ALTER SEQUENCE user__id_seq RESTART WITH 1;';
		END IF;
END;
$$ LANGUAGE plpgsql;

--
-- Permet de changer  les sequences de les tables-- concept_historique
-- 

CREATE OR REPLACE FUNCTION updatesequencesCH() RETURNS VOID AS $$
declare 
id int;
BEGIN
		IF EXISTS (SELECT * FROM concept where concept.id is not null) THEN
	SELECT max(concept.id) from concept into id ; 
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

--
-- Permet de changer  les sequences de les tables-- concept_group_label_historique
-- 

CREATE OR REPLACE FUNCTION updatesequencesCGLH() RETURNS VOID AS $$
declare 
id int;
BEGIN
	IF EXISTS (SELECT * FROM concept_group_label where concept_group_label.id is not null) THEN
	SELECT max(concept_group_label.id) from concept_group_label into id ; 
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

--
-- Permet de changer  les sequences de les tables-- concept_group_historique
-- 

CREATE OR REPLACE FUNCTION updatesequencesCGH() RETURNS VOID AS $$
declare 
id int;
BEGIN
	IF EXISTS (SELECT * FROM concept_group where concept_group.id is not null) THEN
	SELECT max(concept_group.id) from concept_group into id ; 
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

--
-- Permet de changer  les sequences de les tables-- note_historique
-- 

CREATE OR REPLACE FUNCTION updatesequencesNH() RETURNS VOID AS $$
declare 
id int;
BEGIN
	IF EXISTS (SELECT * FROM note where note.id is not null) THEN
	SELECT max(note.id) from note into id ; 
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
-- Permet de changer  les sequences de les tables-- note_historique
-- 

CREATE OR REPLACE FUNCTION updatesequencesalignement_source() RETURNS VOID AS $$
declare 
id int;
BEGIN
	IF EXISTS (SELECT * FROM alignement_source where alignement_source.id is not null) THEN
	SELECT max(alignement_source.id) from alignement_source into id ; 
	id= id+2;
	Execute
		'
		ALTER SEQUENCE alignement_source__id_seq RESTART WITH '|| id||';';
	
	else
	Execute
		'
		ALTER SEQUENCE alignement_source__id_seq RESTART WITH 1;';
		END IF;
END;
$$ LANGUAGE plpgsql;
-- 
-- fin des fonctions
-- 
create or replace function majnote() returns void as $$
begin
	if not exists (SELECT * from information_schema.table_constraints where table_name = 'note' and constraint_type = 'UNIQUE') then 
	execute 
	'ALTER TABLE ONLY note
	  ADD CONSTRAINT note_notetypecode_id_thesaurus_id_concept_lang_key UNIQUE (notetypecode, id_thesaurus, id_concept, lang, lexicalvalue);
	ALTER TABLE ONLY note
	ADD CONSTRAINT note_notetypecode_id_thesaurus_id_term_lang_key UNIQUE (notetypecode, id_thesaurus, id_term, lang, lexicalvalue);';
	else 
	execute
	'alter table note drop constraint note_notetypecode_id_thesaurus_id_concept_lang_key;
	alter table note drop constraint note_notetypecode_id_thesaurus_id_term_lang_key;
	ALTER TABLE ONLY note
	  ADD CONSTRAINT note_notetypecode_id_thesaurus_id_concept_lang_key UNIQUE (notetypecode, id_thesaurus, id_concept, lang, lexicalvalue);
	ALTER TABLE ONLY note
	ADD CONSTRAINT note_notetypecode_id_thesaurus_id_term_lang_key UNIQUE (notetypecode, id_thesaurus, id_term, lang, lexicalvalue);';
  end if;
  end;
  $$LANGUAGE plpgsql;
  
--
--Permet d'ajouter la column 'gps' dans la table 'concept'
-- Valeur par default: false
--

CREATE OR REPLACE FUNCTION ajouter_column_concept() RETURNS VOID AS $$
BEGIN
    IF not EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS
     WHERE COLUMN_NAME = 'gps' AND TABLE_NAME = 'concept') THEN
	Execute
	'Alter TABLE concept ADD COLUMN gps boolean default false;';
    END IF;
END;
$$ LANGUAGE plpgsql;

--
-- Permet d'ajouter la constraint unique sûr la source dans alignement_source
--
create or replace function adjouteconstraint_alignement_source() returns void as $$
begin
	if not exists (SELECT * from information_schema.table_constraints where table_name = 'alignement_source' and constraint_type = 'UNIQUE'
	and constraint_name ='alignement_source_source_key') then 
	execute
	'ALTER TABLE ONLY alignement_source
	  ADD CONSTRAINT alignement_source_source_key UNIQUE 
	  (source);';
  end if;
  end;
  $$LANGUAGE plpgsql;



--////////////////////////////////à partir Version 4.2.0/////////////////////////////
--
--Permet d'effacer la constraint dans la table alignement
--
create or replace function drop_constraint_alignement_source() returns void as $$
begin
	if exists (SELECT * from information_schema.table_constraints where table_name = 'alignement' and constraint_type = 'UNIQUE'
	and constraint_name ='alignement_concept_target_thesaurus_target_alignement_id_ty_key') then 
	execute
	'ALTER TABLE ONLY alignement
	  DROP CONSTRAINT alignement_concept_target_thesaurus_target_alignement_id_ty_key ;';
  end if;
  end;
  $$LANGUAGE plpgsql;


--
--Permet de crée la table de note_type si n'exists pas
-- et de faire ses includes
--
CREATE OR REPLACE FUNCTION create_table_note_type()
  RETURNS void AS $$
BEGIN
    IF NOT EXISTS (SELECT table_name FROM information_schema.tables WHERE table_name = 'note_type') THEN
        execute 
		'CREATE TABLE note_type
		(
		  code text NOT NULL,
		  isterm boolean NOT NULL,
		  isconcept boolean NOT NULL,
		  CONSTRAINT pk_note_type PRIMARY KEY (code),
		  CONSTRAINT chk_not_false_values CHECK (NOT (isterm = false AND isconcept = false))
		);
		INSERT INTO note_type (code, isterm, isconcept) VALUES (''customNote'', false, true);
		INSERT INTO note_type (code, isterm, isconcept) VALUES (''definition'', true, false);
		INSERT INTO note_type (code, isterm, isconcept) VALUES (''editorialNote'', true, false);
		INSERT INTO note_type (code, isterm, isconcept) VALUES (''historyNote'', true, true);
		INSERT INTO note_type (code, isterm, isconcept) VALUES (''scopeNote'', false, true);
		INSERT INTO note_type (code, isterm, isconcept) VALUES (''note'', false, true);
		INSERT INTO note_type (code, isterm, isconcept) VALUES (''example'', true, false);
		INSERT INTO note_type (code, isterm, isconcept) VALUES (''changeNote'', true, false);';

    END IF;
END;
$$  LANGUAGE plpgsql;

--
--Permet de crée la table de alignement_type si n'exists pas
-- et de faire ses includes
--

CREATE OR REPLACE FUNCTION create_table_alignement_type()
  RETURNS void AS $$
BEGIN
    IF NOT EXISTS (SELECT table_name FROM information_schema.tables WHERE table_name = 'alignement_type') THEN
        execute 
		'CREATE TABLE alignement_type
			(
			  id integer NOT NULL,
			  label text NOT NULL,
			  isocode text NOT NULL,
			  label_skos character varying,
			  CONSTRAINT alignment_type_pkey PRIMARY KEY (id)
			);
			INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (1, ''Equivalence exacte'', ''=EQ'', ''exactMatch'');
			INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (2, ''Equivalence inexacte'', ''~EQ'', ''closeMatch'');
			INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (3, ''Equivalence générique'', ''EQB'', ''broadMatch'');
			INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (4, ''Equivalence associative'', ''EQR'', ''relatedMatch'');
			INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (5, ''Equivalence spécifique'', ''EQS'', ''narrowMatch'');';

    END IF;
END;
$$  LANGUAGE plpgsql;

--
--Permet d'ajouter la column gps a la table alignement source
--

CREATE OR REPLACE FUNCTION ajoutercolumngps_alignement_source() RETURNS VOID AS $$
BEGIN
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS
     WHERE COLUMN_NAME = 'gps' AND TABLE_NAME = 'alignement_source') THEN
	Execute
	'
	 Alter TABLE alignement_source ADD COLUMN gps boolean  default false;';
    END IF;
END;
$$ LANGUAGE plpgsql; 

--
--Permet d'ajouter les 3 columns boolean dans la table preferences
--
CREATE OR REPLACE FUNCTION ajoutercolumn_preferences() RETURNS VOID AS $$
BEGIN
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS
     WHERE COLUMN_NAME = 'gps_integrertraduction' AND TABLE_NAME = 'preferences') THEN
	Execute
	'
         Alter TABLE preferences ADD COLUMN gps_id_source Integer  ;
	 Alter TABLE preferences ADD COLUMN gps_integrertraduction boolean  default true;
	 Alter TABLE preferences ADD COLUMN gps_reemplacertraduction boolean  default true;
	 Alter TABLE preferences ADD COLUMN gps_alignementautomatique boolean  default true;
	 ';
    END IF;
END;
$$ LANGUAGE plpgsql; 

--
--Permet de change l'ancian constraint pour autre nouvelle
--
create or replace function changeconstraintalignement() returns void as $$
begin
	if exists (SELECT * from information_schema.table_constraints where table_name = 'alignement' and constraint_type = 'UNIQUE'
	and constraint_name ='alignement_uri_target_internal_id_thesaurus_internal_id_con_key') then 
	execute
	'
            alter table alignement
            drop constraint alignement_uri_target_internal_id_thesaurus_internal_id_con_key;
        alter table alignement 
            add constraint  alignement_internal_id_concept_internal_id_thesaurus_id_alignement_source_key unique 
            (internal_id_concept, internal_id_thesaurus, id_alignement_source, alignement_id_type)
        ';
        end if;
        if exists (SELECT * from information_schema.table_constraints where table_name = 'alignement' and constraint_type = 'UNIQUE'
	and constraint_name ='alignement_internal_id_concept_internal_id_thesaurus_id_alignem') then 
	execute
	'
            alter table alignement
            drop constraint alignement_internal_id_concept_internal_id_thesaurus_id_alignem;
        alter table alignement 
            add constraint  alignement_internal_id_concept_internal_id_thesaurus_id_alignement_source_key unique 
            (internal_id_concept, internal_id_thesaurus, id_alignement_source, alignement_id_type)
        ';
        end if;
  end;
  $$LANGUAGE plpgsql;

--
--Ajoute la PRIMARY KEY dans alignement si n'exists pas
--
create or replace function add_primary_keyalignement() returns void as $$
begin
	if not exists (SELECT * from information_schema.table_constraints where table_name = 'alignement' and constraint_type = 'PRIMARY KEY'
	and constraint_name ='alignement_pkey') then 
	execute
	'
            ALTER TABLE ONLY alignement
			ADD CONSTRAINT alignement_pkey PRIMARY KEY (id);
';
  end if;
  end;
  $$LANGUAGE plpgsql;

create or replace function add_primary_keyalignement_source() returns void as $$
begin
	if not exists (SELECT * from information_schema.table_constraints where table_name = 'alignement_source' and constraint_type = 'PRIMARY KEY'
	and constraint_name ='alignement_source_pkey') then 
	execute
	'
            ALTER TABLE ONLY alignement_source
			ADD CONSTRAINT alignement_source_pkey PRIMARY KEY (id);
';
  end if;
  end;
  $$LANGUAGE plpgsql;
--
-- permet de changer les id du alignement en cass que il est 0;
--
create or replace function id_alignements()
returns void as $$
declare
	p_data text[]:='{}';
	nom text;
	cherchesource text;
	source text;
	source_recuperated text;
	positio1 integer;
	positio2 integer;
	total integer;
	cherche integer;
	id integer:=0;
	begin 
		for nom in 
		select uri_target from alignement where id_alignement_source = 0
		loop
			 select position ('//' in nom) into positio1;
			 select substring (nom from positio1+2) into source_recuperated;
			 select position ('/' in source_recuperated) into positio2;
			 total := positio2 - positio1;
			 select substring (source_recuperated from 0 for positio2) into source_recuperated;
			for cherchesource in 
			select alignement_source.source from alignement_source
			loop
				if(source_recuperated = cherchesource) then
					select alignement_source.id from alignement_source where alignement_source.source = cherchesource into id;
				execute
					'update alignement set  id_alignement_source = '||id||' where uri_target = '''||nom||''' and id_alignement_source = 0;';
				end if;
			end loop;
			if (id=0) then	
				execute
				'INSERT INTO alignement_source (source, requete, type_rqt, alignement_format) VALUES ('''||source_recuperated||''', ''null'', ''REST'', ''xml'');';
				SELECT max(alignement_source.id) from alignement_source  into id;
				execute
				'update alignement set  id_alignement_source = '||id||' where uri_target = '''||nom||''';';
				
			end if;
			id:=0;	
		end loop;
	end;
	$$language plpgsql;

-- mises à jour 
--
--
--mise a jour de la table note
--



-- création ou mise à jour des séquences 
SELECT majnote();
SELECT create_table_info();
SELECT info_donnes();
SELECT table_gps();
SELECT ajouter_column_concept();
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
SELECT updateID_table();
SELECT updateIDusers();
SELECT updatesequencesNH();
SELECT updatesequencesCGH();
SELECT updatesequencesCGLH();
SELECT updatesequencesCH();
SELECT updatesequencesTH();
SELECT adjuteconstraintuser();
SELECT create_table_users_historique();
SELECT add_primary_keyalignement_source();


SELECT create_table_thesaurus_alignement_source();
-- Creation de les types pour alignement_source

SELECT addtype_Alignement_format();

-- Type: public.alignement_type_rqt


SELECT addtype_Alignement_type_rqt();

-- Type: public.auth_method

SELECT add_primary_keyalignement();


SELECT addtype_auth_method();
SELECT create_table_aligenementSources();
SELECT updateColumn_alignement_source();
SELECT ajouterColumn_alignement_source();
SELECT adjouteconstraint_alignement_source();
SELECT ajoutercolumngps_alignement_source();

-- pas toucher
SELECT updatesequencesalignement_source();
SELECT ajoutercolumn_alignement();


UPDATE alignement SET id_alignement_source = 0  WHERE id_alignement_source  is null;
SELECT id_alignements();
SELECT changeconstraintalignement();


SELECT drop_constraint_alignement_source();
SELECT create_table_alignement_type();
SELECT ajoutercolumn_preferences();
SELECT create_table_note_type();
-- delete sequences
SELECT delete_sequence('user_username_seq');
SELECT delete_sequence('editorial_note__id_seq');
SELECT delete_sequence('definition_note__id_seq');
SELECT delete_sequence('history_note__id_seq');


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
   
--mise a jour de la table concept_group
SELECT delete_column('concept_group','idparentgroup');
SELECT delete_column('concept_group','idconcept');

/*
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


INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (1, 'Equivalence exacte', '=EQ', 'exactMatch');
INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (2, 'Equivalence inexacte', '~EQ', 'closeMatch');
INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (3, 'Equivalence générique', 'EQB', 'broadMatch');
INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (4, 'Equivalence associative', 'EQR', 'relatedMatch');
INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (5, 'Equivalence spécifique', 'EQS', 'narrowMatch');
*/

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


--Changer id_alignement_source a 0 si est null

  
--
--Delete toutes les function
--

SELECT delete_fonction ('create_table_info','');
SELECT delete_fonction ('majnote', '');
SELECT delete_fonction ('table_gps','');
SELECT delete_fonction ('info_donnes','');
SELECT delete_fonction ('ajouter_column_concept','');
SELECT delete_fonction ('updateColumn_alignement_source','');
SELECT delete_fonction ('create_table_thesaurus_alignement_source','');
SELECT delete_fonction ('updatesequencesch','');
SELECT delete_fonction ('updatesequencesth','');
SELECT delete_fonction ('updatesequencesnh','');
SELECT delete_fonction ('updatesequencescgh','');
SELECT delete_fonction ('updatesequencescglh','');
SELECT delete_fonction ('updatesequences_user','');
SELECT delete_fonction ('updateidusers','');
SELECT delete_fonction ('adjuteconstraintuser','');
SELECT delete_fonction ('updateid_table','');
SELECT delete_fonction ('create_table_users_historique','');
SELECT delete_fonction ('updatecolumn_alignement_source','');
SELECT delete_fonction ('updatecolumnterm','TEXT');
SELECT delete_fonction ('delete_table','TEXT');
SELECT delete_fonction ('delete_sequence','TEXT');
SELECT delete_fonction ('createuser_role','');
SELECT delete_fonction ('create_table_aligenementsources','');
SELECT delete_fonction ('ajouter_sequence','TEXT');
SELECT delete_fonction ('addtype_auth_method','');
SELECT delete_fonction ('addtype_alignement_type_rqt','');
SELECT delete_fonction ('addtype_alignement_format','');
SELECT delete_fonction1 ('updatecolumn_table','TEXT','TEXT');
SELECT delete_fonction ('delete_column','TEXT','TEXT');
SELECT delete_fonction ('ajoutercolumn_preferences','');
SELECT delete_fonction ('ajoutercolumngps_alignement_source','');
SELECT delete_fonction ('ajoutercolumn_alignement','');
SELECT delete_fonction ('create_table_alignement_type','');
SELECT delete_fonction ('create_table_note_type','');
SELECT delete_fonction ('adjouteconstraint_alignement_source','');
SELECT delete_fonction ('drop_constraint_alignement_source','');
SELECT delete_fonction ('changeconstraintalignement','');
SELECT delete_fonction ('add_primary_keyalignement','');
SELECT delete_fonction ('id_alignements','');
SELECT delete_fonction ('add_primary_keyalignement_source','');
SELECT delete_fonction ('updatesequencesalignement_source','');

--Ne pas toucher le prochain fonction
SELECT delete_fonction ('ajoutercolumn_alignement_source','');
SELECT delete_fonction ('delete_fonction','TEXT','TEXT');
select delete_fonction1('delete_fonction','TEXT','TEXT');
SELECT delete_fonction1 ('delete_fonction1','TEXT','TEXT');

