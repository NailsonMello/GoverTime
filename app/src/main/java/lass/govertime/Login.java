package lass.govertime;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.print.PrinterId;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUser;

    private EditText edtEmail, edtSenha;
    private ProgressDialog dialog;
    private SignInButton mBtnGoogle;
    private GoogleApiClient mGoogleApiClient;

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseUser.keepSynced(true);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtSenha = (EditText) findViewById(R.id.edtSenha);
        dialog = new ProgressDialog(this);
        mBtnGoogle = (SignInButton)findViewById(R.id.btnGoogle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mBtnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        dialog.setMessage("Entrando...");
        dialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mAuth = FirebaseAuth.getInstance();
                            checkUserExiste();
                            dialog.dismiss();

                        } else {

                            Toast.makeText(Login.this, "Autenticação falhou...",
                                    Toast.LENGTH_SHORT).show();


                            dialog.dismiss();
                        }

                        // ...
                    }
                });
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

    public void clickEsqueceu(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Esqueceu a senha?");
        LayoutInflater inflater=this.getLayoutInflater();
        View layout=inflater.inflate(R.layout.txtfield,null);
        builder.setView(layout);
        final EditText input = (EditText)layout.findViewById(R.id.edtEsqueceuEmail);
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                int op = 0;
                if(!isValidEmail(input.getText().toString())){
                    alertEsqueceu(op);
                }else{
                    enviarEmail(input.getText().toString());
                }
            }
        });

        builder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        AlertDialog  alerta = builder.create();
        alerta.setCanceledOnTouchOutside(false);
        alerta.show();
        Button positiveButton = alerta.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout parent = (LinearLayout) positiveButton.getParent();
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        View leftSpacer = parent.getChildAt(1);
        leftSpacer.setVisibility(View.GONE);
    }

    public void enviarEmail(String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        int op;
                        if (task.isSuccessful()) {
                            op = 1;
                            alertEsqueceu(op);
                        } else {
                            op = 0;
                            alertEsqueceu(op);
                        }
                    }
                });
    }

    public void alertEsqueceu(int op){
        if(op == 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Esqueceu a senha?");
            builder.setMessage("Email inválido!");

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });

            AlertDialog  alerta = builder.create();
            alerta.setCanceledOnTouchOutside(false);
            alerta.show();
            Button positiveButton = alerta.getButton(AlertDialog.BUTTON_POSITIVE);
            LinearLayout parent = (LinearLayout) positiveButton.getParent();
            parent.setGravity(Gravity.CENTER_HORIZONTAL);
            View leftSpacer = parent.getChildAt(1);
            leftSpacer.setVisibility(View.GONE);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Esqueceu a senha?");
            builder.setMessage("Foi enviado uma mensagem para seu e-mail para redefinir sua senha, verifique sua caixa de entrada ou spam!");

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });

            AlertDialog  alerta = builder.create();
            alerta.setCanceledOnTouchOutside(false);
            alerta.show();
            Button positiveButton = alerta.getButton(AlertDialog.BUTTON_POSITIVE);
            LinearLayout parent = (LinearLayout) positiveButton.getParent();
            parent.setGravity(Gravity.CENTER_HORIZONTAL);
            View leftSpacer = parent.getChildAt(1);
            leftSpacer.setVisibility(View.GONE);
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        }else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void clickLogin(View view){
        if(edtEmail.getText().toString().equals("")) {
            edtEmail.setError("Campo vazio!");
        }else if(edtSenha.getText().toString().equals("")) {
            edtSenha.setError("Campo vazio!");
        }else {
            dialog.setTitle("Entrando...");
            dialog.setMessage("Aguarde enquanto verificamos suas credenciais.");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtSenha.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(Login.this, "E-mail ou Senha inválido!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(Login.this, "Bem Vindo!", Toast.LENGTH_SHORT).show();
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

    private void checkUserExiste() {
        if (mAuth.getCurrentUser() != null){
            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {

                        Intent inicio = new Intent(Login.this, MainActivity.class);
                        inicio.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(inicio);

                    } else {

                        Intent editar = new Intent(Login.this, EditarPerfil.class);
                        editar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(editar);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
    public void clickCad(View v){
        if(v.getId() == R.id.btnCad){
            startActivity(new Intent(this, Cadastrar.class));
            this.finish();
        }
    }

}
