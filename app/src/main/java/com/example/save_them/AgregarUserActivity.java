package com.example.save_them;


import android.annotation.SuppressLint;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.save_them.controllers.UsersController;
import com.example.save_them.modelos.Users;


public class AgregarUserActivity extends AppCompatActivity {
    private Button btnAgregarContacto, btnCancelarNuevoContacto;
     EditText etNombre, etEdad;
    private UsersController usersController;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_contacto);

        // Instanciar vistas
        etNombre = findViewById(R.id.etNombre);
//        etEdad = findViewById(R.id.etEdadd);
        btnAgregarContacto = findViewById(R.id.btnAgregarContacto);
        btnCancelarNuevoContacto = findViewById(R.id.btnCancelarNuevoContacto);
        // Crear el controlador
        usersController = new UsersController(AgregarUserActivity.this);

        // Agregar listener del botón de guardar
        btnAgregarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Resetear errores a ambos
//                etNombre.setError(null);
//                etEdad.setError(null);
                etEdad = findViewById(R.id.etEdad);
                String nombre = etNombre.getText().toString();
                String numeroComoCadena = etEdad.getText().toString();

                // Ver si es un entero
                long edad;
                try {
                    edad = Long.parseLong(etEdad.getText().toString());

                } catch (NumberFormatException e) {
                    etEdad.setError("Escribe un número");
                    etEdad.requestFocus();
                    return;
                }
                // Ya pasó la validación
                Users nuevaUsers = new Users(nombre, numeroComoCadena);
                long id = usersController.nuevoUser(nuevaUsers);
                if (id == -1) {
                    // De alguna manera ocurrió un error
                    Toast.makeText(AgregarUserActivity.this, "Error al guardar. Intenta de nuevo", Toast.LENGTH_SHORT).show();
                } else {
                    // Terminar
                    finish();
                }
            }
        });

        // El de cancelar simplemente cierra la actividad
        btnCancelarNuevoContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
