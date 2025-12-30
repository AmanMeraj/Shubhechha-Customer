package com.subh.shubhechha.Retrofit;

import com.subh.shubhechha.Model.AddToCartModel;
import com.subh.shubhechha.Model.CartResponse;
import com.subh.shubhechha.Model.CheckoutModel;
import com.subh.shubhechha.Model.GenericPostResponse;
import com.subh.shubhechha.Model.GetAddressResponse;
import com.subh.shubhechha.Model.HomeResponse;
import com.subh.shubhechha.Model.LoginResponse;
import com.subh.shubhechha.Model.NotificationResponse;
import com.subh.shubhechha.Model.OrderDetails;
import com.subh.shubhechha.Model.OrderModel;
import com.subh.shubhechha.Model.PostAddress;
import com.subh.shubhechha.Model.PostAddressResponse;
import com.subh.shubhechha.Model.ProfileResponse;
import com.subh.shubhechha.Model.RegisterUserResponse;
import com.subh.shubhechha.Model.ShopItemResponse;
import com.subh.shubhechha.Model.ShopResponse;
import com.subh.shubhechha.Model.UpdateFcm;
import com.subh.shubhechha.Model.User;
import com.subh.shubhechha.Model.VerifyOtpResponse;
import com.subh.shubhechha.Model.WalletResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiRequest {

//    //Chat Head Api
//    @Headers({"Accept: application/json"})
//    @GET("chats")
//    Call<ChatListModel> getChatHeads(
//            @Header("Authorization") String authorization
//    );
//    //Banner Api
//    @Headers({"Accept: application/json"})
//    @GET("banners")
//    Call<BannerRoommate> getBanner(
//            @Header("Authorization") String authorization
//    );
//    //Housing society Dropdown api
//    @Headers({"Accept: application/json"})
//    @GET("housing-societies")
//    Call<HousingSocietyResponse> getHousing(
//            @Header("Authorization") String authorization
//    );
//    //Room List Api
//    @Headers({"Accept: application/json"})
//    @GET("rooms")
//    Call<RoomListResponse> getRoomList(
//            @Header("Authorization") String authorization,
//            @Query("search_type") String searchType,
//            @Query("pincode") String pincode,
//            @Query("property_type[]") List<String> propertyTypes,
//            @Query("bedrooms[]") List<String> bedrooms,
//            @Query("roommate[]") List<String> roommates,
//            @Query("monthly_rent") String monthlyRent,
//            @Query("sort_by") String sortBy,
//            @Query("housing_society_id") String housing_society_id
//    );
//
//    @Headers({"Accept: application/json"})
//    @Multipart
//    @POST("rooms")
//    Call<GenericResponse> postRooms(
//            @Header("Authorization") String token,
//            @Part("address") RequestBody address,
//            @Part("pincode") RequestBody pincode,
//            @Part("housing_society_id") RequestBody housingSocietyId,
//            @Part("nearby_landmark") RequestBody nearbyLandmark,
//            @Part("property_type") RequestBody propertyType,
//            @Part("bedrooms") RequestBody bedrooms,
//            @Part("room_type") RequestBody roomType,
//            @Part("available_from") RequestBody availableFrom,
//            @Part("monthly_rent") RequestBody monthlyRent,
//            @Part("title") RequestBody title,
//            @Part("description") RequestBody description,
//            @Part("roommate") RequestBody roommate,
//            @Part("age_from") RequestBody ageFrom,
//            @Part("age_to") RequestBody ageTo,
//            @Part List<MultipartBody.Part> room_images,
//            @Part("room_amenities[]") List<RequestBody> roomAmenities,
//            @Part("suitable_for[]") List<RequestBody> suitableFor
//    );
//    @Headers({"Accept: application/json"})
//    @Multipart
//    @POST("chats")
//    Call<GenericResponse> postChats(
//            @Header("Authorization") String token,
//            @Part("receiver_id") RequestBody receiverId,
//            @Part("room_id") RequestBody roomId,
//            @Part("message") RequestBody message,
//            @Part List<MultipartBody.Part> chat_images
//    );
//
//    @Headers({"Accept: application/json"})
//    @GET("rooms/{id}")
//    Call<RoomDetailResponse> getRoomDetails(
//            @Header("Authorization") String authorization,
//            @Path("id") int id
//    );
//
//    @Headers({"Accept: application/json"})
//    @GET("chats/{receiver_id}/{room_id}")
//    Call<ChatMessageResponse> getChats(
//            @Header("Authorization") String token,
//            @Path("receiver_id") String receiver_id,
//            @Path("room_id") String room_id,
//            @Query("page") int page
//    );
//
//
//    @Headers({"Accept: application/json"})
//    @POST("set-room-activity")
//    Call<GenericResponse> updateRoomStatus(
//            @Header("Authorization") String authorization,
//            @Body UpdateRoomRequest updateRoomRequest
//    );
//
    @Headers({"Accept: application/json"})
    @POST("send-otp")
    Call<LoginResponse> login(
            @Body User user
    );
    @Headers({"Accept: application/json"})
    @POST("otp-verify")
    Call<VerifyOtpResponse> otp(
            @Body User user
    );
    @Headers({"Accept: application/json"})
    @POST("sign-up")
    Call<RegisterUserResponse> register(
            @Body User user
    );
    @Headers({"Accept: application/json"})
    @GET("my-profile")
    Call<ProfileResponse> profile(
            @Header("Authorization") String authorization
    );
    @Headers({"Accept: application/json"})
    @POST("addresses")
    Call<PostAddressResponse> postAddress(
            @Header("Authorization") String authorization,
            @Body PostAddress postAddress
    );
    @Headers({"Accept: application/json"})
    @GET("addresses")
    Call<GetAddressResponse> address(
            @Header("Authorization") String authorization
    );
    @DELETE("addresses/{id}")
    Call<GenericPostResponse> deleteAddress(
            @Header("Authorization") String authorization,
            @Path("id") int id
    );

    @Headers({"Accept: application/json"})
    @Multipart
    @POST("profile-update")
    Call<GenericPostResponse> updateProfile(
            @Header("Authorization") String token,
            @Part("name") RequestBody name,
            @Part("email") RequestBody email,
            @Part("mobile") RequestBody mobile,
            @Part MultipartBody.Part image
    );
    @Headers({"Accept: application/json"})
    @GET("banners")
    Call<HomeResponse> home(
            @Header("Authorization") String authorization
    );

    @Headers({"Accept: application/json"})
    @GET("notifications")
    Call<NotificationResponse> getNotification(
            @Header("Authorization") String authorization,
            @Query("page") int page
    );
    @Headers({"Accept: application/json"})
    @GET("shops")
    Call<ShopResponse> shops(
            @Header("Authorization") String authorization,
            @Query("longitude") String longitude,
            @Query("latitude") String latitude,
            @Query("module_id") int moduleId
    );

    @Headers({"Accept: application/json"})
    @GET("shop-items")
    Call<ShopItemResponse> getShopItems(
            @Header("Authorization") String authorization,
            @Query("longitude") String longitude,
            @Query("latitude") String latitude,
            @Query("shop_id") String shopId,
            @Query("menu_id") String menuId,
            @Query("filter_by") String filterBy,
            @Query("sort_by") String sortBy
    );

    @Headers({"Accept: application/json"})
    @POST("cart")
    Call<GenericPostResponse> addToCart(
            @Header("Authorization") String authorization,
            @Body AddToCartModel addToCartModel
    );

    @Headers({"Accept: application/json"})
    @PUT("cart")
    Call<CartResponse> updateCart(
            @Header("Authorization") String authorization,
            @Body AddToCartModel addToCartModel
    );

    @Headers({"Accept: application/json"})
    @GET("cart")
    Call<CartResponse> getCart(
            @Header("Authorization") String authorization
    );

    @Headers({"Accept: application/json"})
    @GET("wallet")
    Call<WalletResponse> getWallet(
            @Header("Authorization") String authorization
    );

    @Headers({"Accept: application/json"})
    @DELETE("cart/item/{id}")
    Call<GenericPostResponse> deleteCartItem(
            @Header("Authorization") String authorization,
            @Path("id") int itemId
    );
    @Headers({"Accept: application/json"})
    @POST("order")
    Call<GenericPostResponse> checkout(
            @Header("Authorization") String authorization,
            @Body CheckoutModel checkoutModel
    );

    @Headers({"Accept: application/json"})
    @GET("order")
    Call<OrderModel> getOrders(
            @Header("Authorization") String authorization
    );

    @Headers({"Accept: application/json"})
    @GET("order/{id}")
    Call<OrderDetails> getOrderDetails(
            @Header("Authorization") String authorization,
            @Path("id") int orderId
    );

    @Headers({"Accept: application/json"})
    @POST("fcm-version-update")
    Call<GenericPostResponse> addFcm(
            @Header("Authorization") String authorization,
            @Body UpdateFcm updateFcm
    );




}
