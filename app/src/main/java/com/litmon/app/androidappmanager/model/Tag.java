package com.litmon.app.androidappmanager.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by fukuo on 2015/09/10.
 */

@Table(name = "Tags")
public class Tag extends Model {
    public interface ColumnString {
        public static String NAME = "name";
        public static String COLOR = "color";
    }

    @Column(name = ColumnString.NAME)
    public String name;

    @Column(name = ColumnString.COLOR)
    public TagColor color;

    public Tag(){
        super();
    }

    public Tag(String name, TagColor color){
        super();

        this.name = name;
        this.color = color;
    }

    private static From query(){
         return new Select().from(Tag.class);
    }

    public static Tag findByName(String name){
        return findBy(ColumnString.NAME, name);
    }

    public static Tag findBy(String key, String value){
        return query().where(key + " = ?", value).executeSingle();
    }

    public List<AppData> getAppDatum(){
        return getMany(AppData.class, AppData.ColumnString.TAG);
    }

    public static Tag create(String tagName, TagColor tagColor) {
        Tag tag = new Tag(tagName, tagColor);
        tag.save();

        return tag;
    }

    public static List<Tag> getAllTag(){
        return query().execute();
    }
}
