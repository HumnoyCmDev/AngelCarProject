package com.dollarandtrump.angelcar.sql;

import com.activeandroid.query.Select;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.dao.ProfileDao;
import com.dollarandtrump.angelcar.interfaces.ShopQueryInterface;

import java.util.List;

/**
 * Created by Kotlin
 **/
public class QueryShop extends ShopQueryInterface{

    @Override
    public ProfileDao queryProfile() {
        return new Select().from(ProfileDao.class).executeSingle();
    }

    @Override
    public PostCarDao queryCarSingle() {
        return new Select().from(PostCarDao.class).executeSingle();
    }

    @Override
    public PostCarCollectionDao queryCarList() {
        List<PostCarDao> model = new Select().from(PostCarDao.class)
                .orderBy("BrandName ASC").execute();
        PostCarCollectionDao newDao = new PostCarCollectionDao();
        newDao.setListCar(model);
        return newDao;
    }

    @Override
    public List<PostCarDao> queryCarSub() {
        return new Select().from(PostCarDao.class)
                .groupBy("CarSub").having("COUNT(CarSub) > 0")
                .orderBy("BrandName ASC").execute();
    }

    @Override
    public PostCarCollectionDao queryBrandDuplicates() {
        List<PostCarDao> model = new Select().from(PostCarDao.class)
                .groupBy("BrandName").having("COUNT(BrandName) > 0")
                .orderBy("BrandName ASC").execute();
        PostCarCollectionDao newDao = new PostCarCollectionDao();
        newDao.setListCar(model);
        return newDao;
    }

    @Override
    public PostCarCollectionDao findCar(String brandName) {
        List<PostCarDao> model = new Select().from(PostCarDao.class)
                .where("BrandName LIKE ?",brandName).execute();
        PostCarCollectionDao newDao = new PostCarCollectionDao();
        newDao.setListCar(model);
        return newDao;
    }

    @Override
    public void deleteShop() {

    }
}
