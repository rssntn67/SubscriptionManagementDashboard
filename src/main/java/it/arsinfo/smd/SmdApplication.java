package it.arsinfo.smd;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Pubblicazione.Tipo;
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
	public CommandLineRunner loadData(AnagraficaDao anagraficaDao, PubblicazioneDao pubblicazioneDao) {
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
			

			Pubblicazione semestrale = new Pubblicazione("Adp Semestrale", Pubblicazione.Tipo.SEMESTRALE);
			semestrale.setActive(true);
			semestrale.setAbbonamento(true);
			semestrale.setCosto(15.45f);
			semestrale.setEditore("ADP");
			pubblicazioneDao.save(semestrale);

			Pubblicazione trimestrale = new Pubblicazione("Adp Trimestrale", Pubblicazione.Tipo.TRIMESTRALE);
			trimestrale.setActive(true);
			trimestrale.setAbbonamento(true);
			trimestrale.setCosto(5.95f);
			trimestrale.setEditore("ADP");
			pubblicazioneDao.save(trimestrale);

			Pubblicazione mensile1 = new Pubblicazione("Adp Messaggero", Pubblicazione.Tipo.MENSILE);
			mensile1.setActive(true);
			mensile1.setAbbonamento(true);
			mensile1.setCosto(5.95f);
			mensile1.setEditore("ADP");
			pubblicazioneDao.save(mensile1);

			Pubblicazione mensile2 = new Pubblicazione("Adp Preghiera", Pubblicazione.Tipo.MENSILE);
			mensile2.setActive(false);
			mensile2.setAbbonamento(true);
			mensile2.setCosto(5.95f);
			mensile2.setEditore("ADP");
			pubblicazioneDao.save(mensile2);
			
			Pubblicazione libro = new Pubblicazione("San Luigi Gonzaga", Pubblicazione.Tipo.UNICO);
			libro.setActive(true);
			libro.setAbbonamento(false);
			libro.setCosto(35.95f);
			libro.setEditore("ADP");
			libro.setAutore("Padre xxx S.J.");
			pubblicazioneDao.save(libro);

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
			
			

		};
	}
}
