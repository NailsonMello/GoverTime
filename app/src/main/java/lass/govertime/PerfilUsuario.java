package lass.govertime;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilUsuario extends Fragment{
    private ImageView imgPerfil;
    private EditText edtNome;
    private Button btneditar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase, mDatabase3;
    private FirebaseUser user;
    private ProgressDialog mProgress;
    private StorageReference mStorage;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String nome,email, img, imgTab;
    private TextView nome2,email2;

    Query query;
    String uid;
    private RecyclerView listaRancking;
    private DatabaseReference mDatabase2;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mRancking;
    private DatabaseReference mSeguir;
    private boolean clickVotar = false;
    private boolean clickSeguir = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_perfil_usuario, container, false);
        DatabaseUtil.getDatabase();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        imgPerfil = (ImageView) view.findViewById(R.id.imgPerfil);
        mAuth = FirebaseAuth.getInstance();
        nome2 = (TextView)view.findViewById(R.id.NomePerfilUsuario);
        email2 = (TextView)view.findViewById(R.id.EmailPerfilUsuario);
        btneditar = (Button)view.findViewById(R.id.btnEditarPerfil);
        mProgress = new ProgressDialog(getActivity());

        listaRancking = (RecyclerView) view.findViewById(R.id.containerUsuario);
        listaRancking.setHasFixedSize(true);
        listaRancking.setLayoutManager(new LinearLayoutManager(getContext()));

        btneditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditarPerfil.class));
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user!= null) {
                    Carregar();
                    pesquisarUsuario();
                }
            }
        };
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        if(user!= null) {
            Carregar();
            pesquisarUsuario();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void pesquisarUsuario(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Rancking");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    if(snapshot.hasChild(uid)) {
                        pesquisarPolitico(snapshot.getKey());

                        break;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void pesquisarPolitico(String id){
        
        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("Eleicao").child(id);
        mDatabase2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nomePolitico = dataSnapshot.child("nome").getValue().toString().trim();
                setPolitico(nomePolitico);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setPolitico(final String nome){
        DatabaseReference mDatabase3 = FirebaseDatabase.getInstance().getReference().child("Eleicao");
        mDatabase3.keepSynced(true);
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Voto");
        mRancking = FirebaseDatabase.getInstance().getReference().child("Rancking");
        mSeguir = FirebaseDatabase.getInstance().getReference().child("Favorito");
        mRancking.keepSynced(true);
        mSeguir.keepSynced(true);
        mDatabaseUser.keepSynced(true);


          query = mDatabase3.orderByChild("nome").equalTo(nome);



        FirebaseRecyclerAdapter<Pessoa, PerfilUsuario.PresidenteViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pessoa, PerfilUsuario.PresidenteViewHolder>
                (
                        Pessoa.class,
                        R.layout.item_rancking,
                        PerfilUsuario.PresidenteViewHolder.class,
                        query
                ) {
            @Override
            public void populateViewHolder(final PerfilUsuario.PresidenteViewHolder viewHolder, Pessoa model, int position) {
                final String chave_presidente = getRef(position).getKey();
                final String nomePolitico = model.getNome();
                viewHolder.setNome(model.getNome());
                viewHolder.setImagem(getContext(), model.getImagem());
                viewHolder.setBtn(chave_presidente);
                viewHolder.setBtnSeguir(chave_presidente);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent telaPres = new Intent(getActivity(), PerfilPolitico.class);
                        telaPres.putExtra("anuncio_id", chave_presidente);
                        telaPres.putExtra("nomePolitico", nomePolitico);
                        startActivity(telaPres);
                        getActivity().finish();
                    }
                });


                viewHolder.btnVotar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                                                        listaRancking.setVisibility(View.GONE);
                                                    }
                                                    clickVotar = false;
                                                } else {

                                                    if (teste == null) {
                                                        mRancking.child(chave_presidente).child(mAuth.getCurrentUser().getUid()).child("voto").setValue(mAuth.getCurrentUser().getUid());
                                                        mDatabaseUser.child(mAuth.getCurrentUser().getUid()).child("votou").setValue(mAuth.getCurrentUser().getUid());
                                                    }else {
                                                        Toast.makeText(getActivity(), "Você Já votou!!!", Toast.LENGTH_SHORT).show();
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
                    }


                });

                viewHolder.btnSeguir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
        RecyclerView listaRan;
        int contador;

        public PresidenteViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;
            btnVotar = (ImageButton)mView.findViewById(R.id.btnVotar);
            btnVotarRed = (ImageButton)mView.findViewById(R.id.btnVotarRed);
            btnSeguir = (ImageButton)mView.findViewById(R.id.btnSeguir);
            voto = (TextView)mView.findViewById(R.id.votos);
            txt = (TextView)mView.findViewById(R.id.votosText);
            listaRan = (RecyclerView) mView.findViewById(R.id.containerUsuario);


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
            Picasso.with(context).load(imagem).placeholder(R.drawable.icones).into(imagem_anuncio);

        }

        public void setNome(String nome) {

            TextView textView = (TextView) mView.findViewById(R.id.candidato);
            textView.setText(nome);

        }

    }

    private void Carregar(){
        uid = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(uid);
        myRef.keepSynced(true);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                nome = dataSnapshot.child("nome").getValue().toString();
                email = dataSnapshot.child("email").getValue().toString();
                img = dataSnapshot.child("imagem").getValue().toString();
                nome2.setText(nome.toString());
                email2.setText(email.toString());
                Picasso.with(getContext()).load(img).into(imgPerfil);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
