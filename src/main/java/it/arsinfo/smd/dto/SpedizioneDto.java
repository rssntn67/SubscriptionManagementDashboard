package it.arsinfo.smd.dto;

import org.springframework.util.Assert;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.SpedizioneItem;

public class SpedizioneDto extends Indirizzo {

	public static SpedizioneDto getSpedizioneDto(SpedizioneItem item, Anagrafica destinatario) {
		return new SpedizioneDto(item, destinatario);
	}

	public static SpedizioneDto getSpedizioneDto(SpedizioneItem item, Anagrafica destinatario, Anagrafica co) {
		return new SpedizioneDto(item, destinatario, co);
	}
	
	public static SpedizioneDto getSpedizioneDto(SpedizioneItem item, Anagrafica destinatario, Anagrafica co, Anagrafica coco) {
		return new SpedizioneDto(item, destinatario, co,coco);
	}

	private final SpedizioneItem item;
	
	private String omaggio = "";

	protected SpedizioneDto(SpedizioneItem item, Anagrafica destinatario) {
		super(destinatario);
		Assert.notNull(item, "spedizioneItem non deve essere null");
		this.item=item;				
	}

	protected SpedizioneDto(SpedizioneItem item, Anagrafica destinatario, Anagrafica co) {
		super(destinatario, co);
		Assert.notNull(item, "spedizioneItem non deve essere null");
		this.item=item;				
	}

	protected SpedizioneDto(SpedizioneItem item, Anagrafica destinatario, Anagrafica co, Anagrafica coco) {
		super(destinatario, co,coco);
		Assert.notNull(item, "spedizioneItem non deve essere null");
		this.item=item;				
	}

	public Integer getNumero() {
		return item.getNumero();
	}
	
    public String getSpedCaption() {
        return item.getSpedCaption();
    }
    
    public String getPubbCaption() {
        return item.getPubbCaption();
    }

	public String getOmaggio() {
		return omaggio;
	}

	public void setOmaggio() {
		this.omaggio = "Omaggio";
	}

}
