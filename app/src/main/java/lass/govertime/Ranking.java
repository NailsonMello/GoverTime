package lass.govertime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by L.A.S.S on 23/05/2018.
 */

public class Ranking extends Fragment {
    private FirebaseAuth mAuth;
    private RecyclerView listaRancking;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mRancking;
    private DatabaseReference mSeguir;
    private boolean clickVotar = false;
    private boolean clickSeguir = false;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_inicio_presidentes, container, false);
        DatabaseUtil.getDatabase();
        mAuth = FirebaseAuth.getInstance();
        listaRancking = (RecyclerView) view.findViewById(R.id.listaRancking);
        listaRancking.setHasFixedSize(true);
        LinearLayoutManager ln = new LinearLayoutManager(getActivity());
        ln.setReverseLayout(true);
        ln.setStackFromEnd(true);
        listaRancking.setLayoutManager(ln);
        carregar();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void carregar(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Eleicao");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Voto");
        mRancking = FirebaseDatabase.getInstance().getReference().child("Rancking");
        mSeguir = FirebaseDatabase.getInstance().getReference().child("Favorito");
        mRancking.keepSynced(true);
        mSeguir.keepSynced(true);
        mDatabaseUser.keepSynced(true);

        Query query = mDatabase.orderByChild("voto");
        final FirebaseRecyclerAdapter<Pessoa, PresidenteViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pessoa, Ranking.PresidenteViewHolder>
                (
                        Pessoa.class,
                        R.layout.item_rancking,
                        Ranking.PresidenteViewHolder.class,
                        query
                ) {
            @Override
            public void populateViewHolder(final Ranking.PresidenteViewHolder viewHolder, Pessoa model, int position) {
                final String chave_presidente = getRef(position).getKey();
                viewHolder.setNome(model.getNome());
                if(mAuth.getCurrentUser() == null) {
                    // viewHolder.setVoto(model.getVoto());
                }
                viewHolder.setImagem(getActivity(), model.getImagem());
                viewHolder.setBtn(chave_presidente);
                viewHolder.setBtnSeguir(chave_presidente);

                final String nomePolitico = model.getNome();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent telaPres = new Intent(getActivity(), PerfilPolitico.class);
                        telaPres.putExtra("anuncio_id", chave_presidente);
                        telaPres.putExtra("nomePolitico", nomePolitico);
                        startActivity(telaPres);
                        Intent tt = new Intent(getActivity(), FragmentComentario.class);
                        tt.putExtra("id", chave_presidente);
                        Bundle bundle = new Bundle();
                        bundle.putString("id", chave_presidente);
                        getActivity().finish();
                    }
                });




                viewHolder.btnVotar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mAuth.getCurrentUser() != null){
                            clickVotar = true;
                            mRancking.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot dataSnapshot) {
                                    final String user = mAuth.getCurrentUser().getUid();

                                    mDatabaseUser.child(user).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot data) {
                                            final String teste = (String) data.child("votou").getValue();


                                            if (clickVotar) {
                                                if (dataSnapshot.child(chave_presidente).hasChild(mAuth.getCurrentUser().getUid())) {

                                                    if (teste != null) {
                                                        mRancking.child(chave_presidente).child(mAuth.getCurrentUser().getUid()).removeValue();
                                                        mDatabaseUser.child(mAuth.getCurrentUser().getUid()).removeValue();
                                                    }
                                                    clickVotar = false;
                                                } else {

                                                    if (teste == null) {
                                                        mRancking.child(chave_presidente).child(mAuth.getCurrentUser().getUid()).child("voto").setValue(mAuth.getCurrentUser().getUid());
                                                        mDatabaseUser.child(mAuth.getCurrentUser().getUid()).child("votou").setValue(mAuth.getCurrentUser().getUid());
                                                    }else {
                                                        Toast.makeText(getContext(), "Você Já votou!!!", Toast.LENGTH_SHORT).show();
                                                    }
                                                    clickVotar = false;
                                                }

                                            }

                                        }


                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }else {
                            Toast.makeText(getContext(), "Vc precisa fazer login para votar...", Toast.LENGTH_SHORT).show();
                        }
                    }


                });





                viewHolder.btnSeguir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAuth.getCurrentUser() != null){
                            clickSeguir = true;
                            mSeguir.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (clickSeguir) {
                                        if (dataSnapshot.child(chave_presidente).hasChild(mAuth.getCurrentUser().getUid())) {

                                            mSeguir.child(chave_presidente).child(mAuth.getCurrentUser().getUid()).removeValue();
                                            clickSeguir = false;

                                        } else {

                                            mSeguir.child(chave_presidente).child(mAuth.getCurrentUser().getUid()).child("seguiu").setValue("seguiu");
                                            clickSeguir = false;

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }else {
                            Toast.makeText(getContext(), "Vc precisa fazer login para seguir o politico...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };

        listaRancking.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PresidenteViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageButton btnVotar;
        ImageButton btnVotarRed;
        ImageButton btnSeguir;
        DatabaseReference mRancking;
        DatabaseReference mDatabaseUser;
        DatabaseReference mSeguir;
        DatabaseReference mEleicao;
        FirebaseAuth mAuth;
        TextView voto;
        TextView txt;
        TextView textView;
        int contador;

        public PresidenteViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;
            btnVotar = (ImageButton)mView.findViewById(R.id.btnVotar);
            btnVotarRed = (ImageButton)mView.findViewById(R.id.btnVotarRed);
            btnSeguir = (ImageButton)mView.findViewById(R.id.btnSeguir);
            voto = (TextView)mView.findViewById(R.id.votos);
            txt = (TextView)mView.findViewById(R.id.votosText);

            mRancking = FirebaseDatabase.getInstance().getReference().child("Rancking");
            mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Voto");
            mSeguir = FirebaseDatabase.getInstance().getReference().child("Favorito");
            mEleicao = FirebaseDatabase.getInstance().getReference().child("Eleicao");
            mAuth = FirebaseAuth.getInstance();
            mRancking.keepSynced(true);

        }

        public void setBtn(final String chave_presidente){

            mRancking.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (mAuth.getCurrentUser() != null) {

                        if (dataSnapshot.child(chave_presidente).hasChild(mAuth.getCurrentUser().getUid())) {

                            contador = (int) dataSnapshot.child(chave_presidente).getChildrenCount();
                            btnVotar.setImageResource(R.drawable.ic_votar_red);

                            if (contador < 2) {
                                voto.setText(Integer.toString(contador));
                                txt.setText("voto");
                            } else {
                                voto.setText(Integer.toString(contador));
                                txt.setText("votos");
                            }
                        }else {

                            contador = (int) dataSnapshot.child(chave_presidente).getChildrenCount();
                            btnVotar.setImageResource(R.drawable.ic_votar);

                            if (contador < 2) {
                                voto.setText(Integer.toString(contador));
                                txt.setText("voto");
                            } else {
                                voto.setText(Integer.toString(contador));
                                txt.setText("votos");
                            }
                        }

                    }else {
                        contador = (int) dataSnapshot.child(chave_presidente).getChildrenCount();
                        if (contador < 2) {
                            voto.setText(Integer.toString(contador));
                            txt.setText("voto");
                        } else {
                            voto.setText(Integer.toString(contador));
                            txt.setText("votos");
                        }
                    }
                    mEleicao.child(chave_presidente).child("voto").setValue(voto.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }

        public void setBtnSeguir(final String chave_presidente){
            if (mAuth.getCurrentUser() != null) {
                mSeguir.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(chave_presidente).hasChild(mAuth.getCurrentUser().getUid())) {

                            btnSeguir.setImageResource(R.drawable.ic_favorito_amarelo);

                        } else {
                            btnSeguir.setImageResource(R.drawable.ic_favorito);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }


        }
        public void setImagem(Context context, String imagem) {

            CircleImageView imagem_anuncio = (CircleImageView) mView.findViewById(R.id.imgRancking);
            Picasso.with(context).load(imagem).placeholder(R.drawable.default_avatar).into(imagem_anuncio);

        }

        public void setNome(String nome) {

            TextView textView = (TextView) mView.findViewById(R.id.candidato);
            textView.setText(nome);

        }

    }
}