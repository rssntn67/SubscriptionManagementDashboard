package it.arsinfo.smd;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.repository.AnagraficaDao;

import java.util.Arrays;

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
	public CommandLineRunner loadData(AnagraficaDao repository) {
		return (args) -> {
			// save a couple of customers
			
			Anagrafica jb =new Anagrafica("Jack", "Bauer");
			jb.setDiocesi(Anagrafica.Diocesi.MILANO);
			jb.setIndirizzo("Piazza Duomo 1, 00200 Milano");
			repository.save(jb);
			
			Anagrafica co = new Anagrafica("Chloe", "O'Brian");
			co.setDiocesi(Anagrafica.Diocesi.MILANO);
			co.setIndirizzo("Piazza Sant'Ambrogio 1, 00200 Milano");
			repository.save(co);
			
			Anagrafica kb = new Anagrafica("Kim", "Bauer");
			kb.setDiocesi(Anagrafica.Diocesi.ROMA);
			kb.setIndirizzo("Piazza del Gesu' 1, 00100 Roma");
			repository.save(kb);
			
			Anagrafica dp = new Anagrafica("David", "Palmer");
			dp.setDiocesi(Anagrafica.Diocesi.ROMA);
			dp.setIndirizzo("Piazza Navona 3, 00100 Roma");
			repository.save(dp);

			Anagrafica md = new Anagrafica("Michelle", "Dessler");
			md.setDiocesi(Anagrafica.Diocesi.NAPOLI);
			md.setIndirizzo("Via Duomo, 81100 Roma");
			repository.save(md);
			

			// fetch all customers
			log.info("Customers found with findAll():");
			log.info("-------------------------------");
			for (Anagrafica customer : repository.findAll()) {
				log.info(customer.toString());
			}
			log.info("");

			// fetch an individual customer by ID
			Anagrafica customer = repository.findById(1L).get();
			log.info("Customer found with findOne(1L):");
			log.info("--------------------------------");
			log.info(customer.toString());
			log.info("");

			// fetch customers by last name
			log.info("Customer found with findByLastNameStartsWithIgnoreCase('Bauer'):");
			log.info("--------------------------------------------");
			for (Anagrafica bauer : repository
					.findByCognomeStartsWithIgnoreCase("Bauer")) {
				log.info(bauer.toString());
			}
			log.info("");
			
			log.info("Customer found with findByDiocesi('ROMA'):");
			log.info("--------------------------------------------");
			for (Anagrafica roma : repository
					.findByDiocesi(Anagrafica.Diocesi.ROMA)) {
				log.info(roma.toString());
			}
			log.info("");

		};
	}
}
