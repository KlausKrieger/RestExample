package de.kriegergilde.restexample;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText countryCode = (EditText) findViewById(R.id.countryCode);
        Button button = (Button) findViewById(R.id.button);
        final TextView countryName = (TextView) findViewById(R.id.countryName);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //countryName.setText("Hallo " + countryCode.getText());
                new LongRunningGetIO().execute(countryCode.getText().toString());
            }
        });

    }


/*
Diese REST-Aufruf nutzt den öffentlichen Service, beschrieben unter
http://www.groupkt.com/post/c9b0ccb9/country-and-other-related-rest-webservices.htm

Ein Aufruf der URL
http://services.groupkt.com/country/get/iso2code/IN
(die letzten beiden Zeichen sind der Länder-Code)
liefern Daten über das Land als JSON zurück, z.B:
{
  "RestResponse" : {
    "messages" : [ "Country found matching code [IN]." ],
    "result" : {
      "name" : "India",
      "alpha2_code" : "IN",
      "alpha3_code" : "IND"
    }
  }
}
 */

    private class LongRunningGetIO extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            if (params.length != 1 && params[0].length() != 2){
                return "ERROR: enter two letter code of country";
            }

            String uri = "http://services.groupkt.com/country/get/iso2code/"+params[0];
            String text = null;
            try {
                URL url = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET"); // default is GET
                int responseCode = con.getResponseCode();

                // read result json to string
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    text = response.toString();

                    // extract country name from json:
                    JSONObject jsonObject = new JSONObject(text);
                    String countryName = jsonObject.getJSONObject("RestResponse")
                            .getJSONObject("result")
                            .getString("name");
                    return countryName;
                }
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                TextView countryName = (TextView) findViewById(R.id.countryName);
                countryName.setText(result);
            } else {
                // TODO error
            }

        }

    }
}
