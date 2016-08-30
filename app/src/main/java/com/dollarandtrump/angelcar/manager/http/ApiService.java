package com.dollarandtrump.angelcar.manager.http;


import com.dollarandtrump.angelcar.dao.CarBrandCollectionDao;
import com.dollarandtrump.angelcar.dao.CarIdDao;
import com.dollarandtrump.angelcar.dao.CarSubCollectionDao;
import com.dollarandtrump.angelcar.dao.CountCarCollectionDao;
import com.dollarandtrump.angelcar.dao.FollowCollectionDao;
import com.dollarandtrump.angelcar.dao.ProvinceCollectionDao;
import com.dollarandtrump.angelcar.dao.ResponseDao;
import com.dollarandtrump.angelcar.dao.MessageAdminCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.PictureCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.RegisterResultDao;
import com.dollarandtrump.angelcar.dao.ShopCollectionDao;
import com.dollarandtrump.angelcar.dao.TopicCollectionDao;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;


public interface ApiService {

    // Chat Api
    @GET("ios/api/ga_chatcar.php?operation=view")
    Call<MessageCollectionDao> viewMessage(@Query("message") String message);
    @GET("ios/api/ga_chatcar.php?operation=view")
    Observable<MessageCollectionDao> observableViewMessage(@Query("message") String message);

    @GET("ios/api/ga_chatcar.php?operation=wait")
    Call<MessageCollectionDao> waitMessage(@Query("message") String message);

    @GET("ios/api/ga_chatcar.php?operation=reqofficer")
    Call<ResponseDao> regOfficer(@Query("message") String message);

    @GET("ios/api/ga_chatcar.php?operation=viewclient")
    Call<MessageCollectionDao> messageClient(@Query("message") String message);
    @GET("ios/api/ga_chatcar.php?operation=viewclient")
    Observable<MessageCollectionDao> observableMessageClient(@Query("message") String message);

    @GET("ios/api/ga_chatcar.php?operation=viewadminarray")
    Call<MessageAdminCollectionDao> messageAdmin(@Query("message") String message);
    @GET("ios/api/ga_chatcar.php?operation=viewadminarray")
    Observable<MessageAdminCollectionDao> observableMessageAdmin(@Query("message") String message);

    //read chat
    @GET("/ios/api/ga_chatcar.php?operation=read")
    Observable<ResponseDao> observableReadMessage(@Query("message") String currentMessageId);
    //read topic chat
    @GET("/ios/api/ga_chatadmin.php?operation=read")
    Observable<ResponseDao> observableReadTopicMessage(@Query("message") String currentMessageId);

    //View Chat Topic
    @GET("ios/api/ga_chatadmin.php?operation=view")
    Observable<MessageCollectionDao> observableViewMessageTopic(@Query("message") String message);
    @GET("ios/api/ga_chatadmin.php?operation=wait")
    Call<MessageCollectionDao> waitMessageTopic(@Query("message") String message);
    // Create Topic
    @GET("ios/api/ga_chatadmin.php?operation=newtopic")
    Observable<ResponseDao> observableCreateTopic(@Query("message") String message);

    // conversation Topic
    @GET("ios/api/ga_chatadmin.php?operation=viewsystems")
    Observable<MessageAdminCollectionDao> observableConversationTopic(@Query("message") String topId);

    //update post
    @GET("android/api/updatepost.php")
    Observable<ResponseDao> observableAnnounce(@Query("carid") String carId);


