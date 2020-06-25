package apps.trichain.hairdresser.network;

import java.util.List;

import apps.trichain.hairdresser.network.responses.OrderResponse;
import apps.trichain.hairdresser.network.responses.OrderStoreResponse;
import apps.trichain.hairdresser.network.responses.ServiceResponse;
import apps.trichain.hairdresser.network.responses.UserResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

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

    @FormUrlEncoded
    @POST("order/store")
    Call<OrderStoreResponse> postOrder(
            @Field("service_id[]")List<String> services,
            @Field("start_date") String start_date,
            @Field("end_date") String end_date,
            @Field("description") String description,
            @Field("city") String city,
            @Field("phone") String phone,
            @Header("Authorization") String token
    );

    @GET("order/index")
    Call<OrderResponse> getOrders(
            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @POST("order/updatepayment")
    Call<OrderResponse> updateOrder(
            @Field("paypal_id") String paypal_id,
            @Field("order_id") String order_id,
            @Header("Authorization") String token
    );

    @FormUrlEncoded
    @PUT("order/rating")
    Call<OrderResponse> rateOrder(
            @Field("rating") Float rating,
            @Field("order_id") String orderId,
            @Header("Authorization") String token
    );
}
