package apps.trichain.hairdresser.network;

import apps.trichain.hairdresser.network.responses.UserResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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

    @FormUrlEncoded
    @POST("login")
    Call<UserResponse> userLogin(
            @Field("email") String email,
            @Field("password") String password
    );


}
