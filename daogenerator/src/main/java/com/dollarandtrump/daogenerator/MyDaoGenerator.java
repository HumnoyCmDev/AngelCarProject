package com.dollarandtrump.daogenerator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.dollarandtrump.daogenerator");
        Entity player = schema.addEntity("PostCarDB");

        player.addIdProperty();
        player.addIntProperty("CarId");
        player.addIntProperty("CarYear");
        player.addIntProperty("Gear");
        player.addStringProperty("ShopRef");
        player.addStringProperty("BrandName");
        player.addStringProperty("CarSub");
        player.addStringProperty("CarSubDetail");
        player.addStringProperty("CarDetail");
        player.addStringProperty("CarPrice");
        player.addStringProperty("CarStatus");
        player.addStringProperty("Plate");
        player.addStringProperty("Name");
        player.addIntProperty("ProvinceId");
        player.addStringProperty("ProvinceName");
        player.addStringProperty("TelNumber");
        player.addDateProperty("DateModifyTime");
        player.addStringProperty("CarImagePath");

        new DaoGenerator().generateAll(schema, args[0]);
    }
}
