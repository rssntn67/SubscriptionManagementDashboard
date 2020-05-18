ALTER TABLE incasso RENAME TO distinta_versamento;
ALTER TABLE versamento RENAME COLUMN incasso_id TO distinta_versamento_id;

UPDATE versamento v 
SET committente_id = 
  (SELECT a.intestatario_id FROM operazione_incasso oi 
  LEFT JOIN abbonamento a 
  ON a.id = oi.abbonamento_id 
  WHERE oi.versamento_id = v.id 
  AND oi.stato_operazione_incasso = 'Incasso'
  limit 1) 
WHERE committente_id is null 
AND incassato > 0;
  