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
-- Name: abbonamento; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.abbonamento (
    id bigint NOT NULL,
    anno character varying(255),
    code_line character varying(255),
    contrassegno boolean NOT NULL,
    data timestamp without time zone,
    importo numeric(19,2),
    incassato numeric(19,2),
    inviatoec boolean NOT NULL,
    pregresso numeric(19,2),
    sollecitato boolean NOT NULL,
    spese numeric(19,2),
    spese_estero numeric(19,2),
    spese_estratto_conto numeric(19,2),
    stato_abbonamento character varying(255),
    campagna_id bigint,
    intestatario_id bigint
);


ALTER TABLE public.abbonamento OWNER TO postgres;

--
-- Name: anagrafica; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.anagrafica (
    id bigint NOT NULL,
    area_spedizione character varying(255),
    cap character varying(255),
    cariche_socialiadp boolean NOT NULL,
    cellulare character varying(255),
    centro_diocesano character varying(255),
    citta character varying(255),
    code_line_base character varying(255) NOT NULL,
    codfis character varying(255),
    consiglio_nazionaleadp boolean NOT NULL,
    delegati_regionaliadp boolean NOT NULL,
    denominazione character varying(255),
    descr character varying(255),
    diocesi character varying(255),
    direttore_diocesiano boolean NOT NULL,
    direttore_zona_milano boolean NOT NULL,
    direzioneadp boolean NOT NULL,
    elenco_marisa_bisi boolean NOT NULL,
    email character varying(255),
    indirizzo character varying(255),
    indirizzo_seconda_riga character varying(255),
    nome character varying(255),
    paese character varying(255),
    piva character varying(255),
    presidente_diocesano boolean NOT NULL,
    presidenzaadp boolean NOT NULL,
    promotore_regionale boolean NOT NULL,
    provincia character varying(255),
    regione_direttore_diocesano character varying(255),
    regione_presidente_diocesano character varying(255),
    regione_vescovi character varying(255),
    telefono character varying(255),
    titolo integer NOT NULL,
    co_id bigint
);


ALTER TABLE public.anagrafica OWNER TO postgres;

--
-- Name: campagna; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.campagna (
    id bigint NOT NULL,
    anno character varying(255) NOT NULL,
    numero integer,
    running boolean NOT NULL,
    stato_campagna character varying(255) NOT NULL
);


ALTER TABLE public.campagna OWNER TO postgres;

--
-- Name: campagna_item; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.campagna_item (
    id bigint NOT NULL,
    campagna_id bigint NOT NULL,
    pubblicazione_id bigint NOT NULL
);


ALTER TABLE public.campagna_item OWNER TO postgres;

--
-- Name: distinta_versamento; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.distinta_versamento (
    id bigint NOT NULL,
    cassa character varying(255),
    ccp character varying(255),
    cuas character varying(255),
    data_contabile timestamp without time zone,
    documenti integer NOT NULL,
    errati integer NOT NULL,
    esatti integer NOT NULL,
    importo numeric(19,2),
    importo_errati numeric(19,2),
    importo_esatti numeric(19,2),
    incassato numeric(19,2)
);


ALTER TABLE public.distinta_versamento OWNER TO postgres;

--
-- Name: documenti_trasporto_cumulati; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.documenti_trasporto_cumulati (
    id bigint NOT NULL,
    anno character varying(255) NOT NULL,
    importo numeric(19,2) NOT NULL
);


ALTER TABLE public.documenti_trasporto_cumulati OWNER TO postgres;

--
-- Name: documento_trasporto; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.documento_trasporto (
    id bigint NOT NULL,
    data timestamp without time zone NOT NULL,
    ddt character varying(255) NOT NULL,
    importo numeric(19,2) NOT NULL,
    operatore character varying(255) NOT NULL,
    stato_operazione_incasso character varying(255) NOT NULL,
    committente_id bigint NOT NULL,
    documenti_trasporto_cumulati_id bigint NOT NULL,
    versamento_id bigint NOT NULL
);


ALTER TABLE public.documento_trasporto OWNER TO postgres;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO postgres;

