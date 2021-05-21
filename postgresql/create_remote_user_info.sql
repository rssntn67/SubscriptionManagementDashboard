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

CREATE TABLE public.remote_user_info (
    id bigint NOT NULL,
    code character varying(255),
    user_info_id bigint
);


ALTER TABLE public.remote_user_info OWNER TO postgres;

--
-- Name: remote_user_info remote_user_info_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.remote_user_info
    ADD CONSTRAINT remote_user_info_pkey PRIMARY KEY (id);


--
-- Name: remote_user_info uk7a7c4pn8hyruacwa8iviywn6c; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.remote_user_info
    ADD CONSTRAINT uk7a7c4pn8hyruacwa8iviywn6c UNIQUE (user_info_id, code);


--
-- Name: remote_user_info fkovh30lcjmaonku8gg2v0lw6v9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.remote_user_info
    ADD CONSTRAINT fkovh30lcjmaonku8gg2v0lw6v9 FOREIGN KEY (user_info_id) REFERENCES public.user_info(id);


--
-- PostgreSQL database dump complete
--

