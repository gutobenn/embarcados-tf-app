package com.truiton.volleyexample;

public class Compra {
    private Integer id;
    private String name;

    public Compra(Integer id, String name){
        this.id = id;
        this.name = name;
    }

    public Integer getId(){
        return this.id;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
