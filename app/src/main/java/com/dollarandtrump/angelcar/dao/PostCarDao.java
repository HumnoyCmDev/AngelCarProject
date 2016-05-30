package com.dollarandtrump.angelcar.dao;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.dollarandtrump.angelcar.utils.AngelCarUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by humnoy on 10/3/59.
 */
@Parcel(value = Parcel.Serialization.BEAN, analyze = PostCarDao.class)
@Table(name = "PostCar")
public class PostCarDao extends Model implements Serializable {
   @Column(name = "CarId")          @SerializedName("carid")            @Expose int carId;
   @Column(name = "ShopRef")        @SerializedName("shopref")          @Expose String shopRef;
   @Column(name = "BrandName")      @SerializedName("brand_name")       @Expose String carName;
   @Column(name = "CarSub")         @SerializedName("sub_name")         @Expose String carSub;
   @Column(name = "CarSubDetail")   @SerializedName("type_name")        @Expose String carSubDetail;
   @Column(name = "CarDetail")      @SerializedName("cardetail")        @Expose String carDetail;
   @Column(name = "CarYear")        @SerializedName("caryear")          @Expose int carYear;
   @Column(name = "CarPrice")       @SerializedName("carprice")         @Expose String carPrice;
   @Column(name = "CarStatus")      @SerializedName("carstatus")        @Expose String carStatus;
   @Column(name = "Gear")           @SerializedName("gear")             @Expose int gear;
   @Column(name = "Plate")          @SerializedName("plate")            @Expose String plate;
   @Column(name = "Name")           @SerializedName("name")             @Expose String name;
   @Column(name = "ProvinceId")     @SerializedName("province_id")      @Expose int provinceId;
   @Column(name = "ProvinceName")   @SerializedName("province_name")    @Expose String province;
   @Column(name = "TelNumber")      @SerializedName("telnumber")        @Expose String phone;
   @Column(name = "DateModifyTime") @SerializedName("carmodify")        @Expose Date carModifyTime;
   @Column(name = "CarImagePath")   @SerializedName("carimagepath")     @Expose String carImagePath;

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getCarSub() {
        return carSub;
    }

    public void setCarSub(String carSub) {
        this.carSub = carSub;
    }

    public String getCarSubDetail() {
        return carSubDetail;
    }

    public void setCarSubDetail(String carSubDetail) {
        this.carSubDetail = carSubDetail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCarModifyTime() {
        return carModifyTime;
    }

    public void setCarModifyTime(Date carModifyTime) {
        this.carModifyTime = carModifyTime;
    }

    public String getCarImagePath() {
        return carImagePath;
    }

    public String getCarImageThumbnailPath(){
        return "http://angelcar.com/ios/data/gadata/thumbnailcarimages/"+carImagePath;
    }
    public String getCarImageFullHDPath(){
        return "http://angelcar.com/ios/data/gadata/carimages/"+carImagePath;
    }

    public void setCarImagePath(String carImagePath) {
        this.carImagePath = carImagePath;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public String getShopRef() {
        return shopRef;
    }

    public void setShopRef(String shopRef) {
        this.shopRef = shopRef;
    }

    public String getCarDetail() {
        return carDetail;
    }

    public void setCarDetail(String carDetail) {
        this.carDetail = carDetail;
    }

    public int getCarYear() {
        return carYear;
    }

    public void setCarYear(int carYear) {
        this.carYear = carYear;
    }

    public String getCarPrice() {
        return carPrice;
    }

    public void setCarPrice(String carPrice) {
        this.carPrice = carPrice;
    }

    public String getCarStatus() {
        return carStatus;
    }

    public void setCarStatus(String carStatus) {
        this.carStatus = carStatus;
    }


    public int getGear() {
        return gear;
    }

    public void setGear(int gear) {
        this.gear = gear;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String toMessage(){
        String titleCar = String.format("<p><b><u><i><h3>%s</h3></i></u></b><p>",
                (getCarName()+" "+getCarSub()+" "+getCarSubDetail()+" ปี "+getCarYear()));
        String topic = String.format("<p><b>%s</b></p><p>%s<b>",
                AngelCarUtils.subTopic(getCarDetail()), AngelCarUtils.subDetail(getCarDetail()));

        String detail = String.format(
                "<p>" +
                "<b>ราคา.</b> %s" + //"<b>ราคา.</b> %s " +
                "</p>",getCarPrice());

        String message = titleCar+topic+detail;
        return String.format("<header>%s</header>",message);
    }

    public String toTopicCar(){
        return getCarName()+" "+getCarSub()+" "+getCarSubDetail()+" ปี "+getCarYear();
    }
}
