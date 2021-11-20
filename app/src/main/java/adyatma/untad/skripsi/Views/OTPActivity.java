package adyatma.untad.skripsi.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import adyatma.untad.skripsi.BuildConfig;
import adyatma.untad.skripsi.databinding.ActivityOtpactivityBinding;

public class OTPActivity extends AppCompatActivity {
    public static final String GET_PHONE = "get_phone";
    public static final String GET_PHONE1 = "get_phone1";
    public static final String GET_VER_ID = "get_ver_id";
    private static final String TAG = "MAIN_TAG";
    public int counter;
    private ActivityOtpactivityBinding activityOtpactivityBinding;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId, to;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog pd;
    private CountDownTimer mTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOtpactivityBinding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(activityOtpactivityBinding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        String get_phone = getIntent().getStringExtra(GET_PHONE);
        to = getIntent().getStringExtra(GET_PHONE);
        mVerificationId = getIntent().getStringExtra(GET_VER_ID);
        pd = new ProgressDialog(this);
        pd.setTitle("Mohon Tunggu....");
        pd.setCanceledOnTouchOutside(false);
        activityOtpactivityBinding.text2.setText("Silahkan Masukkan Kode OTP Yang Dikirimkan Pada Nomor " + to);
        timer();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pd.dismiss();
                Toast.makeText(OTPActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, forceResendingToken);
                Log.d(TAG, "onCodeSent: " + verificationId);
                mVerificationId = verificationId;
                forceResendingToken = token;
                pd.dismiss();

            }
        };

        //resendCodeTv tambah di halaman otp
        activityOtpactivityBinding.sendAgainOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode(get_phone, forceResendingToken);
                timer();
            }
        });


//        activityOtpactivityBinding.back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent back = new Intent(OTPActivity.this, StartActivity.class);
//                startActivity(back);
//                finish();
//            }
//        });

        //codeSubmitBTN tambah di halaman otp
        activityOtpactivityBinding.submitOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = activityOtpactivityBinding.edtOTP.getText().toString().trim();
                verifyPhoneNumberWithCode(mVerificationId, code);
            }
        });
    }


    void cekData(String phone) {
        String url = BuildConfig.LINK_IMAGE+"SKRIPSI/public/Android_check/index/cari?phone=" + phone;
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
                                Intent OKE1 = new Intent(OTPActivity.this, MainActivity.class);
                                startActivity(OKE1);
                                finish();
                            } else if (status.equals("NO")) {
                                Intent OKE2 = new Intent(OTPActivity.this, UpdateData.class);
                                startActivity(OKE2);
                                finish();
                            } else {
                                Toast.makeText(OTPActivity.this, "Mohon Maaf Silahkan Hub Agen. Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OTPActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        Intent back = new Intent(OTPActivity.this, StartActivity.class);
        startActivity(back);
        finish();
    }


    private void timer() {
        mTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                long milis = millisUntilFinished / 1000;
                String s = milis + " Detik";
                activityOtpactivityBinding.waitingButton.setText(s);
                activityOtpactivityBinding.sendAgainOTP.setVisibility(View.GONE);
                activityOtpactivityBinding.waitingButton.setVisibility(View.VISIBLE);
            }

            public void onFinish() {
                activityOtpactivityBinding.sendAgainOTP.setVisibility(View.VISIBLE);
                activityOtpactivityBinding.waitingButton.setVisibility(View.GONE);
            }
        }.start();

    }

    private void resendVerificationCode(String phone, PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("Mengirim Kembali Kode OTP ");
        pd.show();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber(phone).setTimeout(60L, TimeUnit.SECONDS).setActivity(this).setCallbacks(mCallbacks).setForceResendingToken(token).build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void verifyPhoneNumberWithCode(String mVerificationId, String code) {
        pd.setMessage("Memverifikasi Kode OTP");
        pd.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        String get_phones = getIntent().getStringExtra(GET_PHONE1);
        pd.setMessage("Memasuki System....");
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                pd.dismiss();
                String phone = firebaseAuth.getCurrentUser().getPhoneNumber();

                cekData(get_phones);

                Toast.makeText(OTPActivity.this, "Masuk Sebagai " + phone, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(OTPActivity.this, "Kode OTP Yang Anda Masukkan Salah", Toast.LENGTH_SHORT).show();
            }
        });

    }


}