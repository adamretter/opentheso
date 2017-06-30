--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.1
-- Dumped by pg_dump version 9.4.1
-- Started on 2017-06-30 10:46:05 CEST

-- version=4.3.0

SET role = opentheso;


SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 248 (class 3079 OID 12123)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

-- CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2887 (class 0 OID 0)
-- Dependencies: 248
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

-- COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- TOC entry 601 (class 1247 OID 160446)
-- Name: alignement_format; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE alignement_format AS ENUM (
    'skos',
    'json',
    'xml'
);


--
-- TOC entry 604 (class 1247 OID 160454)
-- Name: alignement_type_rqt; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE alignement_type_rqt AS ENUM (
    'SPARQL',
    'REST'
);


--
-- TOC entry 607 (class 1247 OID 160460)
-- Name: auth_method; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE auth_method AS ENUM (
    'DB',
    'LDAP',
    'FILE'
);


--
-- TOC entry 261 (class 1255 OID 225722)
-- Name: unaccent_string(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION unaccent_string(text) RETURNS text
    LANGUAGE plpgsql
    AS $_$
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
$_$;


--
-- TOC entry 172 (class 1259 OID 160468)
-- Name: alignement_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE alignement_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 173 (class 1259 OID 160470)
-- Name: alignement; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE alignement (
    id integer DEFAULT nextval('alignement_id_seq'::regclass) NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    author integer,
    concept_target character varying,
    thesaurus_target character varying,
    uri_target character varying,
    alignement_id_type integer NOT NULL,
    internal_id_thesaurus character varying NOT NULL,
    internal_id_concept character varying,
    id_alignement_source integer
);


--
-- TOC entry 238 (class 1259 OID 170623)
-- Name: alignement_preferences_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE alignement_preferences_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 240 (class 1259 OID 170642)
-- Name: alignement_preferences; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE alignement_preferences (
    id integer DEFAULT nextval('alignement_preferences_id_seq'::regclass) NOT NULL,
    id_thesaurus character varying NOT NULL,
    id_user integer NOT NULL,
    id_concept_depart character varying NOT NULL,
    id_concept_tratees character varying,
    id_alignement_source integer NOT NULL
);


--
-- TOC entry 174 (class 1259 OID 160479)
-- Name: alignement_source__id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE alignement_source__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 175 (class 1259 OID 160481)
-- Name: alignement_source; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE alignement_source (
    source character varying,
    requete character varying,
    type_rqt alignement_type_rqt NOT NULL,
    alignement_format alignement_format NOT NULL,
    id integer DEFAULT nextval('alignement_source__id_seq'::regclass) NOT NULL,
    id_user integer,
    description character varying,
    gps boolean DEFAULT false
);


--
-- TOC entry 176 (class 1259 OID 160488)
-- Name: alignement_type; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE alignement_type (
    id integer NOT NULL,
    label text NOT NULL,
    isocode text NOT NULL,
    label_skos character varying
);


--
-- TOC entry 177 (class 1259 OID 160494)
-- Name: compound_equivalence; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE compound_equivalence (
    id_split_nonpreferredterm text NOT NULL,
    id_preferredterm text NOT NULL
);


--
-- TOC entry 178 (class 1259 OID 160500)
-- Name: concept__id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE concept__id_seq
    START WITH 43
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 244 (class 1259 OID 217479)
-- Name: concept; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE concept (
    id_concept character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    id_ark character varying,
    created timestamp with time zone DEFAULT now() NOT NULL,
    modified timestamp with time zone DEFAULT now() NOT NULL,
    status character varying,
    notation character varying DEFAULT ''::character varying,
    top_concept boolean,
    id integer DEFAULT nextval('concept__id_seq'::regclass) NOT NULL,
    gps boolean DEFAULT false
);


--
-- TOC entry 179 (class 1259 OID 160513)
-- Name: concept_candidat__id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE concept_candidat__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 180 (class 1259 OID 160515)
-- Name: concept_candidat; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE concept_candidat (
    id_concept character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    created timestamp with time zone DEFAULT now() NOT NULL,
    modified timestamp with time zone DEFAULT now() NOT NULL,
    status character varying DEFAULT 'a'::character varying,
    id integer DEFAULT nextval('concept_candidat__id_seq'::regclass),
    admin_message character varying,
    admin_id integer
);


--
-- TOC entry 181 (class 1259 OID 160525)
-- Name: concept_fusion; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE concept_fusion (
    id_concept1 character varying NOT NULL,
    id_concept2 character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    modified timestamp with time zone DEFAULT now() NOT NULL,
    id_user integer NOT NULL
);


--
-- TOC entry 182 (class 1259 OID 160532)
-- Name: concept_group__id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE concept_group__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 183 (class 1259 OID 160534)
-- Name: concept_group; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE concept_group (
    idgroup text NOT NULL,
    id_ark text NOT NULL,
    idthesaurus text NOT NULL,
    idtypecode text NOT NULL,
    notation text,
    id integer DEFAULT nextval('concept_group__id_seq'::regclass) NOT NULL
);


--
-- TOC entry 184 (class 1259 OID 160541)
-- Name: concept_group_concept; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE concept_group_concept (
    idgroup text NOT NULL,
    idthesaurus text NOT NULL,
    idconcept text NOT NULL
);


--
-- TOC entry 185 (class 1259 OID 160547)
-- Name: concept_group_historique__id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE concept_group_historique__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 186 (class 1259 OID 160549)
-- Name: concept_group_historique; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE concept_group_historique (
    idgroup text NOT NULL,
    id_ark text NOT NULL,
    idthesaurus text NOT NULL,
    idtypecode text NOT NULL,
    idparentgroup text,
    notation text,
    idconcept text,
    id integer DEFAULT nextval('concept_group_historique__id_seq'::regclass) NOT NULL,
    modified timestamp(6) with time zone DEFAULT now() NOT NULL,
    id_user integer NOT NULL
);


--
-- TOC entry 187 (class 1259 OID 160557)
-- Name: concept_group_label_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE concept_group_label_id_seq
    START WITH 60
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 188 (class 1259 OID 160559)
-- Name: concept_group_label; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE concept_group_label (
    id integer DEFAULT nextval('concept_group_label_id_seq'::regclass) NOT NULL,
    lexicalvalue text NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    lang character varying(5) NOT NULL,
    idthesaurus text NOT NULL,
    idgroup text NOT NULL
);


--
-- TOC entry 189 (class 1259 OID 160568)
-- Name: concept_group_label_historique__id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE concept_group_label_historique__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 190 (class 1259 OID 160570)
-- Name: concept_group_label_historique; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE concept_group_label_historique (
    id integer DEFAULT nextval('concept_group_label_historique__id_seq'::regclass) NOT NULL,
    lexicalvalue text NOT NULL,
    modified timestamp(6) without time zone DEFAULT now() NOT NULL,
    lang character varying(5) NOT NULL,
    idthesaurus text NOT NULL,
    idgroup text NOT NULL,
    id_user integer NOT NULL
);


--
-- TOC entry 191 (class 1259 OID 160578)
-- Name: concept_group_type; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE concept_group_type (
    code text NOT NULL,
    label text NOT NULL,
    skoslabel text
);


--
-- TOC entry 192 (class 1259 OID 160584)
-- Name: concept_historique__id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE concept_historique__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 193 (class 1259 OID 160586)
-- Name: concept_historique; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE concept_historique (
    id_concept character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    id_ark character varying,
    modified timestamp(6) with time zone DEFAULT now() NOT NULL,
    status character varying,
    notation character varying DEFAULT ''::character varying,
    top_concept boolean,
    id_group character varying NOT NULL,
    id integer DEFAULT nextval('concept_historique__id_seq'::regclass),
    id_user integer NOT NULL
);


--
-- TOC entry 194 (class 1259 OID 160595)
-- Name: concept_orphan; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE concept_orphan (
    id_concept character varying NOT NULL,
    id_thesaurus character varying NOT NULL
);


--
-- TOC entry 195 (class 1259 OID 160601)
-- Name: concept_term_candidat; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE concept_term_candidat (
    id_concept character varying NOT NULL,
    id_term character varying NOT NULL,
    id_thesaurus character varying NOT NULL
);


--
-- TOC entry 196 (class 1259 OID 160607)
-- Name: custom_concept_attribute; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE custom_concept_attribute (
    "idConcept" character varying NOT NULL,
    "lexicalValue" character varying,
    "customAttributeType" character varying,
    lang character varying
);


--
-- TOC entry 197 (class 1259 OID 160613)
-- Name: custom_term_attribute; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE custom_term_attribute (
    identifier character varying NOT NULL,
    "lexicalValue" character varying,
    "customAttributeType" character varying,
    lang character varying
);


--
-- TOC entry 198 (class 1259 OID 160619)
-- Name: facet_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE facet_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 199 (class 1259 OID 160621)
-- Name: gps; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE gps (
    id_concept character varying NOT NULL,
    id_theso character varying NOT NULL,
    latitude double precision,
    longitude double precision
);


--
-- TOC entry 239 (class 1259 OID 170625)
-- Name: gps_preferences_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE gps_preferences_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 241 (class 1259 OID 170651)
-- Name: gps_preferences; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE gps_preferences (
    id integer DEFAULT nextval('gps_preferences_id_seq'::regclass) NOT NULL,
    id_thesaurus character varying NOT NULL,
    id_user integer NOT NULL,
    gps_integrertraduction boolean DEFAULT true,
    gps_reemplacertraduction boolean DEFAULT true,
    gps_alignementautomatique boolean DEFAULT true,
    id_alignement_source integer NOT NULL
);


--
-- TOC entry 200 (class 1259 OID 160627)
-- Name: hierarchical_relationship; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hierarchical_relationship (
    id_concept1 character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    role character varying NOT NULL,
    id_concept2 character varying NOT NULL
);


--
-- TOC entry 201 (class 1259 OID 160633)
-- Name: hierarchical_relationship_historique; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hierarchical_relationship_historique (
    id_concept1 character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    role character varying NOT NULL,
    id_concept2 character varying NOT NULL,
    modified timestamp(6) with time zone DEFAULT now() NOT NULL,
    id_user integer NOT NULL,
    action character varying NOT NULL
);


--
-- TOC entry 202 (class 1259 OID 160640)
-- Name: images; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE images (
    id_concept character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    image_name character varying NOT NULL,
    image_copyright character varying NOT NULL,
    id_user integer
);


--
-- TOC entry 203 (class 1259 OID 160646)
-- Name: info; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE info (
    version_opentheso character varying NOT NULL,
    version_bdd character varying NOT NULL
);


--
-- TOC entry 204 (class 1259 OID 160652)
-- Name: languages_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE languages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 247 (class 1259 OID 225630)
-- Name: languages_iso639; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE languages_iso639 (
    iso639_1 character(3),
    iso639_2 character varying,
    english_name character varying,
    french_name character varying,
    id integer DEFAULT nextval('languages_id_seq'::regclass) NOT NULL
);


--
-- TOC entry 205 (class 1259 OID 160661)
-- Name: node_label; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE node_label (
    facet_id integer NOT NULL,
    id_thesaurus character varying NOT NULL,
    lexical_value character varying,
    created timestamp with time zone DEFAULT now() NOT NULL,
    modified timestamp with time zone DEFAULT now() NOT NULL,
    lang character varying NOT NULL
);


--
-- TOC entry 206 (class 1259 OID 160669)
-- Name: non_preferred_term; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE non_preferred_term (
    id_term character varying NOT NULL,
    lexical_value character varying NOT NULL,
    lang character varying NOT NULL,
    id_thesaurus text NOT NULL,
    created timestamp with time zone DEFAULT now() NOT NULL,
    modified timestamp with time zone DEFAULT now() NOT NULL,
    source character varying,
    status character varying,
    hiden boolean DEFAULT false NOT NULL
);


--
-- TOC entry 207 (class 1259 OID 160678)
-- Name: non_preferred_term_historique; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE non_preferred_term_historique (
    id_term character varying NOT NULL,
    lexical_value character varying NOT NULL,
    lang character varying NOT NULL,
    id_thesaurus text NOT NULL,
    modified timestamp(6) with time zone DEFAULT now() NOT NULL,
    source character varying,
    status character varying,
    hiden boolean DEFAULT false NOT NULL,
    id_user integer NOT NULL,
    action character varying NOT NULL
);


--
-- TOC entry 208 (class 1259 OID 160686)
-- Name: note__id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE note__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 209 (class 1259 OID 160688)
-- Name: note; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE note (
    id integer DEFAULT nextval('note__id_seq'::regclass) NOT NULL,
    notetypecode text NOT NULL,
    id_thesaurus character varying NOT NULL,
    id_term character varying,
    id_concept character varying,
    lang character varying NOT NULL,
    lexicalvalue character varying NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 210 (class 1259 OID 160697)
-- Name: note_historique__id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE note_historique__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 211 (class 1259 OID 160699)
-- Name: note_historique; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE note_historique (
    id integer DEFAULT nextval('note_historique__id_seq'::regclass) NOT NULL,
    notetypecode text NOT NULL,
    id_thesaurus character varying NOT NULL,
    id_term character varying,
    id_concept character varying,
    lang character varying NOT NULL,
    lexicalvalue character varying NOT NULL,
    modified timestamp(6) without time zone DEFAULT now() NOT NULL,
    id_user integer NOT NULL
);


--
-- TOC entry 212 (class 1259 OID 160707)
-- Name: note_type; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE note_type (
    code text NOT NULL,
    isterm boolean NOT NULL,
    isconcept boolean NOT NULL,
    CONSTRAINT chk_not_false_values CHECK ((NOT ((isterm = false) AND (isconcept = false))))
);


--
-- TOC entry 213 (class 1259 OID 160714)
-- Name: permuted; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE permuted (
    ord integer NOT NULL,
    id_concept character varying NOT NULL,
    id_group character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    id_lang character varying NOT NULL,
    lexical_value character varying NOT NULL,
    ispreferredterm boolean NOT NULL,
    original_value character varying
);


--
-- TOC entry 214 (class 1259 OID 160720)
-- Name: pref__id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE pref__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 246 (class 1259 OID 225374)
-- Name: preferences; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE preferences (
    id_pref integer DEFAULT nextval('pref__id_seq'::regclass) NOT NULL,
    id_thesaurus character varying NOT NULL,
    source_lang character varying(2) DEFAULT 'fr'::character varying,
    nb_alert_cdt integer DEFAULT 10,
    alert_cdt boolean DEFAULT false,
    identifier_type integer DEFAULT 2,
    use_ark boolean DEFAULT false,
    server_ark character varying DEFAULT 'http://ark.mondomaine.fr/ark:/'::character varying,
    path_image character varying DEFAULT '/var/www/images/'::character varying,
    dossier_resize character varying DEFAULT 'resize'::character varying,
    bdd_active boolean DEFAULT false,
    bdd_use_id boolean DEFAULT false,
    url_bdd character varying DEFAULT 'http://www.mondomaine.fr/fr/recherche?search=terme'::character varying,
    z3950actif boolean DEFAULT false,
    collection_adresse character varying DEFAULT 'KOHA/biblios'::character varying,
    notice_url character varying DEFAULT 'http://catalogue.mondomaine.fr/cgi-bin/koha/opac-search.pl?type=opac&op=do_search&q=an=terme'::character varying,
    url_encode character varying(10) DEFAULT 'UTF-8'::character varying,
    path_notice1 character varying DEFAULT '/var/www/notices/repositories.xml'::character varying,
    path_notice2 character varying DEFAULT '/var/www/notices/SchemaMappings.xml'::character varying,
    chemin_site character varying DEFAULT 'http://mondomaine.fr/'::character varying
);


--
-- TOC entry 215 (class 1259 OID 160729)
-- Name: preferred_term; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE preferred_term (
    id_concept character varying NOT NULL,
    id_term character varying NOT NULL,
    id_thesaurus character varying NOT NULL
);


--
-- TOC entry 216 (class 1259 OID 160735)
-- Name: proposition; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE proposition (
    id_concept character varying NOT NULL,
    id_user integer NOT NULL,
    id_thesaurus character varying NOT NULL,
    note text,
    created timestamp with time zone DEFAULT now() NOT NULL,
    modified timestamp with time zone DEFAULT now() NOT NULL,
    concept_parent character varying,
    id_group character varying
);


--
-- TOC entry 245 (class 1259 OID 217492)
-- Name: relation_group; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE relation_group (
    id_group1 character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    relation character varying NOT NULL,
    id_group2 character varying NOT NULL
);


--
-- TOC entry 217 (class 1259 OID 160743)
-- Name: roles; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE roles (
    id integer NOT NULL,
    name character varying,
    description character varying
);


--
-- TOC entry 218 (class 1259 OID 160749)
-- Name: role_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2888 (class 0 OID 0)
-- Dependencies: 218
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE role_id_seq OWNED BY roles.id;


--
-- TOC entry 219 (class 1259 OID 160751)
-- Name: split_non_preferred_term; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE split_non_preferred_term (
);


--
-- TOC entry 220 (class 1259 OID 160754)
-- Name: term__id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE term__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 221 (class 1259 OID 160756)
-- Name: term; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE term (
    id_term character varying NOT NULL,
    lexical_value character varying NOT NULL,
    lang character varying NOT NULL,
    id_thesaurus text NOT NULL,
    created timestamp with time zone DEFAULT now() NOT NULL,
    modified timestamp with time zone DEFAULT now() NOT NULL,
    source character varying,
    status character varying DEFAULT 'D'::character varying,
    id integer DEFAULT nextval('term__id_seq'::regclass) NOT NULL,
    contributor integer,
    creator integer
);


--
-- TOC entry 222 (class 1259 OID 160766)
-- Name: term_candidat__id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE term_candidat__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 223 (class 1259 OID 160768)
-- Name: term_candidat; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE term_candidat (
    id_term character varying NOT NULL,
    lexical_value character varying NOT NULL,
    lang character varying NOT NULL,
    id_thesaurus text NOT NULL,
    created timestamp with time zone DEFAULT now() NOT NULL,
    modified timestamp with time zone DEFAULT now() NOT NULL,
    contributor integer NOT NULL,
    id integer DEFAULT nextval('term_candidat__id_seq'::regclass) NOT NULL
);


--
-- TOC entry 224 (class 1259 OID 160777)
-- Name: term_historique__id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE term_historique__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 225 (class 1259 OID 160779)
-- Name: term_historique; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE term_historique (
    id_term character varying NOT NULL,
    lexical_value character varying NOT NULL,
    lang character varying NOT NULL,
    id_thesaurus text NOT NULL,
    modified timestamp(6) with time zone DEFAULT now() NOT NULL,
    source character varying,
    status character varying DEFAULT 'D'::character varying,
    id integer DEFAULT nextval('term_historique__id_seq'::regclass) NOT NULL,
    id_user integer NOT NULL
);


--
-- TOC entry 226 (class 1259 OID 160788)
-- Name: thesaurus_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE thesaurus_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 227 (class 1259 OID 160790)
-- Name: thesaurus; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE thesaurus (
    id_thesaurus character varying NOT NULL,
    id_ark character varying NOT NULL,
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    id integer DEFAULT nextval('thesaurus_id_seq'::regclass) NOT NULL
);


--
-- TOC entry 228 (class 1259 OID 160799)
-- Name: thesaurus_alignement_source; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE thesaurus_alignement_source (
    id_thesaurus character varying NOT NULL,
    id_alignement_source integer NOT NULL
);


--
-- TOC entry 229 (class 1259 OID 160805)
-- Name: thesaurus_array; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE thesaurus_array (
    facet_id integer DEFAULT nextval('facet_id_seq'::regclass) NOT NULL,
    id_thesaurus character varying NOT NULL,
    id_concept_parent character varying NOT NULL,
    ordered boolean DEFAULT false NOT NULL,
    notation character varying
);


--
-- TOC entry 230 (class 1259 OID 160813)
-- Name: thesaurus_array_concept; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE thesaurus_array_concept (
    thesaurusarrayid integer NOT NULL,
    id_concept character varying NOT NULL,
    id_thesaurus character varying NOT NULL,
    arrayorder integer DEFAULT 0 NOT NULL
);


--
-- TOC entry 231 (class 1259 OID 160820)
-- Name: thesaurus_label; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE thesaurus_label (
    id_thesaurus character varying NOT NULL,
    contributor character varying,
    coverage character varying,
    creator character varying,
    created timestamp without time zone DEFAULT now() NOT NULL,
    modified timestamp without time zone DEFAULT now() NOT NULL,
    description character varying,
    format character varying,
    lang character varying NOT NULL,
    publisher character varying,
    relation character varying,
    rights character varying,
    source character varying,
    subject character varying,
    title character varying NOT NULL,
    type character varying
);


--
-- TOC entry 242 (class 1259 OID 189199)
-- Name: theso_preferences_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE theso_preferences_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 243 (class 1259 OID 189201)
-- Name: theso_preferences; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE theso_preferences (
    id integer DEFAULT nextval('theso_preferences_id_seq'::regclass) NOT NULL,
    id_thesaurus character varying NOT NULL,
    work_language character varying,
    identifier_type integer,
    path_image character varying,
    folder_resize character varying,
    server_adress character varying,
    connecting_bdd boolean,
    url_bdd character varying,
    z3950 boolean,
    z3950_collection_adresse character varying,
    z3950_url_notice character varying,
    z3950_url_encode character varying,
    z3950_path_repositories character varying,
    z3950_path_schemamappings character varying,
    email boolean,
    email_protocolmail character varying,
    email_hostmail character varying,
    email_portmail integer,
    email_authmail boolean,
    email_user character varying,
    email_password character varying,
    email_mailfrom character varying,
    email_transportmail character varying
);


--
-- TOC entry 232 (class 1259 OID 160828)
-- Name: user__id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE user__id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 233 (class 1259 OID 160830)
-- Name: user_role; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE user_role (
    id_user integer NOT NULL,
    id_role integer NOT NULL,
    id_thesaurus character varying NOT NULL,
    id_group character varying
);


--
-- TOC entry 234 (class 1259 OID 160836)
-- Name: users; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE users (
    id_user integer DEFAULT nextval('user__id_seq'::regclass) NOT NULL,
    username character varying NOT NULL,
    password character varying NOT NULL,
    active boolean NOT NULL,
    mail character varying,
    passtomodify boolean DEFAULT false
);


--
-- TOC entry 235 (class 1259 OID 160844)
-- Name: users2; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE users2 (
    id_user integer DEFAULT nextval('user__id_seq'::regclass) NOT NULL,
    login character varying NOT NULL,
    fullname character varying,
    password character varying,
    active boolean DEFAULT true NOT NULL,
    mail character varying,
    authentication auth_method DEFAULT 'DB'::auth_method
);


--
-- TOC entry 236 (class 1259 OID 160853)
-- Name: users_historique; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE users_historique (
    id_user integer NOT NULL,
    username character varying,
    created timestamp(6) with time zone DEFAULT now() NOT NULL,
    modified timestamp(6) with time zone DEFAULT now() NOT NULL,
    delete timestamp(6) with time zone
);


--
-- TOC entry 237 (class 1259 OID 160861)
-- Name: version_history; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE version_history (
    "idVersionhistory" character varying NOT NULL,
    "idThesaurus" character varying NOT NULL,
    date date,
    "versionNote" character varying,
    "currentVersion" boolean,
    "thisVersion" boolean NOT NULL
);


--
-- TOC entry 2498 (class 2604 OID 225721)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY roles ALTER COLUMN id SET DEFAULT nextval('role_id_seq'::regclass);


--
-- TOC entry 2806 (class 0 OID 160470)
-- Dependencies: 173
-- Data for Name: alignement; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2889 (class 0 OID 0)
-- Dependencies: 172
-- Name: alignement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('alignement_id_seq', 1, false);


--
-- TOC entry 2873 (class 0 OID 170642)
-- Dependencies: 240
-- Data for Name: alignement_preferences; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO alignement_preferences (id, id_thesaurus, id_user, id_concept_depart, id_concept_tratees, id_alignement_source) VALUES (1, '1', 1, '2', '2#', 1);
INSERT INTO alignement_preferences (id, id_thesaurus, id_user, id_concept_depart, id_concept_tratees, id_alignement_source) VALUES (2, '1', 1, '4', '4#13#4#4#', 1);
INSERT INTO alignement_preferences (id, id_thesaurus, id_user, id_concept_depart, id_concept_tratees, id_alignement_source) VALUES (3, '1', 1, 'C_26975', 'C_26975#C_26976#C_26977#C_26978#C_26979#C_26980#C_26981#C_26982#C_26983#C_26984#C_26985#C_26986#C_26987#', 1);
INSERT INTO alignement_preferences (id, id_thesaurus, id_user, id_concept_depart, id_concept_tratees, id_alignement_source) VALUES (4, '1', 1, 'C_26981', 'C_26981#', 1);


--
-- TOC entry 2890 (class 0 OID 0)
-- Dependencies: 238
-- Name: alignement_preferences_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('alignement_preferences_id_seq', 4, true);


--
-- TOC entry 2808 (class 0 OID 160481)
-- Dependencies: 175
-- Data for Name: alignement_source; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO alignement_source (source, requete, type_rqt, alignement_format, id, id_user, description, gps) VALUES ('wikipedia', 'https://##lang##.wikipedia.org/w/api.php?action=query&list=search&srwhat=text&format=xml&srsearch=##value##&srnamespace=0"', 'REST', 'xml', 1, NULL, NULL, false);
INSERT INTO alignement_source (source, requete, type_rqt, alignement_format, id, id_user, description, gps) VALUES ('Geoname', 'http://api.geonames.org/search?q=##value##&maxRows=10&style=FULL&lang=##lang##&username=opentheso', 'REST', 'xml', 4, NULL, 'test de geonames', true);
INSERT INTO alignement_source (source, requete, type_rqt, alignement_format, id, id_user, description, gps) VALUES ('mondomaine.fr', 'null', 'REST', 'xml', 6, NULL, NULL, false);
INSERT INTO alignement_source (source, requete, type_rqt, alignement_format, id, id_user, description, gps) VALUES ('Pactols - copy', 'http://pactols.frantiq.fr/opentheso/webresources/rest/skos/concept/value=##value##&lang=##lang##&th=TH_1', 'REST', 'skos', 7, 1, 'null', false);
INSERT INTO alignement_source (source, requete, type_rqt, alignement_format, id, id_user, description, gps) VALUES ('Pactols', 'http://pactols.frantiq.fr/opentheso/webresources/rest/skos/concept/value=##value##&lang=##lang##&th=TH_1', 'REST', 'skos', 2, NULL, 'null', false);
INSERT INTO alignement_source (source, requete, type_rqt, alignement_format, id, id_user, description, gps) VALUES ('bnf', 'PREFIX skos: <http://www.w3.org/2004/02/skos/core#>
PREFIX xml: <http://www.w3.org/XML/1998/namespace>
SELECT ?instrument ?prop ?value where {
  <http://data.bnf.fr/ark:/12148/cb119367821> skos:narrower+ ?instrument.
  ?instrument ?prop ?value.
  FILTER( (regex(?prop,skos:prefLabel) || regex(?prop,skos:altLabel))  && regex(?value, $##value##,"i") ) 
    filter(lang(?value) =##lang##)
} LIMIT 20', 'SPARQL', 'skos', 3, NULL, '', false);


--
-- TOC entry 2891 (class 0 OID 0)
-- Dependencies: 174
-- Name: alignement_source__id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('alignement_source__id_seq', 9, false);


--
-- TOC entry 2809 (class 0 OID 160488)
-- Dependencies: 176
-- Data for Name: alignement_type; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (1, 'Equivalence exacte', '=EQ', 'exactMatch');
INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (2, 'Equivalence inexacte', '~EQ', 'closeMatch');
INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (3, 'Equivalence générique', 'EQB', 'broadMatch');
INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (4, 'Equivalence associative', 'EQR', 'relatedMatch');
INSERT INTO alignement_type (id, label, isocode, label_skos) VALUES (5, 'Equivalence spécifique', 'EQS', 'narrowMatch');


--
-- TOC entry 2810 (class 0 OID 160494)
-- Dependencies: 177
-- Data for Name: compound_equivalence; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2877 (class 0 OID 217479)
-- Dependencies: 244
-- Data for Name: concept; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2892 (class 0 OID 0)
-- Dependencies: 178
-- Name: concept__id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('concept__id_seq', 1, false);


--
-- TOC entry 2813 (class 0 OID 160515)
-- Dependencies: 180
-- Data for Name: concept_candidat; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2893 (class 0 OID 0)
-- Dependencies: 179
-- Name: concept_candidat__id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('concept_candidat__id_seq', 1, false);


--
-- TOC entry 2814 (class 0 OID 160525)
-- Dependencies: 181
-- Data for Name: concept_fusion; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2816 (class 0 OID 160534)
-- Dependencies: 183
-- Data for Name: concept_group; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2894 (class 0 OID 0)
-- Dependencies: 182
-- Name: concept_group__id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('concept_group__id_seq', 1, false);


--
-- TOC entry 2817 (class 0 OID 160541)
-- Dependencies: 184
-- Data for Name: concept_group_concept; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2819 (class 0 OID 160549)
-- Dependencies: 186
-- Data for Name: concept_group_historique; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2895 (class 0 OID 0)
-- Dependencies: 185
-- Name: concept_group_historique__id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('concept_group_historique__id_seq', 1, false);


--
-- TOC entry 2821 (class 0 OID 160559)
-- Dependencies: 188
-- Data for Name: concept_group_label; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2823 (class 0 OID 160570)
-- Dependencies: 190
-- Data for Name: concept_group_label_historique; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2896 (class 0 OID 0)
-- Dependencies: 189
-- Name: concept_group_label_historique__id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('concept_group_label_historique__id_seq', 1, false);


--
-- TOC entry 2897 (class 0 OID 0)
-- Dependencies: 187
-- Name: concept_group_label_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('concept_group_label_id_seq', 1, false);


--
-- TOC entry 2824 (class 0 OID 160578)
-- Dependencies: 191
-- Data for Name: concept_group_type; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO concept_group_type (code, label, skoslabel) VALUES ('MT', 'Microthesaurus', 'MicroThesaurus');
INSERT INTO concept_group_type (code, label, skoslabel) VALUES ('G', 'Group', 'ConceptGroup');
INSERT INTO concept_group_type (code, label, skoslabel) VALUES ('C', 'Collection', 'Collection');
INSERT INTO concept_group_type (code, label, skoslabel) VALUES ('T', 'Theme', 'Theme');


--
-- TOC entry 2826 (class 0 OID 160586)
-- Dependencies: 193
-- Data for Name: concept_historique; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2898 (class 0 OID 0)
-- Dependencies: 192
-- Name: concept_historique__id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('concept_historique__id_seq', 1, false);


--
-- TOC entry 2827 (class 0 OID 160595)
-- Dependencies: 194
-- Data for Name: concept_orphan; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2828 (class 0 OID 160601)
-- Dependencies: 195
-- Data for Name: concept_term_candidat; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2829 (class 0 OID 160607)
-- Dependencies: 196
-- Data for Name: custom_concept_attribute; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2830 (class 0 OID 160613)
-- Dependencies: 197
-- Data for Name: custom_term_attribute; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2899 (class 0 OID 0)
-- Dependencies: 198
-- Name: facet_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('facet_id_seq', 1, false);


--
-- TOC entry 2832 (class 0 OID 160621)
-- Dependencies: 199
-- Data for Name: gps; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2874 (class 0 OID 170651)
-- Dependencies: 241
-- Data for Name: gps_preferences; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO gps_preferences (id, id_thesaurus, id_user, gps_integrertraduction, gps_reemplacertraduction, gps_alignementautomatique, id_alignement_source) VALUES (1, '1', 1, true, true, true, 4);
INSERT INTO gps_preferences (id, id_thesaurus, id_user, gps_integrertraduction, gps_reemplacertraduction, gps_alignementautomatique, id_alignement_source) VALUES (2, '20', 1, true, false, true, 4);


--
-- TOC entry 2900 (class 0 OID 0)
-- Dependencies: 239
-- Name: gps_preferences_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('gps_preferences_id_seq', 2, true);


--
-- TOC entry 2833 (class 0 OID 160627)
-- Dependencies: 200
-- Data for Name: hierarchical_relationship; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2834 (class 0 OID 160633)
-- Dependencies: 201
-- Data for Name: hierarchical_relationship_historique; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2835 (class 0 OID 160640)
-- Dependencies: 202
-- Data for Name: images; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2836 (class 0 OID 160646)
-- Dependencies: 203
-- Data for Name: info; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO info (version_opentheso, version_bdd) VALUES ('4.3.0', '4.2.4');


--
-- TOC entry 2901 (class 0 OID 0)
-- Dependencies: 204
-- Name: languages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('languages_id_seq', 186, false);


--
-- TOC entry 2880 (class 0 OID 225630)
-- Dependencies: 247
-- Data for Name: languages_iso639; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('aa ', 'aar', 'Afar', 'afar', 2);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ab ', 'abk', 'Abkhazian', 'abkhaze', 3);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('af ', 'afr', 'Afrikaans', 'afrikaans', 4);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ak ', 'aka', 'Akan', 'akan', 5);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sq ', 'alb (B)
sqi (T)', 'Albanian', 'albanais', 6);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('am ', 'amh', 'Amharic', 'amharique', 7);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ar ', 'ara', 'Arabic', 'arabe', 8);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('an ', 'arg', 'Aragonese', 'aragonais', 9);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('as ', 'asm', 'Assamese', 'assamais', 10);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('av ', 'ava', 'Avaric', 'avar', 11);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ae ', 'ave', 'Avestan', 'avestique', 12);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ay ', 'aym', 'Aymara', 'aymara', 13);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('az ', 'aze', 'Azerbaijani', 'azéri', 14);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ba ', 'bak', 'Bashkir', 'bachkir', 15);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('bm ', 'bam', 'Bambara', 'bambara', 16);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('eu ', 'baq (B)
eus (T)', 'Basque', 'basque', 17);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('be ', 'bel', 'Belarusian', 'biélorusse', 18);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('bn ', 'ben', 'Bengali', 'bengali', 19);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('bh ', 'bih', 'Bihari languages', 'langues biharis', 20);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('bi ', 'bis', 'Bislama', 'bichlamar', 21);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('bs ', 'bos', 'Bosnian', 'bosniaque', 22);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('br ', 'bre', 'Breton', 'breton', 23);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('bg ', 'bul', 'Bulgarian', 'bulgare', 24);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ca ', 'cat', 'Catalan; Valencian', 'catalan; valencien', 25);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ch ', 'cha', 'Chamorro', 'chamorro', 26);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ce ', 'che', 'Chechen', 'tchétchène', 27);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('zh ', 'chi (B)
zho (T)', 'Chinese', 'chinois', 28);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('cv ', 'chv', 'Chuvash', 'tchouvache', 29);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('kw ', 'cor', 'Cornish', 'cornique', 30);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('co ', 'cos', 'Corsican', 'corse', 31);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('cr ', 'cre', 'Cree', 'cree', 32);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('cy ', 'wel (B)
cym (T)', 'Welsh', 'gallois', 33);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('cs ', 'cze (B)
ces (T)', 'Czech', 'tchèque', 34);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('da ', 'dan', 'Danish', 'danois', 35);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('de ', 'ger (B)
deu (T)', 'German', 'allemand', 36);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('dv ', 'div', 'Divehi; Dhivehi; Maldivian', 'maldivien', 37);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('dz ', 'dzo', 'Dzongkha', 'dzongkha', 38);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('el ', 'gre (B)
ell (T)', 'Greek, Modern (1453-)', 'grec moderne (après 1453)', 39);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('en ', 'eng', 'English', 'anglais', 40);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('eo ', 'epo', 'Esperanto', 'espéranto', 41);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('et ', 'est', 'Estonian', 'estonien', 42);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ee ', 'ewe', 'Ewe', 'éwé', 43);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('fo ', 'fao', 'Faroese', 'féroïen', 44);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('fj ', 'fij', 'Fijian', 'fidjien', 45);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('fi ', 'fin', 'Finnish', 'finnois', 46);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('fr ', 'fre (B)
fra (T)', 'French', 'français', 47);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('fy ', 'fry', 'Western Frisian', 'frison occidental', 48);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ff ', 'ful', 'Fulah', 'peul', 49);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ka ', 'geo (B)
kat (T)', 'Georgian', 'géorgien', 50);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('gd ', 'gla', 'Gaelic; Scottish Gaelic', 'gaélique; gaélique écossais', 51);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ga ', 'gle', 'Irish', 'irlandais', 52);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('gl ', 'glg', 'Galician', 'galicien', 53);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('gv ', 'glv', 'Manx', 'manx; mannois', 54);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('gn ', 'grn', 'Guarani', 'guarani', 55);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('gu ', 'guj', 'Gujarati', 'goudjrati', 56);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ht ', 'hat', 'Haitian; Haitian Creole', 'haïtien; créole haïtien', 57);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ha ', 'hau', 'Hausa', 'haoussa', 58);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('he ', 'heb', 'Hebrew', 'hébreu', 59);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('hz ', 'her', 'Herero', 'herero', 60);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('hi ', 'hin', 'Hindi', 'hindi', 61);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ho ', 'hmo', 'Hiri Motu', 'hiri motu', 62);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('hr ', 'hrv', 'Croatian', 'croate', 63);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('hu ', 'hun', 'Hungarian', 'hongrois', 64);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('hy ', 'arm (B)
hye (T)', 'Armenian', 'arménien', 65);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ig ', 'ibo', 'Igbo', 'igbo', 66);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('is ', 'ice (B)
isl (T)', 'Icelandic', 'islandais', 67);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('io ', 'ido', 'Ido', 'ido', 68);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ii ', 'iii', 'Sichuan Yi; Nuosu', 'yi de Sichuan', 69);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('iu ', 'iku', 'Inuktitut', 'inuktitut', 70);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ie ', 'ile', 'Interlingue; Occidental', 'interlingue', 71);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('id ', 'ind', 'Indonesian', 'indonésien', 72);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ik ', 'ipk', 'Inupiaq', 'inupiaq', 73);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('it ', 'ita', 'Italian', 'italien', 74);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('jv ', 'jav', 'Javanese', 'javanais', 75);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ja ', 'jpn', 'Japanese', 'japonais', 76);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('kl ', 'kal', 'Kalaallisut; Greenlandic', 'groenlandais', 77);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('kn ', 'kan', 'Kannada', 'kannada', 78);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ks ', 'kas', 'Kashmiri', 'kashmiri', 79);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('kr ', 'kau', 'Kanuri', 'kanouri', 80);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('kk ', 'kaz', 'Kazakh', 'kazakh', 81);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('km ', 'khm', 'Central Khmer', 'khmer central', 82);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ki ', 'kik', 'Kikuyu; Gikuyu', 'kikuyu', 83);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('rw ', 'kin', 'Kinyarwanda', 'rwanda', 84);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ky ', 'kir', 'Kirghiz; Kyrgyz', 'kirghiz', 85);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('kv ', 'kom', 'Komi', 'kom', 86);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('kg ', 'kon', 'Kongo', 'kongo', 87);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ko ', 'kor', 'Korean', 'coréen', 88);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('kj ', 'kua', 'Kuanyama; Kwanyama', 'kuanyama; kwanyama', 89);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ku ', 'kur', 'Kurdish', 'kurde', 90);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('lo ', 'lao', 'Lao', 'lao', 91);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('la ', 'lat', 'Latin', 'latin', 92);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('lv ', 'lav', 'Latvian', 'letton', 93);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('li ', 'lim', 'Limburgan; Limburger; Limburgish', 'limbourgeois', 94);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ln ', 'lin', 'Lingala', 'lingala', 95);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('lt ', 'lit', 'Lithuanian', 'lituanien', 96);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('lb ', 'ltz', 'Luxembourgish; Letzeburgesch', 'luxembourgeois', 97);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('lu ', 'lub', 'Luba-Katanga', 'luba-katanga', 98);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('lg ', 'lug', 'Ganda', 'ganda', 99);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('mk ', 'mac (B)
mkd (T)', 'Macedonian', 'macédonien', 100);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('mh ', 'mah', 'Marshallese', 'marshall', 101);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ml ', 'mal', 'Malayalam', 'malayalam', 102);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('mr ', 'mar', 'Marathi', 'marathe', 103);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ms ', 'may (B)
msa (T)', 'Malay', 'malais', 104);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('mg ', 'mlg', 'Malagasy', 'malgache', 105);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('mt ', 'mlt', 'Maltese', 'maltais', 106);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('mn ', 'mon', 'Mongolian', 'mongol', 107);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('mi ', 'mao (B)
mri (T)', 'Maori', 'maori', 108);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('my ', 'bur (B)
mya (T)', 'Burmese', 'birman', 109);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('na ', 'nau', 'Nauru', 'nauruan', 110);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('nv ', 'nav', 'Navajo; Navaho', 'navaho', 111);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('nr ', 'nbl', 'Ndebele, South; South Ndebele', 'ndébélé du Sud', 112);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('nd ', 'nde', 'Ndebele, North; North Ndebele', 'ndébélé du Nord', 113);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ng ', 'ndo', 'Ndonga', 'ndonga', 114);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ne ', 'nep', 'Nepali', 'népalais', 115);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('nl ', 'dut (B)
nld (T)', 'Dutch; Flemish', 'néerlandais; flamand', 116);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('nb ', 'nob', 'Bokmål, Norwegian; Norwegian Bokmål', 'norvégien bokmål', 117);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('no ', 'nor', 'Norwegian', 'norvégien', 118);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ny ', 'nya', 'Chichewa; Chewa; Nyanja', 'chichewa; chewa; nyanja', 119);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('oc ', 'oci', 'Occitan (post 1500)', 'occitan (après 1500)', 120);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('oj ', 'oji', 'Ojibwa', 'ojibwa', 121);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('or ', 'ori', 'Oriya', 'oriya', 122);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('om ', 'orm', 'Oromo', 'galla', 123);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('os ', 'oss', 'Ossetian; Ossetic', 'ossète', 124);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('pa ', 'pan', 'Panjabi; Punjabi', 'pendjabi', 125);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('fa ', 'per (B)
fas (T)', 'Persian', 'persan', 126);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('pi ', 'pli', 'Pali', 'pali', 127);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('pl ', 'pol', 'Polish', 'polonais', 128);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('pt ', 'por', 'Portuguese', 'portugais', 129);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ps ', 'pus', 'Pushto; Pashto', 'pachto', 130);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('qu ', 'que', 'Quechua', 'quechua', 131);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('rm ', 'roh', 'Romansh', 'romanche', 132);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ro ', 'rum (B)
ron (T)', 'Romanian; Moldavian; Moldovan', 'roumain; moldave', 133);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('rn ', 'run', 'Rundi', 'rundi', 134);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ru ', 'rus', 'Russian', 'russe', 135);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sg ', 'sag', 'Sango', 'sango', 136);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sa ', 'san', 'Sanskrit', 'sanskrit', 137);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('si ', 'sin', 'Sinhala; Sinhalese', 'singhalais', 138);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sk ', 'slo (B)
slk (T)', 'Slovak', 'slovaque', 139);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sl ', 'slv', 'Slovenian', 'slovène', 140);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('se ', 'sme', 'Northern Sami', 'sami du Nord', 141);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sm ', 'smo', 'Samoan', 'samoan', 142);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sn ', 'sna', 'Shona', 'shona', 143);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sd ', 'snd', 'Sindhi', 'sindhi', 144);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('so ', 'som', 'Somali', 'somali', 145);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('st ', 'sot', 'Sotho, Southern', 'sotho du Sud', 146);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('es ', 'spa', 'Spanish; Castilian', 'espagnol; castillan', 147);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sc ', 'srd', 'Sardinian', 'sarde', 148);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sr ', 'srp', 'Serbian', 'serbe', 149);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ss ', 'ssw', 'Swati', 'swati', 150);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('su ', 'sun', 'Sundanese', 'soundanais', 151);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sw ', 'swa', 'Swahili', 'swahili', 152);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('sv ', 'swe', 'Swedish', 'suédois', 153);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ty ', 'tah', 'Tahitian', 'tahitien', 154);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ta ', 'tam', 'Tamil', 'tamoul', 155);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('tt ', 'tat', 'Tatar', 'tatar', 156);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('te ', 'tel', 'Telugu', 'télougou', 157);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('tg ', 'tgk', 'Tajik', 'tadjik', 158);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('tl ', 'tgl', 'Tagalog', 'tagalog', 159);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('th ', 'tha', 'Thai', 'thaï', 160);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('bo ', 'tib (B)
bod (T)', 'Tibetan', 'tibétain', 161);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ti ', 'tir', 'Tigrinya', 'tigrigna', 162);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('to ', 'ton', 'Tonga (Tonga Islands)', 'tongan (Îles Tonga)', 163);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('tn ', 'tsn', 'Tswana', 'tswana', 164);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ts ', 'tso', 'Tsonga', 'tsonga', 165);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('tk ', 'tuk', 'Turkmen', 'turkmène', 166);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('tr ', 'tur', 'Turkish', 'turc', 167);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('tw ', 'twi', 'Twi', 'twi', 168);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ug ', 'uig', 'Uighur; Uyghur', 'ouïgour', 169);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('uk ', 'ukr', 'Ukrainian', 'ukrainien', 170);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ur ', 'urd', 'Urdu', 'ourdou', 171);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('uz ', 'uzb', 'Uzbek', 'ouszbek', 172);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ve ', 'ven', 'Venda', 'venda', 173);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('vi ', 'vie', 'Vietnamese', 'vietnamien', 174);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('vo ', 'vol', 'Volapük', 'volapük', 175);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('wa ', 'wln', 'Walloon', 'wallon', 176);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('wo ', 'wol', 'Wolof', 'wolof', 177);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('xh ', 'xho', 'Xhosa', 'xhosa', 178);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('yi ', 'yid', 'Yiddish', 'yiddish', 179);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('yo ', 'yor', 'Yoruba', 'yoruba', 180);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('za ', 'zha', 'Zhuang; Chuang', 'zhuang; chuang', 181);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('zu ', 'zul', 'Zulu', 'zoulou', 182);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('cu ', 'chu', 'Church Slavic; Old Slavonic; Church Slavonic; Old Bulgarian; Old Church Slavonic', 'vieux slave; vieux bulgare', 183);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('ia ', 'ina', 'Interlingua (International Auxiliary Language Association)', 'interlingua', 184);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('nn ', 'nno', 'Norwegian Nynorsk; Nynorsk, Norwegian', 'norvégien nynorsk', 185);
INSERT INTO languages_iso639 (iso639_1, iso639_2, english_name, french_name, id) VALUES ('gr ', 'grc', 'Greek, Ancient (to 1453)', 'grec ancien (jusqu''à 1453)', 186);


