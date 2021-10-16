package it.arsinfo.smd.ui.abbonamento;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.NumberRenderer;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.SmdEntity;
import it.arsinfo.smd.ui.entity.EntityGridView;

import java.util.List;

public abstract class AbbonamentoGrid extends EntityGridView<Abbonamento> {

    private Grid.Column<Abbonamento> stato;
    private Grid.Column<Abbonamento> importo;
    private Grid.Column<Abbonamento> spese;
    private Grid.Column<Abbonamento> speseEstero;
    private Grid.Column<Abbonamento> speseEstrattoConto;
    private Grid.Column<Abbonamento> pregresso;
    private Grid.Column<Abbonamento> totale;
    private Grid.Column<Abbonamento> incassato;
    private Grid.Column<Abbonamento> saldo;

    @Override
    public void init(Grid<Abbonamento> grid) {
        super.init(grid);
        configureGrid("statoAbbonamento");
        stato = grid.getColumnByKey("statoAbbonamento");
        importo= grid.addColumn(new NumberRenderer<>(Abbonamento::getImporto, SmdEntity.getEuroCurrency())).setHeader("Importo");
        spese = grid.addColumn(new NumberRenderer<>(Abbonamento::getSpese, SmdEntity.getEuroCurrency())).setHeader("Spese");
        speseEstero = grid.addColumn(new NumberRenderer<>(Abbonamento::getSpeseEstero, SmdEntity.getEuroCurrency())).setHeader("Spese Estero");
        speseEstrattoConto = grid.addColumn(new NumberRenderer<>(Abbonamento::getSpeseEstrattoConto, SmdEntity.getEuroCurrency())).setHeader("Spese EC");
        pregresso = grid.addColumn(new NumberRenderer<>(Abbonamento::getPregresso, SmdEntity.getEuroCurrency())).setHeader("Pregresso");
        totale = grid.addColumn(new NumberRenderer<>(Abbonamento::getTotale, SmdEntity.getEuroCurrency())).setHeader("Totale");
        incassato = grid.addColumn(new NumberRenderer<>(Abbonamento::getIncassato, SmdEntity.getEuroCurrency())).setHeader("Incassato");
        saldo = grid.addColumn(new NumberRenderer<>(Abbonamento::getResiduo, SmdEntity.getEuroCurrency())).setHeader("Saldo");
    }

    public void setFooter(List<Abbonamento> items) {
        stato.setFooter("Totali");
        importo.setFooter(new Html("<b>"+Abbonamento.getImporto(items).toString()+" Eur</b>"));
        spese.setFooter(new Html("<b>"+Abbonamento.getSpese(items).toString()+" Eur</b>"));
        speseEstero.setFooter(new Html("<b>"+Abbonamento.getSpeseEstero(items).toString()+" Eur</b>"));
        speseEstrattoConto.setFooter(new Html("<b>"+Abbonamento.getSpeseEstrattoConto(items).toString()+"Eur</b>"));
        pregresso.setFooter(new Html("<b>"+Abbonamento.getPregresso(items).toString()+" Eur</b>"));
        totale.setFooter(new Html("<b>"+Abbonamento.getTotale(items).toString()+" Eur</b>"));
        incassato.setFooter(new Html("<b>"+Abbonamento.getIncassato(items).toString()+" Eur</b>"));
        saldo.setFooter(new Html("<b>"+Abbonamento.getResiduo(items).toString()+" Eur</b>"));
    }
}
