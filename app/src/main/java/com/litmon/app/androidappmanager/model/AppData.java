package com.litmon.app.androidappmanager.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by fukuo on 2015/09/10.
 */

@Table(name = "AppDatum")
public class AppData extends Model {

    public String getAppName(Context context) {
        setPackageManager(context);

        return pm.getApplicationLabel(appInfo).toString();
    }

    public Drawable getIcon(Context context){
        setPackageManager(context);

        try {
            return pm.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void setPackageManager(Context context) {
        pm = context.getPackageManager();

        try {
            appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }
    }

    public ApplicationInfo getApplicationInfo(Context context) {
        setPackageManager(context);

        return appInfo;
    }

    public interface ColumnString{
        public static String PACKAGE = "Package";
        public static String TAG = "Tag";
        public static String DESCRIPTION = "Description";
    }

    @Column(name = ColumnString.PACKAGE)
    public String packageName;

    @Column(name = ColumnString.TAG)
    public Tag tag;

    @Column(name = ColumnString.DESCRIPTION)
    public String description;

    PackageManager pm;
    ApplicationInfo appInfo;

    public AppData() {
        super();
    }

    public AppData(Context context, String packageName, Tag tag) {
        this(context, packageName, tag, "");
    }

    public AppData(Context context, String packageName, Tag tag, String description) {
        super();

        this.packageName = packageName;
        this.tag = tag;
        this.description = description;

        setPackageManager(context);
    }

    private static From query() {
        return new Select().from(AppData.class);
    }

    public static List<AppData> getAll() {
        return query().execute();
    }

    public static AppData find(long id) {
        return Model.load(AppData.class, id);
    }

    public static AppData findByPackage(String packageName){
        return query().where(ColumnString.PACKAGE + " = ?", packageName).executeSingle();
    }
}
