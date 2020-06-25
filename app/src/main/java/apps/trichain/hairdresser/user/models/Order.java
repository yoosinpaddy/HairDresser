package apps.trichain.hairdresser.user.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Order implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private String endDate;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("transport_cost")
    @Expose
    private Integer transportCost;
    @SerializedName("paypal_id")
    @Expose
    private Object paypalId;
    @SerializedName("rating")
    @Expose
    private Float rating;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getTransportCost() {
        return transportCost;
    }

    public void setTransportCost(Integer transportCost) {
        this.transportCost = transportCost;
    }

    public Object getPaypalId() {
        return paypalId;
    }

    public void setPaypalId(Object paypalId) {
        this.paypalId = paypalId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
