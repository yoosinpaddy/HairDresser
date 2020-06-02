package apps.trichain.hairdresser.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.network.ApiService;
import apps.trichain.hairdresser.network.responses.ServiceResponse;
import apps.trichain.hairdresser.user.adapters.ServiceAdpater;
import apps.trichain.hairdresser.user.models.Service;
import apps.trichain.hairdresser.utils.AppUtils;
import apps.trichain.hairdresser.utils.NetworkUtils;
import apps.trichain.hairdresser.utils.RecyclerTouchListener;
import apps.trichain.hairdresser.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static apps.trichain.hairdresser.utils.AppUtils.displayToast;
import static apps.trichain.hairdresser.utils.AppUtils.hideView;
import static apps.trichain.hairdresser.utils.AppUtils.showView;

public class ServicesActivity extends AppCompatActivity implements View.OnClickListener  {

    private ApiService apiService;
    Call<ServiceResponse> serviceResponseCall;
    SharedPrefManager sharedPrefManager;
    private RecyclerView recycler;
    private ServiceAdpater serviceAdpater;
    private static final String TAG = "Service";
    private ProgressBar progress;
    private LinearLayout error_layout;
    private  String _token;
    private TextView retry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        initUi();
    }

    public void initUi(){
        sharedPrefManager= SharedPrefManager.getInstance(ServicesActivity.this);
        apiService = AppUtils.getApiService();
        recycler =findViewById(R.id.recycler);
        progress = findViewById(R.id.progress);
        error_layout = findViewById(R.id.error_layout);
        retry = findViewById(R.id.retry);
        retry.setOnClickListener(this);
        _token = "Bearer" + " " + sharedPrefManager.getUserToken();
//        LinearLayoutManager layoutManager = new LinearLayoutManager(ServicesActivity.this);
        recycler.setLayoutManager(new StaggeredGridLayoutManager(2 , StaggeredGridLayoutManager.VERTICAL));
        recycler.setItemAnimator(new DefaultItemAnimator());
        if (NetworkUtils.getConnectivityStatus(ServicesActivity.this)) {
            loadServices();
        }else {
            displayToast(ServicesActivity.this,false,"No internet connection!");
            hideView(recycler);
            showView(error_layout);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.retry:
                if (NetworkUtils.getConnectivityStatus(ServicesActivity.this)) {
                    loadServices();
                }else {
                    displayToast(ServicesActivity.this,false,"No internet connection!");
                    hideView(recycler);
                    showView(error_layout);
                }
                break;
        }

    }

    public void loadServices() {
        showView(progress);
        hideView(error_layout);
        serviceResponseCall = apiService.getServices(_token);
        serviceResponseCall.enqueue(new Callback<ServiceResponse>() {
            @Override
            public void onResponse(Call<ServiceResponse> call, Response<ServiceResponse> response) {
                hideView(progress);
                ServiceResponse msg = response.body();
                if (response.isSuccessful()) {
                    Log.e("services", "......" + response.body());
                    if (msg != null) {
                        Log.e("isServices", "......" + response.body());
                        if (!msg.getError()) {
                            AppUtils.displayToast(ServicesActivity.this, true, msg.getMessage());
                            initServiceRecyclerView(msg.getService());
                        } else {
                            AppUtils.displayToast(ServicesActivity.this, false, msg.getMessage());
                        }
                    }
                } else {
                    AppUtils.displayToast(ServicesActivity.this, false, "Something Bad Happened");
                    Log.e("error", "...." + response);
                    AppUtils.displayToast(ServicesActivity.this, false, "Please try Again");

                }
            }

            @Override
            public void onFailure(Call<ServiceResponse> call, Throwable t) {
                hideView(progress);
                showView(error_layout);
                Log.e(TAG, "onFailure: " + call.request().url().toString());
                if (call.isCanceled()) {
                    Log.d(TAG, "onFailure: Canceled! " + t.getLocalizedMessage());
                } else {
                    Log.d(TAG, "onFailure: Failed! " + t.getLocalizedMessage());
                }

            }
        });
    }


    private void initServiceRecyclerView(List<Service> serviceList){
        serviceAdpater = new ServiceAdpater(ServicesActivity.this,serviceList);
        recycler.addOnItemTouchListener(new RecyclerTouchListener(this, recycler,  new RecyclerTouchListener.ClickListener(){
            @Override
            public void onClick(View view, int position) {
//                Service service = serviceAdpater.getItem(position);
//                Intent intent = new Intent(this,ProductDetailsActivivty.class);
//                Bundle extras = new Bundle();
//                extras.putSerializable("productitem", product);
//                intent.putExtras(extras);
//                startActivity(intent);
//                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(serviceAdpater);
        serviceAdpater.notifyDataSetChanged();

    }
    public void next(View view){
        startActivity(new Intent(this,Location.class));
    }


}
