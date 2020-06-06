package apps.trichain.hairdresser.storage.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import apps.trichain.hairdresser.storage.daos.AddressDao;
import apps.trichain.hairdresser.storage.daos.CartDao;
import apps.trichain.hairdresser.user.models.Address;
import apps.trichain.hairdresser.user.models.Cart;

@Database(entities = {Cart.class, Address.class}, version = 1, exportSchema = false)
public abstract class HairstylistDb extends RoomDatabase {

    public abstract CartDao cartDao();
    public abstract AddressDao addressDao();
}