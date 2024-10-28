package com.example.medtrackerapp;

import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button1);
        EditText editText1 = findViewById(R.id.editText1);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                editText1.setText("Incorrect Email");
            editText1.setText("Incorrect password");

            }
        });
    }
}
