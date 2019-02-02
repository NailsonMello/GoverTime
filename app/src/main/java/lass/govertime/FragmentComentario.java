package lass.govertime;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import lass.govertime.Adapter.ComentObject;
import lass.govertime.Adapter.ComentObjectRes;


/**
 * Created by Nailson on 12/05/2018.
 */

public class FragmentComentario extends Fragment {

    private TextView txtComent, nomeComent;
    private TextView txtComentRes, nomeComentRes;
    private EditText edtComent;
    private ImageButton btn_sendComent;
    private FrameLayout frameLayout, frameLayout1;
    private CircleImageView imgComent, imgComentRes;
    private RecyclerView listaComents, listaComentsRes;
    private DatabaseReference mDatabase, mDatabaseRes, mUsersDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private LinearLayout linearLayout;


    public FragmentComentario() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comentarios, container, false);
        DatabaseUtil.getDatabase();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mCurrent_user_id = mAuth.getCurrentUser().getUid();
        }
        frameLayout = (FrameLayout) view.findViewById(R.id.imgFrame);
        frameLayout1 = (FrameLayout) view.findViewById(R.id.imgFrameRes);
        linearLayout = (LinearLayout)view.findViewById(R.id.lei);
        edtComent = (EditText) view.findViewById(R.id.edtComent);
        btn_sendComent = (ImageButton)view.findViewById(R.id.btn_sendComent);
        mDatabaseRes = FirebaseDatabase.getInstance().getReference().child("ComentariosRes");
        mDatabaseRes.keepSynced(true);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Comentarios");
        mDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);
        // mDatabasePres = FirebaseDatabase.getInstance().getReference().child("Eleicao");
        listaComents = (RecyclerView) view.findViewById(R.id.listaComentario);
        listaComents.setHasFixedSize(true);
        listaComents.setLayoutManager(new LinearLayoutManager(getContext()));
        mostrar();
        btn_sendComent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null){
                    Toast.makeText(getContext(), "Você precisa esta logado para enviar comentario", Toast.LENGTH_SHORT).show();
                }else {
                    enviarComent();
                }
            }
        });

        return view;
    }

    private void enviarComent() {
        if (mAuth.getCurrentUser() != null) {
            final String valor = getActivity().getIntent().getStringExtra("anuncio_id");
            final String Coment = edtComent.getText().toString().trim();
            long date = System.currentTimeMillis();
            Date curDateTime = new Date(date);
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy  HH:mm");
            final String dateString = sdf.format(curDateTime);

            if (!TextUtils.isEmpty(Coment)) {
                final DatabaseReference EnviaComent = mDatabase.push();
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        EnviaComent.child("txtComent").setValue(Coment);
                        EnviaComent.child("dataComent").setValue(dateString);
                        EnviaComent.child("idUserComent").setValue(mCurrent_user_id);
                        EnviaComent.child("idPress").setValue(valor);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                edtComent.setText("");
            }
        }
    }

    public void esconderLayoutComent(){
        linearLayout.setVisibility(View.GONE);
    }
    public void mostrarLayoutComent(){
        linearLayout.setVisibility(View.VISIBLE);
    }
    public void mostrar() {

        String valor = getActivity().getIntent().getStringExtra("anuncio_id");
        DatabaseReference mDatabasee = FirebaseDatabase.getInstance().getReference().child("Comentarios");

        Query queryy = mDatabasee.orderByChild("idPress").startAt(valor).endAt(valor + "\uf8ff");

        FirebaseRecyclerAdapter<ComentObject, FragmentComentario.ComentViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<ComentObject, FragmentComentario.ComentViewHolder>(
                ComentObject.class,
                R.layout.lista_comentario,
                FragmentComentario.ComentViewHolder.class,
                queryy

        ) {
            @Override
            protected void populateViewHolder(final FragmentComentario.ComentViewHolder viewHolder, final ComentObject model, int position) {
                final RecyclerView listaComentsRes = (RecyclerView) viewHolder.mView.findViewById(R.id.listaRespostas);
                listaComentsRes.setHasFixedSize(true);
                listaComentsRes.setLayoutManager(new LinearLayoutManager(getContext()));

                final String posicao = getRef(position).getKey();
                final Button btn_excluir = (Button)viewHolder.mView.findViewById(R.id.btn_excluir);
                mUsersDatabase.child(model.getIdUserComent()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String nomeUser = dataSnapshot.child("nome").getValue().toString();
                        String imgUser = dataSnapshot.child("imagem").getValue().toString();
                        final String idUser = dataSnapshot.getKey();
                        viewHolder.setNomeComent(nomeUser);
                        viewHolder.setImagem(getContext(), imgUser);
                        if (mAuth.getCurrentUser() != null){
                            if (!mAuth.getCurrentUser().getUid().equals(idUser)) {
                                btn_excluir.setVisibility(View.GONE);
                            }

                        }else {
                            btn_excluir.setVisibility(View.GONE);
                        }

                        ImageButton btn_sendComentRes = (ImageButton)viewHolder.mView.findViewById(R.id.btn_sendComentRes);
                        if (mAuth.getCurrentUser() != null) {
                            btn_sendComentRes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String valor = getActivity().getIntent().getStringExtra("anuncio_id");
                                    EditText edtComentRes = (EditText) viewHolder.mView.findViewById(R.id.edtComentRes);
                                    final String ComentRes = edtComentRes.getText().toString().trim();
                                    long date = System.currentTimeMillis();
                                    Date curDateTime = new Date(date);
                                    SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy  HH:mm");
                                    final String dateString = sdf.format(curDateTime);

                                    if (!TextUtils.isEmpty(ComentRes)) {
                                        final DatabaseReference EnviaComent = mDatabaseRes.push();
                                        mDatabaseRes.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                EnviaComent.child("txtComentRes").setValue(ComentRes);
                                                EnviaComent.child("dataComentRes").setValue(dateString);
                                                EnviaComent.child("idUserComentRes").setValue(mCurrent_user_id);
                                                EnviaComent.child("idPressRes").setValue(valor);
                                                EnviaComent.child("idComentRes").setValue(posicao);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        edtComentRes.setText("");
                                        listaComentsRes.setVisibility(View.VISIBLE);
                                    }

                                }
                            });
                        }

                        final LinearLayout li = (LinearLayout) viewHolder.mView.findViewById(R.id.layoutResposta);
                        final TextView txbtn = (TextView)viewHolder.mView.findViewById(R.id.statusbBtn);
                        TextView tx = (TextView)viewHolder.mView.findViewById(R.id.btnResponder);

                        tx.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mAuth.getCurrentUser() != null) {
                                    if (txbtn.getText().equals("mostrar")) {
                                        li.setVisibility(View.VISIBLE);
                                        esconderLayoutComent();
                                        txbtn.setText("esconder");
                                    } else {
                                        li.setVisibility(View.GONE);
                                        mostrarLayoutComent();
                                        txbtn.setText("mostrar");
                                    }
                                }else {
                                    Toast.makeText(getContext(), "Você precisa esta logado para responder comentario", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                        TextView txx = (TextView)viewHolder.mView.findViewById(R.id.btnComentarios);
                        txx.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (txbtn.getText().equals("mostrar")) {
                                    listaComentsRes.setVisibility(View.VISIBLE);
                                    txbtn.setText("esconder");

                                }else {
                                    listaComentsRes.setVisibility(View.GONE);
                                    txbtn.setText("mostrar");
                                }
                                // Toast.makeText(getContext(),"Funcionando", Toast.LENGTH_LONG).show();
                            }
                        });

                        mostrarRespostaComentarios(posicao, viewHolder);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                viewHolder.setDataComent(model.getDataComent());
                viewHolder.setTxtComent(model.getTxtComent());

                btn_excluir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Toast.makeText(getContext(), "Excluir", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder alerta = new AlertDialog.Builder(getContext());
                        alerta.setTitle("Excluir comentario?");
                        alerta.setIcon(android.R.drawable.ic_menu_delete);
                        alerta.setMessage("Tem certeza que deseja excluir o comentario?");
                        alerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDatabase.child(posicao).removeValue();

                            }
                        });
                        alerta.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        alerta.show();

                    }
                });
            }
        };
        listaComents.setAdapter(firebaseRecyclerAdapter);
    }

    private void mostrarRespostaComentarios(final String posicao, ComentViewHolder viewHolder) {
        final DatabaseReference mUsersDatabaseRes = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabaseRes.keepSynced(true);

        final RecyclerView listaComentsRes = (RecyclerView) viewHolder.mView.findViewById(R.id.listaRespostas);
        listaComentsRes.setHasFixedSize(true);
        listaComentsRes.setLayoutManager(new LinearLayoutManager(getContext()));

        final DatabaseReference mDatabasee = FirebaseDatabase.getInstance().getReference().child("ComentariosRes");
        Query queryRes = mDatabasee.orderByChild("idComentRes").startAt(posicao).endAt(posicao + "\uf8ff");

        FirebaseRecyclerAdapter<ComentObjectRes, FragmentComentario.ComentViewHolderRes> frComentRes =
                new FirebaseRecyclerAdapter<ComentObjectRes, ComentViewHolderRes>(
                        ComentObjectRes.class,
                        R.layout.responder_comentario,
                        FragmentComentario.ComentViewHolderRes.class,
                        queryRes


                ) {
                    @Override
                    protected void populateViewHolder(final ComentViewHolderRes viewHolderRes, ComentObjectRes model, int position) {
                        final String posicaoRes = getRef(position).getKey();
                        final Button btn_excluirRes = (Button)viewHolderRes.mViewRes.findViewById(R.id.btn_excluirRes);
                        mUsersDatabaseRes.child(model.getIdUserComentRes()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String nomeUserRes = dataSnapshot.child("nome").getValue().toString();
                                String imgUserRes = dataSnapshot.child("imagem").getValue().toString();
                                String idUserRes = dataSnapshot.getKey();
                                viewHolderRes.setNomeComentRes(nomeUserRes);
                                viewHolderRes.setImagemRes(getContext(), imgUserRes);
                                if (mAuth.getCurrentUser() != null){
                                    if (!mAuth.getCurrentUser().getUid().equals(idUserRes)) {
                                        btn_excluirRes.setVisibility(View.GONE);
                                    }
                                }else {
                                    btn_excluirRes.setVisibility(View.GONE);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        viewHolderRes.setTxtComentRes(model.getTxtComentRes());
                        viewHolderRes.setDataComentRes(model.getDataComentRes());

                        btn_excluirRes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Toast.makeText(getContext(), "Excluir", Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder alerta = new AlertDialog.Builder(getContext());
                                alerta.setTitle("Excluir comentario?");
                                alerta.setIcon(android.R.drawable.ic_menu_delete);
                                alerta.setMessage("Tem certeza que deseja excluir o comentario?");
                                alerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mDatabasee.child(posicaoRes).removeValue();

                                    }
                                });
                                alerta.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                alerta.show();

                            }
                        });
                    }
                };
        listaComentsRes.setAdapter(frComentRes);

    }
    public static class ComentViewHolderRes extends RecyclerView.ViewHolder {

        View mViewRes;

        public ComentViewHolderRes(View itemView) {
            super(itemView);
            mViewRes = itemView;
        }
        public void setNomeComentRes(String nomeComentRes){
            TextView NomeComent = (TextView)mViewRes.findViewById(R.id.NomeComentRes);
            NomeComent.setText(nomeComentRes);

        }
        public void setTxtComentRes(String txtComentRes) {
            TextView TxtComent = (TextView)mViewRes.findViewById(R.id.txtComentRes);
            TxtComent.setText(txtComentRes);
            TxtComent.setVisibility(View.VISIBLE);

        }
        public void setImagemRes(Context context, String imgComentRes){

            CircleImageView imagem_anuncio = (CircleImageView)mViewRes.findViewById(R.id.imgComentRes);
            Picasso.with(context).load(imgComentRes).placeholder(R.drawable.default_avatar).into(imagem_anuncio);

        }
        public void setDataComentRes(String dataComentRes) {
            TextView TxtComent = (TextView)mViewRes.findViewById(R.id.dataComentRes);
            TxtComent.setText(dataComentRes);
            TxtComent.setVisibility(View.VISIBLE);

        }

    }



    public static class ComentViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ComentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setNomeComent(String nomeComent){
            TextView NomeComent = (TextView)mView.findViewById(R.id.NomeComent);
            NomeComent.setText(nomeComent);

        }
        public void setTxtComent(String txtComent) {
            TextView TxtComent = (TextView)mView.findViewById(R.id.txtComent);
            TxtComent.setText(txtComent);

        }
        public void setImagem(Context context, String imgComent){

            CircleImageView imagem_anuncio = (CircleImageView)mView.findViewById(R.id.imgComent);
            Picasso.with(context).load(imgComent).placeholder(R.drawable.default_avatar).into(imagem_anuncio);

        }
        public void setDataComent(String dataComent) {
            TextView TxtComent = (TextView)mView.findViewById(R.id.dataComent);
            TxtComent.setText(dataComent);

        }

    }


}
