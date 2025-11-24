package com.subh.shubhechha.ViewModel;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.subh.shubhechha.Model.GenericPostResponse;
import com.subh.shubhechha.Model.GetAddressResponse;
import com.subh.shubhechha.Model.HomeResponse;
import com.subh.shubhechha.Model.LoginResponse;
import com.subh.shubhechha.Model.PostAddress;
import com.subh.shubhechha.Model.PostAddressResponse;
import com.subh.shubhechha.Model.ProfileResponse;
import com.subh.shubhechha.Model.RegisterUserResponse;
import com.subh.shubhechha.Model.ShopItemResponse;
import com.subh.shubhechha.Model.ShopResponse;
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
//
//    public LiveData<RoommateRepository.ApiResponse<GenericResponse>> updateRoomStatus(String auth, UpdateRoomRequest updateRoomRequest) {
//        return repository.updateRoomStatus(auth, updateRoomRequest);
//    }
//
//    public LiveData<RoommateRepository.ApiResponse<GenericResponse>> roomFavourite(String auth, UpdateRoomRequest updateRoomRequest) {
//        return repository.roomFavourite(auth, updateRoomRequest);
//    }
//
//    public LiveData<RoommateRepository.ApiResponse<RoomListResponse>> getRoomList(String token, String searchType, String pincode, List<String> propertyTypes, List<String> bedrooms, List<String> roommates, String monthlyRent, String sortBy,String housing_society_id) {
//        return repository.getRoomList(token, searchType, pincode, propertyTypes, bedrooms, roommates, monthlyRent, sortBy,housing_society_id);
//    }
//
//    public LiveData<RoommateRepository.ApiResponse<RoomDetailResponse>> getRoomListDetails(String token, int id) {
//        return repository.getRoomDetails(token, id);
//    }
//
//    public LiveData<RoommateRepository.ApiResponse<HousingSocietyResponse>> getHousing(String token) {
//        return repository.getHousing(token);
//    }
//
//    public LiveData<RoommateRepository.ApiResponse<GenericResponse>> removeFavourite(String auth, int roomId) {
//        return repository.removeFavourite(auth, roomId);
//    }
//
//    public LiveData<RoommateRepository.ApiResponse<BannerRoommate>> getBanners(String token) {
//        return repository.getBanners(token);
//    }
//
//    public LiveData<RoommateRepository.ApiResponse<ChatListModel>> getChatHeadsLiveData() {
//        return chatHeadsLiveData;
//    }
//
//    public void loadChatHeads(String token) {
//        repository.getChatHeads(token).observeForever(response -> {
//            chatHeadsLiveData.postValue(response);
//        });
//    }
//
//    public LiveData<RoommateRepository.ApiResponse<ChatMessageResponse>> getChats(String token, String receiver_id, String room_id, int page) {
//        return repository.getChats(token, receiver_id, room_id, page);
//    }
//    public LiveData<RoommateRepository.ApiResponse<GenericResponse>> sendMessage(
//            String token,
//            RequestBody receiverId,
//            RequestBody roomId,
//            RequestBody message,
//            List<MultipartBody.Part> chatImages)  {
//        return repository.sendMessage(token, receiverId, roomId, message, chatImages);
//    }
}