--
-- TOC entry 2838 (class 0 OID 160661)
-- Dependencies: 205
-- Data for Name: node_label; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2839 (class 0 OID 160669)
-- Dependencies: 206
-- Data for Name: non_preferred_term; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2840 (class 0 OID 160678)
-- Dependencies: 207
-- Data for Name: non_preferred_term_historique; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2842 (class 0 OID 160688)
-- Dependencies: 209
-- Data for Name: note; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2902 (class 0 OID 0)
-- Dependencies: 208
-- Name: note__id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('note__id_seq', 1, false);


--
-- TOC entry 2844 (class 0 OID 160699)
-- Dependencies: 211
-- Data for Name: note_historique; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2903 (class 0 OID 0)
-- Dependencies: 210
-- Name: note_historique__id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('note_historique__id_seq', 1, false);


--
-- TOC entry 2845 (class 0 OID 160707)
-- Dependencies: 212
-- Data for Name: note_type; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO note_type (code, isterm, isconcept) VALUES ('customNote', false, true);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('definition', true, false);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('editorialNote', true, false);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('historyNote', true, true);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('scopeNote', false, true);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('note', false, true);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('example', true, false);
INSERT INTO note_type (code, isterm, isconcept) VALUES ('changeNote', true, false);


--
-- TOC entry 2846 (class 0 OID 160714)
-- Dependencies: 213
-- Data for Name: permuted; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2904 (class 0 OID 0)
-- Dependencies: 214
-- Name: pref__id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('pref__id_seq', 1, false);