--
-- Name: nota; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nota (
    id bigint NOT NULL,
    data timestamp without time zone,
    description character varying(255),
    operatore character varying(255),
    storico_id bigint NOT NULL
);


ALTER TABLE public.nota OWNER TO postgres;

--
-- Name: offerta; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.offerta (
    id bigint NOT NULL,
    data timestamp without time zone NOT NULL,
    importo numeric(19,2) NOT NULL,
    operatore character varying(255) NOT NULL,
    stato_operazione_incasso character varying(255) NOT NULL,
    committente_id bigint NOT NULL,
    offerte_cumulate_id bigint NOT NULL,
    versamento_id bigint NOT NULL
);


ALTER TABLE public.offerta OWNER TO postgres;

--
-- Name: offerte_cumulate; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.offerte_cumulate (
    id bigint NOT NULL,
    anno character varying(255) NOT NULL,
    importo numeric(19,2) NOT NULL
);


ALTER TABLE public.offerte_cumulate OWNER TO postgres;

--
-- Name: operazione; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.operazione (
    id bigint NOT NULL,
    anno character varying(255),
    anno_pubblicazione character varying(255),
    definitivo_sede integer,
    definitivo_sped integer,
    mese character varying(255),
    mese_pubblicazione character varying(255),
    stato_operazione character varying(255),
    stimato_sede integer,
    stimato_sped integer,
    pubblicazione_id bigint NOT NULL
);


ALTER TABLE public.operazione OWNER TO postgres;

--
-- Name: operazione_campagna; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.operazione_campagna (
    id bigint NOT NULL,
    data timestamp without time zone NOT NULL,
    operatore character varying(255) NOT NULL,
    stato character varying(255) NOT NULL,
    campagna_id bigint NOT NULL
);


ALTER TABLE public.operazione_campagna OWNER TO postgres;

--
-- Name: operazione_incasso; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.operazione_incasso (
    id bigint NOT NULL,
    data timestamp without time zone,
    description character varying(255),
    importo numeric(19,2),
    operatore character varying(255),
    stato_operazione_incasso character varying(255),
    abbonamento_id bigint NOT NULL,
    versamento_id bigint NOT NULL
);


ALTER TABLE public.operazione_incasso OWNER TO postgres;

--
-- Name: operazione_sospendi; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.operazione_sospendi (
    id bigint NOT NULL,
    data timestamp without time zone NOT NULL,
    mese_spedizione character varying(255) NOT NULL,
    operatore character varying(255) NOT NULL,
    campagna_id bigint NOT NULL,
    pubblicazione_id bigint NOT NULL
);


ALTER TABLE public.operazione_sospendi OWNER TO postgres;

--
-- Name: pubblicazione; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pubblicazione (
    id bigint NOT NULL,
    abbonamento numeric(19,2),
    abbonamento_con_sconto numeric(19,2),
    abbonamento_sostenitore numeric(19,2),
    abbonamento_web numeric(19,2),
    active boolean NOT NULL,
    ago boolean NOT NULL,
    anno character varying(255),
    anticipo_spedizione integer NOT NULL,
    apr boolean NOT NULL,
    autore character varying(255),
    costo_unitario numeric(19,2),
    descrizione character varying(255),
    dic boolean NOT NULL,
    editore character varying(255),
    feb boolean NOT NULL,
    gen boolean NOT NULL,
    giu boolean NOT NULL,
    grammi integer NOT NULL,
    lug boolean NOT NULL,
    mag boolean NOT NULL,
    mar boolean NOT NULL,
    nome character varying(255),
    nov boolean NOT NULL,
    ott boolean NOT NULL,
    set boolean NOT NULL,
    tipo character varying(255)
);


ALTER TABLE public.pubblicazione OWNER TO postgres;

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
-- Name: rivista_abbonamento; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.rivista_abbonamento (
    id bigint NOT NULL,
    anno_fine integer,
    anno_inizio integer,
    importo numeric(19,2),
    invio_spedizione character varying(255),
    mese_fine integer,
    mese_inizio integer,
    numero integer,
    numero_totale_riviste integer,
    stato_rivista character varying(255),
    tipo_abbonamento_rivista character varying(255),
    abbonamento_id bigint NOT NULL,
    destinatario_id bigint NOT NULL,
    pubblicazione_id bigint NOT NULL,
    storico_id bigint
);


