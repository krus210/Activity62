package ru.netology.activity62;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewActivity extends AppCompatActivity {

    private static final String TITLE = "title";
    private static final String SNIPPET = "snippet";
    private static final String KEY_TEXT = "KEY_TEXT";
    private List<Map<String,String>> content;
    protected BaseAdapter listContentAdapter;
    protected SharedPreferences sharedPref;
    protected SwipeRefreshLayout swipeLayout;


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

        if (!data.equals(getString(R.string.large_text))) {
            SharedPreferences.Editor myEditor = sharedPref.edit();
            myEditor.putString(KEY_TEXT, getString(R.string.large_text));
            myEditor.apply();
        }

        ListView list = findViewById(R.id.list);
        content = prepareContent(data);
        listContentAdapter = createAdapter(content);
        list.setAdapter(listContentAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

    @NonNull
    private String getData() {
        sharedPref = getPreferences(MODE_PRIVATE);
        String data = sharedPref.getString(KEY_TEXT, "");
        return data != null ? data : "";
    }

}
