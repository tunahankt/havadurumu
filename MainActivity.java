import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "bd5e378503939ddaee76f12ad7a97608";
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s";

    private TextView tvLocation;
    private TextView tvTemperature;
    private TextView tvDescription;
    private Button btnFetchWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocation = findViewById(R.id.tvLocation);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvDescription = findViewById(R.id.tvDescription);
        btnFetchWeather = findViewById(R.id.btnFetchWeather);

        btnFetchWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchWeatherTask task = new FetchWeatherTask();
                task.execute("Bartin");
            }
        });
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String cityName = params[0];
            String apiUrl = String.format(API_URL, cityName, API_KEY);

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonResult = new JSONObject(result);

                    String location = jsonResult.getString("name");
                    JSONObject main = jsonResult.getJSONObject("main");
                    double temperature = main.getDouble("temp");
                    String description = jsonResult.getJSONArray("weather").getJSONObject(0).getString("description");

                    tvLocation.setText(location);
                    tvTemperature.setText(String.valueOf(temperature));
                    tvDescription.setText(description);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
