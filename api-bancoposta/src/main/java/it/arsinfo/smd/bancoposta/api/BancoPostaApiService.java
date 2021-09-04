package it.arsinfo.smd.bancoposta.api;

import it.arsinfo.smd.entity.DistintaVersamento;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public interface BancoPostaApiService {

    File getFile(String filename);

    List<DistintaVersamento> uploadIncasso(File file) throws FileNotFoundException, UnsupportedOperationException;
}
