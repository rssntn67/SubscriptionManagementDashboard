package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Pubblicazione {

	public enum Tipo {
		UNICO,
		GIORNALIERO,
		SETTIMANALE,
		QUINDICINALE,
		MENSILE,
		BIMESTRALE,
		SEMESTRALE,
		ANNUALE;
	}
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nome;
    
    @Enumerated(EnumType.STRING)
    private Tipo tipo;
    
	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

    @Override
	public String toString() {
		return String.format("Pubblicazione[id=%d, Nome='%s', Tipo='%s']", 
				id, nome, tipo);
	}

	public Pubblicazione(String nome, Tipo tipo) {
		super();
		this.nome = nome;
		this.tipo = tipo;
	}

	public Pubblicazione(String nome) {
		super();
		this.nome = nome;
		this.tipo = Tipo.UNICO;
	}
	
	public Pubblicazione() {
		super();
	}

	
    

}
