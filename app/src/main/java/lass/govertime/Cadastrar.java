package lass.govertime;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class Cadastrar extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText edtNome,edtEmail,edtSenha;
    private CircleImageView imgUser;
    private StorageReference mStorageRef;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, Login.class));
                this.finish();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, Login.class));
        this.finish();
        return;
    }

    public void cad(View v){

        edtNome = (EditText) findViewById(R.id.edtNome);
        edtEmail  = (EditText) findViewById(R.id.edtEmail);
        edtSenha  = (EditText) findViewById(R.id.edtSenha);

        if(edtNome.getText().toString().equals("")) {
            edtNome.setError("Campo vazio!");
        }else if(edtEmail.getText().toString().equals("")){
            edtEmail.setError("Campo vazio!");
        }else if(edtSenha.getText().toString().equals("")) {
            edtSenha.setError("Campo vazio!");
        }else if(!isValidEmail(edtEmail.getText().toString())){
            edtEmail.setError("E-mail inválido!");
        }else if(edtSenha.getText().toString().length() < 6){
            edtSenha.setError("Minimo 6 caracteres!");
        } else {
            dialog.setTitle("Aguarde...");
            dialog.setMessage("Seus dados estão sendo salvos!");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtSenha.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(Cadastrar.this, "E-mail já está cadastrado!",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                DatabaseReference myRef = database;
                                myRef.child("nome").setValue(edtNome.getText().toString());
                                myRef.child("email").setValue(edtEmail.getText().toString());
                                myRef.child("imagem").setValue("default");
                                myRef.child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                Toast.makeText(Cadastrar.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                                home();

                            }

                        }
                    });
        }
    }


    public void home(){
        this.finish();
    }

    public final static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
