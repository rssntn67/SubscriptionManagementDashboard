package it.arsinfo.smd.bollettino.api;

import it.arsinfo.smd.config.CcpConfig;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.entity.Anagrafica;

import java.io.File;

public interface BollettinoService {
	
	void getBollettino(String code, Anagrafica anagrafica, Ccp ccp, String reason);

	File getFile(String code, Anagrafica anagrafica, Ccp ccp, String reason);


}
