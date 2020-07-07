package apps.trichain.hairdresser.user.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    
    private Integer id;
    @SerializedName("name")
    
    private String name;
    @SerializedName("email")
    
    private String email;
    @SerializedName("phone")
    
    private String phone;
    @SerializedName("city")
    
    private String city;
    @SerializedName("user_type")
    
    private String userType;
    @SerializedName("created_at")
    
    private String createdAt;
    @SerializedName("token")
    
    private String token;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getToken() {
        return token==null?"":token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getPhone() {
        return phone==null?"":phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", city='" + city + '\'' +
                ", userType='" + userType + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", token='" + getToken() + '\'' +
                '}';
    }
}