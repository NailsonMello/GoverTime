package lass.govertime;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import lass.govertime.Adapter.SearchAdapterPresidentes;

public class Favoritos extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String uid;
    ArrayList<String> nomeListPres;
    ArrayList<String> imgListPres;
    ArrayList<String> idListPres;
    ArrayList<String> votoListPres;
    SearchAdapterPresidentes searchAdapterPresidentes;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseEleicao;
    private RecyclerView listaFavoritos;
    private boolean clickSeguir = false;
    private boolean clickVotar = false;
    private DatabaseReference mSeguir;
    private DatabaseReference mRancking;
    private DatabaseReference mDatabaseUser;
    
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_favoritos, container, false);
        listaFavoritos = (RecyclerView) view.findViewById(R.id.listaFavoritos);
        listaFavoritos.setHasFixedSize(true);
        LinearLayoutManager ln = new LinearLayoutManager(getActivity());
        listaFavoritos.setLayoutManager(ln);
        mAuth = FirebaseAuth.getInstance();
        carregarPre();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    uid = user.getUid();
                    pesquisarUsuario();
                }
            }
        };

        return view;
    }

    private void carregarPre() {
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Voto");
        mRancking = FirebaseDatabase.getInstance().getReference().child("Rancking");
        mSeguir = FirebaseDatabase.getInstance().getReference().child("Favorito");
        Query query;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Eleicao");
        mDatabaseEleicao = FirebaseDatabase.getInstance().getReference().child("Favorito");


        query = mDatabaseEleicao.orderByChild(mAuth.getCurrentUser().getUid()).startAt(mAuth.getCurrentUser().getUid());
        FirebaseRecyclerAdapter<Pessoa,PresidenteViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Pessoa, PresidenteViewHolder>(
                Pessoa.class,
                R.layout.item_rancking,
                PresidenteViewHolder.class,
                query

               ) {
            @Override
            protected void populateViewHolder(final PresidenteViewHolder viewHolder, Pessoa model, int position) {

               final String id = getRef(position).getKey();
                mDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String nome = dataSnapshot.child("nome").getValue(String.class);
                        String img = dataSnapshot.child("imagem").getValue(String.class);
                        viewHolder.setNome(nome);
                        viewHolder.setImagem(getContext(), img);
                        viewHolder.setBtn(id);
                        viewHolder.setBtnSeguir(id);

                        final String nomePolitico = nome;
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent telaPres = new Intent(getActivity(), PerfilPolitico.class);
                                telaPres.putExtra("anuncio_id", id);
                                telaPres.putExtra("nomePolitico", nomePolitico);
                                startActivity(telaPres);
                                Intent tt = new Intent(getActivity(), FragmentComentario.class);
                                tt.putExtra("id", id);
                                Bundle bundle = new Bundle();
                                bundle.putString("id", id);
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
                                                        if (dataSnapshot.child(id).hasChild(mAuth.getCurrentUser().getUid())) {

                                                            if (teste != null) {
                                                                mRancking.child(id).child(mAuth.getCurrentUser().getUid()).removeValue();
                                                                mDatabaseUser.child(mAuth.getCurrentUser().getUid()).removeValue();
                                                            }
                                                            clickVotar = false;
                                                        } else {

                                                            if (teste == null) {
                                                                mRancking.child(id).child(mAuth.getCurrentUser().getUid()).child("voto").setValue(mAuth.getCurrentUser().getUid());
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
                                                if (dataSnapshot.child(id).hasChild(mAuth.getCurrentUser().getUid())) {

                                                    mSeguir.child(id).child(mAuth.getCurrentUser().getUid()).removeValue();
                                                    clickSeguir = false;

                                                } else {

                                                    mSeguir.child(id).child(mAuth.getCurrentUser().getUid()).child("seguiu").setValue("seguiu");
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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        listaFavoritos.setAdapter(firebaseRecyclerAdapter);
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

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void pesquisarUsuario(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Favorito");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> idPresidentes = new ArrayList<>();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    if(snapshot.hasChild(uid)) {
                        idPresidentes.add(snapshot.getKey());

                    }

                }
                if(!idPresidentes.isEmpty()) {
                  //  carregarFavoritos(idPresidentes);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


/*
    private void carregarFavoritos(final ArrayList idPresidentes) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Eleicao");
        mDatabaseEleicao = FirebaseDatabase.getInstance().getReference().child("Favorito");
        nomeListPres = new ArrayList<>();
        imgListPres = new ArrayList<>();
        idListPres = new ArrayList<>();
        votoListPres = new ArrayList<>();

                nomeListPres.clear();
                imgListPres.clear();
                idListPres.clear();
                votoListPres.clear();
                listaFavoritos.removeAllViews();

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshotp) {

                        for (DataSnapshot snapshot : dataSnapshotp.getChildren()) {
                            String nome = snapshot.child("nome").getValue(String.class);
                            String img = snapshot.child("imagem").getValue(String.class);
                            String voto = snapshot.child("voto").getValue(String.class);
                            String id = snapshot.getKey();
                            for (int i = 0; i < idPresidentes.size(); i++) {
                                if (idPresidentes.get(i).toString().contains(id)) {
                                    nomeListPres.add(nome);
                                    imgListPres.add(img);
                                    idListPres.add(id);
                                    votoListPres.add(voto);
                                    break;
                                }
                            }

                        }
                        if (searchAdapterPresidentes == null) {
                            searchAdapterPresidentes = new SearchAdapterPresidentes(getActivity(), nomeListPres, imgListPres, idListPres, votoListPres);
                            listaFavoritos.setAdapter(searchAdapterPresidentes);
                        } else {
                            searchAdapterPresidentes.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }*/

}
