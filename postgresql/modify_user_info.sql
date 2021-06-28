--
-- PostgreSQL database dump
--

-- Dumped from database version 12.6 (Debian 12.6-1.pgdg100+1)
-- Dumped by pg_dump version 12.6 (Debian 12.6-1.pgdg100+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: remote_user_info; Type: TABLE; Schema: public; Owner: postgres
--

alter TABLE public.user_info add COLUMN provider character varying(255) not null;

alter TABLE public.user_info drop CONSTRAINT uk_f2ksd6h8hsjtd57ipfq9myr64;

alter TABLE public.user_info add CONSTRAINT user_info_unique_pk
     UNIQUE (username, provider);