--
-- TOC entry 2879 (class 0 OID 225374)
-- Dependencies: 246
-- Data for Name: preferences; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2848 (class 0 OID 160729)
-- Dependencies: 215
-- Data for Name: preferred_term; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2849 (class 0 OID 160735)
-- Dependencies: 216
-- Data for Name: proposition; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2878 (class 0 OID 217492)
-- Dependencies: 245
-- Data for Name: relation_group; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2905 (class 0 OID 0)
-- Dependencies: 218
-- Name: role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('role_id_seq', 1, false);


--
-- TOC entry 2850 (class 0 OID 160743)
-- Dependencies: 217
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO roles (id, name, description) VALUES (1, 'superAdmin', 'Super Administrateur pour tout gérer tout thésaurus et tout utilisateur');
INSERT INTO roles (id, name, description) VALUES (2, 'admin', 'administrateur par thésaurus ou plus');
INSERT INTO roles (id, name, description) VALUES (3, 'user', 'utilisateur par thésaurus ou plus');
INSERT INTO roles (id, name, description) VALUES (4, 'traducteur', 'traducteur par thésaurus ou plus');
INSERT INTO roles (id, name, description) VALUES (5, 'images', 'gestion des images par thésaurus ou plus');


--
-- TOC entry 2852 (class 0 OID 160751)
-- Dependencies: 219
-- Data for Name: split_non_preferred_term; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2854 (class 0 OID 160756)
-- Dependencies: 221
-- Data for Name: term; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2906 (class 0 OID 0)
-- Dependencies: 220
-- Name: term__id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('term__id_seq', 1, false);


