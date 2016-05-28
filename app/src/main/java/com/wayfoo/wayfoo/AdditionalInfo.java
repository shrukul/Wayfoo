package com.wayfoo.wayfoo;


/**
 * Created by shrukul on 12/4/16.
 */

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class AdditionalInfo extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Button btn;
    View parentLayout;
    Spinner spinner;
    String hotel = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.additional_info);

        btn = (Button)findViewById(R.id.btn_setup);
        // Spinner element
        spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(AdditionalInfo.this);

        initList();

        parentLayout = findViewById(android.R.id.content);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup();
            }
        });


    }

    private void initList() {
        List<String> rest = new ArrayList<String>();
        rest.add("1st Block");
        rest.add("2nd Block");
        rest.add("3rd Block");
        rest.add("4th Block");
        rest.add("5th Block");
        rest.add("6th Block");
        rest.add("7th Block");
        rest.add("8th Block");
        rest.add("Mega Tower 1");
        rest.add("Mega Tower 2");
        rest.add("Mega Tower 3");

        ArrayAdapter<String> data = new ArrayAdapter<String>(this, R.layout.custom_spinner, rest);

        // Drop down layout style - list view with radio button
        data.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        // attaching data adapter to spinner
        spinner.setAdapter(data);
        spinner.setSelection(0);
        hotel = data.getItem(0);

    }

    private void setup() {

        Intent it = new Intent(AdditionalInfo.this,Cart.class);
        it.putExtra("addr",hotel);
        System.out.println("Hotel name - "+hotel);
        startActivity(it);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        hotel = parent.getItemAtPosition(position).toString();
    }

    public void showdrop(View v){
        spinner.performClick();

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}