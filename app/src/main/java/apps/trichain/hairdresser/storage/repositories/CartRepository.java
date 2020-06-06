package apps.trichain.hairdresser.storage.repositories;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

import apps.trichain.hairdresser.storage.db.HairstylistDb;
import apps.trichain.hairdresser.user.models.Cart;

public class CartRepository {

    private String DB_NAME = "db_cart";

    private HairstylistDb cartTempDb;
    public CartRepository(Context context) {
        cartTempDb = Room.databaseBuilder(context, HairstylistDb.class, DB_NAME).build();
    }


    public void insertItem(Integer service_id,String title,
                           Integer price,
                           byte[] imagestring) {

        Cart cart = new Cart();
        cart.setServiceId(service_id);
        cart.setTitle(title);
        cart.setPrice(price);
        cart.setImagestring(imagestring);

        insertItem(cart);
    }

    private void insertItem(final Cart cart) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                cartTempDb.cartDao().insertItem(cart);
                return null;
            }
        }.execute();
    }

    public void updateItem(final Cart cart) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                cartTempDb.cartDao().updateItem(cart);
                return null;
            }
        }.execute();
    }

    public void deleteItem(final int id) {
        final LiveData<Cart> cartitem = getItem(id);
        Log.e("deleteItem: ", String.valueOf(id));
        if(cartitem != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    cartTempDb.cartDao().deleteItem(cartitem.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public void deleteCartItem(final Cart cart) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                cartTempDb.cartDao().deleteItem(cart);
                return null;
            }
        }.execute();
    }

    public LiveData<Cart> getItem(int id) {
        return cartTempDb.cartDao().getItem(id);
    }

    public LiveData<List<Cart>> getService(int id) {
        return cartTempDb.cartDao().getService(id);
    }


    public LiveData<List<Cart>> getItems() {
        return cartTempDb.cartDao().fetchAllItems();
    }

    public LiveData<Integer> getCount() {
        return cartTempDb.cartDao().getCount();
    }

}