ALTER TABLE public.rivista_abbonamento OWNER TO postgres;

--
-- Name: spedizione; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.spedizione (
    id bigint NOT NULL,
    anno_spedizione character varying(255),
    invio_spedizione character varying(255),
    mese_spedizione character varying(255),
    peso_stimato integer,
    spese_postali numeric(19,2),
    abbonamento_id bigint,
    destinatario_id bigint
);


ALTER TABLE public.spedizione OWNER TO postgres;

--
-- Name: spedizione_item; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.spedizione_item (
    id bigint NOT NULL,
    anno_pubblicazione character varying(255),
    mese_pubblicazione character varying(255),
    numero integer,
    posticipata boolean NOT NULL,
    stato_spedizione character varying(255),
    pubblicazione_id bigint NOT NULL,
    rivista_abbonamento_id bigint NOT NULL,
    spedizione_id bigint NOT NULL
);


ALTER TABLE public.spedizione_item OWNER TO postgres;

--
-- Name: spesa_spedizione; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.spesa_spedizione (
    id bigint NOT NULL,
    area_spedizione character varying(255),
    cor24h numeric(19,2),
    cor3gg numeric(19,2),
    range_spese_spedizione character varying(255),
    spese numeric(19,2)
);


ALTER TABLE public.spesa_spedizione OWNER TO postgres;

--
-- Name: storico; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.storico (
    id bigint NOT NULL,
    contrassegno boolean NOT NULL,
    invio_spedizione character varying(255) NOT NULL,
    numero integer,
    stato_storico character varying(255) NOT NULL,
    tipo_abbonamento_rivista character varying(255) NOT NULL,
    destinatario_id bigint NOT NULL,
    intestatario_id bigint NOT NULL,
    pubblicazione_id bigint NOT NULL
);


ALTER TABLE public.storico OWNER TO postgres;

--
-- Name: user_info; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_info (
    id bigint NOT NULL,
    data timestamp without time zone,
    password_hash character varying(255),
    provider character varying(255),
    role character varying(255),
    username character varying(255)
);


ALTER TABLE public.user_info OWNER TO postgres;

--
-- Name: versamento; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.versamento (
    id bigint NOT NULL,
    accettazione character varying(255),
    bobina character varying(255),
    bollettino character varying(255),
    code_line character varying(255),
    data_contabile timestamp without time zone,
    data_pagamento timestamp without time zone,
    importo numeric(19,2),
    incassato numeric(19,2),
    progressivo character varying(255),
    progressivo_bobina character varying(255),
    provincia character varying(255),
    sostitutivo character varying(255),
    sportello character varying(255),
    ufficio character varying(255),
    committente_id bigint,
    distinta_versamento_id bigint NOT NULL
);


ALTER TABLE public.versamento OWNER TO postgres;

--
-- Name: woo_commerce_order; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.woo_commerce_order (
    id bigint NOT NULL,
    data timestamp without time zone,
    description character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    permalink character varying(255) NOT NULL,
    price numeric(19,2) NOT NULL,
    product_id integer NOT NULL,
    short_description character varying(255) NOT NULL,
    status character varying(255) NOT NULL,
    abbonamento_id bigint NOT NULL
);


--
-- Name: abbonamento abbonamento_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.abbonamento
    ADD CONSTRAINT abbonamento_pkey PRIMARY KEY (id);


--
-- Name: anagrafica anagrafica_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.anagrafica
    ADD CONSTRAINT anagrafica_pkey PRIMARY KEY (id);


--
-- Name: campagna_item campagna_item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.campagna_item
    ADD CONSTRAINT campagna_item_pkey PRIMARY KEY (id);


--
-- Name: campagna campagna_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.campagna
    ADD CONSTRAINT campagna_pkey PRIMARY KEY (id);


