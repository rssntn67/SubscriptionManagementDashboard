package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class CampagnaItem
    implements SmdEntity {

        public CampagnaItem() {
    }

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;
        
        @ManyToOne
        private Campagna campagna;

        @ManyToOne
        private Pubblicazione pubblicazione;
        
        public Long getId() {
            return id;
        }

        public Campagna getCampagna() {
            return campagna;
        }

        public void setCampagna(Campagna campagna) {
            this.campagna = campagna;
        }

        public Pubblicazione getPubblicazione() {
            return pubblicazione;
        }

        public void setPubblicazione(Pubblicazione pubblicazione) {
            this.pubblicazione = pubblicazione;
        }

        public CampagnaItem(Campagna campagna) {
            this.campagna = campagna;
        }

        public CampagnaItem(Campagna campagna, Pubblicazione pubblicazione) {
            super();
            this.campagna = campagna;
            this.pubblicazione = pubblicazione;
        }


}
