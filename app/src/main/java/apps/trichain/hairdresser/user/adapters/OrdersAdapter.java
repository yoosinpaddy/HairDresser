package apps.trichain.hairdresser.user.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.user.models.Order;
import apps.trichain.hairdresser.utils.AppUtils;
import de.hdodenhof.circleimageview.CircleImageView;

import static apps.trichain.hairdresser.utils.AppUtils.FormatCurrency;
import static apps.trichain.hairdresser.utils.AppUtils.hideView;
import static apps.trichain.hairdresser.utils.AppUtils.showView;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.CustomViewHolder>{

    public List<Order> orderitems;
    private List<Order> orderitemsfiltered;
    Context mContext;
    private Activity activity;
    RecyclerViewClickListener itemListener;

    public OrdersAdapter(Activity activity, List<Order> orderitems,RecyclerViewClickListener itemListener) {

        this.orderitems = orderitems;
        this.orderitemsfiltered = orderitems;
        this.activity = activity;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public OrdersAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, null);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.CustomViewHolder holder, int position) {
        Order order = getItem(position);

            holder.date.setText(order.getCreatedAt());
            holder.ratingbar.setRating(order.getRating());
            holder.total_amount.setText(FormatCurrency(order.getAmount()));
            holder.transportCost.setText(FormatCurrency(order.getTransportCost()));
            holder.grand_total.setText(FormatCurrency(order.getAmount()+order.getTransportCost()));
        Integer status = order.getStatus();
        if (status == 1){
            holder.status.setText(R.string.waiting_approval);
            hideView(holder.makePayment);
        }else if (status ==2 ){
            holder.status.setText(R.string.in_progress);
            hideView(holder.makePayment);
        }else if (status == 3){
            holder.status.setText(R.string.completed_order);
            hideView(holder.makePayment);
            showView(holder.ratingbar);
        }else if(status ==4){
            holder.status.setText(R.string.waiting_payment);
            showView(holder.makePayment);
        }else {
            hideView(holder.makePayment);
        }

        holder.makePayment.setOnClickListener(view -> {
            Integer amount = order.getAmount()+order.getTransportCost();
            itemListener.makePayment(view,position,amount,order.getId());
        });

        holder.ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    itemListener.onRatingBarChange(ratingBar,position,order.getId(), rating);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return orderitems.size();
    }


    public Order getItem(int position) {

        return orderitems.get(position);
    }





    public void removeAt(int position) {
        orderitems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, orderitems.size());
    }


    static class CustomViewHolder extends RecyclerView.ViewHolder {

        MaterialButton makePayment ;
        TextView date,total_amount,transportCost,grand_total,status;
        RatingBar ratingbar;
        
        CustomViewHolder(View itemView) {
            super(itemView);
            makePayment = itemView.findViewById(R.id.makePayment);
            date = itemView.findViewById(R.id.date);
            total_amount = itemView.findViewById(R.id.total_amount);
            transportCost = itemView.findViewById(R.id.transportCost);
            grand_total = itemView.findViewById(R.id.grand_total);
            status = itemView.findViewById(R.id.status);
            ratingbar = itemView.findViewById(R.id.ratingbar);

        }
    }

    public interface RecyclerViewClickListener {
        public void makePayment(View v, int position,Integer amount,String orderId);
        void onRatingBarChange(View v, int position, String orderId,float value);
    }


}