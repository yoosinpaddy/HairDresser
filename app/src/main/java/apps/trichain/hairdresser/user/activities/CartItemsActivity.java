package apps.trichain.hairdresser.user.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.storage.repositories.CartRepository;
import apps.trichain.hairdresser.user.adapters.CartAdapter;
import apps.trichain.hairdresser.user.models.Cart;
import apps.trichain.hairdresser.utils.SwipeToDeleteCallback;

import static apps.trichain.hairdresser.utils.AppUtils.displayToast;

public class CartItemsActivity extends AppCompatActivity  implements View.OnClickListener{

    private static final String TAG ="CartItemsActivity" ;
    RecyclerView cartrecycler;
    ImageView back;
    Button checkout;
    CartAdapter cartAdapter;
    TextView empty_view,instructtitle;
    CartRepository cartRepository;
    private boolean isCartItem =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_items);
        initUI();
        FetchCartItems();
        enableSwipeToDelete();
    }

    private void initUI(){
        checkout =findViewById(R.id.checkout);
        back =findViewById(R.id.back);
        empty_view =findViewById(R.id.empty_view);
        instructtitle =findViewById(R.id.instructtitle);
        cartrecycler =findViewById(R.id.cartrecycler);
        back.setOnClickListener(this);
        checkout.setOnClickListener(this);
        cartRepository =new CartRepository(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                onBackPressed();
                break;
            case R.id.checkout:
                if (isCartItem){
                    startActivity(new Intent(CartItemsActivity.this,OrderSummary.class));
                    overridePendingTransition(R.anim.transition_slide_in_right, R.anim.transition_slide_out_left);
                    finish();
                }else displayToast(CartItemsActivity.this,false,"Please add services to cart");

                break;
        }
    }
    private void FetchCartItems(){
        cartRepository.getItems().observe(this, cartList -> {
            assert cartList != null;
            if(cartList.size() > 0) {
                empty_view.setVisibility(View.GONE);
                instructtitle.setVisibility(View.VISIBLE);
                cartrecycler.setVisibility(View.VISIBLE);
                initRecylerView(cartList);
                isCartItem=true;
            } else updateEmptyView();
        });
    }

    private void initRecylerView(List<Cart> cartList){
        cartAdapter = new CartAdapter(CartItemsActivity.this,cartList);
        cartrecycler.setLayoutManager( new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        cartrecycler.setItemAnimator(new DefaultItemAnimator());
        cartrecycler.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

    }

    private void updateEmptyView() {
        empty_view.setVisibility(View.VISIBLE);
        instructtitle.setVisibility(View.GONE);
        cartrecycler.setVisibility(View.GONE);

    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();

                Cart cart = cartAdapter.getItem(position);
                Log.e("onSwiped:id ","----- " + cart.getId() );
                cartRepository.deleteCartItem(cart);
                cartAdapter.notifyDataSetChanged();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(cartrecycler);
    }

}
