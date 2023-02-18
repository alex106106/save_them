package com.example.save_them.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.save_them.AyudanteBaseDeDatos;
import com.example.save_them.modelos.Users;

import java.util.ArrayList;



public class UsersController {
    private AyudanteBaseDeDatos ayudanteBaseDeDatos;
    private String NOMBRE_TABLA = "contact";

    public UsersController(Context contexto) {
        ayudanteBaseDeDatos = new AyudanteBaseDeDatos(contexto);
    }


    public int eliminarUser(Users users) {

        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        String[] argumentos = {String.valueOf(users.getId())};
        return baseDeDatos.delete(NOMBRE_TABLA, "id = ?", argumentos);
    }

    public long nuevoUser(Users users) {
        // writable porque vamos a insertar
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaInsertar = new ContentValues();
        valoresParaInsertar.put("name", users.getNombre());
        valoresParaInsertar.put("number", users.getNumero());
        return baseDeDatos.insert(NOMBRE_TABLA, null, valoresParaInsertar);
    }

    public int guardarCambios(Users usersEditada) {
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getWritableDatabase();
        ContentValues valoresParaActualizar = new ContentValues();
        valoresParaActualizar.put("name", usersEditada.getNombre());
        valoresParaActualizar.put("number", usersEditada.getNumero());
        // where id...
        String campoParaActualizar = "id = ?";
        // ... = idContacto
        String[] argumentosParaActualizar = {String.valueOf(usersEditada.getId())};
        return baseDeDatos.update(NOMBRE_TABLA, valoresParaActualizar, campoParaActualizar, argumentosParaActualizar);
    }

    public ArrayList<Users> obtenerUsers() {
        ArrayList<Users> users = new ArrayList<>();
        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBaseDeDatos.getReadableDatabase();
        // SELECT nombre, edad, id
        String[] columnasAConsultar = {"name", "number", "id"};
        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA,//from contact
                columnasAConsultar,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            /*
                Salimos aquí porque hubo un error, regresar
                lista vacía
             */
            return users;

        }
        // Si no hay datos, igualmente regresamos la lista vacía
        if (!cursor.moveToFirst()) return users;

        // En caso de que sí haya, iteramos y vamos agregando los
        // datos a la lista de contactos
        do {
            // El 0 es el número de la columna, como seleccionamos
            // nombre, edad,id entonces el nombre es 0, edad 1 e id es 2
            String nombreObtenidoDeBD = cursor.getString(0);
            String edadObtenidaDeBD = cursor.getString(1);
            long idContacto = cursor.getLong(2);
            Users usersObtenidaDeBD = new Users(nombreObtenidoDeBD, edadObtenidaDeBD, idContacto);
            users.add(usersObtenidaDeBD);
        } while (cursor.moveToNext());

        // Fin del ciclo. Cerramos cursor y regresamos la lista de contactos :)
        cursor.close();
        return users;
    }
}