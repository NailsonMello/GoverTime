package lass.govertime.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import lass.govertime.Cadastrar;
import lass.govertime.MainActivity;
import lass.govertime.PerfilPolitico;
import lass.govertime.R;

public class SearchAdapterPresidentes extends RecyclerView.Adapter<SearchAdapterPresidentes.SearchViewHolder>{

    Context context;
    ArrayList<String> nomeListPres;
    ArrayList<String> imgListPres;
    ArrayList<String> idListPres;
    ArrayList<String> votoListPres;

    class SearchViewHolder extends RecyclerView.ViewHolder{

        CircleImageView imgPresidentes;
        TextView nomePresidentes, votoPresidentes;
        TextView voto;
        ImageButton btnVotar;
        ImageButton btnVotarRed;
        ImageButton btnSeguir;
        DatabaseReference mRancking;
        DatabaseReference mDatabaseUser;
        DatabaseReference mSeguir;
        DatabaseReference mEleicao;
        FirebaseAuth mAuth;

        private boolean clickVotar = false;
        private boolean clickSeguir = false;


        TextView txt;
        int contador;



        public SearchViewHolder(View itemView) {
            super(itemView);

            imgPresidentes = (CircleImageView)itemView.findViewById(R.id.imgRancking);
            nomePresidentes = (TextView)itemView.findViewById(R.id.candidato);
            btnVotar = (ImageButton)itemView.findViewById(R.id.btnVotar);
            btnVotarRed = (ImageButton)itemView.findViewById(R.id.btnVotarRed);
            btnSeguir = (ImageButton)itemView.findViewById(R.id.btnSeguir);
            voto = (TextView)itemView.findViewById(R.id.votos);
            txt = (TextView)itemView.findViewById(R.id.votosText);

            mRancking = FirebaseDatabase.getInstance().getReference().child("Rancking");
            mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Voto");
            mSeguir = FirebaseDatabase.getInstance().getReference().child("Favorito");
            mEleicao = FirebaseDatabase.getInstance().getReference().child("Eleicao");
            mAuth = FirebaseAuth.getInstance();
            mRancking.keepSynced(true);



        }
    }

    public SearchAdapterPresidentes(Context context, ArrayList<String> nomeListPres,
           ArrayList<String> imgListPres, ArrayList<String> idListPres, ArrayList<String> votoListPres) {
        this.context = context;
        this.nomeListPres = nomeListPres;
        this.imgListPres = imgListPres;
        this.idListPres = idListPres;
        this.votoListPres = votoListPres;
    }

