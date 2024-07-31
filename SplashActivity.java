package lk.jiat.eshop;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import lk.jiat.eshop.MainActivity;
import lk.jiat.eshop.R;

public class SplashActivity extends AppCompatActivity implements SensorEventListener {

    private SnowfallView snowfallView;
    private SensorManager sensorManager;
    private Sensor lightSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_EShop_FullScreen);
        setContentView(R.layout.activity_splash);

        snowfallView = findViewById(R.id.snowfallView);
        snowfallView.setVisibility(View.VISIBLE);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }, 1000);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }, 2000);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float lightLevel = sensorEvent.values[0];

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = lightLevel / 255.0f;
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (lightSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }


}
