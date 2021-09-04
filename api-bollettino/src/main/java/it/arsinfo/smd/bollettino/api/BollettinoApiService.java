package it.arsinfo.smd.bollettino.api;

import it.arsinfo.smd.entity.Ccp;
import it.arsinfo.smd.entity.Anagrafica;

import java.io.File;

public interface BollettinoApiService {
	
	void getBollettino(String code, Anagrafica anagrafica, Ccp ccp, String reason);

	File getFile(String code, Anagrafica anagrafica, Ccp ccp, String reason);


}
