package apps.trichain.hairdresser.network.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResetResponse {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
