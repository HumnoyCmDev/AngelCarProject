package com.dollarandtrump.daogenerator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.dollarandtrump.daogenerator.PostCarDB;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "POST_CAR_DB".
*/
public class PostCarDBDao extends AbstractDao<PostCarDB, Long> {

    public static final String TABLENAME = "POST_CAR_DB";

    /**
     * Properties of entity PostCarDB.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property CarId = new Property(1, Integer.class, "CarId", false, "CAR_ID");
        public final static Property CarYear = new Property(2, Integer.class, "CarYear", false, "CAR_YEAR");
        public final static Property Gear = new Property(3, Integer.class, "Gear", false, "GEAR");
        public final static Property ShopRef = new Property(4, String.class, "ShopRef", false, "SHOP_REF");
        public final static Property BrandName = new Property(5, String.class, "BrandName", false, "BRAND_NAME");
        public final static Property CarSub = new Property(6, String.class, "CarSub", false, "CAR_SUB");
        public final static Property CarSubDetail = new Property(7, String.class, "CarSubDetail", false, "CAR_SUB_DETAIL");
        public final static Property CarDetail = new Property(8, String.class, "CarDetail", false, "CAR_DETAIL");
        public final static Property CarPrice = new Property(9, String.class, "CarPrice", false, "CAR_PRICE");
        public final static Property CarStatus = new Property(10, String.class, "CarStatus", false, "CAR_STATUS");
        public final static Property Plate = new Property(11, String.class, "Plate", false, "PLATE");
        public final static Property Name = new Property(12, String.class, "Name", false, "NAME");
        public final static Property ProvinceId = new Property(13, Integer.class, "ProvinceId", false, "PROVINCE_ID");
        public final static Property ProvinceName = new Property(14, String.class, "ProvinceName", false, "PROVINCE_NAME");
        public final static Property TelNumber = new Property(15, String.class, "TelNumber", false, "TEL_NUMBER");
        public final static Property DateModifyTime = new Property(16, java.util.Date.class, "DateModifyTime", false, "DATE_MODIFY_TIME");
        public final static Property CarImagePath = new Property(17, String.class, "CarImagePath", false, "CAR_IMAGE_PATH");
    };


    public PostCarDBDao(DaoConfig config) {
        super(config);
    }
    
    public PostCarDBDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"POST_CAR_DB\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"CAR_ID\" INTEGER," + // 1: CarId
                "\"CAR_YEAR\" INTEGER," + // 2: CarYear
                "\"GEAR\" INTEGER," + // 3: Gear
                "\"SHOP_REF\" TEXT," + // 4: ShopRef
                "\"BRAND_NAME\" TEXT," + // 5: BrandName
                "\"CAR_SUB\" TEXT," + // 6: CarSub
                "\"CAR_SUB_DETAIL\" TEXT," + // 7: CarSubDetail
                "\"CAR_DETAIL\" TEXT," + // 8: CarDetail
                "\"CAR_PRICE\" TEXT," + // 9: CarPrice
                "\"CAR_STATUS\" TEXT," + // 10: CarStatus
                "\"PLATE\" TEXT," + // 11: Plate
                "\"NAME\" TEXT," + // 12: Name
                "\"PROVINCE_ID\" INTEGER," + // 13: ProvinceId
                "\"PROVINCE_NAME\" TEXT," + // 14: ProvinceName
                "\"TEL_NUMBER\" TEXT," + // 15: TelNumber
                "\"DATE_MODIFY_TIME\" INTEGER," + // 16: DateModifyTime
                "\"CAR_IMAGE_PATH\" TEXT);"); // 17: CarImagePath
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"POST_CAR_DB\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PostCarDB entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer CarId = entity.getCarId();
        if (CarId != null) {
            stmt.bindLong(2, CarId);
        }
 
        Integer CarYear = entity.getCarYear();
        if (CarYear != null) {
            stmt.bindLong(3, CarYear);
        }
 
        Integer Gear = entity.getGear();
        if (Gear != null) {
            stmt.bindLong(4, Gear);
        }
 
        String ShopRef = entity.getShopRef();
        if (ShopRef != null) {
            stmt.bindString(5, ShopRef);
        }
 
        String BrandName = entity.getBrandName();
        if (BrandName != null) {
            stmt.bindString(6, BrandName);
        }
 
        String CarSub = entity.getCarSub();
        if (CarSub != null) {
            stmt.bindString(7, CarSub);
        }
 
        String CarSubDetail = entity.getCarSubDetail();
        if (CarSubDetail != null) {
            stmt.bindString(8, CarSubDetail);
        }
 
        String CarDetail = entity.getCarDetail();
        if (CarDetail != null) {
            stmt.bindString(9, CarDetail);
        }
 
        String CarPrice = entity.getCarPrice();
        if (CarPrice != null) {
            stmt.bindString(10, CarPrice);
        }
 
        String CarStatus = entity.getCarStatus();
        if (CarStatus != null) {
            stmt.bindString(11, CarStatus);
        }
 
        String Plate = entity.getPlate();
        if (Plate != null) {
            stmt.bindString(12, Plate);
        }
 
        String Name = entity.getName();
        if (Name != null) {
            stmt.bindString(13, Name);
        }
 
        Integer ProvinceId = entity.getProvinceId();
        if (ProvinceId != null) {
            stmt.bindLong(14, ProvinceId);
        }
 
        String ProvinceName = entity.getProvinceName();
        if (ProvinceName != null) {
            stmt.bindString(15, ProvinceName);
        }
 
        String TelNumber = entity.getTelNumber();
        if (TelNumber != null) {
            stmt.bindString(16, TelNumber);
        }
 
        java.util.Date DateModifyTime = entity.getDateModifyTime();
        if (DateModifyTime != null) {
            stmt.bindLong(17, DateModifyTime.getTime());
        }
 
        String CarImagePath = entity.getCarImagePath();
        if (CarImagePath != null) {
            stmt.bindString(18, CarImagePath);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PostCarDB readEntity(Cursor cursor, int offset) {
        PostCarDB entity = new PostCarDB( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // CarId
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // CarYear
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // Gear
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // ShopRef
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // BrandName
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // CarSub
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // CarSubDetail
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // CarDetail
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // CarPrice
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // CarStatus
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // Plate
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // Name
            cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13), // ProvinceId
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // ProvinceName
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // TelNumber
            cursor.isNull(offset + 16) ? null : new java.util.Date(cursor.getLong(offset + 16)), // DateModifyTime
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17) // CarImagePath
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PostCarDB entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCarId(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setCarYear(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setGear(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setShopRef(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setBrandName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setCarSub(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setCarSubDetail(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setCarDetail(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setCarPrice(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setCarStatus(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setPlate(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setName(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setProvinceId(cursor.isNull(offset + 13) ? null : cursor.getInt(offset + 13));
        entity.setProvinceName(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setTelNumber(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setDateModifyTime(cursor.isNull(offset + 16) ? null : new java.util.Date(cursor.getLong(offset + 16)));
        entity.setCarImagePath(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PostCarDB entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PostCarDB entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
