package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
public class secondScreen extends AppCompatActivity{

    @Override
            protected void onCreate(Bundle savedInstanceState){
                super.onCreate(savedInstanceState);
                EdgeToEdge.enable(this);
                setContentView(R.layout.activity_alternative);
    }
    Button buton2 = (Button) findViewById(R.id.button2);

    buton2.setOnClickListener(new View.OnClickListener() {
        @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), firstScreen.class));
                    startActivity(new Intent("android.intent.action.MAIN"));
        }
    });
}
