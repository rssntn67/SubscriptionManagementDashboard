package it.arsinfo.smd.data;

public enum StatoAbbonamento {
    Duplicato, //la rivista in abbonamento corrisponde ad un invio duplicato
    Nuovo, // La rivista in Abbonamento è stata creata
    Proposto, //La rivista in abbonamento è stata inviato al sottoscrittore
    Valido,   //L'abbonamento è stato incassato oppure la rivista è omaggio
    ValidoConResiduo, //L'abbonamento è stato incassato con Residuo
    Sospeso, //L'abbonamento non è stato incassato o incassato parzialmente, ma non è stato inviato estratto conto 
    SospesoInviatoEC, //L'abbonamento non è stato incassato o incassato parzialmente ma l'EC è stato inviato
    ValidoInviatoEC, // L'abbonamento è stato incassato o incassato con Residuo ma l'EC è stato inviato
    Annullato // il numero totale delle riviste è 0;
}
