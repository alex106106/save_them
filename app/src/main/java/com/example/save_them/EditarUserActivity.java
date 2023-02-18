package com.example.save_them;


import android.annotation.SuppressLint;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.example.crudsqlite.R;
//import com.example.crudsqlite.R;
import com.example.save_them.controllers.UsersController;
import com.example.save_them.modelos.Users;


public class EditarUserActivity extends AppCompatActivity {
    private EditText etEditarNombre, etEditarEdad;
    private Button btnGuardarCambios, btnCancelarEdicion;
    private Users users;
    private UsersController usersController;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_contacto);

        // Recuperar datos que enviaron
        Bundle extras = getIntent().getExtras();
        // Si no hay datos (cosa rara) salimos
        if (extras == null) {
            finish();
            return;
        }
        // Instanciar el controlador del contacto
        usersController = new UsersController(EditarUserActivity.this);


        long idUser = extras.getLong("idContact");
        String nombreUser = extras.getString("nameContact");
        String numeroUser = extras.getString("numberContact");
        users = new Users(nombreUser, numeroUser, idUser);


        //  declaramos las vistas
        etEditarEdad = findViewById(R.id.etEditarEdad);
        etEditarNombre = findViewById(R.id.etEditarNombre);
        btnCancelarEdicion = findViewById(R.id.btnCancelarEdicionContacto);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambiosContacto);


        // Rellenar los EditText con los datos del contacto
        etEditarEdad.setText(String.valueOf(users.getNumero()));
        etEditarNombre.setText(users.getNombre());

        // Listener del click del botón para salir, simplemente cierra la actividad
        btnCancelarEdicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Listener del click del botón que guarda cambios
        btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remover previos errores si existen
                etEditarNombre.setError(null);
                etEditarEdad.setError(null);
                // Crear el contacto con los nuevos cambios pero ponerle
                // el id de la anterior
                String nuevoNombre = etEditarNombre.getText().toString();
                String posibleNuevaEdad = etEditarEdad.getText().toString();
                if (nuevoNombre.isEmpty()) {
                    etEditarNombre.setError("Escribe el nombre");
                    etEditarNombre.requestFocus();
                    return;
                }
                if (posibleNuevaEdad.isEmpty()) {
                    etEditarEdad.setError("Escribe el numero");
                    etEditarEdad.requestFocus();
                    return;
                }
                // Si no es entero, igualmente marcar error
                String nuevoNumero;
                try {
                    nuevoNumero = String.valueOf(posibleNuevaEdad);
                } catch (NumberFormatException e) {
                    etEditarEdad.setError("Escribe un número");
                    etEditarEdad.requestFocus();
                    return;
                }
                // Si llegamos hasta aquí es porque los datos ya están validados
                Users usersConNuevosCambios = new Users(nuevoNombre, nuevoNumero, users.getId());
                int filasModificadas = usersController.guardarCambios(usersConNuevosCambios);
                if (filasModificadas != 1) {
                    // De alguna forma ocurrió un error porque se debió modificar únicamente una fila
                    Toast.makeText(EditarUserActivity.this, "Error guardando cambios. Intente de nuevo.", Toast.LENGTH_SHORT).show();
                } else {
                    // Si las cosas van bien, volvemos a la principal
                    // cerrando esta actividad
                    finish();
                }
            }
        });
    }
}
