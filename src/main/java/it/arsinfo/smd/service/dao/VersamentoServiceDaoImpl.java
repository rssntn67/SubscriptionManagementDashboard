package it.arsinfo.smd.service.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import it.arsinfo.smd.dao.SmdServiceDao;
import it.arsinfo.smd.dao.repository.VersamentoDao;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.service.Smd;

@Service
public class VersamentoServiceDaoImpl implements SmdServiceDao<Versamento> {

    @Autowired
    private VersamentoDao repository;

	@Override
	public Versamento save(Versamento entity) throws Exception {
		return repository.save(entity);
	}

	@Override
	public void delete(Versamento entity) throws Exception {
		repository.delete(entity);
	}

	@Override
	public Versamento findById(Long id) {
		return repository.findById(id).get();
	}

	@Override
	public List<Versamento> findAll() {
		return repository.findAll();
	}

	public VersamentoDao getRepository() {
		return repository;
	}

	public List<Versamento> searchBy(String importo, LocalDate dataContabile, LocalDate dataPagamento,
			String codeLine) {
        if (StringUtils.isEmpty(importo) && dataContabile == null
                && dataPagamento == null && StringUtils.isEmpty(codeLine)) {
            return findAll();
        }
        if (!StringUtils.isEmpty(importo)) {
            try {
                new BigDecimal(importo);
            } catch (NumberFormatException e) {
                return new ArrayList<>();
            }
        }
         
        if (dataContabile == null && dataPagamento == null && StringUtils.isEmpty(codeLine)) {
                return repository
                    .findByImporto(new BigDecimal(importo));
        }

        if (dataContabile == null && dataPagamento == null && StringUtils.isEmpty(importo)) {
            return repository
                    .findByCodeLineContainingIgnoreCase(codeLine);
        }

        if (StringUtils.isEmpty(importo) && dataPagamento == null && StringUtils.isEmpty(codeLine)) {
            return repository
                    .findByDataContabile(Smd.getStandardDate(dataContabile));
        }

        if (StringUtils.isEmpty(importo) && dataContabile == null && StringUtils.isEmpty(codeLine)) {
            return repository
                    .findByDataPagamento(Smd.getStandardDate(dataPagamento));
        }

        if (dataContabile == null && dataPagamento == null) {
            return repository
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v-> v.getImporto().compareTo(new BigDecimal(importo)) == 0)
                    .collect(Collectors.toList());
        }


        if (dataContabile == null && StringUtils.isEmpty(codeLine)) {
            return repository
                    .findByImporto(new BigDecimal(importo))
                    .stream()
                    .filter(v -> v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime())
                    .collect(Collectors.toList());
        }

        if (dataPagamento == null && StringUtils.isEmpty(codeLine)) {
            return repository
                    .findByImporto(new BigDecimal(importo))
                    .stream()
                    .filter(v -> v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime())
                    .collect(Collectors.toList());
        }

        if (dataContabile == null && StringUtils.isEmpty(importo)) {
            return repository
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v -> v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime())
                    .collect(Collectors.toList());
        }

        if (dataPagamento == null && StringUtils.isEmpty(importo)) {
            return repository
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v -> v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime())
                    .collect(Collectors.toList());
        }

        if (StringUtils.isEmpty(codeLine) && StringUtils.isEmpty(importo)) {
            return repository
                    .findByDataPagamento(Smd.getStandardDate(dataPagamento))
                    .stream()
                    .filter(v -> v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime())
                    .collect(Collectors.toList());
        }

        if (StringUtils.isEmpty(codeLine)) {
            return repository
                    .findByImporto(new BigDecimal(importo))
                    .stream()
                    .filter(v -> 
                       v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime()
                    && v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime())
                    .collect(Collectors.toList());
        }

        if (StringUtils.isEmpty(importo)) {
            return repository
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v -> 
                       v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime()
                    && v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime())
                    .collect(Collectors.toList());
        }

        if (dataPagamento == null) {
            return repository
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v -> 
                       v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime()
                    && v.getImporto().compareTo(new BigDecimal(importo)) == 0 )
                    .collect(Collectors.toList());
            
        }
        if (dataContabile == null) {
            return repository
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v -> 
                       v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime()
                    && v.getImporto().compareTo(new BigDecimal(importo)) == 0 )
                    .collect(Collectors.toList());
            
        }
        return repository
                .findByCodeLineContainingIgnoreCase(codeLine)
                .stream()
                .filter(v -> 
                   v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime()
                && v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime()
                && v.getImporto().compareTo(new BigDecimal(importo)) == 0 )
                .collect(Collectors.toList());
	}
	
}
