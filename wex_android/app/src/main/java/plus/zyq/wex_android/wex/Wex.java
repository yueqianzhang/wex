package plus.zyq.wex_android.wex;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Looper;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import plus.zyq.wex_android.BuildConfig;


public class Wex {

    public interface ResponseCallback {
        void onCallback(JSONObject resp);
    }

    public interface Handler {
        void onReceive(JSONObject data, ResponseCallback responseCallback);
    }

    private WeakReference<WebView> webView;
    private Map<String, Handler> handlers;

    @SuppressLint("SetJavaScriptEnabled")
    public static Wex bridgeFor(WebView webView) {
        Wex instance = new Wex();
        instance.webView = new WeakReference<>(webView);
        instance.handlers = new HashMap<>();

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                view.evaluateJavascript("var wex=(function(){var callbacks={};function invoke(name,data,callback){var cid;if(callback){cid=new Date().getTime().toString();callbacks[cid]=callback}window.wexBridge.postMessage(JSON.stringify({cid:cid,name:name,data:data}))};function nativeCallback(cid,res){if(!cid)return;var callback=callbacks[cid];callback(res);callback=undefined;}return{invoke:invoke,nativeCallback:nativeCallback}}());", null);
            }
        });
        webView.addJavascriptInterface(new WexBridge(instance), "wexBridge");

        return instance;
    }

    public void registerHandler(String name, Handler handler) {
        handlers.put(name, handler);
    }

    private Wex() {
    }

    private static class WexBridge {

        private final WeakReference<Wex> wexRef;

        WexBridge(Wex wex) {
            wexRef = new WeakReference<>(wex);
        }

        private final android.os.Handler mainHandler = new android.os.Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                JSONObject jsonObject = (JSONObject) msg.obj;
                Wex wex = wexRef.get();
                Handler handler = wex.handlers.get(jsonObject.optString("name"));
                if (handler == null) return;

                JSONObject data = jsonObject.optJSONObject("data");
                if (data == null) data = new JSONObject();
                ResponseCallback callback = new ResponseCallback() {
                    @Override
                    public void onCallback(JSONObject resp) {
                        String cid = jsonObject.optString("cid");
                        if (resp == null) resp = new JSONObject();
                        wex.webView.get().evaluateJavascript(String.format("wex.nativeCallback(%s, %s);", cid, jsonString(resp)), null);
                    }
                };
                handler.onReceive(data, callback);
            }

            private String jsonString(JSONObject jsonObject) {
                if (jsonObject == null) return "";
                return jsonObject.toString();
            }
        };

        @JavascriptInterface
        public void postMessage(String message) {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(message);
            } catch (JSONException e) {
                return;
            }
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Message msg = mainHandler.obtainMessage(0, jsonObject);
                    mainHandler.sendMessage(msg);
                }
            });
        }
    }

}
