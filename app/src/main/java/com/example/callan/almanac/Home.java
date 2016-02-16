package com.example.callan.almanac;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

public class Home extends Activity implements View.OnClickListener{

    String name, password, email, Err;
    TextView nameTV, emailTV, passwordTV, fieldAreaTV, logoutTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        nameTV = (TextView) findViewById(R.id.home_name);
        fieldAreaTV = (TextView) findViewById(R.id.field_area);
        logoutTV = (TextView) findViewById(R.id.logoutText);
        logoutTV.setOnClickListener(this);
        //emailTV = (TextView) findViewById(R.id.home_email);
        //passwordTV = (TextView) findViewById(R.id.home_password);

        name = getIntent().getStringExtra("name");
        password = getIntent().getStringExtra("password");
        email = getIntent().getStringExtra("email");
        Err = getIntent().getStringExtra("err");

        nameTV.setText("Welcome "+name);
        //passwordTV.setText("Your password is "+password);
        //emailTV.setText("Your email is "+email);

        if(!getData().equals("")){
            fieldAreaTV.setText(getData());
        }else{
            fieldAreaTV.setText("No data");
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.logoutText){
            logout();
        }
    }

    public String getData() {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        return sharedPreferences.getString("fieldArea", "");
    }

    public void logout(){
        startActivity(new Intent(Home.this, Main.class));
        finish();
    }
}