--
-- TOC entry 2856 (class 0 OID 160768)
-- Dependencies: 223
-- Data for Name: term_candidat; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2907 (class 0 OID 0)
-- Dependencies: 222
-- Name: term_candidat__id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('term_candidat__id_seq', 1, false);


--
-- TOC entry 2858 (class 0 OID 160779)
-- Dependencies: 225
-- Data for Name: term_historique; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2908 (class 0 OID 0)
-- Dependencies: 224
-- Name: term_historique__id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('term_historique__id_seq', 1, false);


--
-- TOC entry 2860 (class 0 OID 160790)
-- Dependencies: 227
-- Data for Name: thesaurus; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2861 (class 0 OID 160799)
-- Dependencies: 228
-- Data for Name: thesaurus_alignement_source; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2862 (class 0 OID 160805)
-- Dependencies: 229
-- Data for Name: thesaurus_array; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2863 (class 0 OID 160813)
-- Dependencies: 230
-- Data for Name: thesaurus_array_concept; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2909 (class 0 OID 0)
-- Dependencies: 226
-- Name: thesaurus_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('thesaurus_id_seq', 1, false);


--
-- TOC entry 2864 (class 0 OID 160820)
-- Dependencies: 231
-- Data for Name: thesaurus_label; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2876 (class 0 OID 189201)
-- Dependencies: 243
-- Data for Name: theso_preferences; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2910 (class 0 OID 0)
-- Dependencies: 242
-- Name: theso_preferences_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('theso_preferences_id_seq', 1, false);


