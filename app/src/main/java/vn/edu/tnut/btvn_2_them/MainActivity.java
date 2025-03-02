package vn.edu.tnut.btvn_2_them;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WeatherApp";
    private static final String API_KEY = "e21337af28bb2446acc0515af087a4ed";
    private static final String CITY_NAME = "Hanoi";

    private TextView txtCity, txtTemperature, txtHumidity, txtDescription;
    private Button btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCity = findViewById(R.id.txtCity);
        txtTemperature = findViewById(R.id.txtTemperature);
        txtHumidity = findViewById(R.id.txtHumidity);
        txtDescription = findViewById(R.id.txtDescription);
        btnRefresh = findViewById(R.id.btnRefresh);

        // Gọi API lần đầu khi mở ứng dụng
        fetchWeatherData();

        // Nút cập nhật thời tiết
        btnRefresh.setOnClickListener(v -> fetchWeatherData());
    }

    private void fetchWeatherData() {
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + CITY_NAME + "&appid=" + API_KEY + "&units=metric";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request failed: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseData);

                        // Lấy dữ liệu từ JSON
                        String city = jsonObject.getString("name");
                        JSONObject main = jsonObject.getJSONObject("main");
                        double temp = main.getDouble("temp");
                        int humidity = main.getInt("humidity");
                        String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

                        runOnUiThread(() -> {
                            txtCity.setText("Thành phố: " + city);
                            txtTemperature.setText("Nhiệt độ: " + temp + "°C");
                            txtHumidity.setText("Độ ẩm: " + humidity + "%");
                            txtDescription.setText("Trạng thái: " + description);
                        });

                    } catch (Exception e) {
                        Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                    }
                }
            }
        });
    }
}

