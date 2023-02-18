package com.example.save_them.modelos;

public class Users {

    private String nombre;
    private String numero;

    private long id; // El ID de la BD

    public Users(String nombre, String numero) {
        this.nombre = nombre;
        this.numero = numero;
    }

    // Constructor para cuando instanciamos desde la BD
    public Users(String nombre, String numero, long id) {
        this.nombre = nombre;
        this.numero = numero;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + nombre + '\'' +
                ", number=" + numero +
                '}';
    }
}
