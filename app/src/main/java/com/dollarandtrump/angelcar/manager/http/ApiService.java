package com.dollarandtrump.angelcar.manager.http;


import com.dollarandtrump.angelcar.dao.CarBrandCollectionDao;
import com.dollarandtrump.angelcar.dao.CarIdDao;
import com.dollarandtrump.angelcar.dao.CarSubCollectionDao;
import com.dollarandtrump.angelcar.dao.CountCarCollectionDao;
import com.dollarandtrump.angelcar.dao.FollowCollectionDao;
import com.dollarandtrump.angelcar.dao.LogFromServerDao;
import com.dollarandtrump.angelcar.dao.MessageAdminCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.PictureCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.RegisterResultDao;
import com.dollarandtrump.angelcar.dao.ShopCollectionDao;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/***************************************
 * สร้างสรรค์ผลงานดีๆ
 * โดย humnoy Android Developer
 * ลงวันที่ 13/2/59. เวลา 13:57
 ***************************************/

public interface ApiService {
    // Chat Api
    @GET("ios/api/ga_chatcar.php?operation=view")
    Call<MessageCollectionDao> viewMessage(@Query("message") String message);

    @GET("ios/api/ga_chatcar.php?operation=wait")
    Call<MessageCollectionDao> waitMessage(@Query("message") String message);

    @GET("ios/api/ga_chatcar.php?operation=reqofficer")
    Call<LogFromServerDao> regOfficer(@Query("message") String message);

    @GET("ios/api/ga_chatcar.php?operation=viewclient")
    Call<MessageCollectionDao> messageClient(@Query("message") String message);
    @GET("ios/api/ga_chatcar.php?operation=viewclient")
    Observable<MessageCollectionDao> observableMessageClient(@Query("message") String message);

    @GET("ios/api/ga_chatcar.php?operation=viewadminarray")
    Call<MessageAdminCollectionDao> messageAdmin(@Query("message") String message);
    @GET("ios/api/ga_chatcar.php?operation=viewadminarray")
    Observable<MessageAdminCollectionDao> observableMessageAdmin(@Query("message") String message);


    //Insert Post Car
    @FormUrlEncoded
    @POST("android/api/android/api/insertpost.php")
    Call<LogFromServerDao> postCar
    (
            @Field("shopref") String shopPref, // 1
            @Field("brandref") int brand, // toyota **ตัด
            @Field("subref") int subBrand,
            @Field("typeref") int subDetailBrand,
            @Field("cardetail") String carDetail, // ชื่อสั้นๆ
            @Field("caryear") int carYear, // ปีรถ
            @Field("carprice") String carPrice,// ราคารถ
            @Field("carstatus") String carStatus,// online or offline
            @Field("province") String province, // 0 - 77
            @Field("gear") String gear, // 0 or 1
            @Field("plate") String plate, // text ทะเบียนน
            @Field("name") String name, // ชื่อ นามสกุล
            @Field("telnumber") String telNumber // ชื่อ นามสกุล
    );

    @FormUrlEncoded
    @POST("android/api/android/api/insertpost.php")
    Observable<LogFromServerDao> observablePostCar
            (
                    @Field("shopref") String shopPref, // 1
                    @Field("brandref") int brand, // toyota **ตัด
                    @Field("subref") int subBrand,
                    @Field("typeref") int subDetailBrand,
                    @Field("cardetail") String carDetail, // ชื่อสั้นๆ
                    @Field("caryear") int carYear, // ปีรถ
                    @Field("carprice") String carPrice,// ราคารถ
                    @Field("carstatus") String carStatus,// online or offline
                    @Field("province") String province, // 0 - 77
                    @Field("gear") String gear, // 0 or 1
                    @Field("plate") String plate, // text ทะเบียนน
                    @Field("name") String name, // ชื่อ นามสกุล
                    @Field("telnumber") String telNumber // ชื่อ นามสกุล
            );

    // Feed Post Car
    @GET("android/api/getfeedpost.php")
    Call<PostCarCollectionDao> loadPostCar();
    @GET("android/api/getfeedpost.php")
    Call<PostCarCollectionDao> loadMorePostCar(@Query("datemore") String dateTime);
    @GET("android/api/getfeedpost.php")
    Call<PostCarCollectionDao> loadNewerPostCar(@Query("datenew") String dateTime);

    // countCar
    @GET("android/api/countcar.php")
    Call<CountCarCollectionDao> loadCountCar();

    //Registration Email Address
    @GET("android/api/createuser.php")
    Call<RegisterResultDao> registrationEmail(@Query("email") String email);

    //GET Picture All
    @FormUrlEncoded
    @POST("android/api/getimage.php")
    Call<PictureCollectionDao> loadAllPicture(@Field("carid") String carId);

    //GET CarID
    @GET("android/api/getcarid.php")
    Call<CarIdDao> loadCarId(@Query("shopref") String shopref);
    @GET("android/api/getcarid.php")
    Observable<CarIdDao> observableLoadCarId(@Query("shopref") String shopref);

    //GET CarModel
    @GET("android/api/getcarmodel.php")
    Call<PostCarCollectionDao> loadCarModel(@Query("carid") String carId);

    //GET Delete Chat
    @GET("android/api/deletechat.php")
    Call<LogFromServerDao> deleteChatList(@Query("userref") String userRef);

    //GET Add or Delete Follow
    @GET("android/api/controlfollow.php")
    Call<LogFromServerDao> follow(@Query("status") String status, @Query("carref") String carRef, @Query("shopref") String shopRef);

    //GET Follow Car Model
    @GET("android/api/getfollowmodel.php")
    Call<PostCarCollectionDao> loadFollowCarModel(@Query("shopref") String shopRef);

    //GET Check Follow
    @GET("android/api/checkfollow.php")
    Call<FollowCollectionDao> loadFollow(@Query("shopref") String shopRef);

    //GET Shop Data
    @GET("android/api/getDataShop.php")
    Call<ShopCollectionDao> loadDataShop(@Query("userref") String userRef, @Query("shopref") String shopRef);
    @GET("android/api/getDataShop.php")
    Observable<ShopCollectionDao> observableLoadShop(@Query("userref") String userRef, @Query("shopref") String shopRef);

    //GET Brand Car
    @GET("android/api/selectbrand.php")
    Call<CarBrandCollectionDao> loadDataBrand();
    //GET Brand Car
    @GET("android/api/selectbrand.php")
    Observable<CarBrandCollectionDao> observableLoadDataBrand();

    //GET Brand Sub
    @GET("android/api/selectbrand.php")
    Call<CarSubCollectionDao> loadDataBrandSub(@Query("brandid") int brandId);
    //GET Brand SubDetail
    @GET("android/api/selectbrand.php")
    Call<CarSubCollectionDao> loadDataBrandSubDetail(@Query("subid") int subId);

    //Filter Car
    /* :: parameter ::
    * 1. carname
    * 2. cartype_sub
    * 3. cardetail_sub
    * 4. pricestart
    * 5. priceend
    * 6. year
    * 7. gear
    * 8. datemore
    * */

    @GET("android/api/filtercar.php")
    Call<PostCarCollectionDao> loadFilterFeed(@QueryMap Map<String,String> queryMap);
}
