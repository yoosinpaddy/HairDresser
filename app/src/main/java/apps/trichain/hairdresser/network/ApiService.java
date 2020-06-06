package apps.trichain.hairdresser.network;

import apps.trichain.hairdresser.network.responses.ServiceResponse;
import apps.trichain.hairdresser.network.responses.UserResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("newregister")
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
    @POST("newlogin")
    Call<UserResponse> userLogin(
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("service/index")
    Call<ServiceResponse> getServices(
            @Header("Authorization") String token
    );

}
