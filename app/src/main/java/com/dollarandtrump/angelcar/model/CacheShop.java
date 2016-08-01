package com.dollarandtrump.angelcar.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.dollarandtrump.angelcar.dao.PostCarDao;
import com.dollarandtrump.angelcar.dao.ProfileDao;

/**
 * สร้างสรรค์ผลงานโดย humnoyDeveloper ลงวันที่ 8/6/59.09:07น.
 *
 * @AngelCarProject
 */

@Table(name = "CacheShop")
public class CacheShop  extends Model{

    @Column(name = "Profile")
    public ProfileDao profileDao;

    @Column(name = "PostCar")
    public PostCarDao postCarDao;

    public ProfileDao getProfileDao() {
        return profileDao;
    }

    public void setProfileDao(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    public PostCarDao getPostCarDao() {
        return postCarDao;
    }

    public void setPostCarDao(PostCarDao postCarDao) {
        this.postCarDao = postCarDao;
    }
}
