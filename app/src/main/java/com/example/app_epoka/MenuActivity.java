package com.example.app_epoka;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MenuActivity extends Activity {

    private TextView tv;
    private String nom;
    private String prenom;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        tv = (TextView) findViewById(R.id.tv_bonjour);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        nom = intent.getStringExtra("nom");
        prenom = intent.getStringExtra("prenom");
        tv.append("Bonjour " + prenom + " " + nom);
    }

    public void btn_ajoutMission(View view){
        Intent intent = new Intent(getApplicationContext(), MissionActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void deconnexion(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
