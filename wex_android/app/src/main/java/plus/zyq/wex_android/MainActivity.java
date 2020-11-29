package plus.zyq.wex_android;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import plus.zyq.wex_android.wex.Wex;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private Wex wex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webView);

        wex = Wex.bridgeFor(webView);
        wex.registerHandler("nativeSayHello", new Wex.Handler() {
            @Override
            public void onReceive(JSONObject data, Wex.ResponseCallback responseCallback) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("xxx", String.format("Hello %s, I'M %s", data.optString("name"), "native"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                responseCallback.onCallback(jsonObject);
            }
        });

        webView.loadUrl("file:///android_asset/html/index.html");
    }

}