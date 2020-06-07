package apps.trichain.hairdresser.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

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
    private SearchView service_Search;
    LinearLayout queryview;
    ImageView orders, cart;
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
        queryview =findViewById(R.id.queryview);
        cart =findViewById(R.id.cart);
        orders =findViewById(R.id.orders);
        progress = findViewById(R.id.progress);
        error_layout = findViewById(R.id.error_layout);
        retry = findViewById(R.id.retry);
        service_Search = findViewById(R.id.service_Search);
        retry.setOnClickListener(this);
        orders.setOnClickListener(this);
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
            case R.id.orders:
                startActivity(new Intent(ServicesActivity.this,OrderSummary.class));
                overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
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
                Service service = serviceAdpater.getItem(position);
                Intent intent = new Intent(ServicesActivity.this,ServiceDetailActivity.class);
                Bundle extras = new Bundle();
                extras.putSerializable("serviceitem", service);
                intent.putExtras(extras);
                startActivity(intent);
                overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(serviceAdpater);
        serviceAdpater.notifyDataSetChanged();

        initSearch(serviceAdpater,serviceList);
    }


    private void initSearch(ServiceAdpater adpater,List<Service> serviceList){
        // attach setOnQueryTextListener
        // to search view defined above
        service_Search.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    // Override onQueryTextSubmit method
                    // which is call
                    // when submitquery is searched

                    @Override
                    public boolean onQueryTextSubmit(String query)
                    {
                        // If the list contains the search query
                        // than filter the adapter
                        // using the filter method
                        // with the query as its argument
                        if (serviceList.contains(query)) {
                            serviceAdpater.getFilter().filter(query);
                        }
                        else {
                            // Search query not found in List View
                            showView(queryview);
                        }
                        adpater.getFilter().filter(query);
                        displayToast(ServicesActivity.this,true,query);
                        return false;
                    }

                    // This method is overridden to filter
                    // the adapter according to a search query
                    // when the user is typing search
                    @Override
                    public boolean onQueryTextChange(String newText)
                    {
                        if (serviceList.contains(newText)) {
                            serviceAdpater.getFilter().filter(newText);
                        }else if (serviceList.isEmpty()){
                            hideView(queryview);
                            return true;
                        }
                        else {
                            adpater.getFilter().filter(newText);
                            // Search query not found in List View
                            showView(queryview);
                        }

                        return false;
                    }

                });

        service_Search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // This is where you can be notified when the `SearchView` is closed
                // and change your views you see fit.
                hideView(queryview);
                return true;

            }
        });

    }


    public void next(View view){
        startActivity(new Intent(this, AddressActivity.class));
    }


}
