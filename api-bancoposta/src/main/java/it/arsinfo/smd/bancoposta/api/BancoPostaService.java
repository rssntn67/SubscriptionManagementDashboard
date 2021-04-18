package it.arsinfo.smd.bancoposta.api;

import it.arsinfo.smd.config.CcpConfig;
import it.arsinfo.smd.entity.DistintaVersamento;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public interface BancoPostaService {

    File getFile(CcpConfig ccpconfig, String filename);

    List<DistintaVersamento> uploadIncasso(File file) throws FileNotFoundException, UnsupportedOperationException;
}
