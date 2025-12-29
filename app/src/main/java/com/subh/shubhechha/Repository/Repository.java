package com.subh.shubhechha.Repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.subh.shubhechha.Model.AddToCartModel;
import com.subh.shubhechha.Model.CartResponse;
import com.subh.shubhechha.Model.CheckoutModel;
import com.subh.shubhechha.Model.GenericPostResponse;
import com.subh.shubhechha.Model.GetAddressResponse;
import com.subh.shubhechha.Model.HomeResponse;
import com.subh.shubhechha.Model.LoginResponse;
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
import com.subh.shubhechha.Retrofit.ApiRequest;
import com.subh.shubhechha.Retrofit.RetrofitRequest;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private static final String TAG = Repository.class.getSimpleName();
    private final ApiRequest apiRequest;

    public static final int ERROR_SESSION_EXPIRED = 401;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public Repository() {
        apiRequest = RetrofitRequest.getRetrofitInstance().create(ApiRequest.class);
    }

    public LiveData<Boolean> getLoadingState() {
        return isLoading;
    }

    private void setLoading(boolean loading) {
        isLoading.postValue(loading);
    }

    public LiveData<ApiResponse<LoginResponse>> login(User user) {
        Call<LoginResponse> call = apiRequest.login(user);
        return performRequest(call);
    }

    public LiveData<ApiResponse<VerifyOtpResponse>> otp(User user) {
        Call<VerifyOtpResponse> call = apiRequest.otp(user);
        return performRequest(call);
    }

    public LiveData<ApiResponse<RegisterUserResponse>> register(User user) {
        Call<RegisterUserResponse> call = apiRequest.register(user);
        return performRequest(call);
    }
    public LiveData<ApiResponse<ProfileResponse>> profile(String auth) {
        Call<ProfileResponse> call = apiRequest.profile(auth);
        return performRequest(call);
    }
    public LiveData<ApiResponse<HomeResponse>> home(String auth) {
        Call<HomeResponse> call = apiRequest.home(auth);
        return performRequest(call);
    }
    public LiveData<ApiResponse<ShopResponse>> shops(String auth, String longitude, String latitude, int moduleId) {
        Call<ShopResponse> call = apiRequest.shops(auth,longitude,latitude,moduleId);
        return performRequest(call);
    }

    public LiveData<ApiResponse<PostAddressResponse>> postAddress(String auth, PostAddress postAddress) {
        Call<PostAddressResponse> call = apiRequest.postAddress(auth,postAddress);
        return performRequest(call);
    }
    public LiveData<ApiResponse<GetAddressResponse>> addresses(String auth) {
        Call<GetAddressResponse> call = apiRequest.address(auth);
        return performRequest(call);
    }
    public LiveData<ApiResponse<GenericPostResponse>> deleteAddress(String auth, int id) {
        Call<GenericPostResponse> call = apiRequest.deleteAddress(auth,id);
        return performRequest(call);
    }

    public LiveData<ApiResponse<ShopItemResponse>> getShopItems(String auth, String longitude, String latitude, String shopId, String menuId, List<String> filterBy, String sortBy) {
        Call<ShopItemResponse> call = apiRequest.getShopItems(auth, longitude, latitude, shopId, menuId, filterBy, sortBy
        );
        return performRequest(call);
    }
    public LiveData<ApiResponse<GenericPostResponse>> updateProfile(String token, String name, String email, String mobile, Uri imageUri, Context context) {

        try {
            // Create RequestBody for text fields
            RequestBody nameBody = createPartFromString(name);
            RequestBody emailBody = createPartFromString(email);
            RequestBody mobileBody = createPartFromString(mobile);

            // Create MultipartBody.Part for image (null-safe)
            MultipartBody.Part imagePart = null;
            if (imageUri != null) {
                File imageFile = getFileFromUri(imageUri, context);
                if (imageFile != null && imageFile.exists()) {
                    RequestBody requestFile = RequestBody.create(
                            MediaType.parse("image/*"),
                            imageFile
                    );
                    imagePart = MultipartBody.Part.createFormData(
                            "image",
                            imageFile.getName(),
                            requestFile
                    );
                    Log.d(TAG, "Image added for upload: " + imageFile.getAbsolutePath());
                } else {
                    Log.w(TAG, "Image file not found or invalid URI");
                }
            }

            Call<GenericPostResponse> call = apiRequest.updateProfile(
                    token,
                    nameBody,
                    emailBody,
                    mobileBody,
                    imagePart
            );

            return performRequest(call);

        } catch (Exception e) {
            Log.e(TAG, "Error preparing profile update request: " + e.getMessage(), e);
            MutableLiveData<ApiResponse<GenericPostResponse>> errorLiveData = new MutableLiveData<>();
            errorLiveData.setValue(new ApiResponse<>(null, false, "Failed to prepare request: " + e.getMessage(), -1));
            return errorLiveData;
        }
    }

    public LiveData<ApiResponse<GenericPostResponse>> addToCart(String auth, AddToCartModel addToCartModel) {
        Call<GenericPostResponse> call = apiRequest.addToCart(auth,addToCartModel);
        return performRequest(call);
    }

    public LiveData<ApiResponse<CartResponse>> updateCart(String auth, AddToCartModel addToCartModel) {
        Call<CartResponse> call = apiRequest.updateCart(auth, addToCartModel);
        return performRequest(call);
    }

    public LiveData<ApiResponse<CartResponse>> getCart(String auth) {
        Call<CartResponse> call = apiRequest.getCart(auth);
        return performRequest(call);
    }

    public LiveData<ApiResponse<OrderModel>> getOrdes(String auth) {
        Call<OrderModel> call = apiRequest.getOrders(auth);
        return performRequest(call);
    }
    public LiveData<ApiResponse<OrderDetails>> getOrderDetails(String auth, int orderId) {
        Call<OrderDetails> call = apiRequest.getOrderDetails(auth, orderId);
        return performRequest(call);
    }

    public LiveData<ApiResponse<GenericPostResponse>> addFcm(String auth, UpdateFcm updateFcm) {
        Call<GenericPostResponse> call = apiRequest.addFcm(auth, updateFcm );
        return performRequest(call);
    }

    public LiveData<ApiResponse<GenericPostResponse>> deleteCartItem(String auth, int itemId) {
        Call<GenericPostResponse> call = apiRequest.deleteCartItem(auth, itemId);
        return performRequest(call);
    }


    /**
     * Convert URI to File (null-safe)
     */
    private File getFileFromUri(Uri uri, Context context) {
        if (uri == null || context == null) {
            return null;
        }

        try {
            // Handle content:// URIs
            if ("content".equals(uri.getScheme())) {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream == null) {
                    return null;
                }

                // Create a temporary file
                File tempFile = new File(context.getCacheDir(), "profile_image_" + System.currentTimeMillis() + ".jpg");
                FileOutputStream outputStream = new FileOutputStream(tempFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                return tempFile;
            }
            // Handle file:// URIs
            else if ("file".equals(uri.getScheme())) {
                return new File(uri.getPath());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error converting URI to File: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Create RequestBody from String (null-safe)
     */
    private RequestBody createPartFromString(String value) {
        if (value == null) {
            value = "";
        }
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }
    public LiveData<ApiResponse<GenericPostResponse>> checkout(String auth, CheckoutModel checkoutModel) {
        Call<GenericPostResponse> call = apiRequest.checkout(auth,checkoutModel);
        return performRequest(call);
    }



    /**
     * Generic request performer for all API calls.
     * This method can be used by all future repositories.
     */
    protected <T> LiveData<ApiResponse<T>> performRequest(Call<T> call) {
        final MutableLiveData<ApiResponse<T>> liveData = new MutableLiveData<>();
        setLoading(true);

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(new ApiResponse<>(response.body(), true, null, response.code()));
                } else {
                    handleErrorResponse(response, liveData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                setLoading(false);
                handleNetworkFailure(call, t, liveData);
            }
        });

        return liveData;
    }

    /**
     * Handles error responses for any API.
     */
    private <T> void handleErrorResponse(Response<?> response, MutableLiveData<ApiResponse<T>> liveData) {
        try {
            String errorMessage = "An unknown error occurred.";

            if (response.code() == ERROR_SESSION_EXPIRED) {
                errorMessage = "Your session has expired. Please login again.";
                liveData.setValue(new ApiResponse<>(null, false, errorMessage, ERROR_SESSION_EXPIRED));
            } else if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                errorMessage = extractDynamicErrorMessage(errorBody);
                liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
            } else {
                liveData.setValue(new ApiResponse<>(null, false, errorMessage, response.code()));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error response: " + e.getMessage());
            liveData.setValue(new ApiResponse<>(null, false, "An unknown error occurred.", response.code()));
        }
    }

    /**
     * Extracts dynamic error message from JSON or HTML.
     */
    private String extractDynamicErrorMessage(String errorBody) {
        try {
            if (errorBody.trim().startsWith("{")) {
                JSONObject jsonObject = new JSONObject(errorBody);
                return jsonObject.optString("message", "An error occurred.");
            }
            Document document = Jsoup.parse(errorBody);
            return document.body() != null ? document.body().text().trim() : "An unknown error occurred.";
        } catch (Exception e) {
            Log.e(TAG, "Error while parsing the error message: " + e.getMessage());
            return "An unknown error occurred.";
        }
    }

    /**
     * Handles network failure.
     */
    private <T> void handleNetworkFailure(Call<?> call, Throwable t, MutableLiveData<ApiResponse<T>> liveData) {
        Log.e(TAG, "API call failed: " + t.getMessage(), t);
        String errorMessage = call.isCanceled()
                ? "Request was canceled"
                : "Failed to connect. Please check your network.";
        liveData.setValue(new ApiResponse<>(null, false, errorMessage, -1));
    }

    /**
     * Generic API response wrapper.
     */
    public static class ApiResponse<T> {
        public final T data;
        public final boolean isSuccess;
        public final String message;
        public final int code;

        public ApiResponse(T data, boolean isSuccess, String message, int code) {
            this.data = data;
            this.isSuccess = isSuccess;
            this.message = message;
            this.code = code;
        }

        public boolean isSuccess() {
            return isSuccess;
        }
    }
}