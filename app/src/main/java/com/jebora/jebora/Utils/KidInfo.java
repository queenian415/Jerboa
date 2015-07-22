package com.jebora.jebora.Utils;

import com.parse.ParseObject;
import com.parse.ParseRelation;

/**
 * Created by Tiffanie on 15-07-21.
 */
public class KidInfo {
    private String kidId;
    private String kidBirthday;
    private String kidGender;
    private String kidName;
    private String kidRelation;

    public KidInfo(String id, String bd, String gender, String name, String relation) {
        kidId = id;
        kidBirthday = bd;
        kidGender = gender;
        kidName = name;
        kidRelation = relation;
    }

    public String getKidId() { return kidId; }
    public String getKidBirthday() { return kidBirthday; }
    public String getKidGender() { return kidGender; }
    public String getKidName() { return kidName; }
    public String getKidRelation() { return kidRelation; }

}