    @Override
    public SearchAdapterPresidentes.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rancking, parent, false);
        return new SearchAdapterPresidentes.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, final int position) {
       final String chave_presidente = idListPres.get(position);
       final String nomePolitico = nomeListPres.get(position);
       holder.nomePresidentes.setText(nomeListPres.get(position));
       Glide.with(context).load(imgListPres.get(position)).asBitmap().placeholder(R.drawable.default_avatar).into(holder.imgPresidentes);
       holder.voto.setText(votoListPres.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context ctx = holder.itemView.getContext();
                Intent telaPres = new Intent(ctx, PerfilPolitico.class);
                telaPres.putExtra("anuncio_id", chave_presidente);
                telaPres.putExtra("nomePolitico", nomePolitico);
                telaPres.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(telaPres);


            }
        });

       setBtnVotar(chave_presidente, holder);
       setBtnSeguir(chave_presidente, holder);
          holder.mRancking.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (holder.mAuth.getCurrentUser() != null) {

                    if (dataSnapshot.child(chave_presidente).hasChild(holder.mAuth.getCurrentUser().getUid())) {

                        holder.contador = (int) dataSnapshot.child(chave_presidente).getChildrenCount();
                        holder.btnVotar.setImageResource(R.drawable.ic_votar_red);

                        if (holder.contador < 2) {
                            holder.voto.setText(Integer.toString(holder.contador));
                            holder.txt.setText("voto");
                        } else {
                            holder.voto.setText(Integer.toString(holder.contador));
                            holder.txt.setText("votos");
                        }
                    }else {

                        holder.contador = (int) dataSnapshot.child(chave_presidente).getChildrenCount();
                        holder.btnVotar.setImageResource(R.drawable.ic_votar);

                        if (holder.contador < 2) {
                            holder.voto.setText(Integer.toString(holder.contador));
                            holder.txt.setText("voto");
                        } else {
                            holder.voto.setText(Integer.toString(holder.contador));
                            holder.txt.setText("votos");
                        }
                    }

                }else {
                    holder.contador = (int) dataSnapshot.child(chave_presidente).getChildrenCount();
                    if (holder.contador < 2) {
                        holder.voto.setText(Integer.toString(holder.contador));
                        holder.txt.setText("voto");
                    } else {
                        holder.voto.setText(Integer.toString(holder.contador));
                        holder.txt.setText("votos");
                    }
                }
                holder.mEleicao.child(chave_presidente).child("voto").setValue(holder.voto.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        if (holder.mAuth.getCurrentUser() != null) {
            holder.mSeguir.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(chave_presidente).hasChild(holder.mAuth.getCurrentUser().getUid())) {

                        holder.btnSeguir.setImageResource(R.drawable.ic_favorito_amarelo);

                    } else {
                        holder.btnSeguir.setImageResource(R.drawable.ic_favorito);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }

    private void setBtnSeguir(final String chave_presidente, final SearchViewHolder viewHolder) {
        viewHolder.btnSeguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.mAuth.getCurrentUser() != null){
                    viewHolder.clickSeguir = true;
                    viewHolder.mSeguir.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (viewHolder.clickSeguir) {
                                if (dataSnapshot.child(chave_presidente).hasChild(viewHolder.mAuth.getCurrentUser().getUid())) {

                                    viewHolder.mSeguir.child(chave_presidente).child(viewHolder.mAuth.getCurrentUser().getUid()).removeValue();
                                    viewHolder.clickSeguir = false;
                                } else {

                                    viewHolder.mSeguir.child(chave_presidente).child(viewHolder.mAuth.getCurrentUser().getUid()).child("seguiu").setValue("seguiu");
                                    viewHolder.clickSeguir = false;

                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else {
                    Toast.makeText(context, "Vc precisa fazer login para seguir o politico...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setBtnVotar(final String chave_presidente, final SearchViewHolder viewHolder) {
        viewHolder.btnVotar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewHolder.mAuth.getCurrentUser() != null){
                    viewHolder.clickVotar = true;
                    viewHolder.mRancking.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            final String user = viewHolder.mAuth.getCurrentUser().getUid();

                            viewHolder.mDatabaseUser.child(user).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot data) {
                                    final String teste = (String) data.child("votou").getValue();


                                    if (viewHolder.clickVotar) {
                                        if (dataSnapshot.child(chave_presidente).hasChild(viewHolder.mAuth.getCurrentUser().getUid())) {

                                            if (teste != null) {
                                                viewHolder.mRancking.child(chave_presidente).child(viewHolder.mAuth.getCurrentUser().getUid()).removeValue();
                                                viewHolder.mDatabaseUser.child(viewHolder.mAuth.getCurrentUser().getUid()).removeValue();
                                            }
                                            viewHolder.clickVotar = false;
                                        } else {

                                            if (teste == null) {
                                                viewHolder.mRancking.child(chave_presidente).child(viewHolder.mAuth.getCurrentUser().getUid()).child("voto").setValue(viewHolder.mAuth.getCurrentUser().getUid());
                                                viewHolder.mDatabaseUser.child(viewHolder.mAuth.getCurrentUser().getUid()).child("votou").setValue(viewHolder.mAuth.getCurrentUser().getUid());
                                            }else {
                                                Toast.makeText(context, "Você Já votou!!!", Toast.LENGTH_SHORT).show();
                                            }
                                            viewHolder.clickVotar = false;
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
                    Toast.makeText(context, "Vc precisa fazer login para votar...", Toast.LENGTH_SHORT).show();
                }
            }


        });




    }


    @Override
    public int getItemCount() {
        return nomeListPres.size();
    }
}
