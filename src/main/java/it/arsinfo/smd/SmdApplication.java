package it.arsinfo.smd;

import javax.transaction.Transactional;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Abbonamento.Mese;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Pubblicazione.Tipo;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;
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
	public static void main(String[] args) {
		SpringApplication.run(SmdApplication.class, args);
	}
	
	@Bean
	@Transactional
	public CommandLineRunner loadData(AnagraficaDao anagraficaDao, 
										PubblicazioneDao pubblicazioneDao,
										AbbonamentoDao abbonamentoDao
								) {
		return (args) -> {
			// save a couple of customers
			
			Anagrafica jb =new Anagrafica("Jack", "Bauer");
			jb.setDiocesi(Anagrafica.Diocesi.MILANO);
			jb.setIndirizzo("Piazza Duomo 1");
			jb.setCitta("Milano");
			jb.setCap("20100");
			jb.setEmail("jb@adp.it");
			jb.setTelefono("+3902000009");
			anagraficaDao.save(jb);
			
			Anagrafica co = new Anagrafica("Chloe", "O'Brian");
			co.setDiocesi(Anagrafica.Diocesi.MILANO);
			co.setIndirizzo("Piazza Sant'Ambrogio 1");
			co.setCitta("Milano");
			co.setCap("20110");
			co.setEmail("co@adp.it");
			co.setTelefono("+3902000010");
			anagraficaDao.save(co);
			
			Anagrafica kb = new Anagrafica("Kim", "Bauer");
			kb.setDiocesi(Anagrafica.Diocesi.ROMA);
			kb.setIndirizzo("Piazza del Gesu' 1");
			kb.setCitta("Roma");
			kb.setCap("00192");
			kb.setEmail("kb@adp.it");
			kb.setTelefono("+3906000020");
			anagraficaDao.save(kb);
			
			Anagrafica dp = new Anagrafica("David", "Palmer");
			dp.setDiocesi(Anagrafica.Diocesi.ROMA);
			dp.setIndirizzo("Piazza Navona 3, 00100 Roma");
			dp.setCitta("Roma");
			dp.setCap("00195");
			dp.setEmail("dp@adp.it");
			dp.setTelefono("+3906000020");
			anagraficaDao.save(dp);

			Anagrafica md = new Anagrafica("Michelle", "Dessler");
			md.setDiocesi(Anagrafica.Diocesi.NAPOLI);
			md.setIndirizzo("Via Duomo 10");
			md.setCitta("Napoli");
			md.setCap("80135");
			md.setEmail("md@adp.it");
			md.setTelefono("+39081400022");
			anagraficaDao.save(md);
			

			Pubblicazione blocchetti = new Pubblicazione("Blocchetti", Pubblicazione.Tipo.SEMESTRALE);
			blocchetti.setActive(true);
			blocchetti.setAbbonamento(true);
			blocchetti.setCosto(15.45f);
			blocchetti.setEditore("ADP");
			blocchetti.setPrimaPubblicazione(Mese.MARZO);
			pubblicazioneDao.save(blocchetti);

			Pubblicazione estratti = new Pubblicazione("Estratti", Pubblicazione.Tipo.ANNUALE);
			estratti.setActive(true);
			estratti.setAbbonamento(true);
			estratti.setCosto(5.95f);
			estratti.setEditore("ADP");
			estratti.setPrimaPubblicazione(Mese.LUGLIO);
			pubblicazioneDao.save(estratti);

			Pubblicazione messaggio = new Pubblicazione("Messaggio", Pubblicazione.Tipo.MENSILE);
			messaggio.setActive(true);
			messaggio.setAbbonamento(true);
			messaggio.setCosto(5.95f);
			messaggio.setEditore("ADP");
			messaggio.setPrimaPubblicazione(Mese.GENNAIO);
			pubblicazioneDao.save(messaggio);

			Pubblicazione lodare = new Pubblicazione("Lodare e Servire", Pubblicazione.Tipo.MENSILE);
			lodare.setActive(false);
			lodare.setAbbonamento(true);
			lodare.setCosto(5.95f);
			lodare.setEditore("ADP");
			lodare.setPrimaPubblicazione(Mese.GENNAIO);
			pubblicazioneDao.save(lodare);
			
			Pubblicazione spese = new Pubblicazione("Spese di Spedizione", Pubblicazione.Tipo.UNICO);
			spese.setActive(true);
			spese.setAbbonamento(false);
			spese.setCosto(5.95f);
			pubblicazioneDao.save(spese);

			Abbonamento abbonamentoMd = new Abbonamento(md);
			abbonamentoMd.setCampo("0003299900000");
			abbonamentoMd.setCost(10.0f);
			abbonamentoMd.setBlocchetti(true);
			abbonamentoMd.setLodare(true);
			abbonamentoMd.setInizio(Mese.GIUGNO);			
			abbonamentoDao.save(abbonamentoMd);
			
			Abbonamento abbonamentoCo = new Abbonamento(co);
			abbonamentoCo.setCampo("00032999000132");
			abbonamentoCo.setCost(20.0f);
			abbonamentoCo.setBlocchetti(true);
			abbonamentoCo.setLodare(true);
			abbonamentoCo.setEstratti(true);
			abbonamentoCo.setMessaggio(true);
			abbonamentoDao.save(abbonamentoCo);
			
			Abbonamento abbonamentoDp = new Abbonamento(dp);
			abbonamentoDp.setCampo("0003255559000132");
			abbonamentoDp.setCost(10.0f);
			abbonamentoDp.setLodare(true);
			abbonamentoDp.setCosti(true);
			abbonamentoDp.setInizio(Mese.MAGGIO);
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
					.findByDiocesi(Anagrafica.Diocesi.ROMA)) {
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
