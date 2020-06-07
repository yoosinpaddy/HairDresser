package apps.trichain.hairdresser.user.models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Cart implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;


    @ColumnInfo(name = "serviceId")
    private String serviceId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "price")
    private Integer price;

    @ColumnInfo(name = "imagestring")
    private byte[] imagestring;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public byte[] getImagestring() {
        return imagestring;
    }

    public void setImagestring(byte[] imagestring) {
        this.imagestring = imagestring;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}

