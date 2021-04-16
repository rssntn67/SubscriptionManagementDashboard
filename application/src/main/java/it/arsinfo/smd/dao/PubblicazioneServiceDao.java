package it.arsinfo.smd.dao;

import java.util.List;

import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;

public interface PubblicazioneServiceDao extends SmdServiceDao<Pubblicazione> {
	
	List<Pubblicazione> searchBy(String nome, TipoPubblicazione tipo);}
