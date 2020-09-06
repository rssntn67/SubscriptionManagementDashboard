package it.arsinfo.smd.dto;

import org.springframework.util.Assert;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.SpedizioneItem;

public class SpedizioneDto extends Indirizzo {
	private final SpedizioneItem item;
	
	public SpedizioneDto(SpedizioneItem item, Anagrafica destinatario, Anagrafica co) {
		super(destinatario, co);
		Assert.notNull(item, "spedizioneItem non deve essere null");
		this.item=item;				
	}

	public SpedizioneDto(SpedizioneItem item, Anagrafica destinatario, Anagrafica co, Anagrafica coco) {
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

}
