package com.company.IllegraDesafio;
import com.company.IllegraDesafio.Costumer;
import com.company.IllegraDesafio.Sale;
import com.company.IllegraDesafio.SaleItem;
import com.company.IllegraDesafio.Salesman;

import  java.util.List;
import  java.util.LinkedList;
import  java.util.Map;
import  java.util.HashMap;

/**
 *   Criação: Matheus Mignoni
 * Descrição: "Estrutura" que armazena um relatorio de um arquivo
 *
 */
public class MyReport {

    private String  fileName;               //Caminho completo do arquivo de report

    private List<Costumer> LstCostumers;    //Lista de todos os costumers
    private List<Salesman> LstSalesman;     //Lista de todos os salesman
    private List<Sale>     LstSales;        //Lista de todos os sales

    Map<String, Float> dictSalesman;        //Dicionário com os valres de vendas totais dos salesmans, utilizado para calcular quem vendeu quanto.


    ///////////////////////////////////////
    // Most Expensive Sale
    private Float   expensiveSalePrice;
    private Integer expensiveSaleId;


    ///////////////////////////////////////
    // Worst Salesman Ever
    private Float  worstSalesmanSalary;
    private String worstSalesmanName;
    private String worstSalesmanCPF;


    /*******************************************************************************************************************
     *
     * Construtor
     *
     *******************************************************************************************************************/
    public MyReport(String name){
        this.fileName   = name;

        this.LstCostumers = new LinkedList<Costumer>();
        this.LstSalesman  = new LinkedList<Salesman>();
        this.LstSales     = new LinkedList<Sale>();

        this.expensiveSalePrice = 0.0f;
        this.expensiveSaleId = 0;

        this.dictSalesman = new HashMap<String, Float>();
    }

    /*******************************************************************************************************************
     *
     * Adiciona salesman
     *
     *******************************************************************************************************************/
    public void addSalesman(String CPF, String name, Float salary){
        Salesman s = new Salesman(CPF, name, salary);

        //TODO: implementar um método melhor para não inserir salesman duplicados.
        if (!this.LstSalesman.contains(s))
            this.LstSalesman.add(s);
    }

    /*******************************************************************************************************************
     *
     * Adiciona costumer
     *
     *******************************************************************************************************************/
    public void addCostumer(String CNPJ, String name, String businessArea){
        Costumer c = new Costumer(CNPJ, name, businessArea);

        //TODO: implementar um método melhor para não inserir costumers duplicados.
        if (!this.LstCostumers.contains(c))
            this.LstCostumers.add(c);
    }

    /*******************************************************************************************************************
     *
     * Adiciona Venda
     *
     *******************************************************************************************************************/
    public void addSale(Integer ID, String LstItems, String SalesmanName){
        Sale s = new Sale(ID, SalesmanName);

        ////////////////////////////////////////////////
        // Faz parte da lista de itens
        List<SaleItem> itemlist = parseItemList(LstItems);
        s.addItemList( itemlist );

        ////////////////////////////////////////////////
        // Verifica se é a venda mais cara até agora
        Float salePrice = s.getTotalPrice();
        if (salePrice > this.expensiveSalePrice){
            this.expensiveSalePrice = salePrice;
            this.expensiveSaleId    = s.getId();
        }

        /////////////////////////////////////////////////
        // Adiciona valor da venda para o salesman
        if (this.dictSalesman.containsKey(SalesmanName)){
            float newPrice = this.dictSalesman.get(SalesmanName) + salePrice;
            this.dictSalesman.put(SalesmanName, newPrice);
        } else {
            this.dictSalesman.put(SalesmanName, salePrice);
        }

        //TODO: implementar busca por salesman iguais na lista.
        this.LstSales.add(s);
    }

    /*******************************************************************************************************************
     *
     * Faz parse de um item
     *
     *******************************************************************************************************************/
    public List<SaleItem> parseItemList(String itemList){

        List<SaleItem> lstResult = new LinkedList<SaleItem>();

        ///////////////////////////////////////////////////////////
        // Uma lista válida tem pelo menos 2 caracteres: "[]",
        // e começa com "[" e termina com "]"
        if ((itemList.length() >= 2) &&
            (itemList.charAt(0) == '[') &&
            (itemList.charAt(itemList.length()-1) == '[')) {

            ///////////////////////////////////////////////////////
            // Remove primeiro e últimos caracteres da lista
            String listAux = itemList.substring(1, itemList.length()-1);
            if (listAux.length() > 0){

                ///////////////////////////////////////////////////
                // Separa itens da lista pelo caractere ","
                String[] items = listAux.split(",");

                ///////////////////////////////////////////////////
                //Processa individualmente cada item da lista de itens
                for (String item: items) {
                    ///////////////////////////////////////////////////
                    // Separa valores do item pelo caractere "-"
                    String[] itemValues = item.split("-");

                    ///////////////////////////////////////////////////
                    // Espera-se ter pelo menos 3 valores em um item
                    if (itemValues.length >= 3){
                        Integer ID    = Integer.parseInt(itemValues[0]);
                        Integer qty   = Integer.parseInt(itemValues[1]);
                        Float   price = Float.parseFloat(itemValues[2]);

                        SaleItem s = new SaleItem(ID, qty, price);
                        lstResult.add(s);
                    }
                }
            }
        }

        //////////////////////////////////////////////////////////////
        // Retorna lista parseada
        return lstResult;
    }

    /*******************************************************************************************************************
     *
     * Retora o pior salesman
     *
     * O pior salesman pode ser definido como aqquele que menos vendeu, levando em consideração seu salário, o
     * número de clientes que ele atendeu e a maior abrangência de áreas diferentes (business areas)
     *
     * //TODO: implementar resto do cálculo. Não finalizado por falta de tempo
     *
     *******************************************************************************************************************/
    public String getWorstSalesmanName(){
        String name = "";
        ///////////////////////////////////////////////////////////////////////////
        // Percorre todos elementos do dicionario, para verificar quem menos vendeu
        Float lowerSell = Float.MAX_VALUE;
        for(Map.Entry<String, Float> e : this.dictSalesman.entrySet()){
            if (e.getValue() < lowerSell){
                lowerSell = e.getValue();
                name      = e.getKey();
            }
        }
        return name;
    }

    /*******************************************************************************************************************
     *
     * Retorna caminho do arquivo de saida
     *
     *******************************************************************************************************************/
    public String getFileName(){
        return this.fileName;
    }

    /*******************************************************************************************************************
     *
     * Retorna um resumo do relatório, em formato de um array de Strings
     *
     *******************************************************************************************************************/
    public String[] summaryReport(){
        String[] arrResult = new String[4];

        arrResult[0] = String.valueOf(this.LstCostumers.size());    //Amount of Clients
        arrResult[1] = String.valueOf(this.LstSalesman.size());     //Amount of Salesman:
        arrResult[2] = String.valueOf(this.expensiveSaleId);        //Most Expensive Sale ID
        arrResult[3] = String.valueOf(this.getWorstSalesmanName()); //Worst Salesman Ever

        return arrResult;
    }

}
