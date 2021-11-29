package adyatma.untad.skripsi.Views;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import adyatma.untad.skripsi.BuildConfig;
import adyatma.untad.skripsi.databinding.ActivityDetailHistoryBinding;


public class DetailHistory extends AppCompatActivity {
    public static final String EXTRA_ID_OUTLET = "DASD";
    public static final String EXTRA_ID_ORDER = "SAD";
    String getUid = "";
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
String id_order;
    String id_outlet = "";
    String order_55 = "";
    String order_12 = "";
    String alamat_order = "";
    String konfirmasi = "";

    int harga55 = 0;
    int harga12 = 0;
    int sub55 = 0;
    int sub12 = 0;
    int total = 0;
    int year, month;
    private ActivityDetailHistoryBinding activityDetailHistoryBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDetailHistoryBinding = ActivityDetailHistoryBinding.inflate(getLayoutInflater());
        setContentView(activityDetailHistoryBinding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        id_outlet = getIntent().getStringExtra(EXTRA_ID_OUTLET);
        int time_load = 0;
        new Handler().postDelayed(() -> {
            if (user == null) {
                Intent intent = new Intent(DetailHistory.this, StartActivity.class);
                startActivity(intent);
                finish();
            } else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String getPhone = user.getPhoneNumber();
                String phone = getPhone.replaceFirst("\\+62", "0");
                getdata();
                cekData();

            }
        }, time_load);


        activityDetailHistoryBinding.IconBack.setOnClickListener(view -> {
            Intent Main = new Intent(DetailHistory.this, MainActivity.class);
            startActivity(Main);
            finish();
        });


        activityDetailHistoryBinding.btnCancel.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetailHistory.this);
            alertDialogBuilder
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah Anda Yakin Ingin Membatalkan Pesanan Ini ?")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });

//        Toast.makeText(getApplicationContext(), getIntent().getStringExtra(EXTRA_ID_ORDER), Toast.LENGTH_SHORT).show();
    }

    private void cancel() {
        String url = BuildConfig.LINK_IMAGE + "SKRIPSI/public/Android_tambah/cancel/Android/cancel/?id_order=" + getIntent().getStringExtra(EXTRA_ID_ORDER);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");

                            if (status.equals("Ok")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(DetailHistory.this);
                                builder.setTitle("Sukses");
                                builder.setMessage("Order Berhasil Di Batalkan");
                                builder.setPositiveButton("Oke",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                Intent Main = new Intent(DetailHistory.this, MainActivity.class);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(DetailHistory.this);
                        builder.setTitle("Gagal");
                        builder.setMessage("Data Gagal Dibatalkan \n"+error.getMessage());
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
                form.put("complete_data", "YES");
                return form;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    void getdata() {
        String url = BuildConfig.LINK_IMAGE + "SKRIPSI/public/Android_tambah/detailOrder/Android/DetailOrder/?id_order=" + getIntent().getStringExtra(EXTRA_ID_ORDER) + "&id_outlet=" + getIntent().getStringExtra(EXTRA_ID_OUTLET);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArray = jsonObject.getJSONArray("hasil");
                            ArrayList<Get_data> list_data;

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hasil = jsonArray.getJSONObject(i);
                                order_55 = hasil.getString("order_55");
                                order_12 = hasil.getString("order_12");
                                alamat_order = hasil.getString("alamat_order");
                                konfirmasi = hasil.getString("konfirmasi");

                                activityDetailHistoryBinding.qty55.setText(order_55);
                                activityDetailHistoryBinding.qty12.setText(order_12);
                                activityDetailHistoryBinding.alamatPengiriman.setText(alamat_order);
                                if (konfirmasi.equals("YA")) {
                                    activityDetailHistoryBinding.btnApprove.setVisibility(View.VISIBLE);
                                } else if (konfirmasi.equals("TIDAK")) {
                                    activityDetailHistoryBinding.btnCancel.setVisibility(View.VISIBLE);
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetailHistory.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }


    private void cekData() {
        String url = BuildConfig.LINK_IMAGE + "/SKRIPSI/public/Android_check/information/?id=1";
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responsed) {
                        try {
                            JSONObject jsonObject = new JSONObject(responsed);
                            String harga_5kg = jsonObject.getString("harga_5kg");
                            String harga_12kg = jsonObject.getString("harga_12kg");
                            String h5 = harga_5kg.replaceAll("[.]", "");
                            String h12 = harga_12kg.replaceAll("[.]", "");
                            harga55 = Integer.parseInt(h5);
                            harga12 = Integer.parseInt(h12);


                            activityDetailHistoryBinding.qty55.addTextChangedListener(new TextWatcher() {

                                @SuppressLint({"SetTextI18n", "DefaultLocale"})
                                public void afterTextChanged(Editable s) {
                                    final String DEFAULT_VALUE = "0";

                                    if (TextUtils.isEmpty(activityDetailHistoryBinding.qty55.getText())) {
                                        activityDetailHistoryBinding.qty55.setText(DEFAULT_VALUE);
                                    } else {
                                        activityDetailHistoryBinding.qty55.getText();
                                    }
                                    String str_qty55 = activityDetailHistoryBinding.qty55.getText().toString().trim();
                                    int int_qty55 = Integer.parseInt(str_qty55);
                                    NumberFormat nf = NumberFormat.getCurrencyInstance();
                                    sub55 = harga55 * int_qty55;
                                    String str55 = nf.format(sub55);
                                    activityDetailHistoryBinding.sub5Kg.setText(str55);

                                    total = sub55 + sub12;
                                    String sd = String.valueOf(total);
                                    NumberFormat currency = NumberFormat.getCurrencyInstance();
                                    String as = currency.format(total);

                                    activityDetailHistoryBinding.totalPembayaran.setText(as);
//                                    activityOrderBinding.totalPembayaran.setText("Rp. "+total);
                                }

                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }
                            });


                            activityDetailHistoryBinding.qty12.addTextChangedListener(new TextWatcher() {

                                public void afterTextChanged(Editable s) {
                                    final String DEFAULT_VALUE = "0";

                                    if (TextUtils.isEmpty(activityDetailHistoryBinding.qty12.getText())) {
                                        activityDetailHistoryBinding.qty12.setText(DEFAULT_VALUE);
                                    } else {
                                        activityDetailHistoryBinding.qty12.getText();
                                    }
                                    String str_qty12 = activityDetailHistoryBinding.qty12.getText().toString().trim();
                                    int int_qty12 = Integer.parseInt(str_qty12);

                                    sub12 = harga12 * int_qty12;
                                    NumberFormat nfa = NumberFormat.getCurrencyInstance();
                                    String str12 = nfa.format(sub12);

                                    activityDetailHistoryBinding.sub12Kg.setText(str12);

                                    total = sub55 + sub12;
                                    String sd = String.valueOf(total);
                                    NumberFormat currency = NumberFormat.getCurrencyInstance();
                                    String as = currency.format(total);

                                    activityDetailHistoryBinding.totalPembayaran.setText(as);
//                                    activityOrderBinding.totalPembayaran.setText("Rp. "+total);
                                }

                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DetailHistory.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);


    }
}