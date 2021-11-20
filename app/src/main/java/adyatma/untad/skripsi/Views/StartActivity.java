package adyatma.untad.skripsi.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import adyatma.untad.skripsi.BuildConfig;
import adyatma.untad.skripsi.databinding.ActivityStartBinding;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "MAIN_TAG";
    private ActivityStartBinding activityStartBinding;
    private ProgressDialog pd;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityStartBinding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(activityStartBinding.getRoot());
        pd = new ProgressDialog(this);
        pd.setTitle("Mohon Tunggu....");
        pd.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();

        activityStartBinding.btnCheck.setOnClickListener(view -> {
            String getPhone = activityStartBinding.edtCheck.getText().toString().trim();
            String phone = getPhone.replaceFirst("0", "+62");
            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(StartActivity.this, "Mohon Masukkan Nomor Telepon Anda", Toast.LENGTH_SHORT).show();
            } else {
                cekData(getPhone);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                // signInWithPhoneAuthCredential(phoneAuthCredential); lanjut ke Halaman OTP
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pd.dismiss();
                Toast.makeText(StartActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                String getPhone = activityStartBinding.edtCheck.getText().toString().trim();
                String phone = getPhone.replaceFirst("0", "+62");
                super.onCodeSent(verificationId, forceResendingToken);
                Log.d(TAG, "onCodeSent: " + verificationId);
                mVerificationId = verificationId;
                forceResendingToken = token;
                pd.dismiss();
                Intent moveWithDataIntent = new Intent(StartActivity.this, OTPActivity.class);
                moveWithDataIntent.putExtra(OTPActivity.GET_PHONE, phone);
                moveWithDataIntent.putExtra(OTPActivity.GET_PHONE1, getPhone);
                moveWithDataIntent.putExtra(OTPActivity.GET_VER_ID, mVerificationId);
                startActivity(moveWithDataIntent);
                finish();
            }
        };
    }


    void cekData(String phone) {
        String url = BuildConfig.LINK_IMAGE+"SKRIPSI/public/Android_check/index/cari?phone="+phone;
        String phones = phone.replaceFirst("0", "+62");
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status=jsonObject.getString("status");
                            if (status.equals("data_ok"))
                            {
                                startPhoneNumberVerification(phones);
                            }
                            else{
                                Toast.makeText(StartActivity.this, "Mohon Maaf Silahkan Hub Agen. Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(StartActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }


    private void startPhoneNumberVerification(String phone) {
        pd.setMessage("Memverifikasi Nomor Telepon");
        pd.show();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

}