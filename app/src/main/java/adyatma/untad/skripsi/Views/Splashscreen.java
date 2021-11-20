package adyatma.untad.skripsi.Views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import adyatma.untad.skripsi.BuildConfig;
import adyatma.untad.skripsi.databinding.ActivitySplashscreenBinding;

public class Splashscreen extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    @SuppressWarnings("FieldCanBeLocal")
    private ActivitySplashscreenBinding activitySplashscreenBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySplashscreenBinding = ActivitySplashscreenBinding.inflate(getLayoutInflater());
        setContentView(activitySplashscreenBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        int time_load = 4000;
        new Handler().postDelayed(() -> {
            if (firebaseUser == null) {
                Intent intent = new Intent(Splashscreen.this, StartActivity.class);
                startActivity(intent);
                finish();
            } else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String getPhone = user.getPhoneNumber();
                String phone = getPhone.replaceFirst("\\+62", "0");
                cekData(phone);
            }
        }, time_load);
    }

    void cekData(String phone) {
        String url = BuildConfig.LINK_IMAGE + "SKRIPSI/public/Android_check/index/cari?phone=" + phone;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("complete_data");

                            if (status.equals("YES")) {
                                Intent OKE1 = new Intent(Splashscreen.this, MainActivity.class);
                                startActivity(OKE1);
                                finish();
                            } else if (status.equals("NO")) {
                                Intent OKE2 = new Intent(Splashscreen.this, UpdateData.class);
                                startActivity(OKE2);
                                finish();
                            } else {
                                Toast.makeText(Splashscreen.this, "Mohon Maaf Silahkan Hub Agen. Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Splashscreen.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

}