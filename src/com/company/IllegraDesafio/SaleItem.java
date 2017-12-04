package com.company.IllegraDesafio;

/**
 * Created by mmignoni on 2017-11-11.
 */
public class SaleItem {
    private Integer ID;
    private Integer quantity;
    private Float   price;

    /*******************************************************************************************************************
     *
     * Construtor
     *
     *******************************************************************************************************************/
    SaleItem(Integer ID, Integer quantity, Float price){
        this.ID        = ID;
        this.quantity  = quantity;
        this.price     = price;

    }

    /*******************************************************************************************************************
     *
     * Retorna preço do item
     *
     *******************************************************************************************************************/
    public Float getPrice(){
        return this.price;
    }
}
