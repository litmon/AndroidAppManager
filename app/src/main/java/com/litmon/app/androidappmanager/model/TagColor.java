package com.litmon.app.androidappmanager.model;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.litmon.app.androidappmanager.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fukuo on 2015/09/11.
 */
@Table(name = "TagColors")
public class TagColor extends Model {

    public interface ColumnString{
        public static String NAME = "name";
        public static String COLOR = "color";
    }

    @Column(name = ColumnString.NAME)
    public String name;

    @Column(name = ColumnString.COLOR)
    public int color;

    static {
        findOrCreate("アメジスト", 0x9b59b6);
        findOrCreate("ピーターリバー", 0x3498db);
        findOrCreate("エメラルド", 0x2ecc71);
        findOrCreate("サンフラワー", 0xf1c40f);
        findOrCreate("オレンジ", 0xf39c12);
        findOrCreate("パンプキン", 0xd35400);
        findOrCreate("シルバー", 0xbdc3c7);
    }

    public TagColor() {
        super();
    }

    public TagColor(String name, int color) {
        super();

        this.name = name;
        this.color = color;
    }

    public static List<TagColor> getAllColors(Context context) {
        return new Select().from(TagColor.class).execute();
    }

    static TagColor findOrCreate(String name, int color) {
        TagColor tagColor = findByName(name);
        if (findByName(name) == null) {
            tagColor = create(name, color);
        }

        return tagColor;
    }

    static TagColor findByName(String name) {
        return new Select().from(TagColor.class).where("name = ?", name).executeSingle();
    }

    static TagColor create(String name, int color) {
        TagColor tagColor = new TagColor(name, color);
        tagColor.save();

        return tagColor;
    }

    public int getColorCode(){
        return hexToColorCode(color);
    }

    public static int hexToColorCode(int color){
        return Color.parseColor("#" + Integer.toHexString(color));
    }
}