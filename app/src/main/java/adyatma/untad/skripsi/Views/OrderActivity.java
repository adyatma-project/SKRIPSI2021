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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import adyatma.untad.skripsi.BuildConfig;
import adyatma.untad.skripsi.databinding.ActivityOrderBinding;

public class OrderActivity extends AppCompatActivity {
    public static final String EXTRAID="k";
    public static final String EXTRANAMA="jk";
    public static final String EXTRAALAMAT="kj";
    public static final String EXTRATELP="sdjlhh";

    String idOutlet = "";
    String alamat_order = "";
    String no_telp = "";
    String nama_outlet = "";
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    String phone;
    int harga55 = 0;
    int harga12 = 0;
    int sub55 = 0;
    int sub12 = 0;
    int total = 0;
    int year, month;

    private ActivityOrderBinding activityOrderBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityOrderBinding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(activityOrderBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        idOutlet = getIntent().getStringExtra(EXTRAID);
        nama_outlet = getIntent().getStringExtra(EXTRANAMA);
        alamat_order = getIntent().getStringExtra(EXTRAALAMAT);
        no_telp = getIntent().getStringExtra(EXTRATELP);
activityOrderBinding.alamatPengiriman.setText(alamat_order);

        int time_load = 0;
        new Handler().postDelayed(() -> {
            if (user == null) {
                Intent intent = new Intent(OrderActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            } else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String getPhone = user.getPhoneNumber();
                phone = getPhone.replaceFirst("\\+62", "0");

            }
        }, time_load);


        activityOrderBinding.IconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        activityOrderBinding.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NumberFormat currency = NumberFormat.getCurrencyInstance();
                String as = currency.format(total);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderActivity.this);
                alertDialogBuilder
                        .setTitle("Konfirmasi")
                        .setMessage("Harap Bayarkan Sejumlah " + as + " Kepada Sopir Saat Tabung Tiba Di Tempat Anda \n Proses Order ?")
                        .setCancelable(false)
                        .setPositiveButton("Ya, Proses Order", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                prosesOrder();
                            }
                        })
                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        cekInfo();
//        calculate();


    }



    private void prosesOrder() {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressLint("SimpleDateFormat") DateFormat sbulan = new SimpleDateFormat("MM");
        @SuppressLint("SimpleDateFormat") DateFormat stahun = new SimpleDateFormat("yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        String bulan = sbulan.format(Calendar.getInstance().getTime());
        String tahun = stahun.format(Calendar.getInstance().getTime());
        String url = BuildConfig.LINK_IMAGE + "SKRIPSI/public/Android_tambah/addOrder/Android/addOrder?id_outlet="+idOutlet+"&nama_outlet="+nama_outlet+"&waktu_order="+date+"&bulan="+bulan+"&tahun="+tahun+"&alamat_order="+alamat_order+"&no_telp="+no_telp+"&order_55="+activityOrderBinding.qty55.getText().toString()+"&order_12="+activityOrderBinding.qty12.getText().toString()+"&konfirmasi=TIDAK";
        StringRequest respon = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");


                            if (status.equals("Ok")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
                                builder.setTitle("Sukses");
                                builder.setMessage("Order Telah Di Kirim");
                                builder.setPositiveButton("Oke",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                Intent Main = new Intent(OrderActivity.this, MainActivity.class);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
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
               form.put("complete_data", "YES");
                return form;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(respon);

    }

    private void cekInfo() {
        String url = BuildConfig.LINK_IMAGE + "/SKRIPSI/public/Android_check/information/?id=1";
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String harga_5kg = jsonObject.getString("harga_5kg");
                            String harga_12kg = jsonObject.getString("harga_12kg");
                            String h5 = harga_5kg.replaceAll("[.]", "");
                            String h12 = harga_12kg.replaceAll("[.]", "");
                            harga55 = Integer.parseInt(h5);
                            harga12 = Integer.parseInt(h12);


                            activityOrderBinding.qty55.addTextChangedListener(new TextWatcher() {

                                @SuppressLint({"SetTextI18n", "DefaultLocale"})
                                public void afterTextChanged(Editable s) {
                                    final String DEFAULT_VALUE = "0";

                                    if (TextUtils.isEmpty(activityOrderBinding.qty55.getText())) {
                                        activityOrderBinding.qty55.setText(DEFAULT_VALUE);
                                    } else {
                                        activityOrderBinding.qty55.getText();
                                    }
                                    String str_qty55 = activityOrderBinding.qty55.getText().toString().trim();
                                    int int_qty55 = Integer.parseInt(str_qty55);
                                    NumberFormat nf = NumberFormat.getCurrencyInstance();
                                    sub55 = harga55 * int_qty55;
                                    String str55 = nf.format(sub55);
                                    activityOrderBinding.sub5Kg.setText(str55);

                                    total = sub55 + sub12;
                                    String sd = String.valueOf(total);
                                    NumberFormat currency = NumberFormat.getCurrencyInstance();
                                    String as = currency.format(total);

                                    activityOrderBinding.totalPembayaran.setText(as);
//                                    activityOrderBinding.totalPembayaran.setText("Rp. "+total);
                                }

                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }
                            });


                            activityOrderBinding.qty12.addTextChangedListener(new TextWatcher() {

                                public void afterTextChanged(Editable s) {
                                    final String DEFAULT_VALUE = "0";

                                    if (TextUtils.isEmpty(activityOrderBinding.qty12.getText())) {
                                        activityOrderBinding.qty12.setText(DEFAULT_VALUE);
                                    } else {
                                        activityOrderBinding.qty12.getText();
                                    }
                                    String str_qty12 = activityOrderBinding.qty12.getText().toString().trim();
                                    int int_qty12 = Integer.parseInt(str_qty12);

                                    sub12 = harga12 * int_qty12;
                                    NumberFormat nfa = NumberFormat.getCurrencyInstance();
                                    String str12 = nfa.format(sub12);

                                    activityOrderBinding.sub12Kg.setText(str12);

                                    total = sub55 + sub12;
                                    String sd = String.valueOf(total);
                                    NumberFormat currency = NumberFormat.getCurrencyInstance();
                                    String as = currency.format(total);

                                    activityOrderBinding.totalPembayaran.setText(as);
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
                        Toast.makeText(OrderActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);


    }

    void calculate() {

        activityOrderBinding.qty55.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String str_qty12 = activityOrderBinding.qty12.getText().toString().trim();
                int int_qty12 = Integer.parseInt(str_qty12);
                sub12 = harga12 * int_qty12;
                String sd = String.valueOf(sub12);
                activityOrderBinding.totalPembayaran.setText(sd);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        activityOrderBinding.qty12.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String str_qty12 = activityOrderBinding.qty12.getText().toString().trim();
                int int_qty12 = Integer.parseInt(str_qty12);
                sub12 = harga12 * int_qty12;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        total = sub55 + sub12;
        String str_total = String.valueOf(total);
        activityOrderBinding.totalPembayaran.setText("Rp. " + str_total);


    }


    void getAddress(String phone) {
        String url = BuildConfig.LINK_IMAGE + "SKRIPSI/public/Android_check/index/cari?phone=" + phone;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
//                            String name_outlet = jsonObject.getString("name_outlet");
                            String address_outlet = jsonObject.getString("address_outlet");
//                            activityOrderBinding.edtNamaPemilik.setText(name_outlet);
                            activityOrderBinding.alamatPengiriman.setText(address_outlet);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(OrderActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }
}