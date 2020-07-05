package apps.trichain.hairdresser.network.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import apps.trichain.hairdresser.user.models.User;

public class UserResponse {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("user")
    @Expose
    private User user;

    public Boolean getError() {
        return error==null?false:true;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message==null?"":message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "error=" + getError() +
                ", message='" + getMessage() + '\'' +
                ", user=" + user.toString() +
                '}';
    }
}
