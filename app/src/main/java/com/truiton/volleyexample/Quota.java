package com.truiton.volleyexample;

public class Quota{
    private Integer id;
    private String name;
    private Integer quantity;

    public Quota(Integer id, String name, Integer quantity){
        this.id = id;
        this.name = name;
        this.quantity= quantity;
    }

    public Integer getId(){
        return this.id;
    }

    @Override
    public String toString() {
        return quantity.toString() + " cota(s) - " + this.name;
    }

}
