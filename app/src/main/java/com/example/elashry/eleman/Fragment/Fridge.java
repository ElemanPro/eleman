package com.example.elashry.eleman.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.elashry.eleman.Activities.Check_Internet_connection;
import com.example.elashry.eleman.Adapter.Product_Adapter;
import com.example.elashry.eleman.App_URL;
import com.example.elashry.eleman.Controller;
import com.example.elashry.eleman.Model.Product_Model;
import com.example.elashry.eleman.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Delta on 01/08/2017.
 */

public class Fridge extends Fragment {

    private RecyclerView mrRecyclerView;
    private Context mContext;
    private List<Product_Model> pro_List;
    //private final String products_url ="http://semicolonsoft.com/clients/emc/api/find/products";
    private TextView nopro_txt;
    private LinearLayout progBar_container;
    private ProgressBar prog_bar;
    private SwipeRefreshLayout mRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fridge,container,false);
        init_View(view);

        Get_proData(App_URL.product_url);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Get_proData(App_URL.product_url);
            }
        });
        return view;
    }
    private void Get_proData(String products_url) {

        JsonArrayRequest mJsonArrayRequest = new JsonArrayRequest(products_url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("data",response.toString());
                        JSONObject object;
                        pro_List = new ArrayList<>();

                        for (int index=0;index<response.length();index++)
                        {
                            try {
                                object =response.getJSONObject(index);
                                if (object.get("cat_id_fk").toString().equals("2"))
                                {
                                    pro_List.add(new Product_Model(object.get("product_id_fk").toString(),object.get("cat_id_fk").toString(),object.get("product_title").toString(),object.get("product_photo").toString()));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (pro_List.size()>0)
                        {
                            Product_Adapter adapter = new Product_Adapter(mContext,pro_List);
                            mrRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            mrRecyclerView.setVisibility(View.VISIBLE);
                            progBar_container.setVisibility(View.GONE);
                            mRefreshLayout.setRefreshing(false);
                        }
                        else if (pro_List.size()==0)
                        {
                            nopro_txt.setVisibility(View.VISIBLE);
                            mrRecyclerView.setVisibility(View.GONE);
                            progBar_container.setVisibility(View.GONE);
                            mRefreshLayout.setRefreshing(false);
                        }

                    }
                }
                ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mRefreshLayout.setRefreshing(false);
                        if (error.getMessage().toString().contains("java.net.UnknownHostException: Unable to resolve host \"semicolonsoft.com\": No address associated with hostname"))
                        {
                            mrRecyclerView.setVisibility(View.GONE);
                            progBar_container.setVisibility(View.GONE);
                        }
                        else
                        {
                            mrRecyclerView.setVisibility(View.VISIBLE);
                            progBar_container.setVisibility(View.GONE);

                        }
                    }
                }
        );
        Controller.getInstance().addToRequestQueue(mJsonArrayRequest,"json array req");

    }
    private void init_View(View view) {
        mContext =view.getContext();
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext,R.color.colorPrimary));

        mrRecyclerView = (RecyclerView) view.findViewById(R.id.fridge_recyView);
        mrRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mrRecyclerView.setVisibility(View.GONE);
        prog_bar          = (ProgressBar) view.findViewById(R.id.f_progBar);
        prog_bar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(mContext,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        progBar_container = (LinearLayout) view.findViewById(R.id.progressBar_container);
        progBar_container.setVisibility(View.VISIBLE);
        nopro_txt         = (TextView) view.findViewById(R.id.nopro_txt);



    }
    private void Network_aviliable()
    {
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean data = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();

        if (!wifi && !data)
        {
            startActivity(new Intent(mContext, Check_Internet_connection.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        }
        else {
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        Network_aviliable();
    }

}
