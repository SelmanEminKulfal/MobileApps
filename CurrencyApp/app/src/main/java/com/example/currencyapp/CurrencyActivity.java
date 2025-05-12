package com.example.currencyapp;

import android.os.Bundle;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView chfText;
    TextView tryText;
    TextView cadText;
    TextView jpyText;
    TextView usdText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        chfText = findViewById(R.id.chfText);
        tryText = findViewById(R.id.tryText);
        cadText = findViewById(R.id.cadText);
        jpyText = findViewById(R.id.jpyText);
        usdText = findViewById(R.id.usdText);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRates();
            }
        });


    }

    public void getRates() {
        DownloadData downloadData = new DownloadData();
        downloadData.execute("https://data.fixer.io/api/latest?access_key=47340437047fa6d248adee83d9c0f460");
    }

    public class DownloadData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("DownloadData", "Error downloading data: " + e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String base = jsonObject.getString("base");
                JSONObject rates = jsonObject.getJSONObject("rates");

                String chf = rates.getString("CHF");
                String turkishlira = rates.getString("TRY");
                String cad = rates.getString("CAD");
                String jpy = rates.getString("JPY");
                String usd = rates.getString("USD");

                chfText.setText("CHF: " + chf);
                tryText.setText("TRY: " + turkishlira);
                cadText.setText("CAD: " + cad);
                jpyText.setText("JPY: " + jpy);
                usdText.setText("USD: " + usd);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("DownloadData", "Error parsing JSON: " + e.getMessage());
            }
        }
    }
}