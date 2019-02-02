package lass.govertime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GoverTimeSplashScreen extends Activity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseUtil.getDatabase();
        FirebaseApp.initializeApp(GoverTimeSplashScreen.this);
        setContentView(R.layout.splash_screen);

        Thread tempoThread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(3500);
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("UltimasNoticias");
                    Intent inicio = new Intent(GoverTimeSplashScreen.this, MainActivity.class);
                    startActivity(inicio);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        tempoThread.start();
    }

}
