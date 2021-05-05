package com.example.app_epoka;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {

    private String urlServiceWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void authentification(View view) throws JSONException {
        EditText no = (EditText) findViewById(R.id.et_No);
        EditText mdp = (EditText) findViewById(R.id.et_mdp);

        if (no.getText().toString().matches("")) {
            Toast.makeText(this, "Veuillez renseigner votre numéro", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mdp.getText().toString().matches("")) {
            Toast.makeText(this, "Veuillez renseigner votre mot de passe", Toast.LENGTH_SHORT).show();
            return;
        }

        urlServiceWeb = "http://192.168.1.10/Epoka_Web/Services/connexion.php?user=" + no.getText() + "&mdp=" + mdp.getText();
        JSONObject resultat = getServerDataJson(urlServiceWeb);

        if(resultat.has("erreur")){
            Toast.makeText(this, "Numéro ou mot de passe incorect", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("id", resultat.getInt("sal_id"));
            intent.putExtra("nom", resultat.getString("sal_nom"));
            intent.putExtra("prenom", resultat.getString("sal_prenom"));
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private JSONObject getServerDataJson(String urlString) throws JSONException {
        InputStream is = null;
        String result = "";
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //échange http avec le serveur
            URL url = new URL(urlString);
            HttpURLConnection connexion = (HttpURLConnection) url.openConnection();
            connexion.connect();
            is = connexion.getInputStream();

            //exploitation de la réponse
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String ligne;
            while ((ligne = br.readLine()) != null){
                result = result + ligne + "\n";
            }
        } catch (Exception expt) {
            Log.e("log-tag", "Erreur pendant la récupération des données : " + expt.toString());
        }

        JSONArray jArray = new JSONArray(result);
        JSONObject jsonData = null;
        for (int i = 0; i < jArray.length(); i++){
            jsonData = jArray.getJSONObject(i);
        }
        return jsonData;
    }
}