--
-- TOC entry 2911 (class 0 OID 0)
-- Dependencies: 232
-- Name: user__id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('user__id_seq', 2, false);


--
-- TOC entry 2866 (class 0 OID 160830)
-- Dependencies: 233
-- Data for Name: user_role; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO user_role (id_user, id_role, id_thesaurus, id_group) VALUES (1, 1, '', '');


--
-- TOC entry 2867 (class 0 OID 160836)
-- Dependencies: 234
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO users (id_user, username, password, active, mail, passtomodify) VALUES (1, 'admin', '21232f297a57a5a743894a0e4a801fc3', true, 'admin@domaine.fr', false);


--
-- TOC entry 2868 (class 0 OID 160844)
-- Dependencies: 235
-- Data for Name: users2; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO users2 (id_user, login, fullname, password, active, mail, authentication) VALUES (8, 'miled', 'Miled Rousset', NULL, true, 'mie@mile', 'DB');
INSERT INTO users2 (id_user, login, fullname, password, active, mail, authentication) VALUES (9, 'toto', 'toeot', NULL, true, 'mil@mf', 'LDAP');


--
-- TOC entry 2869 (class 0 OID 160853)
-- Dependencies: 236
-- Data for Name: users_historique; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO users_historique (id_user, username, created, modified, delete) VALUES (1, 'admin', '2016-12-01 10:13:33.472398+01', '2016-12-01 10:13:33.472398+01', NULL);
INSERT INTO users_historique (id_user, username, created, modified, delete) VALUES (2, 'miled', '2017-02-10 10:03:08.08475+01', '2017-02-10 10:03:08.08475+01', NULL);
INSERT INTO users_historique (id_user, username, created, modified, delete) VALUES (3, 'toto', '2017-06-22 11:09:38.782809+02', '2017-06-22 11:09:38.782809+02', NULL);


