package apps.trichain.hairdresser.user.models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
@Entity
public class Cart implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;


    @ColumnInfo(name = "serviceId")
    private Integer serviceId;

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

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }
}

