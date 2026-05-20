package com.gestionStock.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LigneStockResteDto {
    private final LocalDate dateEntree;
    private final BigDecimal qteRestante;
    private final BigDecimal puEntree;
    private final BigDecimal valeurRestante;

    public LigneStockResteDto(LocalDate dateEntree, BigDecimal qteRestante, BigDecimal puEntree) {
        this.dateEntree = dateEntree;
        this.qteRestante = qteRestante;
        this.puEntree = puEntree;
        this.valeurRestante = qteRestante.multiply(puEntree);
    }

    public LocalDate getDateEntree() {
        return dateEntree;
    }

    public BigDecimal getQteRestante() {
        return qteRestante;
    }

    public BigDecimal getPuEntree() {
        return puEntree;
    }

    public BigDecimal getValeurRestante() {
        return valeurRestante;
    }
}
