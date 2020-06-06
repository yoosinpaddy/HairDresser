package apps.trichain.hairdresser.storage.repositories;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

import apps.trichain.hairdresser.storage.db.HairstylistDb;
import apps.trichain.hairdresser.user.models.Address;

public class AddressRepository {
    private String DB_NAME = "db_address";

    private HairstylistDb hairstylistDb;
    public AddressRepository(Context context) {
        hairstylistDb = Room.databaseBuilder(context, HairstylistDb.class, DB_NAME).build();
    }


    public void insertAddress(String  city,String description,
                           String phone) {

        Address address = new Address();
        address.setCity(city);
        address.setDescription(description);
        address.setPhone(phone);

        insertAddress(address);
    }

    private void insertAddress(final Address address) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                hairstylistDb.addressDao().insertAddress(address);
                return null;
            }
        }.execute();
    }

    public void updateAddress(final Address address) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                hairstylistDb.addressDao().updateAddress(address);
                return null;
            }
        }.execute();
    }

    public void deleteAddress(final int id) {
        final LiveData<Address> addressitem = getAddress(id);
        Log.e("deleteAddress: ", String.valueOf(id));
        if(addressitem != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    hairstylistDb.addressDao().deleteAddress(addressitem.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public void deleteAddressAddress(final Address address) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                hairstylistDb.addressDao().deleteAddress(address);
                return null;
            }
        }.execute();
    }

    public LiveData<Address> getAddress(int id) {
        return hairstylistDb.addressDao().getAddress(id);
    }


    public LiveData<List<Address>> getFullAddress() {
        return hairstylistDb.addressDao().getFullAddress();
    }

    public LiveData<Integer> getCount() {
        return hairstylistDb.addressDao().getCount();
    }

}
