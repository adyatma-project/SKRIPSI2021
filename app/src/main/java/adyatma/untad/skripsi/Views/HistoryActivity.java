package adyatma.untad.skripsi.Views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

import java.util.ArrayList;

import adyatma.untad.skripsi.BuildConfig;
import adyatma.untad.skripsi.R;
import adyatma.untad.skripsi.databinding.ActivityHistoryBinding;

public class HistoryActivity extends AppCompatActivity {
    public static final String EXTRA_ID_OTLET="sdjh";
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    String phone;
   String id_outlet;
    FirebaseUser firebaseUser;
    private ActivityHistoryBinding activityHistoryBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHistoryBinding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(activityHistoryBinding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        int time_load = 0;
        new Handler().postDelayed(() -> {
            if (user == null) {
                Intent intent = new Intent(HistoryActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            } else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String getPhone = user.getPhoneNumber();
                phone = getPhone.replaceFirst("\\+62", "0");
               id_outlet=getIntent().getStringExtra(EXTRA_ID_OTLET);
                ambil_data(id_outlet);
            }
        }, time_load);


    }

    private void ambil_data(String id_outlet) {
        String link = BuildConfig.LINK_IMAGE + "SKRIPSI/public/Android_tambah/order/?id_outlet="+id_outlet;
        StringRequest respon = new StringRequest(
                Request.Method.GET, link,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("hasil");
                            ArrayList<Get_data> list_data;
                            list_data = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hasil = jsonArray.getJSONObject(i);
                                String id_order = hasil.getString("id_order");
                                String id_outlet = hasil.getString("id_outlet");
                                String waktu_order = hasil.getString("waktu_order");
                                String no_telp = hasil.getString("no_telp");
                                String order_55 = hasil.getString("order_55");
                                String order_12 = hasil.getString("order_12");
                                String konfirmasi = hasil.getString("konfirmasi");

                                list_data.add(new Get_data(
                                        id_order,
                                        id_outlet,
                                        waktu_order,
                                        no_telp,
                                        order_55,
                                        order_12,
                                        konfirmasi
                                ));
                            }

                            ListView listView = findViewById(R.id.list);
                            Custom_adapter adapter = new Custom_adapter(HistoryActivity.this, list_data);
                            listView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(respon);
    }
}


class Get_data {
    String id_order = "", id_outlet = "", waktu_order = "", no_telp = "", order_55 = "", order_12 = "", konfirmasi = "";

    public Get_data(String id_order, String id_outlet, String waktu_order, String no_telp, String order_55, String order_12, String konfirmasi) {
        this.id_order = id_order;
        this.id_outlet = id_outlet;
        this.waktu_order = waktu_order;
        this.no_telp = no_telp;
        this.order_55 = order_55;
        this.order_12 = order_12;
        this.konfirmasi = konfirmasi;
    }

    public String getId_order() {
        return id_order;
    }

    public String getId_outlet() {
        return id_outlet;
    }

    public String getWaktu_order() {
        return waktu_order;
    }

    public String getNo_telp() {
        return no_telp;
    }

    public String getOrder_55() {
        return order_55;
    }

    public String getOrder_12() {
        return order_12;
    }

    public String getKonfirmasi() {
        return konfirmasi;
    }
}


class Custom_adapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Get_data> model;

    Custom_adapter(Context context, ArrayList<Get_data> model) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.model = model;
    }

    @Override
    public int getCount() {
        return model.size();
    }

    @Override
    public Object getItem(int position) {
        return model.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.item_history, null);
        TextView waktu_order, order_55, order_12, konfirmasi, konfirmasinull;
        waktu_order = view.findViewById(R.id.tanggal);
        order_55 = view.findViewById(R.id.item_qty5);
        order_12 = view.findViewById(R.id.item_qty12);
        konfirmasi = view.findViewById(R.id.confirm);
        konfirmasinull = view.findViewById(R.id.nullConfirm);

        Button btn_detail = view.findViewById(R.id.btn_detail);
        ConstraintLayout listView = view.findViewById(R.id.item_histry);
        btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailHistory.class);
                intent.putExtra(DetailHistory.EXTRA_ID_ORDER, model.get(position).getId_order());
                intent.putExtra(DetailHistory.EXTRA_ID_OUTLET, model.get(position).getId_outlet());
                context.startActivity(intent);

                //    Toast.makeText(context, model.get(position).getWaktu_order(), Toast.LENGTH_SHORT).show();
            }

        });
        String konfirm = model.get(position).getKonfirmasi();
        if (konfirm.equals("YA")) {
            konfirmasi.setVisibility(View.VISIBLE);
        } else if (konfirm.equals("TIDAK")) {
            konfirmasinull.setVisibility(View.VISIBLE);
        }
        waktu_order.setText(model.get(position).getWaktu_order());
        order_55.setText(model.get(position).getOrder_55());
        order_12.setText(model.get(position).getOrder_12());
        return view;
    }
}