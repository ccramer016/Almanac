package com.example.callan.almanac;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;

public class Home extends Activity implements View.OnClickListener{

    String name, password, email, Err, area;
    TextView nameTV, fieldAreaTV, logoutTV, data1TV, data2TV, data3TV, data4TV, data5TV, data6TV;
    Spinner spinner, spinner2;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> areas = new ArrayList<>();
    String[] fieldtypes = {"sesame", "rice", "blackgram"};
    int fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        nameTV = (TextView) findViewById(R.id.home_name);
        fieldAreaTV = (TextView) findViewById(R.id.field_area);
        logoutTV = (TextView) findViewById(R.id.logoutText);
        data1TV = (TextView) findViewById(R.id.data1);
        data2TV = (TextView) findViewById(R.id.data2);
        data3TV = (TextView) findViewById(R.id.data3);
        data4TV = (TextView) findViewById(R.id.data4);
        data5TV = (TextView) findViewById(R.id.data5);
        data6TV = (TextView) findViewById(R.id.data6);
        logoutTV.setOnClickListener(this);

        name = getIntent().getStringExtra("name");
        password = getIntent().getStringExtra("password");
        email = getIntent().getStringExtra("email");
        Err = getIntent().getStringExtra("err");
        area = getIntent().getStringExtra("area");
        fields = getData("fieldnum");
        areas = getDataSet("sizes");

        if(name!=null){
            save("name", name.toString());
        }else{
            name = getStringData("name");
        }

            Log.e("Areas size on start: ", String.valueOf(areas.size()));
            Log.e("Fields value on start: ", String.valueOf(fields));

        nameTV.setText("Welcome " + name);
        fieldAreaTV.setText(email);
        if(area!=null && Float.parseFloat(area)>0){
            areas.add(area);
            fields++;
            save("fieldnum", fields);
            save("sizes", areas);
        }

        if(fields==0){
            data1TV.setText("No field data");
            data2TV.setText("");
            data3TV.setText("");
            data4TV.setText("");
            data5TV.setText("");
            data6TV.setText("");
        }

        for(int i=0; i<fields; i++){
            items.add("Field "+(i+1));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fieldtypes);
        spinner.setAdapter(adapter);
        spinner2.setAdapter(adapter2);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(areas.size()>0) {
                    switch (spinner2.getSelectedItemPosition()) {
                        case 0:
                            sesame();
                            break;
                        case 1:
                            rice();
                            break;
                        case 2:
                            blackgram();
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(areas.size()>0) {
                    switch (position) {
                        case 0:
                            sesame();
                            break;
                        case 1:
                            rice();
                            break;
                        case 2:
                            blackgram();
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.logoutText){
            logout();
        }
        if(v.getId()==R.id.buttonMeasure){
            startActivity(new Intent(Home.this, Measure.class));
        }
    }

    public int getData(String title) {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        int temp = sharedPreferences.getInt(title, 0);
        sharedPreferences.edit().remove(title).apply();
        return temp;
    }

    public String getStringData(String title) {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String temp = sharedPreferences.getString(title, null);
        sharedPreferences.edit().remove(title).apply();
        return temp;
    }

    public ArrayList<String> getDataSet(String title) {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ArrayList<String> list = new ArrayList<>();
        int temp = sharedPreferences.getInt("array", 0);
        for(int i=0; i<temp; i++) {
            double temp2 = Double.parseDouble(sharedPreferences.getString(i + title, null));
            String temp3 = String.valueOf(temp2);
            list.add(temp3);
            editor.remove(i + title).apply();
        }
        return list;
    }

    public void startMeasure(View v){
        startActivity(new Intent(this, Measure.class));
    }

    public void deletefield(View v){
        Log.e("areas array size:", String.valueOf(areas.size()));
        int position = spinner.getSelectedItemPosition();
        if(position>=0) {
            areas.remove(position);
            fields = fields - 1;
            save("fieldnum", fields);
            save("sizes", areas);
            Log.e("areas array new size:", String.valueOf(areas.size()));
            finish();
            startActivity(getIntent());
        }else{
            finish();
            startActivity(getIntent());
        }
    }

    public void logout(){
        startActivity(new Intent(Home.this, Main.class));
        finish();
    }

    public void save(String title, int value) {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(title, value);
        editor.apply();
    }

    public void save(String title, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(title, value);
        editor.apply();
    }

    public void save(String title, ArrayList<String> areas) {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("array", areas.size());
        editor.apply();
        for(int i=0; i<areas.size(); i++){
            editor.putString(i+title, areas.get(i));
            editor.apply();
        }
    }

    public void sesame(){
        int position = spinner.getSelectedItemPosition();
        double area = Double.parseDouble(areas.get(position));
        String temp = "Field " + (position + 1) + " area: " + String.format("%.2f", area) + " acres";
        String water = "Water: " + String.format("%.2f", 647521*area) + " liters";
        String seeds = "Seeds: " + String.format("%.2f", 1.36*area) + " kg";
        String nitrogen = "Nitrogen: " + String.format("%.2f", 36.3*area) + " kg (" + String.format("%.2f", 15.9*area) + " if blackgram was planted last season)";
        String P2O5 = "P2O5: " + String.format("%.2f", 9.07*area) + " kg";
        String K2O = "K2O: " + String.format("%.2f", 9.07*area) + " kg";
        data1TV.setText(temp);
        data2TV.setText(water);
        data3TV.setText(seeds);
        data4TV.setText(nitrogen);
        data5TV.setText(P2O5);
        data6TV.setText(K2O);
    }

    public void rice(){
        int position = spinner.getSelectedItemPosition();
        double area = Double.parseDouble(areas.get(position));
        String temp = "Field " + (position + 1) + " area: " + String.format("%.2f", area) + " acres";
        String water = "Water: none";
        String seeds = "Seeds: " + String.format("%.2f", 68*area) + " kg";
        String nitrogen = "Nitrogen: " + String.format("%.2f", 68*area) + " kg";
        String P2O5 = "note: have properly drained fields";
        String K2O = "";
        data1TV.setText(temp);
        data2TV.setText(water);
        data3TV.setText(seeds);
        data4TV.setText(nitrogen);
        data5TV.setText(P2O5);
        data6TV.setText(K2O);
    }

    public void blackgram(){
        int position = spinner.getSelectedItemPosition();
        double area = Double.parseDouble(areas.get(position));
        String temp = "Field " + (position + 1) + " area: " + String.format("%.2f", area) + " acres";
        String water = "Water: " + String.format("%.2f", 2023503*area) + " liters";
        String seeds = "Seeds: " + String.format("%.2f", 8.16*area) + " kg";
        String nitrogen = "Nitrogen: none, produces own nitrogen from atmosphere";
        String P2O5 = "P2O5: " + String.format("%.2f", 15.9*area) + " kg";
        String K2O = "S: " + String.format("%.2f", 6.8*area) + " kg";
        data1TV.setText(temp);
        data2TV.setText(water);
        data3TV.setText(seeds);
        data4TV.setText(nitrogen);
        data5TV.setText(P2O5);
        data6TV.setText(K2O);
    }
}