--
-- TOC entry 2870 (class 0 OID 160861)
-- Dependencies: 237
-- Data for Name: version_history; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- TOC entry 2675 (class 2606 OID 160869)
-- Name: VersionHistory_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY version_history
    ADD CONSTRAINT "VersionHistory_pkey" PRIMARY KEY ("idVersionhistory");


--
-- TOC entry 2556 (class 2606 OID 225716)
-- Name: alignement_internal_id_concept_internal_id_thesaurus_id_alignem; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY alignement
    ADD CONSTRAINT alignement_internal_id_concept_internal_id_thesaurus_id_alignem UNIQUE (internal_id_concept, internal_id_thesaurus, id_alignement_source, alignement_id_type);


--
-- TOC entry 2558 (class 2606 OID 160873)
-- Name: alignement_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY alignement
    ADD CONSTRAINT alignement_pkey PRIMARY KEY (id);


--
-- TOC entry 2677 (class 2606 OID 170650)
-- Name: alignement_preferences_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY alignement_preferences
    ADD CONSTRAINT alignement_preferences_pkey PRIMARY KEY (id_thesaurus, id_user, id_concept_depart, id_alignement_source);


--
-- TOC entry 2560 (class 2606 OID 160875)
-- Name: alignement_source_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY alignement_source
    ADD CONSTRAINT alignement_source_pkey PRIMARY KEY (id);


