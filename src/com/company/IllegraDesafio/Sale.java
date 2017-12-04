package com.company.IllegraDesafio;
import  java.util.List;
import  java.util.LinkedList;

/**
 * Created by mmignoni on 2017-11-11.
 */
public class Sale {
    private Integer ID;
    private String  salesmanName;

    List<SaleItem>  Items;

    /*******************************************************************************************************************
     *
     * Construtor
     *
     *******************************************************************************************************************/
    Sale(Integer ID, String salesmanName){
        this.ID           = ID;
        this.salesmanName = salesmanName;

        this.Items        = new LinkedList<SaleItem>();
    }

    /*******************************************************************************************************************
     *
     * Adiciona Item da compra
     *
     *******************************************************************************************************************/
    public void addItem(SaleItem item){
        this.Items.add(item);
    }

    /*******************************************************************************************************************
     *
     * Adiciona Lista inteira de Items
     *
     *******************************************************************************************************************/
    public void addItemList(List<SaleItem> listItem){
        this.Items = listItem;
    }

    /*******************************************************************************************************************
     *
     * Retorna preço total da venda
     *
     *******************************************************************************************************************/
    public Float getTotalPrice(){
        Float resultPrice = 0.0f;

        /////////////////////////////////////////////
        // Percorre lista de todos os itens somando seus preços
        for (SaleItem item: this.Items) {
            resultPrice += item.getPrice();
        }

        return resultPrice;
    }

    /*******************************************************************************************************************
     *
     * Retorna o Id da compra
     *
     *******************************************************************************************************************/
    public Integer getId(){
        return this.ID;
    }


}
