package apps.trichain.hairdresser.storage.daos;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import apps.trichain.hairdresser.user.models.Cart;

@Dao
public interface CartDao {
    @Insert
    Long insertItem(Cart cart);

    @Query("SELECT * FROM Cart ORDER BY id desc")
    LiveData<List<Cart>> fetchAllItems();


    @Query("SELECT * FROM Cart WHERE id =:id")
    LiveData<Cart> getItem(int id);

    @Query("SELECT * FROM Cart WHERE serviceId =:serviceId")
    LiveData<List<Cart>> getService(int serviceId);


    @Query("SELECT COUNT(id) FROM Cart")
    LiveData<Integer> getCount();


    @Update
    void updateItem(Cart cart);


    @Delete
    void deleteItem(Cart cart);
}