--
-- TOC entry 2562 (class 2606 OID 160877)
-- Name: alignement_source_source_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY alignement_source
    ADD CONSTRAINT alignement_source_source_key UNIQUE (source);


--
-- TOC entry 2564 (class 2606 OID 160879)
-- Name: alignment_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY alignement_type
    ADD CONSTRAINT alignment_type_pkey PRIMARY KEY (id);


--
-- TOC entry 2566 (class 2606 OID 160881)
-- Name: compound_equivalence_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY compound_equivalence
    ADD CONSTRAINT compound_equivalence_pkey PRIMARY KEY (id_split_nonpreferredterm, id_preferredterm);


--
-- TOC entry 2568 (class 2606 OID 160883)
-- Name: concept_candidat_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY concept_candidat
    ADD CONSTRAINT concept_candidat_id_key UNIQUE (id);


--
-- TOC entry 2570 (class 2606 OID 160885)
-- Name: concept_candidat_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY concept_candidat
    ADD CONSTRAINT concept_candidat_pkey PRIMARY KEY (id_concept);


--
-- TOC entry 2590 (class 2606 OID 225720)
-- Name: concept_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY concept_historique
    ADD CONSTRAINT concept_copy_pkey PRIMARY KEY (id_concept, id_thesaurus, id_group, id_user, modified);


--
-- TOC entry 2572 (class 2606 OID 160889)
-- Name: concept_fusion_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY concept_fusion
    ADD CONSTRAINT concept_fusion_pkey PRIMARY KEY (id_concept1, id_concept2, id_thesaurus);


--
-- TOC entry 2576 (class 2606 OID 160891)
-- Name: concept_group_concept_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY concept_group_concept
    ADD CONSTRAINT concept_group_concept_pkey PRIMARY KEY (idgroup, idthesaurus, idconcept);


--
-- TOC entry 2578 (class 2606 OID 160893)
-- Name: concept_group_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY concept_group_historique
    ADD CONSTRAINT concept_group_copy_pkey PRIMARY KEY (idgroup, idthesaurus, modified, id_user);


--
-- TOC entry 2584 (class 2606 OID 160895)
-- Name: concept_group_label_copy_idgrouplabel_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY concept_group_label_historique
    ADD CONSTRAINT concept_group_label_copy_idgrouplabel_key UNIQUE (id);


--
-- TOC entry 2586 (class 2606 OID 160897)
-- Name: concept_group_label_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY concept_group_label_historique
    ADD CONSTRAINT concept_group_label_copy_pkey PRIMARY KEY (lang, idthesaurus, lexicalvalue, modified, id_user);


--
-- TOC entry 2580 (class 2606 OID 160899)
-- Name: concept_group_label_idgrouplabel_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY concept_group_label
    ADD CONSTRAINT concept_group_label_idgrouplabel_key UNIQUE (id);


--
-- TOC entry 2582 (class 2606 OID 160901)
-- Name: concept_group_label_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY concept_group_label
    ADD CONSTRAINT concept_group_label_pkey PRIMARY KEY (lang, idthesaurus, lexicalvalue);


--
-- TOC entry 2574 (class 2606 OID 160903)
-- Name: concept_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY concept_group
    ADD CONSTRAINT concept_group_pkey PRIMARY KEY (idgroup, idthesaurus);


--
-- TOC entry 2588 (class 2606 OID 160905)
-- Name: concept_group_type_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY concept_group_type
    ADD CONSTRAINT concept_group_type_pkey PRIMARY KEY (code, label);


--
-- TOC entry 2592 (class 2606 OID 160907)
-- Name: concept_orphan_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY concept_orphan
    ADD CONSTRAINT concept_orphan_pkey PRIMARY KEY (id_concept, id_thesaurus);


--
-- TOC entry 2594 (class 2606 OID 160911)
-- Name: concept_term_candidat_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY concept_term_candidat
    ADD CONSTRAINT concept_term_candidat_pkey PRIMARY KEY (id_concept, id_term, id_thesaurus);


--
-- TOC entry 2685 (class 2606 OID 217491)
-- Name: concept_theso_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY concept
    ADD CONSTRAINT concept_theso_pkey PRIMARY KEY (id_concept, id_thesaurus);


--
-- TOC entry 2596 (class 2606 OID 160913)
-- Name: custom_concept_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY custom_concept_attribute
    ADD CONSTRAINT custom_concept_attribute_pkey PRIMARY KEY ("idConcept");


--
-- TOC entry 2598 (class 2606 OID 160915)
-- Name: custom_term_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY custom_term_attribute
    ADD CONSTRAINT custom_term_attribute_pkey PRIMARY KEY (identifier);


--
-- TOC entry 2600 (class 2606 OID 225699)
-- Name: gps_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY gps
    ADD CONSTRAINT gps_pkey PRIMARY KEY (id_concept, id_theso);


--
-- TOC entry 2679 (class 2606 OID 170662)
-- Name: gps_preferences_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY gps_preferences
    ADD CONSTRAINT gps_preferences_pkey PRIMARY KEY (id_thesaurus, id_user, id_alignement_source);


--
-- TOC entry 2604 (class 2606 OID 160919)
-- Name: hierarchical_relationship_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hierarchical_relationship_historique
    ADD CONSTRAINT hierarchical_relationship_copy_pkey PRIMARY KEY (id_concept1, id_thesaurus, role, id_concept2, modified, id_user);


--
-- TOC entry 2602 (class 2606 OID 160921)
-- Name: hierarchical_relationship_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hierarchical_relationship
    ADD CONSTRAINT hierarchical_relationship_pkey PRIMARY KEY (id_concept1, id_thesaurus, role, id_concept2);


--
-- TOC entry 2606 (class 2606 OID 160923)
-- Name: images_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY images
    ADD CONSTRAINT images_pkey PRIMARY KEY (id_concept, id_thesaurus, image_name);


--
-- TOC entry 2608 (class 2606 OID 170767)
-- Name: info_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY info
    ADD CONSTRAINT info_pkey PRIMARY KEY (version_opentheso, version_bdd);


--
-- TOC entry 2693 (class 2606 OID 225640)
-- Name: languages_iso639_iso639_1_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY languages_iso639
    ADD CONSTRAINT languages_iso639_iso639_1_key UNIQUE (iso639_1);


