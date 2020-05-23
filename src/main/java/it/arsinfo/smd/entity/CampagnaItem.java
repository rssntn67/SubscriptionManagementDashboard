package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
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
        
        @ManyToOne(optional=false,fetch=FetchType.EAGER)
        private Campagna campagna;

        @ManyToOne(optional=false,fetch=FetchType.EAGER)
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

        @Override
        public String toString() {
            return String.format("CampagnaItem[id=%d, %s %s]", 
                                 id, campagna, pubblicazione);
        }

		@Override
		public String getHeader() {
            return String.format("%s:%s", 
                    campagna.getHeader(), pubblicazione.getHeader());
		}

}
