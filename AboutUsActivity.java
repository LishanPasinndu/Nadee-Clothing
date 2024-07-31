package lk.jiat.eshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class AboutUsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ConstraintLayout mainView;
    private ImageButton callBtn;
    private String Number;
    private MapView mapview;
    private GoogleMap map;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        mainView = findViewById(R.id.main);

        mapview = findViewById(R.id.mapView);
        mapview.onCreate(savedInstanceState);
        mapview.getMapAsync(this);

        Number = "077 960 4128";

        callBtn = findViewById(R.id.callBtn);
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openDialPad(Number);

            }
        });

        loadAnimation();

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        LatLng latLng = new LatLng(6.9069634,79.9161765);
        map.addMarker(new MarkerOptions().position(latLng).title("Location"));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 100));

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapview.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapview.onLowMemory();
    }

    private void openDialPad(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    public void loadAnimation(){

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mainView, "scaleX", 1f, 0.98f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mainView, "scaleY", 1f, 0.98f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mainView, "alpha", 1f, 0.6f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, alpha);

        animatorSet.setDuration(700);

        final boolean[] isReverseAnimationTriggered = {false};

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isReverseAnimationTriggered[0]) {
                    animatorSet.reverse();
                    isReverseAnimationTriggered[0] = true;
                }
            }
        });

        animatorSet.start();
    }

}