package com.example.save_them;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.save_them.modelos.Users;

import java.util.List;


public class AdaptadorUsers extends RecyclerView.Adapter<AdaptadorUsers.MyViewHolder> {

    private List<Users> listaDeUsers;

    public void setListaDeContactos(List<Users> listaDeUsers) {
        this.listaDeUsers = listaDeUsers;
    }

    public AdaptadorUsers(List<Users> users) {
        this.listaDeUsers = users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View filaContactos = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fila_contactos, viewGroup, false);
        return new MyViewHolder(filaContactos);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        // Obtener  de nuestra lista gracias al índice i
        Users users = listaDeUsers.get(i);

        // Obtener los datos de la lista
        String nombreUser = users.getNombre();
        String numeroUser = users.getNumero();
        // Y poner a los TextView los datos con setText
        myViewHolder.nombre.setText(nombreUser);
        myViewHolder.edad.setText(String.valueOf(numeroUser));
    }

    @Override
    public int getItemCount() {
        return listaDeUsers.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, edad;

        MyViewHolder(View itemView) {
            super(itemView);
            this.nombre = itemView.findViewById(R.id.tvNombre);
            this.edad = itemView.findViewById(R.id.tvEdad);
        }
    }
}
