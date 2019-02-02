package lass.govertime;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import lass.govertime.Adapter.SearchAdapterNoticias;
import lass.govertime.R;


/**
 * Created by Nailson on 12/05/2018.
 */

public class FragmentNoticia extends Fragment {

    View view;
    private RecyclerView listaNoticias;
    private DatabaseReference mDatabaseNoticias;

    ArrayList<String> textoList;
    ArrayList<String> descList;
    ArrayList<String> imgNList;
    ArrayList<String> img1NList;
    ArrayList<String> idNList;
    ArrayList<String> linkList;
    ArrayList<String> dataList;
    SearchAdapterNoticias searchAdapterNoticias;


    public FragmentNoticia() {
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.noticia, container, false);
        DatabaseUtil.getDatabase();
        String valor = getActivity().getIntent().getStringExtra("anuncio_id");
        String nomePolitico = getActivity().getIntent().getStringExtra("nomePolitico");

        listaNoticias = (RecyclerView)view.findViewById(R.id.listaNoticiasFragmento);
        listaNoticias.setHasFixedSize(true);
        LinearLayoutManager ln = new LinearLayoutManager(getContext());
        listaNoticias.setLayoutManager(ln);
        carregar(nomePolitico);
        return view;
    }

    private void carregar(final String nomePolitico) {
        mDatabaseNoticias = FirebaseDatabase.getInstance().getReference().child("UltimasNoticias");

        textoList = new ArrayList<>();
        descList = new ArrayList<>();
        imgNList = new ArrayList<>();
        img1NList = new ArrayList<>();
        idNList = new ArrayList<>();
        linkList = new ArrayList<>();
        dataList = new ArrayList<>();

        mDatabaseNoticias.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                textoList.clear();
                descList.clear();
                imgNList.clear();
                img1NList.clear();
                idNList.clear();
                linkList.clear();
                dataList.clear();
                listaNoticias.removeAllViews();

                int cont = 0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String nome = snapshot.child("texto").getValue(String.class);
                    String desc = snapshot.child("descricao").getValue(String.class);
                    String img = snapshot.child("imagem").getValue(String.class);
                    String img1 = snapshot.child("imagem1").getValue(String.class);
                    String link = snapshot.child("link").getValue(String.class);
                    String data = snapshot.child("data").getValue(String.class);
                    String id = snapshot.getKey();

                    Toast.makeText(getContext(), ""+desc, Toast.LENGTH_SHORT).show();
                    if (nome.toLowerCase().contains(nomePolitico.toLowerCase())){

                        textoList.add(nome);
                        descList.add(desc);
                        imgNList.add(img);
                        img1NList.add(img1);
                        idNList.add(id);
                        linkList.add(link);
                        dataList.add(data);
                        cont++;
                    }
                    if (cont == 15)
                        break;

                }

                searchAdapterNoticias = new SearchAdapterNoticias(getContext(), textoList, descList, imgNList,img1NList, idNList, linkList, dataList);
                listaNoticias.setAdapter(searchAdapterNoticias);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
