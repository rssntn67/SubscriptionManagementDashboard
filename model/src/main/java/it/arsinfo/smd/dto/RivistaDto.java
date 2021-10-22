package it.arsinfo.smd.dto;

import java.util.ArrayList;
import java.util.List;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Rivista;
import it.arsinfo.smd.entity.SpedizioneItem;

public class RivistaDto {
	private final List<Rivista> rivisteToSave = new ArrayList<>();
	private final List<Rivista> rivisteToDelete = new ArrayList<>();
	private Abbonamento abbonamentoToSave;
	private List<SpedizioneItem> itemsToDelete = new ArrayList<>();
	private List<SpedizioneItemsDto> spedizioniToSave = new ArrayList<>();
	
	public List<Rivista> getRivisteToSave() {
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
	public List<SpedizioneItemsDto> getSpedizioniToSave() {
		return spedizioniToSave;
	}
	public void setSpedizioniToSave(List<SpedizioneItemsDto> spedizioniToSave) {
		this.spedizioniToSave = spedizioniToSave;
	}
	public List<Rivista> getRivisteToDelete() {
		return rivisteToDelete;
	}

}
