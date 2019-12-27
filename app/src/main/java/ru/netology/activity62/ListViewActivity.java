package ru.netology.activity62;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewActivity extends AppCompatActivity {

    private static final String TITLE = "title";
    private static final String SNIPPET = "snippet";
    private static final String KEY_TEXT = "KEY_TEXT";
    private static final String KEY_INSTANCE_STATE = "key instance state";
    private List<Map<String,String>> content;
    protected BaseAdapter listContentAdapter;
    protected SharedPreferences sharedPref;
    protected SwipeRefreshLayout swipeLayout;
    private ArrayList<Integer> listDeleted;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();

    }

    private void initViews() {
        String data = getData();

        if (data == null) {
            data = getString(R.string.large_text);
            SharedPreferences.Editor myEditor = sharedPref.edit();
            myEditor.putString(KEY_TEXT, data);
            myEditor.apply();
        }

        ListView list = findViewById(R.id.list);
        content = prepareContent(data);
        listContentAdapter = createAdapter(content);
        list.setAdapter(listContentAdapter);
        listDeleted = new ArrayList<>();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listDeleted.add(position);
                content.remove(position);
                listContentAdapter.notifyDataSetChanged();
            }
        });

        swipeLayout = findViewById(R.id.swipeRefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String data = getData();
                content.clear();
                List<Map<String, String>> newContent = prepareContent(data);
                content.addAll(newContent);
                listContentAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            }
        });

    }

    @NonNull
    private BaseAdapter createAdapter(List<Map<String,String>> content) {
        return new SimpleAdapter(this, content, R.layout.views,
                new String[]{TITLE, SNIPPET}, new int[]{R.id.txtTitle, R.id.txtSnippet});
    }

    @NonNull
    private List<Map<String,String>> prepareContent(String data) {
        List<Map<String, String>> content = new ArrayList<>();
        String[] arrayContent = data.split("\n\n");
        for (String row : arrayContent) {
            Map<String, String> mapRow  = new HashMap<>();
            mapRow.put(TITLE, row);
            mapRow.put(SNIPPET, String.valueOf(row.length()));
            content.add(mapRow);
        }
        return content;
    }

    private String getData() {
        sharedPref = getPreferences(MODE_PRIVATE);
        return sharedPref.getString(KEY_TEXT, "");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(KEY_INSTANCE_STATE, listDeleted);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(KEY_INSTANCE_STATE)) {
            listDeleted = savedInstanceState.getIntegerArrayList(KEY_INSTANCE_STATE);
            if (listDeleted != null) {
                for (Integer position : listDeleted) {
                    content.remove(position.intValue());
                }
            }
            listContentAdapter.notifyDataSetChanged();
        }

    }
}
