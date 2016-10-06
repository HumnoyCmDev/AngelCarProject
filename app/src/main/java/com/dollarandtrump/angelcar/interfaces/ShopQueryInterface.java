package com.dollarandtrump.angelcar.interfaces;

import com.activeandroid.Model;
import com.dollarandtrump.angelcar.dao.PostCarCollectionDao;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.dao.ProfileDao;

import java.util.List;

/**
 * Created by Kotlin
 **/
public abstract class ShopQueryInterface  {
   public abstract ProfileDao queryProfile();
   public abstract PostCarDao queryCarSingle();
   public abstract PostCarCollectionDao queryCarList();
   public abstract List<PostCarDao> queryCarSub();
   public abstract PostCarCollectionDao queryBrandDuplicates();
   public abstract PostCarCollectionDao findCar(String brandName);
   public abstract void deleteShop();
}
