package it.arsinfo.smd.service.dto;

import java.util.ArrayList;
import java.util.List;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.entity.SpedizioneItem;

public class RivistaAbbonamentoAggiorna {
	private final List<RivistaAbbonamento> rivisteToSave = new ArrayList<>();
	private final List<RivistaAbbonamento> rivisteToDelete = new ArrayList<>();
	private Abbonamento abbonamentoToSave;
	private List<SpedizioneItem> itemsToDelete = new ArrayList<>();
	private List<SpedizioneWithItems> spedizioniToSave = new ArrayList<>();
	
	public List<RivistaAbbonamento> getRivisteToSave() {
		return rivisteToSave;
	}
	public Abbonamento getAbbonamentoToSave() {
		return abbonamentoToSave;
	}
	public void setAbbonamentoToSave(Abbonamento abbonamentoToSave) {
		this.abbonamentoToSave = abbonamentoToSave;
	}
	public List<SpedizioneItem> getItemsToDelete() {
		return itemsToDelete;
	}
	public void setItemsToDelete(List<SpedizioneItem> itemsToDelete) {
		this.itemsToDelete = itemsToDelete;
	}
	public List<SpedizioneWithItems> getSpedizioniToSave() {
		return spedizioniToSave;
	}
	public void setSpedizioniToSave(List<SpedizioneWithItems> spedizioniToSave) {
		this.spedizioniToSave = spedizioniToSave;
	}
	public List<RivistaAbbonamento> getRivisteToDelete() {
		return rivisteToDelete;
	}

}
