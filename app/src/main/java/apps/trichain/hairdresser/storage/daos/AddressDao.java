package apps.trichain.hairdresser.storage.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import apps.trichain.hairdresser.user.models.Address;
@Dao
public interface AddressDao {

    @Insert
    Long insertAddress(Address address);

    @Query("SELECT * FROM Address ORDER BY id desc")
    LiveData<List<Address>> getFullAddress();


    @Query("SELECT * FROM Address WHERE id =:id")
    LiveData<Address> getAddress(int id);


    @Query("SELECT COUNT(id) FROM Address")
    LiveData<Integer> getCount();


    @Update
    void updateAddress(Address address);

    @Query("UPDATE Address SET city = :city, description = :description, phone = :phone WHERE id =:addressID")
    void updateAddressById(int addressID,String  city,String description,
                           String phone);


    @Delete
    void deleteAddress(Address address);
}
