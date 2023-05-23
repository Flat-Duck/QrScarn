package ly.smarthive.qrscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ResultActivity extends AppCompatActivity {
    public static final String TAG = ResultActivity.class.getSimpleName();
    private String URL = "";
    RelativeLayout bg;
    private final List<Shot> ShotsList = new ArrayList<>();
    private ShotsDataAdapter mAdapter;
    TextView name,status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        RecyclerView recyclerView = findViewById(R.id.shots_rv);
         bg = findViewById(R.id.bg_warning);
         name = findViewById(R.id.name);
         status = findViewById(R.id.status);

        mAdapter = new ShotsDataAdapter(ShotsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            URL = extras.getString("KEY_URL");
            Log.w("XXXXXXXXXXX",URL);
            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(URL);
            if (entry != null) {
                // fetch the data from cache
                String data = new String(entry.data, StandardCharsets.UTF_8);
                try {
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                getUserData(URL);
            }
        }
    }

    private void getUserData(String url) {
        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            VolleyLog.d(TAG, "Response: " + response.toString());
            Log.e("RE", response.toString());
            parseJsonFeed(response);
        }, error -> Log.d("VOLLEY ERROR", error.toString())) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("Content-Type", "application/json");
                return headerMap;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Parsing json response and passing the data to feed view list adapter
     **/

    @SuppressLint("NotifyDataSetChanged")
    private void parseJsonFeed(JSONObject response) {
        try {
            JSONObject feedObj = response.getJSONObject("data");
            String names = feedObj.getString("passport");
            String statuss = feedObj.getString("status");
            name.setText(names);
            status.setText(statuss);
            if(statuss.equals("مصاب")){

                bg.setBackgroundColor(Color.RED);
            }else{
                bg.setBackgroundColor(Color.GREEN);
            }
            JSONArray feedArray = feedObj.getJSONArray("shots");
            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject obj = (JSONObject) feedArray.get(i);
                Shot shot = new Shot();
                shot.setNumber(obj.getString("number"));
                shot.setType(obj.getString("type"));
                shot.setTakenAt(obj.getString("taken_at"));
                ShotsList.add(shot);
                mAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}