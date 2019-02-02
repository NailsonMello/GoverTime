package lass.govertime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListaPresidente extends Fragment {
    private RecyclerView listaPresidentes;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_lista_presidente, container, false);
        DatabaseUtil.getDatabase();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Presidentes");
        listaPresidentes = (RecyclerView) view.findViewById(R.id.listaPresidentes);
        listaPresidentes.setHasFixedSize(true);
        listaPresidentes.setLayoutManager(new LinearLayoutManager(getActivity()));
        carregar();

        return view;
    }

    private void carregar(){
        FirebaseRecyclerAdapter<Pessoa, PresidenteViewHolder> firebaseRecyclerAdapter  = new FirebaseRecyclerAdapter<Pessoa, PresidenteViewHolder>
                (
                        Pessoa.class,
                        R.layout.item_presidentes,
                        ListaPresidente.PresidenteViewHolder.class,
                        mDatabase

                ) {
            @Override
            public void populateViewHolder(ListaPresidente.PresidenteViewHolder viewHolder, Pessoa model, int position) {

                final String chave_presidente = getRef(position).getKey();
                viewHolder.setNome(model.getNome());
                viewHolder.setImagem(getActivity(), model.getImagem());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent telaPres = new Intent(getActivity(), PerfilPoliticoAntigo.class);
                        telaPres.putExtra("anuncio_id", chave_presidente);
                        startActivity(telaPres);
                    }
                });
            }
        };
        listaPresidentes.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PresidenteViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public PresidenteViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;

        }
        public void setImagem(Context context, String imagem){

            CircleImageView imagem_anuncio = (CircleImageView)mView.findViewById(R.id.imagem_anuncio);
            Picasso.with(context).load(imagem).placeholder(R.drawable.default_avatar).into(imagem_anuncio);

        }
        public void setNome(String nome){

            TextView textView = (TextView)mView.findViewById(R.id.idNome);
            textView.setText(nome);

        }

    }
}
