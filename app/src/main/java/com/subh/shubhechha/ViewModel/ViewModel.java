package com.subh.shubhechha.ViewModel;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
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
import com.subh.shubhechha.Repository.Repository;

import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ViewModel extends AndroidViewModel {

    private Repository repository;


    public ViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository();
    }

    public LiveData<Repository.ApiResponse<LoginResponse>> login(User user ) {
        return repository.login(user);
    }

    public LiveData<Repository.ApiResponse<VerifyOtpResponse>> otp(User user ) {
        return repository.otp(user);
    }

    public LiveData<Repository.ApiResponse<RegisterUserResponse>> register(User user ) {
        return repository.register(user);
    }

    public LiveData<Repository.ApiResponse<ProfileResponse>>profile(String auth ) {
        return repository.profile(auth);
    }
    public LiveData<Repository.ApiResponse<PostAddressResponse>> postAddress(String auth, PostAddress postAddress) {
        return repository.postAddress(auth,postAddress);
    }

    public LiveData<Repository.ApiResponse<GetAddressResponse>>addresses(String auth ) {
        return repository.addresses(auth);
    }

    public LiveData<Repository.ApiResponse<GenericPostResponse>>deleteAddress(String auth,int id ) {
        return repository.deleteAddress(auth,id);
    }
    public LiveData<Repository.ApiResponse<HomeResponse>>home(String auth) {
        return repository.home(auth);
    }
    public LiveData<Repository.ApiResponse<ShopResponse>>shops(String auth, String longitude, String latitude, int moduleId) {
        return repository.shops(auth,longitude,latitude,moduleId);
    }

    public LiveData<Repository.ApiResponse<ShopItemResponse>> getShopItems(String auth, String longitude, String latitude, String shopId, String menuId, List<String> filterBy, String sortBy) {
        return repository.getShopItems(auth, longitude, latitude, shopId, menuId, filterBy, sortBy);
    }

    public LiveData<Repository.ApiResponse<GenericPostResponse>> updateProfile(String token, String name, String email, String mobile, Uri imageUri, Context context) {
        return repository.updateProfile(token, name, email, mobile, imageUri, context);
    }

    public LiveData<Repository.ApiResponse<GenericPostResponse>> addToCart(String auth, AddToCartModel addToCartModel) {
        return repository.addToCart(auth,addToCartModel);
    }
    public LiveData<Repository.ApiResponse<CartResponse>> updateCart(String auth, AddToCartModel addToCartModel) {
        return repository.updateCart(auth, addToCartModel);
    }

    public LiveData<Repository.ApiResponse<CartResponse>> getCart(String auth) {
        return repository.getCart(auth);
    }
    public LiveData<Repository.ApiResponse<OrderModel>> getOrders(String auth) {
        return repository.getOrdes(auth);
    }
    public LiveData<Repository.ApiResponse<OrderDetails>> getOrderDetails(String auth, int orderId) {
        return repository.getOrderDetails(auth, orderId);
    }
    public LiveData<Repository.ApiResponse<GenericPostResponse>> addFcm(String auth, UpdateFcm updateFcm) {
        return repository.addFcm(auth, updateFcm);
    }
    public LiveData<Repository.ApiResponse<GenericPostResponse>> deleteCartItem(String auth, int itemId) {
        return repository.deleteCartItem(auth, itemId);
    }

    public LiveData<Repository.ApiResponse<GenericPostResponse>> checkout(String auth, CheckoutModel checkoutModel) {
        return repository.checkout(auth,checkoutModel);
    }

}