--
-- TOC entry 2695 (class 2606 OID 225638)
-- Name: languages_iso639_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY languages_iso639
    ADD CONSTRAINT languages_iso639_pkey PRIMARY KEY (id);


--
-- TOC entry 2610 (class 2606 OID 160927)
-- Name: node_label_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY node_label
    ADD CONSTRAINT node_label_pkey PRIMARY KEY (facet_id, id_thesaurus, lang);


--
-- TOC entry 2612 (class 2606 OID 160929)
-- Name: non_prefered_term_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY non_preferred_term
    ADD CONSTRAINT non_prefered_term_pkey PRIMARY KEY (id_term, lexical_value, lang, id_thesaurus);


--
-- TOC entry 2614 (class 2606 OID 160931)
-- Name: non_preferred_term_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY non_preferred_term_historique
    ADD CONSTRAINT non_preferred_term_copy_pkey PRIMARY KEY (id_term, lexical_value, lang, id_thesaurus, modified, id_user);


--
-- TOC entry 2622 (class 2606 OID 160933)
-- Name: note_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY note_historique
    ADD CONSTRAINT note_copy_pkey PRIMARY KEY (id, modified, id_user);


--
-- TOC entry 2616 (class 2606 OID 225695)
-- Name: note_notetypecode_id_thesaurus_id_concept_lang_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY note
    ADD CONSTRAINT note_notetypecode_id_thesaurus_id_concept_lang_key UNIQUE (notetypecode, id_thesaurus, id_concept, lang, lexicalvalue);


--
-- TOC entry 2618 (class 2606 OID 225697)
-- Name: note_notetypecode_id_thesaurus_id_term_lang_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY note
    ADD CONSTRAINT note_notetypecode_id_thesaurus_id_term_lang_key UNIQUE (notetypecode, id_thesaurus, id_term, lang, lexicalvalue);


--
-- TOC entry 2620 (class 2606 OID 160939)
-- Name: note_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY note
    ADD CONSTRAINT note_pkey PRIMARY KEY (id);


--
-- TOC entry 2627 (class 2606 OID 160941)
-- Name: permuted_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY permuted
    ADD CONSTRAINT permuted_pkey PRIMARY KEY (ord, id_concept, id_group, id_thesaurus, id_lang, lexical_value, ispreferredterm);


--
-- TOC entry 2624 (class 2606 OID 160943)
-- Name: pk_note_type; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY note_type
    ADD CONSTRAINT pk_note_type PRIMARY KEY (code);


--
-- TOC entry 2687 (class 2606 OID 217499)
-- Name: pk_relation_group; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY relation_group
    ADD CONSTRAINT pk_relation_group PRIMARY KEY (id_group1, id_thesaurus, relation, id_group2);


--
-- TOC entry 2689 (class 2606 OID 225402)
-- Name: preferences_id_thesaurus_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY preferences
    ADD CONSTRAINT preferences_id_thesaurus_key UNIQUE (id_thesaurus);


--
-- TOC entry 2691 (class 2606 OID 225400)
-- Name: preferences_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY preferences
    ADD CONSTRAINT preferences_pkey PRIMARY KEY (id_pref);


--
-- TOC entry 2629 (class 2606 OID 160949)
-- Name: preferred_term_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY preferred_term
    ADD CONSTRAINT preferred_term_pkey PRIMARY KEY (id_concept, id_thesaurus);


--
-- TOC entry 2631 (class 2606 OID 160951)
-- Name: proposition_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY proposition
    ADD CONSTRAINT proposition_pkey PRIMARY KEY (id_concept, id_user, id_thesaurus);


--
-- TOC entry 2633 (class 2606 OID 160953)
-- Name: role_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 2642 (class 2606 OID 160955)
-- Name: term_candidat_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY term_candidat
    ADD CONSTRAINT term_candidat_pkey PRIMARY KEY (id_term, lexical_value, lang, id_thesaurus, contributor);


--
-- TOC entry 2645 (class 2606 OID 225718)
-- Name: term_copy_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY term_historique
    ADD CONSTRAINT term_copy_pkey PRIMARY KEY (id, modified, id_user);


--
-- TOC entry 2636 (class 2606 OID 160959)
-- Name: term_id_term_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY term
    ADD CONSTRAINT term_id_term_key UNIQUE (id_term, lang, id_thesaurus);


--
-- TOC entry 2638 (class 2606 OID 160961)
-- Name: term_id_term_lexical_value_lang_id_thesaurus_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY term
    ADD CONSTRAINT term_id_term_lexical_value_lang_id_thesaurus_key UNIQUE (id_term, lexical_value, lang, id_thesaurus);


--
-- TOC entry 2640 (class 2606 OID 160963)
-- Name: term_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY term
    ADD CONSTRAINT term_pkey PRIMARY KEY (id);


--
-- TOC entry 2649 (class 2606 OID 160965)
-- Name: thesaurus_alignement_source_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY thesaurus_alignement_source
    ADD CONSTRAINT thesaurus_alignement_source_pkey PRIMARY KEY (id_thesaurus, id_alignement_source);


--
-- TOC entry 2653 (class 2606 OID 160967)
-- Name: thesaurus_array_concept_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY thesaurus_array_concept
    ADD CONSTRAINT thesaurus_array_concept_pkey PRIMARY KEY (thesaurusarrayid, id_concept, id_thesaurus);


--
-- TOC entry 2651 (class 2606 OID 160969)
-- Name: thesaurus_array_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY thesaurus_array
    ADD CONSTRAINT thesaurus_array_pkey PRIMARY KEY (facet_id, id_thesaurus, id_concept_parent);


--
-- TOC entry 2655 (class 2606 OID 160971)
-- Name: thesaurus_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY thesaurus_label
    ADD CONSTRAINT thesaurus_pkey PRIMARY KEY (id_thesaurus, lang, title);


--
-- TOC entry 2647 (class 2606 OID 160973)
-- Name: thesaurus_pkey1; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY thesaurus
    ADD CONSTRAINT thesaurus_pkey1 PRIMARY KEY (id_thesaurus, id_ark);


--
-- TOC entry 2681 (class 2606 OID 189211)
-- Name: theso_preferences_id_id_thesaurus_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY theso_preferences
    ADD CONSTRAINT theso_preferences_id_id_thesaurus_key UNIQUE (id, id_thesaurus);


--
-- TOC entry 2683 (class 2606 OID 189209)
-- Name: theso_preferences_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY theso_preferences
    ADD CONSTRAINT theso_preferences_pkey PRIMARY KEY (id);


--
-- TOC entry 2657 (class 2606 OID 160975)
-- Name: unique_thesau_lang; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY thesaurus_label
    ADD CONSTRAINT unique_thesau_lang UNIQUE (id_thesaurus, lang);


--
-- TOC entry 2661 (class 2606 OID 160977)
-- Name: user_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT user_pkey PRIMARY KEY (id_user);


--
-- TOC entry 2659 (class 2606 OID 160979)
-- Name: user_role_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY user_role
    ADD CONSTRAINT user_role_pkey PRIMARY KEY (id_user, id_role, id_thesaurus);


--
-- TOC entry 2663 (class 2606 OID 160981)
-- Name: user_username_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT user_username_key UNIQUE (username);


--
-- TOC entry 2673 (class 2606 OID 160983)
-- Name: users_historique_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY users_historique
    ADD CONSTRAINT users_historique_pkey PRIMARY KEY (id_user);


--
-- TOC entry 2667 (class 2606 OID 160985)
-- Name: users_login_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY users2
    ADD CONSTRAINT users_login_key UNIQUE (login);


--
-- TOC entry 2669 (class 2606 OID 160987)
-- Name: users_mail_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY users2
    ADD CONSTRAINT users_mail_key UNIQUE (mail);


--
-- TOC entry 2665 (class 2606 OID 160989)
-- Name: users_mail_key1; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_mail_key1 UNIQUE (mail);


--
-- TOC entry 2671 (class 2606 OID 160991)
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY users2
    ADD CONSTRAINT users_pkey PRIMARY KEY (id_user);


--
-- TOC entry 2634 (class 1259 OID 160992)
-- Name: index_lexical_value; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX index_lexical_value ON term USING btree (lexical_value);


--
-- TOC entry 2643 (class 1259 OID 160993)
-- Name: index_lexical_value_copy; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX index_lexical_value_copy ON term_historique USING btree (lexical_value);


--
-- TOC entry 2625 (class 1259 OID 160994)
-- Name: permuted_lexical_value_idx; Type: INDEX; Schema: public; Owner: -; Tablespace: 
--

CREATE INDEX permuted_lexical_value_idx ON permuted USING btree (lexical_value);


-- Completed on 2017-06-30 10:46:06 CEST

--
-- PostgreSQL database dump complete
--

