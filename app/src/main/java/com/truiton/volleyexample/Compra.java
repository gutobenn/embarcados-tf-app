package com.truiton.volleyexample;

public class Compra implements Comparable<Compra>{
    private Integer id;
    private String name;
    private float distance;

    public Compra(Integer id, String name, float distance){
        this.id = id;
        this.name = name;
        this.distance = distance;
    }

    public Integer getId(){
        return this.id;
    }

    public float getDistance(){
        return this.distance;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int compareTo(Compra outraCompra) {
        if (this.distance > outraCompra.getDistance()) {
            return 1;
        }
        if (this.distance < outraCompra.getDistance()) {
            return -1;
        }
        return 0;
    }
}