--
-- Name: distinta_versamento distinta_versamento_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.distinta_versamento
    ADD CONSTRAINT distinta_versamento_pkey PRIMARY KEY (id);


--
-- Name: documenti_trasporto_cumulati documenti_trasporto_cumulati_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documenti_trasporto_cumulati
    ADD CONSTRAINT documenti_trasporto_cumulati_pkey PRIMARY KEY (id);


--
-- Name: documento_trasporto documento_trasporto_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_trasporto
    ADD CONSTRAINT documento_trasporto_pkey PRIMARY KEY (id);


--
-- Name: nota nota_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nota
    ADD CONSTRAINT nota_pkey PRIMARY KEY (id);


--
-- Name: offerta offerta_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offerta
    ADD CONSTRAINT offerta_pkey PRIMARY KEY (id);


--
-- Name: offerte_cumulate offerte_cumulate_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offerte_cumulate
    ADD CONSTRAINT offerte_cumulate_pkey PRIMARY KEY (id);


--
-- Name: operazione_campagna operazione_campagna_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.operazione_campagna
    ADD CONSTRAINT operazione_campagna_pkey PRIMARY KEY (id);


--
-- Name: operazione_incasso operazione_incasso_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.operazione_incasso
    ADD CONSTRAINT operazione_incasso_pkey PRIMARY KEY (id);


--
-- Name: operazione operazione_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.operazione
    ADD CONSTRAINT operazione_pkey PRIMARY KEY (id);


--
-- Name: operazione_sospendi operazione_sospendi_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.operazione_sospendi
    ADD CONSTRAINT operazione_sospendi_pkey PRIMARY KEY (id);


--
-- Name: pubblicazione pubblicazione_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pubblicazione
    ADD CONSTRAINT pubblicazione_pkey PRIMARY KEY (id);


--
-- Name: remote_user_info remote_user_info_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.remote_user_info
    ADD CONSTRAINT remote_user_info_pkey PRIMARY KEY (id);


--
-- Name: rivista_abbonamento rivista_abbonamento_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rivista_abbonamento
    ADD CONSTRAINT rivista_abbonamento_pkey PRIMARY KEY (id);


--
-- Name: spedizione_item spedizione_item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spedizione_item
    ADD CONSTRAINT spedizione_item_pkey PRIMARY KEY (id);


--
-- Name: spedizione spedizione_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spedizione
    ADD CONSTRAINT spedizione_pkey PRIMARY KEY (id);


--
-- Name: spesa_spedizione spesa_spedizione_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spesa_spedizione
    ADD CONSTRAINT spesa_spedizione_pkey PRIMARY KEY (id);


--
-- Name: storico storico_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.storico
    ADD CONSTRAINT storico_pkey PRIMARY KEY (id);


--
-- Name: operazione uk68u58h2afgei03w4sngr398wa; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.operazione
    ADD CONSTRAINT uk68u58h2afgei03w4sngr398wa UNIQUE (anno, mese, pubblicazione_id);


--
-- Name: remote_user_info uk7a7c4pn8hyruacwa8iviywn6c; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.remote_user_info
    ADD CONSTRAINT uk7a7c4pn8hyruacwa8iviywn6c UNIQUE (user_info_id, code);


--
-- Name: user_info ukcrghtogts6k0k79tuhdowxnec; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_info
    ADD CONSTRAINT ukcrghtogts6k0k79tuhdowxnec UNIQUE (username, provider);


--
-- Name: operazione_campagna ukd4gg6ebnl3wwtbjw7xrvfkawo; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.operazione_campagna
    ADD CONSTRAINT ukd4gg6ebnl3wwtbjw7xrvfkawo UNIQUE (campagna_id, stato);


--
-- Name: campagna ukdfp1sy7sfi45p9er5fly0ttm6; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.campagna
    ADD CONSTRAINT ukdfp1sy7sfi45p9er5fly0ttm6 UNIQUE (anno);


--
-- Name: abbonamento ukfx8hn7ppq1jecib3sj6jy5uha; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.abbonamento
    ADD CONSTRAINT ukfx8hn7ppq1jecib3sj6jy5uha UNIQUE (code_line);


