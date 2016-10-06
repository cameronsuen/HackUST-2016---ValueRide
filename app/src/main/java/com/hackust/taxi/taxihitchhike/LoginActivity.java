package com.hackust.taxi.taxihitchhike;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void sign_in(View v)
    {
        TextView uv = (TextView) findViewById(R.id.username);
        username = uv.getText().toString();
        SharedPreferences pref = getSharedPreferences("username", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username",username);
        editor.commit();
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);

    }

}
