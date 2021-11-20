package adyatma.untad.skripsi.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import adyatma.untad.skripsi.BuildConfig;
import adyatma.untad.skripsi.databinding.ActivityUpdateDataBinding;

public class UpdateData extends AppCompatActivity {
    String getUid = "";
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    private ActivityUpdateDataBinding activityDataCompletionBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDataCompletionBinding = ActivityUpdateDataBinding.inflate(getLayoutInflater());
        setContentView(activityDataCompletionBinding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        int time_load = 0;
        new Handler().postDelayed(() -> {
            if (user == null) {
                Intent intent = new Intent(UpdateData.this, StartActivity.class);
                startActivity(intent);
                finish();
            } else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String getPhone = user.getPhoneNumber();
                String phone = getPhone.replaceFirst("\\+62", "0");
                cekData(phone);
            }
        }, time_load);

        String getPhone = user.getPhoneNumber();
        getUid = user.getUid();
        String phone = getPhone.replaceFirst("\\+62", "0");
        activityDataCompletionBinding.btnSendData.setOnClickListener(view -> {
            String name_outlet = activityDataCompletionBinding.edtNamaPemilik.getText().toString().trim();
            String address_outlet = activityDataCompletionBinding.alamatOutlet.getText().toString().trim();
            if (TextUtils.isEmpty(name_outlet)) {
                activityDataCompletionBinding.edtNamaPemilik.setError("Nama Pemilik Tidak Boleh Kosong");
            } else if (TextUtils.isEmpty(address_outlet)) {
                activityDataCompletionBinding.alamatOutlet.setError("Alamat Pemilik Tidak Boleh Kosong");
            } else {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UpdateData.this);
                alertDialogBuilder
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah Data Sudah Benar ?")
                        .setCancelable(false)
                        .setPositiveButton("Ya, Benar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                input_data(phone);
                                Intent inent = new Intent(UpdateData.this, MainActivity.class);
                                startActivity(inent);
                                Toast.makeText(getApplicationContext(), "Terima Kasih, Data Telah Tersimpan", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    void input_data(String phone) {
        String url = BuildConfig.LINK_IMAGE + "SKRIPSI/public/Android_tambah/index/Android/tambah?phone_outlet=" +phone+"&name_outlet="+activityDataCompletionBinding.edtNamaPemilik.getText().toString()+"&address_outlet="+activityDataCompletionBinding.alamatOutlet.getText().toString()+"&complete_data=YES";
        StringRequest respon = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");


                            if (status.equals("Ok")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateData.this);
                                builder.setTitle("Sukses");
                                builder.setMessage("Data Sukses Tersimpan");
                                builder.setPositiveButton("Oke",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                Intent Main = new Intent(UpdateData.this, MainActivity.class);
                                                startActivity(Main);
                                                finish();
                                            }
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateData.this);
                        builder.setTitle("Gagal");
                        builder.setMessage("Data Gagal Tersimpan \n"+error.getMessage());
                        builder.setPositiveButton("Oke",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> form = new HashMap<>();
                form.put("name_outlet", activityDataCompletionBinding.edtNamaPemilik.getText().toString());
                form.put("address_outlet", activityDataCompletionBinding.alamatOutlet.getText().toString());
                form.put("complete_data", "YES");
                return form;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(respon);


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
                            String name_outlet = jsonObject.getString("name_outlet");
                            String address_outlet = jsonObject.getString("address_outlet");
                            activityDataCompletionBinding.edtNamaPemilik.setText(name_outlet);
                            activityDataCompletionBinding.alamatOutlet.setText(address_outlet);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UpdateData.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

}