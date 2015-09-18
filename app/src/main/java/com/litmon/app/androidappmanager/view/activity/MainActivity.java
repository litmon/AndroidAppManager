package com.litmon.app.androidappmanager.view.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.litmon.app.androidappmanager.R;
import com.litmon.app.androidappmanager.adapter.AppDataAdapter;
import com.litmon.app.androidappmanager.model.AppData;
import com.litmon.app.androidappmanager.model.Tag;
import com.litmon.app.androidappmanager.model.TagColor;

import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_APP_DELETE     = 1001;
    private static final int REQUEST_APP_DELETE_ALL = 1002;

    ListView lv;
    AppDataAdapter adapter;

    PackageManager pm;

    Tag tag;
    AppData uninstallAppData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pm = getPackageManager();

        lv = (ListView) findViewById(R.id.listView);
        adapter = new AppDataAdapter(this, 0);

        refresh();

        lv.setAdapter(adapter);
        setAddTagMode();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_APP_DELETE) {
            switch (resultCode) {
                case RESULT_OK:
                    Log.d("AppManager", "App delete RESULT_OK");
                    adapter.remove(uninstallAppData);

                    break;
                case RESULT_CANCELED:
                    Log.d("AppManager", "App delete RESULT_CANCELED");

                    break;
                case RESULT_FIRST_USER:
                    Log.d("AppManager", "App delete RESULT_FIRST_USER");

                    break;
            }

            uninstallAppData = null;
        } else if (requestCode == REQUEST_APP_DELETE_ALL) {
            switch (resultCode) {
                case RESULT_OK:
                    adapter.remove(uninstallAppData);
                    break;
                case RESULT_CANCELED:
                case RESULT_FIRST_USER:
                    deleteNumber++;
                    break;
            }

            uninstallAllAppData();
        }
    }

    public void sortAdapter() {
        adapter.sort(new Comparator<AppData>() {
            @Override
            public int compare(AppData lhs, AppData rhs) {
                try {
                    return -Long.valueOf(pm.getPackageInfo(lhs.packageName, PackageManager.GET_META_DATA).firstInstallTime)
                            .compareTo(pm.getPackageInfo(rhs.packageName, PackageManager.GET_META_DATA).firstInstallTime);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                return 1;
            }
        });
    }

    public void refresh() {
        adapter.clear();

        if (tag == null) {
            for (ApplicationInfo info : pm.getInstalledApplications(BIND_AUTO_CREATE)) {
                AppData appData = AppData.findByPackage(info.packageName);

                if (appData == null) {
                    appData = new AppData(this, info.packageName, null);
                }

                adapter.add(appData);
            }
        } else {
            adapter.addAll(tag.getAppDatum());
        }

        sortAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.add_tag:
                final View content = View.inflate(this, R.layout.dialog_add_tag, null);

                final Spinner tagColorSpinner = (Spinner) content.findViewById(R.id.tagColor);
                final EditText tagNameEditText = (EditText) content.findViewById(R.id.tagName);

                ColorAdapter colorAdapter = new ColorAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        TagColor.getAllColors(this)
                );
                tagColorSpinner.setAdapter(colorAdapter);

                new AlertDialog.Builder(this)
                        .setTitle("追加したいタグの情報を入力してください")
                        .setView(content)
                        .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String tagName = tagNameEditText.getText().toString();
                                TagColor tagColor = (TagColor) tagColorSpinner.getSelectedItem();

                                Tag.create(tagName, tagColor);
                            }
                        })
                        .show();

                return true;

            case R.id.show_tag:
                final TagAdapter tagAdapter = new TagAdapter(this, android.R.layout.simple_expandable_list_item_1, Tag.getAllTag());

                new AlertDialog.Builder(this)
                        .setTitle("タグの表示")
                        .setAdapter(tagAdapter, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        tag = tagAdapter.getItem(which);

                                        refresh();
                                    }
                                }
                        )
                        .show();

                return true;

            case R.id.add_tag_mode:
                setAddTagMode();

                return true;
            case R.id.app_delete_mode:
                setUninstallMode();

                return true;
            case R.id.app_delete_all_mode:
                new AlertDialog.Builder(this)
                        .setTitle("全アプリアンインストール")
                        .setMessage("本当に実行しますか？")
                        .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uninstallAllAppData();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setAddTagMode() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final TagAdapter tagAdapter = new TagAdapter(MainActivity.this, android.R.layout.simple_list_item_1, Tag.getAllTag());
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("タグ登録")
                        .setAdapter(tagAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Tag tag = tagAdapter.getItem(which);
                                AppData appData = adapter.getItem(position);

                                appData.tag = tag;
                                appData.save();
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .show();
            }
        });
    }

    public void setUninstallMode() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                uninstallAppData = adapter.getItem(position);
                ApplicationInfo info = uninstallAppData.getApplicationInfo(MainActivity.this);

                Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.fromParts("package", info.packageName, null));
                intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);

                startActivityForResult(intent, REQUEST_APP_DELETE);
            }
        });
    }

    static int deleteNumber = 0;
    public void uninstallAllAppData() {
        if (deleteNumber >= adapter.getCount()) return;

        uninstallAppData = adapter.getItem(deleteNumber);

        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, Uri.fromParts("package", uninstallAppData.packageName, null));
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);

        startActivityForResult(intent, REQUEST_APP_DELETE_ALL);
    }

    class ColorAdapter extends ArrayAdapter<TagColor> {

        public ColorAdapter(Context context, int resource) {
            super(context, resource);
        }

        public ColorAdapter(Context context, int resource, List<TagColor> tagColors) {
            super(context, resource, tagColors);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView v = (TextView) super.getView(position, convertView, parent);
            v.setText(getItem(position).name);
            v.setTextColor(getItem(position).getColorCode());

            return v;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView v = (TextView) super.getDropDownView(position, convertView, parent);
            v.setText(getItem(position).name);
            v.setTextColor(getItem(position).getColorCode());

            return v;
        }
    }

    class TagAdapter extends ArrayAdapter<Tag> {

        public TagAdapter(Context context, int resource) {
            super(context, resource);
        }

        public TagAdapter(Context context, int resource, List<Tag> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView v = (TextView) super.getDropDownView(position, convertView, parent);
            v.setText(getItem(position).name);

            return v;
        }
    }
}

