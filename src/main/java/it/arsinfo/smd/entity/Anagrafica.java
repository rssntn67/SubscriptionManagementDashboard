package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Anagrafica {
	
	public enum Diocesi {
		DIOCESI000("000 | Estero"),
		DIOCESI001("001 | Acerenza"),
		DIOCESI002("002 | Acerra"),
		DIOCESI003("003 | Acireale"),
		DIOCESI004("004 | Acqui"),
		DIOCESI005("005 | Adria-Rovigo"),
		DIOCESI006("006 | Agrigento"),
		DIOCESI007("007 | Alba"),
		DIOCESI008("008 | Albano"),
		DIOCESI009("009 | Albenga-Imperia"),
		DIOCESI010("010 | Ales-Terralba"),
		DIOCESI011("011 | Alessandria"),
		DIOCESI012("012 | Alghero-Bosa"),
		DIOCESI013("013 | Alife-Caiazzo"),
		DIOCESI014("014 | Altamura-Gravina-Acquaviva Fonti"),
		DIOCESI015("015 | Amalfi-Cava dei Tirreni"),
		DIOCESI016("016 | Anagni - Alatri"),
		DIOCESI017("017 | Ancona - Osimo"),
		DIOCESI018("018 | Andria"),
		DIOCESI019("019 | Aosta"),
		DIOCESI020("020 | Arezzo - Cortona -San Sepolcro"),
		DIOCESI021("021 | Ariano Irpino - Lacedonia"),
		DIOCESI022("022 | Ascoli Piceno"),
		DIOCESI023("023 | Assisi- Nocera Umbra -Gualdo Tadino"),
		DIOCESI024("024 | Asti"),
		DIOCESI025("025 | Avellino"),
		DIOCESI026("026 | Aversa"),
		DIOCESI027("027 | Avezzano"),
		DIOCESI028("028 | Bari - Bitonto"),
		DIOCESI029("029 | Belluno - Feltre"),
		DIOCESI030("030 | Benevento"),
		DIOCESI031("031 | Bergamo"),
		DIOCESI032("032 | Biella"),
		DIOCESI033("033 | Bologna"),
		DIOCESI034("034 | Bolzano - Bressanone"),
		DIOCESI035("035 | Brescia"),
		DIOCESI036("036 | Brindisi - Ostuni"),
		DIOCESI037("037 | Cagliari"),
		DIOCESI038("038 | Caltagirone"),
		DIOCESI039("039 | Caltanissetta"),
		DIOCESI040("040 | Camerino - San Severino Marche"),
		DIOCESI041("041 | Campobasso - Boiano"),
		DIOCESI042("042 | Capua"),
		DIOCESI043("043 | Carpi"),
		DIOCESI044("044 | Casale Monferrato"),
		DIOCESI045("045 | Caserta"),
		DIOCESI046("046 | Cassano allo Jonio"),
		DIOCESI047("047 | Castellaneta"),
		DIOCESI048("048 | Catania"),
		DIOCESI049("049 | Catanzaro - Squillace"),
		DIOCESI050("050 | Cefalù"),
		DIOCESI051("051 | Cerignola - Ascoli Satriano"),
		DIOCESI052("052 | Cerreto Sannita -Telese- Sant'Agata de' Goti"),
		DIOCESI053("053 | Cesena - Sarsina"),
		DIOCESI054("054 | Chiavari"),
		DIOCESI055("055 | Chieti- Vasto"),
		DIOCESI056("056 | Chioggia"),
		DIOCESI057("057 | Città di Castello"),
		DIOCESI058("058 | Civitacastellana"),
		DIOCESI059("059 | Civitavecchia - Tarquinia"),
		DIOCESI060("060 | Como"),
		DIOCESI061("061 | Concordia - Pordenone"),
		DIOCESI062("062 | Conversano -Monopoli"),
		DIOCESI063("063 | Cosenza - Bisignano"),
		DIOCESI064("064 | Crema"),
		DIOCESI065("065 | Cremona"),
		DIOCESI066("066 | Crotone - Santa Severina"),
		DIOCESI067("067 | Cuneo"),
		DIOCESI068("068 | Fabriano - Matelica"),
		DIOCESI069("069 | Faenza - Modigliana"),
		DIOCESI070("070 | Fano - Fossombrone - Cagli - Pergola"),
		DIOCESI071("071 | Ferno"),
		DIOCESI072("072 | Ferrara - Comacchio"),
		DIOCESI073("073 | Fidenza"),
		DIOCESI074("074 | Fiesole"),
		DIOCESI075("075 | Firenze"),
		DIOCESI076("076 | Foggia - Bovino"),
		DIOCESI077("077 | Foligno"),
		DIOCESI078("078 | Forlì - Bertinoro"),
		DIOCESI079("079 | Fossano"),
		DIOCESI080("080 | Frascati"),
		DIOCESI081("081 | Frosinone-Veroli-Ferentino"),
		DIOCESI082("082 | Gaeta"),
		DIOCESI083("083 | Genova"),
		DIOCESI084("084 | Gorizia"),
		DIOCESI085("085 | Grosseto"),
		DIOCESI086("086 | Gubbio"),
		DIOCESI087("087 | Iglesias"),
		DIOCESI088("088 | Imola"),
		DIOCESI089("089 | Ischia"),
		DIOCESI090("090 | Isernia - Venafro"),
		DIOCESI091("091 | Ivrea"),
		DIOCESI092("092 | Jesi"),
		DIOCESI093("093 | Lamezia Terme"),
		DIOCESI094("094 | Lanciano - Ortona"),
		DIOCESI095("095 | Lanusei"),
		DIOCESI096("096 | L'Aquila"),
		DIOCESI097("097 | La Spezia -Sarzana -Brugnato"),
		DIOCESI098("098 | Latina-Terracina-Sezze-Priverno"),
		DIOCESI099("099 | Lecce"),
		DIOCESI100("100 | Livorno"),
		DIOCESI101("101 | Locri - Gerace"),
		DIOCESI102("102 | Lodi"),
		DIOCESI103("103 | Loreto"),
		DIOCESI104("104 | Lucca"),
		DIOCESI105("105 | Lucera - Troia"),
		DIOCESI106("106 | Lungro"),
		DIOCESI107("107 | Macerata-Tolentino-Recanati-Cingoli-Treia"),
		DIOCESI108("108 | Manfredonia-Vieste-S.Giovanni Rotondo"),
		DIOCESI109("109 | Mantova"),
		DIOCESI110("110 | Massa Carrara - Pontremoli"),
		DIOCESI111("111 | Massa Marittima - Piombino"),
		DIOCESI112("112 | Matera - Irsina"),
		DIOCESI113("113 | Mazara del Vallo"),
		DIOCESI114("114 | Melfi-Rapolla-Venosa"),
		DIOCESI115("115 | Messina-Lipari-Santa Lucia del Mela"),
		DIOCESI116("116 | Milano"),
		DIOCESI117("117 | Mileto-Nicotera-Tropea"),
		DIOCESI118("118 | Modena-Nonantola"),
		DIOCESI119("119 | Molfetta-Ruvo-Giovinazzo-Terlizzi"),
		DIOCESI120("120 | Mondovì"),
		DIOCESI121("121 | Monreale"),
		DIOCESI122("122 | Montecassino"),
		DIOCESI123("123 | Monte Oliveto Maggiore"),
		DIOCESI124("124 | Montepulciano - Chiusi- Pienza"),
		DIOCESI125("125 | Montevergine"),
		DIOCESI126("126 | Napoli"),
		DIOCESI127("127 | Nardò - Gallipoli"),
		DIOCESI128("128 | Nicosia"),
		DIOCESI129("129 | Nocera Inferiore - Sarno"),
		DIOCESI130("130 | Nola"),
		DIOCESI131("131 | Noto"),
		DIOCESI132("132 | Novara"),
		DIOCESI133("133 | Nuoro"),
		DIOCESI134("134 | Oppido Mamertina-Palmi"),
		DIOCESI135("135 | Oria"),
		DIOCESI136("136 | Oristano"),
		DIOCESI137("137 | Orvieto - Todi"),
		DIOCESI138("138 | Ostia"),
		DIOCESI139("139 | Otranto"),
		DIOCESI140("140 | Ozieri"),
		DIOCESI141("141 | Padova"),
		DIOCESI142("142 | Palermo"),
		DIOCESI143("143 | Palestrina"),
		DIOCESI144("144 | Parma"),
		DIOCESI145("145 | Patti"),
		DIOCESI146("146 | Pavia"),
		DIOCESI147("147 | Perugia - Città della Pieve"),
		DIOCESI148("148 | Pesaro"),
		DIOCESI149("149 | Pescara - Penne"),
		DIOCESI150("150 | Pescia"),
		DIOCESI151("151 | Piacenza - Bobbio"),
		DIOCESI152("152 | Piana degli Albanesi"),
		DIOCESI153("153 | Piazza Armerina"),
		DIOCESI154("154 | Pinerolo"),
		DIOCESI155("155 | Pisa"),
		DIOCESI156("156 | Pistoia"),
		DIOCESI157("157 | Pompei o Beata Vergine Maria del S.Rosario"),
		DIOCESI158("158 | Porto -S.Rufina"),
		DIOCESI159("159 | Potenza-Muro Lucano-Marsico Nuovo"),
		DIOCESI160("160 | Pozzuoli"),
		DIOCESI161("161 | Prato"),
		DIOCESI162("162 | Ragusa"),
		DIOCESI163("163 | Ravenna - Cervia"),
		DIOCESI164("164 | Reggio Calabria - Bova"),
		DIOCESI165("165 | Reggio Emilia - Guastalla"),
		DIOCESI166("166 | Rieti"),
		DIOCESI167("167 | Rimini"),
		DIOCESI168("168 | Roma"),
		DIOCESI169("169 | Rossano - Cariati"),
		DIOCESI170("170 | Sabina - Poggio Mirteto"),
		DIOCESI171("171 | Salerno-Campagna-Acerno"),
		DIOCESI172("172 | Saluzzo"),
		DIOCESI173("173 | S.Benedetto d/Tronto,Ripatransone,Montalto"),
		DIOCESI174("174 | S.Marco Argentano - Scalea"),
		DIOCESI175("175 | S.Marino - Montefeltro"),
		DIOCESI176("176 | S.Miniato"),
		DIOCESI177("177 | S.Paolo fuori le Mura (soppressa)"),
		DIOCESI178("178 | S.Severo"),
		DIOCESI179("179 | S.Maria di Grottaferrata"),
		DIOCESI180("180 | Sant'Angelo d.Lombardi-Conza-Nusco-Bisaccia"),
		DIOCESI181("181 | SS.ma Trinità di Cava de' Tirreni"),
		DIOCESI182("182 | Sassari"),
		DIOCESI183("183 | Savona - Noli"),
		DIOCESI184("184 | Senigallia"),
		DIOCESI185("185 | Sessa Aurunca"),
		DIOCESI186("186 | Siena - Colle di Val D'Elsa-Montalcino"),
		DIOCESI187("187 | Siracusa"),
		DIOCESI188("188 | Subiaco"),
		DIOCESI189("189 | Sulmona - Valva"),
		DIOCESI190("190 | Susa"),
		DIOCESI191("191 | Taranto"),
		DIOCESI192("192 | Teano - Calvi"),
		DIOCESI193("193 | Teggiano - Policastro"),
		DIOCESI194("194 | Tempio - Ampurias"),
		DIOCESI195("195 | Teramo - Atri"),
		DIOCESI196("196 | Termoli - Larino"),
		DIOCESI197("197 | Terni - Narni - Amelia"),
		DIOCESI198("198 | Tivoli"),
		DIOCESI199("199 | Torino"),
		DIOCESI200("200 | Tortona"),
		DIOCESI201("201 | Trani - Barletta - Bisceglie"),
		DIOCESI202("202 | Trapani"),
		DIOCESI203("203 | Trento"),
		DIOCESI204("204 | Treviso"),
		DIOCESI205("205 | Tricarico"),
		DIOCESI206("206 | Trieste"),
		DIOCESI207("207 | Trivento"),
		DIOCESI208("208 | Tursi - Lagonegro"),
		DIOCESI209("209 | Udine"),
		DIOCESI210("210 | Ugento - S.Maria di Leuca"),
		DIOCESI211("211 | Urbino -Urbania - S.Angelo in Vado"),
		DIOCESI212("212 | Vallo della Lucania"),
		DIOCESI213("213 | Velletri - Segni"),
		DIOCESI214("214 | Venezia"),
		DIOCESI215("215 | Ventimiglia - S.Remo"),
		DIOCESI216("216 | Vercelli"),
		DIOCESI217("217 | Verona"),
		DIOCESI218("218 | Vicenza"),
		DIOCESI219("219 | Vigevano"),
		DIOCESI220("220 | Viterbo"),
		DIOCESI221("221 | Vittorio Veneto"),
		DIOCESI222("222 | Volterra"),
		DIOCESI223("223 | Sora-Aquino-Pontecorvo"),
		DIOCESI224("224 | Pitigliano -Sovana  -Orbetello"),
		DIOCESI225("225 | Spoleto - Norcia"),
		DIOCESI226("226 | Sorrento - Castellamare di Stabia");
		
		private String details;

		public String getDetails() {
			return details;
		}
		
		Diocesi(String details) {
			this.details=details;
		}
		
	}

	public enum Paese {
		ITALIA,
		VATICANO,
		SANMARINO,
		ESTERO;
	}

	public enum CentroDiocesano {
		CentroDiocesanoBergamo, //020 | CENTRO DIOCESANO BERGAMO
		CentroDiocesanoRoma; //00011 | CENTRO DIOCESANO ROMA
	}
	
	public enum BmCassa {
		CcpBmCassa, //200 | CCP BM CASSA 
		contrassegnoBmCassa; //201 | CONTRASSEGNO BM CASSA
	}

	public enum Regione {
		ABRUZZO,
		BASILICATA,
		CALABRIA,
		CAMPANIA,
		EMILIAROMAGNA,
		LAZIO,
		LIGURIA,
		LOMBARDIA,
		MARCHE,
		MOLISE,
		PIEMONTEVALLEDAOSTA,
		PUGLIA,
		SARDEGNA,
		SICILIA,
		TOSCANA,
		TRIVENETO,
		UMBRIA;
	}

	public enum Titolo {
		Signor,
		Signora,
		Dottore,
		Spett,
		Vescovo,
		VescovoAusiliare,
		Cardinale;
	}

	//143 | RIVISTE OMAGGIO CURIA GENERALIZIA -> Tutte in omaggio --> Questo omaggio, ma mettiamo anche la categoria curia generalizia
	//9 | OMAGGIO GESUITI -> Solo Messaggio li prepara Sandro e li mette in portineria
	// quindi non vanno messi dallo spedizionere
	//OmaggioCuriaDiocesiana va specificato

	public enum Omaggio {
		OmaggioBlocchettiMensiliCuriaGeneralizia,
		OmaggioBlocchettiMensiliGesuiti,
		OmaggioCuriaDiocesiana,
		AbbonatoConSconto;
	}
	

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Diocesi diocesi;
	@Enumerated(EnumType.STRING)
    private Regione regioneVescovi;
    @Enumerated(EnumType.STRING)
    private CentroDiocesano centroDiocesano;

	private Titolo titolo;   
    private String nome;
    private String cognome;
	private String intestazione;
    
	private String indirizzo;
	private String cap;
	private String citta;
	@Enumerated(EnumType.STRING)
	private Paese paese;

	private String email;
	private String telefono;
	private String cellulare;
	private String note;
    
    private String codfis;
	private String piva;

	@Enumerated(EnumType.STRING)
	private Omaggio omaggio; 
	@Enumerated(EnumType.STRING)
	private BmCassa bmCassa;

	
	// Aggiornamento manuale
	private boolean consiglioNazionaleADP; //10 | CONSIGLIO NAZIONALE A.D.P.
	private boolean presidenzaADP; //49 | CONSIGLIO PRESIDENZA ADP
	private boolean direzioneADP; //15 | MEMBRI DIREZIONE ADP
	private boolean caricheSocialiADP; //141 | CARICHE SOCIALI E RAPPRESENTANTI
	private boolean delegatiRegionaliADP; //140 | DELEGATI REGIONALI

	// se e' vescovo	non invio ma utilizzo gli invii per le Diocesi
	//66 | CARDINALI // serve per le lettere, nella corrispondenza deve comparire eminenza vescovi che sono anche cardinali
	//In Anagrafica la Curia Diocesana
	// Viene usata soprattutto per l'invio omaggio del messaggio ai vescovi
	// Bisogna mettere il nome del Vescovo
	// dal sito chiesacattolicaitaliana.it
	// I controlli vanno fatti prima di ogni invio delle riviste	
	// Logica implementativa Associare All'indirizzo della diocesi
	// l'Id della Diocesi.
	// Pertanto invio al Vescovo della diocesi avendo aggiornato il Vescovo.
			
	private boolean presidenteDiocesano;//52 | Presidenti e Referenti DIOCESANI    
	private boolean direttoreDiocesiano;//1 | DIRETTORE DIOCESANO	
	private boolean direttoreZonaMilano;//00013 | DIRETTORI ZONE MILANO	
	private boolean elencoMarisaBisi; //144 | MARISA BISI ELENCO
	private boolean promotoreRegionale; //12 | PROMOTORI REGIONALI

	@Enumerated(EnumType.STRING)
	private Regione regionePresidenteDiocesano;
    
	@Enumerated(EnumType.STRING)
    private Regione regioneDirettoreDiocesano;

    	
	public Anagrafica(String nome, String cognome) {
    	this.nome=nome;
    	this.cognome=cognome;
    	this.paese=Paese.ITALIA;
	}

	public Anagrafica() {
		this.cognome="";
    	this.paese=Paese.ITALIA;
	}

	public Long getId() {
		return id;
	}

    public Diocesi getDiocesi() {
		return diocesi;
	}

	public void setDiocesi(Diocesi diocesi) {
		this.diocesi = diocesi;
	}


	public String getNome() {
		return nome;
	}
	
    public void setNome(String nome) {
		this.nome = nome;
	}
	
    public String getCognome() {
		return cognome;
	}
	
    public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	
    public String getIndirizzo() {
		return indirizzo;
	}
	
    public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}
    
    @Override
	public String toString() {
		return String.format("Anagrafica[id=%d, Nome='%s', Cognome='%s', Diocesi='%s']", 
				id, nome, cognome, diocesi);
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getCitta() {
		return citta;
	}

	public void setCitta(String citta) {
		this.citta = citta;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getCellulare() {
		return cellulare;
	}

	public void setCellulare(String cellulare) {
		this.cellulare = cellulare;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Omaggio getOmaggio() {
		return omaggio;
	}

	public void setOmaggio(Omaggio omaggio) {
		this.omaggio = omaggio;
	}

	public Paese getPaese() {
		return paese;
	}

	public void setPaese(Paese paese) {
		this.paese = paese;
	}

	public String getCodfis() {
		return codfis;
	}

	public void setCodfis(String codfis) {
		this.codfis = codfis;
	}

	public String getPiva() {
		return piva;
	}

	public void setPiva(String piva) {
		this.piva = piva;
	}

	public CentroDiocesano getCentroDiocesano() {
		return centroDiocesano;
	}

	public void setCentroDiocesano(CentroDiocesano centroDiocesano) {
		this.centroDiocesano = centroDiocesano;
	}

	public BmCassa getBmCassa() {
		return bmCassa;
	}

	public void setBmCassa(BmCassa bmCassa) {
		this.bmCassa = bmCassa;
	}

	public boolean isConsiglioNazionaleADP() {
		return consiglioNazionaleADP;
	}

	public void setConsiglioNazionaleADP(boolean consiglioNazionaleADP) {
		this.consiglioNazionaleADP = consiglioNazionaleADP;
	}

	public boolean isPresidenzaADP() {
		return presidenzaADP;
	}

	public void setPresidenzaADP(boolean presidenzaADP) {
		this.presidenzaADP = presidenzaADP;
	}

	public boolean isDirezioneADP() {
		return direzioneADP;
	}

	public void setDirezioneADP(boolean direzioneADP) {
		this.direzioneADP = direzioneADP;
	}

	public boolean isCaricheSocialiADP() {
		return caricheSocialiADP;
	}

	public void setCaricheSocialiADP(boolean caricheSocialiADP) {
		this.caricheSocialiADP = caricheSocialiADP;
	}

	public boolean isDelegatiRegionaliADP() {
		return delegatiRegionaliADP;
	}

	public void setDelegatiRegionaliADP(boolean delegatiRegionaliADP) {
		this.delegatiRegionaliADP = delegatiRegionaliADP;
	}

	public Regione getRegioneVescovi() {
		return regioneVescovi;
	}

	public void setRegioneVescovi(Regione regioneVescovi) {
		this.regioneVescovi = regioneVescovi;
	}

	public boolean isDirettoreZonaMilano() {
		return direttoreZonaMilano;
	}

	public void setDirettoreZonaMilano(boolean direttoreZonaMilano) {
		this.direttoreZonaMilano = direttoreZonaMilano;
	}

	public boolean isPresidenteDiocesano() {
		return presidenteDiocesano;
	}

	public void setPresidenteDiocesano(boolean presidenteDiocesano) {
		this.presidenteDiocesano = presidenteDiocesano;
	}

	public Regione getRegionePresidenteDiocesano() {
		return regionePresidenteDiocesano;
	}

	public void setRegionePresidenteDiocesano(
			Regione regionePresidenteDiocesano) {
		this.regionePresidenteDiocesano = regionePresidenteDiocesano;
	}

	public boolean isDirettoreDiocesiano() {
		return direttoreDiocesiano;
	}

	public void setDirettoreDiocesiano(boolean direttoreDiocesiano) {
		this.direttoreDiocesiano = direttoreDiocesiano;
	}

	public Regione getRegioneDirettoreDiocesano() {
		return regioneDirettoreDiocesano;
	}

	public void setRegioneDirettoreDiocesano(
			Regione regioneDirettoreDiocesano) {
		this.regioneDirettoreDiocesano = regioneDirettoreDiocesano;
	}

	public boolean isElencoMarisaBisi() {
		return elencoMarisaBisi;
	}

	public void setElencoMarisaBisi(boolean elencoMarisaBisi) {
		this.elencoMarisaBisi = elencoMarisaBisi;
	}

	public boolean isPromotoreRegionale() {
		return promotoreRegionale;
	}

	public void setPromotoreRegionale(boolean promotoreRegionale) {
		this.promotoreRegionale = promotoreRegionale;
	}

	public Titolo getTitolo() {
		return titolo;
	}

	public void setTitolo(Titolo titolo) {
		this.titolo = titolo;
	}

	public String getIntestazione() {
		return intestazione;
	}

	public void setIntestazione(String intestazione) {
		this.intestazione = intestazione;
	}
	
	@Transient
	public String getCaption() {
		return this.nome + " " +this.cognome; 
	}
}
