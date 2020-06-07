package apps.trichain.hairdresser.user.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import org.json.JSONObject;

import java.util.List;

import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.user.models.Cart;
import apps.trichain.hairdresser.utils.AppUtils;
import de.hdodenhof.circleimageview.CircleImageView;

import static apps.trichain.hairdresser.utils.AppUtils.FormatCurrency;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CustomViewHolder>{

    public List<Cart> cartItems;
    private List<Cart> cartitemsfiltered;
    Context mContext;
    private Activity activity;

    public CartAdapter(Activity activity, List<Cart> cartItems) {

        this.cartItems = cartItems;
        this.cartitemsfiltered = cartItems;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CartAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, null);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CustomViewHolder holder, int position) {
        Cart cart = getItem(position);

        loadImageFromDB(cart,holder.cartImage);

            holder.tvName.setText(cart.getTitle());
            holder.tvPrice.setText(FormatCurrency(cart.getPrice()));

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }


    public Cart getItem(int position) {

        return cartItems.get(position);
    }

    public String getServiceId(int position) {
        Cart cart = getItem(position);

        String serviceid = cart.getServiceId();

        return serviceid;
    }

    public Integer getServicePrice(int position) {
        Cart cart = getItem(position);
        Integer productPrice = cart.getPrice();

        return productPrice;
    }

    public void removeAt(int position) {
        cartItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cartItems.size());
    }


    static class CustomViewHolder extends RecyclerView.ViewHolder {

        CircleImageView cartImage ;
        TextView tvName,tvPrice;
        
        CustomViewHolder(View itemView) {
            super(itemView);
            cartImage = itemView.findViewById(R.id.cartImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);

        }
    }

    private void loadImageFromDB(final Cart cart, final CircleImageView itemImage) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final byte[] bytes = cart.getImagestring();
                    // Show Image from DB in ImageView
                    itemImage.post(new Runnable() {
                        @Override
                        public void run() {
                            itemImage.setImageBitmap(AppUtils.getImage(bytes));

                        }
                    });
                } catch (Exception e) {
                    Log.e("Tag", "<loadImageFromDB> Error : " + e.getLocalizedMessage());
                }
            }
        }).start();
    }


}