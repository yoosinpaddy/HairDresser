package apps.trichain.hairdresser.user.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image
{
    @SerializedName("image_path")
    @Expose
    private String image_path;
    @SerializedName("type")
    @Expose
    private String type;

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}