--
-- Name: anagrafica uki3mtg5fl1kvsal9lnjkgvpogr; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.anagrafica
    ADD CONSTRAINT uki3mtg5fl1kvsal9lnjkgvpogr UNIQUE (code_line_base);


--
-- Name: operazione_sospendi ukki5a24g6594osoutb3ta019bp; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.operazione_sospendi
    ADD CONSTRAINT ukki5a24g6594osoutb3ta019bp UNIQUE (campagna_id, pubblicazione_id);


--
-- Name: distinta_versamento ukn4901wy9daxjtcco1vqwc5ul6; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.distinta_versamento
    ADD CONSTRAINT ukn4901wy9daxjtcco1vqwc5ul6 UNIQUE (data_contabile, cassa, cuas, ccp);


--
-- Name: pubblicazione uknkrgwcsaqmfwe3s0qfe0odrfs; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pubblicazione
    ADD CONSTRAINT uknkrgwcsaqmfwe3s0qfe0odrfs UNIQUE (nome);


--
-- Name: offerte_cumulate uknr6t7r8sugf1jb987macjdetd; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offerte_cumulate
    ADD CONSTRAINT uknr6t7r8sugf1jb987macjdetd UNIQUE (anno);


--
-- Name: documenti_trasporto_cumulati uks17ukj4xhw9wvg6jd2q01fxd2; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documenti_trasporto_cumulati
    ADD CONSTRAINT uks17ukj4xhw9wvg6jd2q01fxd2 UNIQUE (anno);


--
-- Name: spesa_spedizione uksa8uvg37kygmdml5uw8jtuh1x; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spesa_spedizione
    ADD CONSTRAINT uksa8uvg37kygmdml5uw8jtuh1x UNIQUE (range_spese_spedizione, area_spedizione);


--
-- Name: abbonamento uksl0978ydwlfa0xduanay6n5n2; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.abbonamento
    ADD CONSTRAINT uksl0978ydwlfa0xduanay6n5n2 UNIQUE (intestatario_id, campagna_id, contrassegno);


--
-- Name: user_info user_info_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_info
    ADD CONSTRAINT user_info_pkey PRIMARY KEY (id);


--
-- Name: versamento versamento_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.versamento
    ADD CONSTRAINT versamento_pkey PRIMARY KEY (id);


--
-- Name: woo_commerce_order woo_commerce_order_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.woo_commerce_order
    ADD CONSTRAINT woo_commerce_order_pkey PRIMARY KEY (id);


--
-- Name: abbonamento fk1dpllmfk747eya094loty33o; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.abbonamento
    ADD CONSTRAINT fk1dpllmfk747eya094loty33o FOREIGN KEY (campagna_id) REFERENCES public.campagna(id);


--
-- Name: spedizione_item fk1ri90tpxifnye5o2gok8vgemf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spedizione_item
    ADD CONSTRAINT fk1ri90tpxifnye5o2gok8vgemf FOREIGN KEY (spedizione_id) REFERENCES public.spedizione(id);


--
-- Name: operazione_sospendi fk2jaay7l5q6ydwlpfra1tb2ccr; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.operazione_sospendi
    ADD CONSTRAINT fk2jaay7l5q6ydwlpfra1tb2ccr FOREIGN KEY (campagna_id) REFERENCES public.campagna(id);


--
-- Name: documento_trasporto fk35u1t0vsseneq9chelj47amb4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_trasporto
    ADD CONSTRAINT fk35u1t0vsseneq9chelj47amb4 FOREIGN KEY (documenti_trasporto_cumulati_id) REFERENCES public.documenti_trasporto_cumulati(id);


--
-- Name: operazione_incasso fk37hdd5edcujeawvih8i9ivlbm; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.operazione_incasso
    ADD CONSTRAINT fk37hdd5edcujeawvih8i9ivlbm FOREIGN KEY (abbonamento_id) REFERENCES public.abbonamento(id);


