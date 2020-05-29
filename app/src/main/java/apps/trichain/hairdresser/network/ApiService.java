package apps.trichain.hairdresser.network;

import apps.trichain.hairdresser.user.models.UserResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("register")
    Call<UserResponse> userRegister(
            @Field("first_name") String first_name,
            @Field("surname") String surname,
            @Field("email") String email,
            @Field("phone") String phone,
            @Field("city") String city,
            @Field("user_type") String user_type,
            @Field("password") String password
    );


}
