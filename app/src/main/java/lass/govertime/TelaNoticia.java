package lass.govertime;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class TelaNoticia extends AppCompatActivity {

    private WebView webView;
    private String linkNoticias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_noticia);
        final ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setTitle("Noticias");

        linkNoticias = getIntent().getStringExtra("linkNoticia");

        webView = (WebView)findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(linkNoticias);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else {
            startActivity(new Intent(TelaNoticia.this, MainActivity.class));
            finish();

        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        startActivity(new Intent(TelaNoticia.this, MainActivity.class));
        finish();
        return true;
    }

}