--
-- Name: versamento fk3dyaodn0n2c3och41rkrdon93; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.versamento
    ADD CONSTRAINT fk3dyaodn0n2c3och41rkrdon93 FOREIGN KEY (distinta_versamento_id) REFERENCES public.distinta_versamento(id);


--
-- Name: documento_trasporto fk41xptns2fgym681qv5gli36mr; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_trasporto
    ADD CONSTRAINT fk41xptns2fgym681qv5gli36mr FOREIGN KEY (committente_id) REFERENCES public.anagrafica(id);


--
-- Name: documento_trasporto fk46wgpti715gnp6rjoj2h4ef2u; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documento_trasporto
    ADD CONSTRAINT fk46wgpti715gnp6rjoj2h4ef2u FOREIGN KEY (versamento_id) REFERENCES public.versamento(id);


--
-- Name: woo_commerce_order fk4eoswhgsccsub2iu2i1ia7x0l; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.woo_commerce_order
    ADD CONSTRAINT fk4eoswhgsccsub2iu2i1ia7x0l FOREIGN KEY (abbonamento_id) REFERENCES public.abbonamento(id);


--
-- Name: spedizione fk5kfl2mk1nr94jrl8yhvthu8jb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spedizione
    ADD CONSTRAINT fk5kfl2mk1nr94jrl8yhvthu8jb FOREIGN KEY (abbonamento_id) REFERENCES public.abbonamento(id);


--
-- Name: operazione_incasso fk5tkok0gn784dwmk6uq1ytcb0m; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.operazione_incasso
    ADD CONSTRAINT fk5tkok0gn784dwmk6uq1ytcb0m FOREIGN KEY (versamento_id) REFERENCES public.versamento(id);


--
-- Name: storico fk6rp92ad3i20tnmln3e5argyt0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.storico
    ADD CONSTRAINT fk6rp92ad3i20tnmln3e5argyt0 FOREIGN KEY (intestatario_id) REFERENCES public.anagrafica(id);


--
-- Name: offerta fk755k0q54ere3a7qdiaupicuhw; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offerta
    ADD CONSTRAINT fk755k0q54ere3a7qdiaupicuhw FOREIGN KEY (committente_id) REFERENCES public.anagrafica(id);


--
-- Name: offerta fk8agi1ks0q7h9v3kqva34q4cb; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offerta
    ADD CONSTRAINT fk8agi1ks0q7h9v3kqva34q4cb FOREIGN KEY (versamento_id) REFERENCES public.versamento(id);


--
-- Name: nota fkatuvqpgqwkldqaxhxi09x068t; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nota
    ADD CONSTRAINT fkatuvqpgqwkldqaxhxi09x068t FOREIGN KEY (storico_id) REFERENCES public.storico(id);


--
-- Name: abbonamento fkbl6b9c0i9s6xvk1rbh57jf4hm; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.abbonamento
    ADD CONSTRAINT fkbl6b9c0i9s6xvk1rbh57jf4hm FOREIGN KEY (intestatario_id) REFERENCES public.anagrafica(id);


--
-- Name: operazione fkcttti88wk2s9pqgp21s63xf0o; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.operazione
    ADD CONSTRAINT fkcttti88wk2s9pqgp21s63xf0o FOREIGN KEY (pubblicazione_id) REFERENCES public.pubblicazione(id);


--
-- Name: versamento fkdmn92jhrpgc4vpsrn1symqj0o; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.versamento
    ADD CONSTRAINT fkdmn92jhrpgc4vpsrn1symqj0o FOREIGN KEY (committente_id) REFERENCES public.anagrafica(id);


--
-- Name: operazione_campagna fkfau61dydrxi0kwms9lokot76v; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.operazione_campagna
    ADD CONSTRAINT fkfau61dydrxi0kwms9lokot76v FOREIGN KEY (campagna_id) REFERENCES public.campagna(id);


--
-- Name: spedizione_item fkg8qpi08s1pljxx5hgvd4tbkam; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spedizione_item
    ADD CONSTRAINT fkg8qpi08s1pljxx5hgvd4tbkam FOREIGN KEY (pubblicazione_id) REFERENCES public.pubblicazione(id);


