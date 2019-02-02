package lass.govertime;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.florent37.diagonallayout.DiagonalLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import lass.govertime.Adapter.ViewPagerAdapter;

public class PerfilPolitico extends AppCompatActivity {

    private TextView nome;
    private ImageView imageView;
    private String mChave_presidente = null;
    private DatabaseReference mDatabaseSeguidores, mDatabase;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DiagonalLayout diagonalLayout;
    private FirebaseAuth mAuth;
    private TextView QtdSeguidores;
    private int contador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_politico);
        DatabaseUtil.getDatabase();
        final ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Eleicao");
        mDatabaseSeguidores = FirebaseDatabase.getInstance().getReference().child("Favorito");
        mChave_presidente = getIntent().getExtras().getString("anuncio_id");
        nome = (TextView)findViewById(R.id.nomeCandidato);
        QtdSeguidores = (TextView)findViewById(R.id.ContSeguir);
        imageView = (ImageView)findViewById(R.id.imgCandidato);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout_id);
        viewPager = (ViewPager)findViewById(R.id.viewPager_id);
        diagonalLayout = (DiagonalLayout)findViewById(R.id.diagonal);
        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diagonalLayout.setVisibility(View.GONE);
            }
        });

        mDatabase.child(mChave_presidente).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nomePres = dataSnapshot.child("nome").getValue().toString();
                String imgPres = dataSnapshot.child("imagem").getValue().toString();
                act.setTitle(nomePres);
                nome.setText(nomePres);
                Picasso.with(PerfilPolitico.this).load(imgPres).into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseSeguidores.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if (mAuth.getCurrentUser() != null) {
                        if (dataSnapshot.child(mChave_presidente).hasChild(mAuth.getCurrentUser().getUid())) {
                            contador = (int) dataSnapshot.child(mChave_presidente).getChildrenCount();
                            if (contador < 2) {
                                QtdSeguidores.setText(Integer.toString(contador) + " seguidor");
                            } else {
                                QtdSeguidores.setText(Integer.toString(contador) + " seguidores");
                            }
                        } else {
                            contador = (int) dataSnapshot.child(mChave_presidente).getChildrenCount();
                            if (contador < 2) {
                                QtdSeguidores.setText(Integer.toString(contador) + " seguidor");
                            } else {
                                QtdSeguidores.setText(Integer.toString(contador) + " seguidores");
                            }
                        }
                    }else {
                        contador = (int) dataSnapshot.child(mChave_presidente).getChildrenCount();
                        if (contador < 2) {
                            QtdSeguidores.setText(Integer.toString(contador) + " seguidor");
                        } else {
                            QtdSeguidores.setText(Integer.toString(contador) + " seguidores");
                        }
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
       /* if (mAuth.getCurrentUser() == null) {
            Intent inicio = new Intent(this, Login.class);
            startActivity(inicio);
        }*/
    }

    public void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new FragmentSobre(),"Sobre");
        adapter.AddFragment(new FragmentNoticia(),"Noticias");
        adapter.AddFragment(new FragmentComentario(), "Comentarios");
        viewPager.setAdapter(adapter);

    }


    @Override
    public boolean onSupportNavigateUp(){
        startActivity(new Intent(PerfilPolitico.this, MainActivity.class));
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (diagonalLayout.isShown()){
            startActivity(new Intent(PerfilPolitico.this, MainActivity.class));
            finish();
        }else {
            diagonalLayout.setVisibility(View.VISIBLE);
        }
    }
}
