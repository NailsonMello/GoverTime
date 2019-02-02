package lass.govertime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import lass.govertime.R;

/**
 * Created by Nailson on 12/05/2018.
 */

public class FragmentSobre extends Fragment {

    View view;
    TextView textView, textView1, cargo;
    DatabaseReference mDatabase;
    ExpandableTextView ext, visaoPositiva, visaoNegativa, formacao;

    public FragmentSobre() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.sobre, container, false);
        DatabaseUtil.getDatabase();
        textView = (TextView)view.findViewById(R.id.nomep);
        textView1 = (TextView)view.findViewById(R.id.partido);
        cargo = (TextView)view.findViewById(R.id.cargo);
        ext = (ExpandableTextView)view.findViewById(R.id.expand_text_viewFrag);
        visaoPositiva = (ExpandableTextView)view.findViewById(R.id.expandvisaoPositiva);
        visaoNegativa = (ExpandableTextView)view.findViewById(R.id.expandvisaoNegativa);
        formacao = (ExpandableTextView)view.findViewById(R.id.expandFormacao);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Eleicao");
        String valor = getActivity().getIntent().getStringExtra("anuncio_id");
        mDatabase.child(valor).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String txtNome = dataSnapshot.child("Candidato").getValue().toString();
                String txtVoto = dataSnapshot.child("Corrupcao").getValue().toString();
                String txtvPositiva = dataSnapshot.child("Visao Positiva").getValue().toString();
                String txtvNegativa = dataSnapshot.child("Visao Negativa").getValue().toString();
                String txtPartido = dataSnapshot.child("Partido").getValue().toString();
                String txtFormacao = dataSnapshot.child("Formacao").getValue().toString();
                String txtCargo = dataSnapshot.child("Cargo").getValue().toString();

                textView.setText(txtNome);
                textView1.setText(txtPartido);
                ext.setText(txtVoto);
                visaoPositiva.setText(txtvPositiva);
                visaoNegativa.setText(txtvNegativa);
                formacao.setText(txtFormacao);
                cargo.setText(txtCargo);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }
}
