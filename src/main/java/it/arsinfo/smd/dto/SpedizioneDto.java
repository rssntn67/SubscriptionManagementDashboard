package it.arsinfo.smd.dto;

import org.springframework.util.Assert;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;

public class SpedizioneDto extends Indirizzo {
	
	public static SpedizioneDto getSpedizioneDto(Spedizione sped,SpedizioneItem item, Anagrafica destinatario) {
		return new SpedizioneDto(sped,item, destinatario);
	}

	public static SpedizioneDto getSpedizioneDto(Spedizione sped,SpedizioneItem item, Anagrafica destinatario, Anagrafica co) {
		return new SpedizioneDto(sped,item, destinatario, co);
	}
	
	public static SpedizioneDto getSpedizioneDto(Spedizione sped,SpedizioneItem item, Anagrafica destinatario, Anagrafica co, Anagrafica coco) {
		return new SpedizioneDto(sped,item, destinatario, co,coco);
	}

	private final SpedizioneItem item;
	private final Spedizione sped;
	
	private String omaggio = "";

	protected SpedizioneDto(Spedizione sped,SpedizioneItem item, Anagrafica destinatario) {
		super(destinatario);
		Assert.notNull(sped, "spedizione non deve essere null");
		Assert.notNull(item, "spedizioneItem non deve essere null");
		this.item=item;		
		this.sped=sped;
	}

	protected SpedizioneDto(Spedizione sped,SpedizioneItem item, Anagrafica destinatario, Anagrafica co) {
		super(destinatario, co);
		Assert.notNull(sped, "spedizione non deve essere null");
		Assert.notNull(item, "spedizioneItem non deve essere null");
		this.item=item;				
		this.sped=sped;
	}

	protected SpedizioneDto(Spedizione sped,SpedizioneItem item, Anagrafica destinatario, Anagrafica co, Anagrafica coco) {
		super(destinatario, co,coco);
		Assert.notNull(sped, "spedizione non deve essere null");
		Assert.notNull(item, "spedizioneItem non deve essere null");
		this.item=item;				
		this.sped=sped;
	}

	public Integer getNumero() {
		return item.getNumero();
	}
	
    public String getSpedCaption() {
        return sped.getSpedCaption();
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