    //Insert Post Car
    @FormUrlEncoded
    @POST("android/api/insertpost.php")
    Call<ResponseDao> postCar
    (
            @Field("shopref") String shopPref, // 1
            @Field("brandref") int brand, // toyota **ตัด
            @Field("subref") int subBrand,
            @Field("typeref") int subDetailBrand,
            @Field("cartitle") String carTitle, // ชื่อสั้นๆ
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
    @POST("android/api/insertpost.php")
    Observable<ResponseDao> observablePostCar
            (
                    @Field("shopref") String shopPref, // 1
                    @Field("brandref") int brand, // toyota **ตัด
                    @Field("subref") int subBrand,
                    @Field("typeref") int subDetailBrand,
                    @Field("cartitle") String carTitle, // ชื่อสั้นๆ
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
    @POST("ios/api/ga_car.php?operation=new")
    Observable<ResponseDao> observablePost(@Query("message") String message);

    @FormUrlEncoded
    @POST("android/api/updateshop.php")
    Observable<ResponseDao> observableUpdatePostCar
            (
                    @Field("carid") int carId,
                    @Field("brandref") int brand, // toyota **ตัด
                    @Field("subref") int subBrand,
                    @Field("typeref") int subDetailBrand,
                    @Field("cartitle") String carTitle,
                    @Field("cardetail") String carDetail, // ชื่อสั้นๆ
                    @Field("caryear") int carYear, // ปีรถ
                    @Field("carprice") String carPrice,// ราคารถ
                    @Field("province") String province, // 0 - 77
                    @Field("gear") String gear, // 0 or 1
                    @Field("name") String name, // ชื่อ นามสกุล
                    @Field("telnumber") String telNumber // ชื่อ นามสกุล
            );

    /**delete/update car**/
    @GET("ios/api/ga_car.php?operation=updatestatus")
    Observable<ResponseDao> observableDeleteCar(@Query("message") String message);/**carid||{offline(ลบ),online(เลื่อนประกาศ)}**/

     /**Feed Post Car**/
    @GET("android/api/getfeedpost.php")
    Call<PostCarCollectionDao> loadPostCar();
    @GET("android/api/getfeedpost.php")
    Call<PostCarCollectionDao> loadMorePostCar(@Query("datemore") String dateTime);
    @GET("android/api/getfeedpost.php")
    Call<PostCarCollectionDao> loadNewerPostCar(@Query("datenew") String dateTime);

    /** countCar*/
    @GET("android/api/countcar.php")
    Call<CountCarCollectionDao> loadCountCar();
    @GET("android/api/countcar.php")
    Observable<CountCarCollectionDao> observableLoadCountCar();

    //Registration Email Address
    @GET("android/api/createuser.php")
    Call<RegisterResultDao> registrationEmail(@Query("email") String email);

    //GET Picture All
    @FormUrlEncoded
    @POST("android/api/getimage.php")
    Call<PictureCollectionDao> loadAllPicture(@Field("carid") String carId);
    @FormUrlEncoded
    @POST("android/api/getimage.php")
    Observable<PictureCollectionDao> observableLoadAllImage(@Field("carid") String carId);

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
    Call<ResponseDao> deleteChatList(@Query("userref") String userRef);

    //GET Add or Delete Follow
    @GET("android/api/controlfollow.php")
    Call<ResponseDao> follow(@Query("status") String status, @Query("carref") String carRef, @Query("shopref") String shopRef);

    //GET Follow Car Model
    @GET("android/api/getfollowmodel.php")
    Call<PostCarCollectionDao> loadFollowCarModel(@Query("shopref") String shopRef);

    //GET Check Follow
    @GET("android/api/checkfollow.php")
    Call<FollowCollectionDao> loadFollow(@Query("shopref") String shopRef);
    @GET("android/api/checkfollow.php")
    Observable<FollowCollectionDao> observableLoadFollow(@Query("shopref") String shopRef);

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
    /**GET Brand SubDetail**/
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

    //Edit Shop
    @GET("ios/api/cls_shop.php?operation=edit")
    Observable<ResponseDao> observableEditShop(@Query("message") String message);

    //Register
    @FormUrlEncoded
    @POST("android/api/registerfirebase.php")
    Observable<ResponseDao> sendTokenRegistration(@Field("userref") String userRef, @Field("shopref") String shopRef, @Field("firebaseid") String token);

    /**Feed Topic**/
    @GET("ios/api/ga_chatadmin.php?operation=viewtopic")
    Observable<TopicCollectionDao> observableFeedTopic(@Query("message") String message);

    /**หลักฐานรูป**/
    @GET("ios/api/ga_car.php?operation=evidence")
    Observable<ResponseDao> observableEvidence(@Query("message") String message);/**carid||shopid||{owner,delegate,dealers}**/

    /**จังหวัด**/
    @GET("ios/api/getprovince.php")
    Observable<ProvinceCollectionDao> observableProvince();
}
