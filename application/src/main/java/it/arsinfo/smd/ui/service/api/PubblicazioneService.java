package it.arsinfo.smd.ui.service.api;

import java.util.List;

import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;

public interface PubblicazioneService extends SmdServiceBase<Pubblicazione> {
	
	List<Pubblicazione> searchBy(String nome, TipoPubblicazione tipo);}