--
-- Name: spedizione fkid5dvh9uv6ycecui4mrbe6tpk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spedizione
    ADD CONSTRAINT fkid5dvh9uv6ycecui4mrbe6tpk FOREIGN KEY (destinatario_id) REFERENCES public.anagrafica(id);


--
-- Name: rivista_abbonamento fkjitga2v84hnagpdmpel77etg8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rivista_abbonamento
    ADD CONSTRAINT fkjitga2v84hnagpdmpel77etg8 FOREIGN KEY (abbonamento_id) REFERENCES public.abbonamento(id);


--
-- Name: offerta fkjnjjlhr48wb98sy5it57y5l5g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.offerta
    ADD CONSTRAINT fkjnjjlhr48wb98sy5it57y5l5g FOREIGN KEY (offerte_cumulate_id) REFERENCES public.offerte_cumulate(id);


--
-- Name: spedizione_item fkm83bn6owi6gs2kcy09fqy9iij; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spedizione_item
    ADD CONSTRAINT fkm83bn6owi6gs2kcy09fqy9iij FOREIGN KEY (rivista_abbonamento_id) REFERENCES public.rivista_abbonamento(id);


--
-- Name: rivista_abbonamento fknuj9gq5i23156d65k14hgwt5h; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rivista_abbonamento
    ADD CONSTRAINT fknuj9gq5i23156d65k14hgwt5h FOREIGN KEY (pubblicazione_id) REFERENCES public.pubblicazione(id);


--
-- Name: rivista_abbonamento fknwpdd6lk6ej6ffqx4gmoa4f4a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rivista_abbonamento
    ADD CONSTRAINT fknwpdd6lk6ej6ffqx4gmoa4f4a FOREIGN KEY (storico_id) REFERENCES public.storico(id);


--
-- Name: remote_user_info fkovh30lcjmaonku8gg2v0lw6v9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.remote_user_info
    ADD CONSTRAINT fkovh30lcjmaonku8gg2v0lw6v9 FOREIGN KEY (user_info_id) REFERENCES public.user_info(id);


--
-- Name: storico fkq07qh5otc22dvrke5r8xs8srx; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.storico
    ADD CONSTRAINT fkq07qh5otc22dvrke5r8xs8srx FOREIGN KEY (destinatario_id) REFERENCES public.anagrafica(id);


--
-- Name: campagna_item fkqu3pogndumjprmyjynx4ehjla; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.campagna_item
    ADD CONSTRAINT fkqu3pogndumjprmyjynx4ehjla FOREIGN KEY (pubblicazione_id) REFERENCES public.pubblicazione(id);


--
-- Name: rivista_abbonamento fkrj9gx0k8c73bf14hwc9kx39jo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.rivista_abbonamento
    ADD CONSTRAINT fkrj9gx0k8c73bf14hwc9kx39jo FOREIGN KEY (destinatario_id) REFERENCES public.anagrafica(id);


--
-- Name: storico fkrs1gochov8o8agechrnwry15c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.storico
    ADD CONSTRAINT fkrs1gochov8o8agechrnwry15c FOREIGN KEY (pubblicazione_id) REFERENCES public.pubblicazione(id);


--
-- Name: campagna_item fksrcpkjkehvgbpsg5khdshewvi; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.campagna_item
    ADD CONSTRAINT fksrcpkjkehvgbpsg5khdshewvi FOREIGN KEY (campagna_id) REFERENCES public.campagna(id);


--
-- Name: operazione_sospendi fktd8vxarfqw60w2s0r4nvb5emj; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.operazione_sospendi
    ADD CONSTRAINT fktd8vxarfqw60w2s0r4nvb5emj FOREIGN KEY (pubblicazione_id) REFERENCES public.pubblicazione(id);


--
-- Name: anagrafica fktm3w7nxtai9fmoi3d1a9je3uq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.anagrafica
    ADD CONSTRAINT fktm3w7nxtai9fmoi3d1a9je3uq FOREIGN KEY (co_id) REFERENCES public.anagrafica(id);


--
-- PostgreSQL database dump complete
--

