package apps.trichain.hairdresser.user.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import apps.trichain.hairdresser.R;
import apps.trichain.hairdresser.user.models.Image;
import apps.trichain.hairdresser.user.models.Service;
import apps.trichain.hairdresser.utils.AppUtils;

public class ServiceAdpater extends RecyclerView.Adapter<BaseViewHolder> implements Filterable {


    public List<Service> servicelist;
    private List<Service> servicefiltered;
    Context mContext;
    private Activity activity;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;


    public ServiceAdpater(Activity activity, List<Service> servicelist) {

        this.servicelist = servicelist;
        this.servicefiltered = servicelist;
        this.activity = activity;

    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);

    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == servicefiltered.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return servicefiltered.size();
    }

    public Service getItem(int position) {
        return servicefiltered.get(position);
    }

    public void addItems(List<Service> postItems) {
        servicefiltered.addAll(postItems);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        servicefiltered.add(new Service());
        notifyItemInserted(servicefiltered.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = servicefiltered.size() - 1;
        Service item = getItem(position);
        if (item != null) {
            servicefiltered.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        servicefiltered.clear();
        notifyDataSetChanged();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    servicefiltered = servicelist;
                } else {
                    List<Service> filteredList = new ArrayList<>();
                    for (Service row : servicelist) {

                        // Product match condition. this might differ depending on your requirement
                        // here we are looking for Product body match or title match
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) || row.getDescription().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    servicefiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = servicefiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                servicefiltered = (ArrayList<Service>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends BaseViewHolder {
        TextView tv_service_name, tv_service_price;
        ImageView img_service;

        ViewHolder(View itemView) {
            super(itemView);

            tv_service_name = itemView.findViewById(R.id.tv_service_name);
            tv_service_price = itemView.findViewById(R.id.tv_service_price);
            img_service = itemView.findViewById(R.id.img_service);

        }

        protected void clear() {
        }

        public void onBind(int position) {
            super.onBind(position);
            Service service = getItem(position);

            List<Image> imageList = service.getImage();
            String featuredimage=null;
            for (int i = 0; i < imageList.size(); i++) {
                Image images= imageList.get(i);
               if (images.getType().equals("main_image")){
                   featuredimage = images.getImage_path();
                   break;
               }
            }
        Glide.with(activity)
                .load( featuredimage)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .placeholder(R.drawable.logo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fallback(R.drawable.logo)
                .into(img_service);

            tv_service_name.setText(service.getTitle());
            tv_service_price.setText(AppUtils.FormatCurrency(Integer.parseInt(service.getPrice())));

        }
    }
    public static class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
        }
        @Override
        protected void clear() {
        }
    }


}