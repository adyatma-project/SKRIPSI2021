package adyatma.untad.skripsi.Views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

import adyatma.untad.skripsi.BuildConfig;
import adyatma.untad.skripsi.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    String getUid = "";
    FirebaseUser user;
    String id_outlet;
    String nama_outlet;
    String alamat_outlet;
    String no_telp;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        int time_load = 0;
        new Handler().postDelayed(() -> {
            if (user == null) {
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
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
         no_telp = getPhone.replaceFirst("\\+62", "0");
        activityMainBinding.phone.setText(no_telp);

        activityMainBinding.editProfil.setOnClickListener(view -> {
            Intent Main = new Intent(MainActivity.this, UpdateData.class);
            startActivity(Main);
        });


        activityMainBinding.orderNow.setOnClickListener(view -> {
            Intent Main = new Intent(MainActivity.this, OrderActivity.class);
            Main.putExtra(OrderActivity.EXTRAID, id_outlet);
            Main.putExtra(OrderActivity.EXTRANAMA, nama_outlet);
            Main.putExtra(OrderActivity.EXTRAALAMAT, alamat_outlet);
            Main.putExtra(OrderActivity.EXTRATELP, no_telp);
            startActivity(Main);
        });

        activityMainBinding.Historycal.setOnClickListener(view -> {
            Intent Main = new Intent(MainActivity.this, HistoryActivity.class);
            Main.putExtra(HistoryActivity.EXTRA_ID_OTLET, id_outlet);
            startActivity(Main);
        });


        //Data
        String url = BuildConfig.LINK_IMAGE + "SKRIPSI/public/Android_check/index/cari?phone=" + no_telp;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String name_outlet = jsonObject.getString("name_outlet");
                            id_outlet = jsonObject.getString("id");
                            nama_outlet = jsonObject.getString("name_outlet");
                            alamat_outlet = jsonObject.getString("address_outlet");
                            no_telp = jsonObject.getString("phone_outlet");
                            activityMainBinding.phone.setText(name_outlet);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }


    public void navigateLogOut(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder
                .setTitle("")
                .setMessage("Apakah Anda Yakin Ingin Keluar ?")
                .setCancelable(false)
                .setPositiveButton("Ya, Keluar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseAuth.getInstance().signOut();
                        Intent inent = new Intent(MainActivity.this, StartActivity.class);
                        startActivity(inent);
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


    public void navigatehelp(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder
                .setTitle("Bantuan")
                .setMessage("Apabila anda mengalami kesulitan dalam penggunaan aplikasi, silahkan hubungi 085213219361 via Whatsapp")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    void cekData(String phone) {
        String url = BuildConfig.LINK_IMAGE + "/SKRIPSI/public/Android_check/information/?id=1" ;
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
                            String h5 = harga_5kg.replaceAll("[.]","");
                            String h12 = harga_12kg.replaceAll("[.]","");
                            int i1=Integer.parseInt(h5);
                            int i2=Integer.parseInt(h12);
                            NumberFormat nf = NumberFormat.getCurrencyInstance();
                            activityMainBinding.textHarga5.setText(nf.format(i1));
                            activityMainBinding.textHarga12.setText(nf.format(i2));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }
}