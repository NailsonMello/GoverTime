package lass.govertime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

public class PerfilPoliticoAntigo extends AppCompatActivity {

    private TextView nome, fonte;
    private ImageView imageView;
    private String mChave_presidente = null;
    private DatabaseReference mDatabaseUser, mDatabase;
    private ExpandableTextView expandableTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_politico_antigo);
        DatabaseUtil.getDatabase();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, Login.class));
            this.finish();
        }else{
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Presidentes");
            mChave_presidente = getIntent().getExtras().getString("anuncio_id");
            nome = (TextView)findViewById(R.id.nome);
            fonte = (TextView)findViewById(R.id.fonte);
            // texto = (TextView)findViewById(R.id.texto);
            imageView = (ImageView) findViewById(R.id.img);
            expandableTextView = (ExpandableTextView)findViewById(R.id.expand_text_viewes);
            mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Presidentes").child(mChave_presidente);
            mDatabase.child(mChave_presidente).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String nomePres = dataSnapshot.child("nome").getValue().toString();
                    String fontePres = dataSnapshot.child("fonte").getValue().toString();
                    String textoPres = dataSnapshot.child("sobre").getValue().toString();
                    String imgPres = dataSnapshot.child("imagem").getValue().toString();
                    getSupportActionBar().setTitle(nomePres);
                    nome.setText(nomePres);
                    fonte.setText(fontePres);
                    expandableTextView.setText(textoPres);

                    Picasso.with(PerfilPoliticoAntigo.this).load(imgPres).into(imageView);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onBackPressed(){
        this.finish();
        return;
    }
}
