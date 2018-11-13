package it.arsinfo.smd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.Mese;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Pubblicazione.Tipo;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SmdApplication {

	private static final Logger log = LoggerFactory.getLogger(SmdApplication.class);
	
	static int startabbonamento = 0;
	/*
	 * Codice Cliente (TD 674/896) si compone di 16 caratteri numerici 
	 * riservati al correntista 
	 * che intende utilizzare tale campo
	 * 2 caratteri numerici di controcodice 
	 * pari al resto della divisione 
	 * dei primi 16 caratteri per 93 
	 * (Modulo 93. Valori possibili
	 * dei caratteri di controcodice: 00 - 92)
	 */
	public static String generateCampo(Anno anno, Mese inizio, Mese fine) {
		// primi 4 caratteri anno
		String campo = anno.getAnnoAsString();
		// 5 e 6 inizio
		campo+=inizio.getCode();
		// 7 e 8 fine
		campo+=fine.getCode();
		// 9-16
		startabbonamento++;
		campo+=String.format("%08d", startabbonamento);
		campo+=String.format("%02d", Long.parseLong(campo)%93);
		return campo;
	}
	
	public static int getNumeroPubblicazioni(Abbonamento abb, Pubblicazione pub) {
		int numero = 0;
		switch (pub.getTipo()) {
			case ANNUALE: 
				if (abb.getInizio().getPosizione() < pub.getPrimaPubblicazione().getPosizione() &&
						abb.getFine().getPosizione() > pub.getPrimaPubblicazione().getPosizione()) {
					numero = 1;
				}
				break;
			case SEMESTRALE:  
				if (abb.getInizio().getPosizione() < pub.getPrimaPubblicazione().getPosizione() &&
						abb.getFine().getPosizione() > pub.getPrimaPubblicazione().getPosizione()) {
					numero += 1;
				}
				if (abb.getFine().getPosizione() > pub.getPrimaPubblicazione().getPosizione()+6) {
					numero += 1;
				}
				break;
			case MENSILE: 
				numero = abb.getFine().getPosizione() - abb.getInizio().getPosizione() + 1;
				break;
			case UNICO:
				numero=1;
				break;
			default:
				break;
		}
		return numero;
	}

	public static BigDecimal getCosto(Abbonamento abb, List<Pubblicazione> pubs) {
		if (abb.isOmaggio()) {
			return BigDecimal.ZERO;
		}
		double costo=0.0;
		if (abb.getAnagrafica().getOmaggio() != null) {
			switch (abb.getAnagrafica().getOmaggio()) {
			case AbbonatoConSconto:
				costo = pubs.stream().mapToDouble( pubblicazione -> 
					pubblicazione.getCostoScontato().doubleValue() * getNumeroPubblicazioni(abb, pubblicazione)
				).sum();
				break;
			case OmaggioCuriaDiocesiana:
				break;

			case OmaggioBlocchettiMensiliGesuiti:
				break;

			case OmaggioBlocchettiMensiliCuriaGeneralizia:
				break;

			default:
				break;
			}
		} else {
			costo = pubs.stream().mapToDouble( pubblicazione -> 
				pubblicazione.getCosto().doubleValue() * getNumeroPubblicazioni(abb, pubblicazione)
				).sum();
		}

		return BigDecimal.valueOf(costo).add(abb.getSpese());
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SmdApplication.class, args);
	}
	
	@Bean
	@Transactional
	public CommandLineRunner loadData(AnagraficaDao anagraficaDao, 
										PubblicazioneDao pubblicazioneDao,
										AbbonamentoDao abbonamentoDao, CampagnaDao campagnaDao
								) {
		return (args) -> {
			// save a couple of customers
			
			Anagrafica jb =new Anagrafica("Jack", "Bauer");
			jb.setDiocesi(Anagrafica.Diocesi.DIOCESI116);
			jb.setIndirizzo("Piazza Duomo 1");
			jb.setCitta("Milano");
			jb.setCap("20100");
			jb.setEmail("jb@adp.it");
			jb.setTelefono("+3902000009");
			anagraficaDao.save(jb);
			
			Anagrafica co = new Anagrafica("Chloe", "O'Brian");
			co.setDiocesi(Anagrafica.Diocesi.DIOCESI116);
			co.setIndirizzo("Piazza Sant'Ambrogio 1");
			co.setCitta("Milano");
			co.setCap("20110");
			co.setEmail("co@adp.it");
			co.setTelefono("+3902000010");
			anagraficaDao.save(co);
			
			Anagrafica kb = new Anagrafica("Kim", "Bauer");
			kb.setDiocesi(Anagrafica.Diocesi.DIOCESI168);
			kb.setIndirizzo("Piazza del Gesu' 1");
			kb.setCitta("Roma");
			kb.setCap("00192");
			kb.setEmail("kb@adp.it");
			kb.setTelefono("+3906000020");
			anagraficaDao.save(kb);
			
			Anagrafica dp = new Anagrafica("David", "Palmer");
			dp.setDiocesi(Anagrafica.Diocesi.DIOCESI168);
			dp.setIndirizzo("Piazza Navona 3, 00100 Roma");
			dp.setCitta("Roma");
			dp.setCap("00195");
			dp.setEmail("dp@adp.it");
			dp.setTelefono("+3906000020");
			anagraficaDao.save(dp);

			Anagrafica md = new Anagrafica("Michelle", "Dessler");
			md.setDiocesi(Anagrafica.Diocesi.DIOCESI126);
			md.setIndirizzo("Via Duomo 10");
			md.setCitta("Napoli");
			md.setCap("80135");
			md.setEmail("md@adp.it");
			md.setTelefono("+39081400022");
			anagraficaDao.save(md);
			

			Pubblicazione blocchetti = new Pubblicazione("Blocchetti", Pubblicazione.Tipo.SEMESTRALE);
			blocchetti.setActive(true);
			blocchetti.setAbbonamento(true);
			blocchetti.setCosto(new BigDecimal(3.00));
			blocchetti.setCostoScontato(new BigDecimal(2.40));
			blocchetti.setEditore("ADP");
			blocchetti.setPrimaPubblicazione(Mese.MARZO);
			pubblicazioneDao.save(blocchetti);

			Pubblicazione estratti = new Pubblicazione("Estratti", Pubblicazione.Tipo.ANNUALE);
			estratti.setActive(true);
			estratti.setAbbonamento(true);
			estratti.setCosto(new BigDecimal(10.00));
			estratti.setCostoScontato(new BigDecimal(10.00));
			estratti.setEditore("ADP");
			estratti.setPrimaPubblicazione(Mese.LUGLIO);
			pubblicazioneDao.save(estratti);

			Pubblicazione messaggio = new Pubblicazione("Messaggio", Pubblicazione.Tipo.MENSILE);
			messaggio.setActive(true);
			messaggio.setAbbonamento(true);
			messaggio.setCosto(new BigDecimal(1.25));
			messaggio.setCostoScontato(new BigDecimal(1.25));
			messaggio.setEditore("ADP");
			messaggio.setPrimaPubblicazione(Mese.GENNAIO);
			pubblicazioneDao.save(messaggio);

			Pubblicazione lodare = new Pubblicazione("Lodare e Servire", Pubblicazione.Tipo.MENSILE);
			lodare.setActive(true);
			lodare.setAbbonamento(true);
			lodare.setCosto(new BigDecimal(1.50));
			lodare.setCostoScontato(new BigDecimal(1.50));
			lodare.setEditore("ADP");
			lodare.setPrimaPubblicazione(Mese.GENNAIO);
			pubblicazioneDao.save(lodare);
						
			List<Pubblicazione> pubblMd = new ArrayList<Pubblicazione>();
			pubblMd.add(blocchetti);
			pubblMd.add(lodare);
			Abbonamento abbonamentoMd = new Abbonamento(md);
			abbonamentoMd.setBlocchetti(true);
			abbonamentoMd.setLodare(true);
			abbonamentoMd.setInizio(Mese.GIUGNO);			
			abbonamentoMd.setAnno(Anno.ANNO2018);	
			abbonamentoMd.setCampo(generateCampo(abbonamentoMd.getAnno(),abbonamentoMd.getInizio(),abbonamentoMd.getFine()));
			abbonamentoMd.setCost(getCosto(abbonamentoMd, pubblMd));
			abbonamentoDao.save(abbonamentoMd);
			
			List<Pubblicazione> pubblCo = new ArrayList<Pubblicazione>();
			pubblCo.add(blocchetti);
			pubblCo.add(lodare);
			pubblCo.add(messaggio);
			pubblCo.add(estratti);
			Abbonamento abbonamentoCo = new Abbonamento(co);
			abbonamentoCo.setBlocchetti(true);
			abbonamentoCo.setLodare(true);
			abbonamentoCo.setEstratti(true);
			abbonamentoCo.setMessaggio(true);
			abbonamentoCo.setAnno(Anno.ANNO2018);			
			abbonamentoCo.setCampo(generateCampo(abbonamentoCo.getAnno(),abbonamentoCo.getInizio(),abbonamentoCo.getFine()));
			abbonamentoCo.setCost(getCosto(abbonamentoCo, pubblCo));
			abbonamentoDao.save(abbonamentoCo);
			
			List<Pubblicazione> pubblDp = new ArrayList<Pubblicazione>();
			pubblDp.add(lodare);
			Abbonamento abbonamentoDp = new Abbonamento(dp);
			abbonamentoDp.setLodare(true);
			abbonamentoDp.setSpese(new BigDecimal("3.75"));
			abbonamentoDp.setInizio(Mese.MAGGIO);
			abbonamentoDp.setAnno(Anno.ANNO2018);			
			abbonamentoDp.setCampo(generateCampo(abbonamentoDp.getAnno(),abbonamentoDp.getInizio(),abbonamentoDp.getFine()));
			abbonamentoDp.setCost(getCosto(abbonamentoDp, pubblDp));
			abbonamentoDao.save(abbonamentoDp);
			

			// fetch all customers
			log.info("Customers found with findAll():");
			log.info("-------------------------------");
			for (Anagrafica customer : anagraficaDao.findAll()) {
				log.info(customer.toString());
			}
			log.info("");

			// fetch an individual customer by ID
			Anagrafica firstcustomer = anagraficaDao.findById(1L).get();
			log.info("Customer found with findOne(1L):");
			log.info("--------------------------------");
			log.info(firstcustomer.toString());
			log.info("");

			// fetch customers by last name
			log.info("Customer found with findByLastNameStartsWithIgnoreCase('Bauer'):");
			log.info("--------------------------------------------");
			for (Anagrafica bauer : anagraficaDao
					.findByCognomeStartsWithIgnoreCase("Bauer")) {
				log.info(bauer.toString());
			}
			log.info("");
			
			log.info("Customer found with findByDiocesi('ROMA'):");
			log.info("--------------------------------------------");
			for (Anagrafica roma : anagraficaDao
					.findByDiocesi(Anagrafica.Diocesi.DIOCESI168)) {
				log.info(roma.toString());
			}
			log.info("");
			
			// fetch all pubblicazioni
			log.info("Pubblicazioni found with findAll():");
			log.info("-------------------------------");
			for (Pubblicazione pubblicazione : pubblicazioneDao.findAll()) {
				log.info(pubblicazione.toString());
			}
			log.info("");

			// fetch an individual pubblicazione by ID
			Pubblicazione pubblicazione1 = pubblicazioneDao.findById(6L).get();
			log.info("Pubblicazione found with findOne(6L):");
			log.info("--------------------------------");
			log.info(pubblicazione1.toString());
			log.info("");

			// fetch customers by last name
			log.info("Pubblicazione found with findByNameStartsWithIgnoreCase('ADP'):");
			log.info("--------------------------------------------");
			for (Pubblicazione adp : pubblicazioneDao
					.findByNomeStartsWithIgnoreCase("ADP")) {
				log.info(adp.toString());
			}
			log.info("");
			
			log.info("Pubblicazione found with findByTipo('MENSILE'):");
			log.info("--------------------------------------------");
			for (Pubblicazione mensile : pubblicazioneDao
					.findByTipo(Tipo.MENSILE)) {
				log.info(mensile.toString());
			}
			log.info("");
			
			// fetch all customers
			log.info("Abbonamenti found with findAll():");
			log.info("-------------------------------");
			for (Abbonamento abbonamento : abbonamentoDao.findAll()) {
				log.info(abbonamento.toString());
			}
			
			log.info("Abbonamenti found with findByAnagrafica(Md):");
			log.info("-------------------------------");
			for (Abbonamento abbonamentomd : abbonamentoDao.findByAnagrafica(md)) {
				log.info(abbonamentomd.toString());
			}

			log.info("");

			

		};
	}
}
