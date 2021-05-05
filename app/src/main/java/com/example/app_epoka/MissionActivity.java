package com.example.app_epoka;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.List;

public class MissionActivity extends Activity {
    private String urlServiceWeb;
    private List<LibelleEtId> list;
    private Spinner ville;

    private String nom;
    private String prenom;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        list = new ArrayList<LibelleEtId>();

        urlServiceWeb = "http://192.168.1.10/Epoka_Web/Services/villes.php";
        getServerDataJson(urlServiceWeb);

        ArrayAdapter<LibelleEtId> adapter = new ArrayAdapter<LibelleEtId>(this, android.R.layout.simple_spinner_item, list);
        ville = (Spinner) findViewById(R.id.sVille);
        ville.setAdapter(adapter);
    }

    public class LibelleEtId{
        public String libelle;
        public int cp;
        public String resultat;
        public int id;

        public LibelleEtId(String unLibelle, int unId, int unCp){
            libelle = unLibelle;
            id = unId;
            cp = unCp;

            resultat = libelle + " (" + cp + ")";
        }

        @Override
        public String toString(){
            return resultat;
        }
    }

    public void ajoutMission(View view){
        EditText dateDebut = (EditText) findViewById(R.id.et_Date1);
        EditText dateFin = (EditText) findViewById(R.id.et_Date2);

        LibelleEtId libelleEtId = (LibelleEtId) ville.getSelectedItem();
        int idVille = libelleEtId.id;

        Intent intentId = getIntent();
        id = intentId.getIntExtra("id", 0);

        String urlServiceAjout = "http://192.168.1.10/Epoka_Web/Services/missions.php?dateDebut=" + dateDebut.getText() + "&dateFin=" + dateFin.getText() +"&destination=" + idVille + "&salarie=" + id;

        InputStream is = null;
        String result = "";
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //échange http avec le serveur
            URL url = new URL(urlServiceAjout);
            HttpURLConnection connexion = (HttpURLConnection) url.openConnection();
            connexion.connect();
            is = connexion.getInputStream();

            Toast.makeText(this, "Ajout effectué", Toast.LENGTH_SHORT).show();
            finish();

        } catch (Exception expt) {
            Log.e("log-tag", "Erreur pendant la récupération des données : " + expt.toString());
        }
    }

    private void getServerDataJson(String urlString) {
        InputStream is = null;
        String result = "";
        ArrayList<LibelleEtId> tableau = new ArrayList<>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            //échange http avec le serveur
            URL url = new URL(urlString);
            HttpURLConnection connexion = (HttpURLConnection) url.openConnection();
            connexion.connect();
            is = connexion.getInputStream();

            //exploitation de la réponse
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String ligne;
            while ((ligne = br.readLine()) != null) {
                result = result + ligne + "\n";
            }
        } catch (Exception expt) {
            Log.e("log-tag", "Erreur pendant la récupération des données : " + expt.toString());
        }

        //Parse les données JSON
        try {
            JSONArray jarray = new JSONArray(result);
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jsonData = jarray.getJSONObject(i);
                LibelleEtId ville = new LibelleEtId(jsonData.getString("vil_nom"), jsonData.getInt("vil_id"), jsonData.getInt("vil_cp"));
                tableau.add(ville);
            }
        } catch (JSONException expt) {
            Log.e("log-tag", "Erreur pendant l'analyse des données : " + expt.toString());
        }

        for (int i = 0; i < tableau.size(); i++) {
            list.add(new LibelleEtId(tableau.get(i).libelle, tableau.get(i).id, tableau.get(i).cp));
        }
    }
}
