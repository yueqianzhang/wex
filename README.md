# WEX - WebView Extension with Naitve

## features

- 统一化。iOS/Android 在 WebView 使用相同方法调用    
- 无耦合。不依赖 WebView 具体实现    
- 轻量级。单文件，仅 100 行左右代码

## installation

Native 直接导入 Wex，即可开始使用。    
- iOS Wex 位置： https://github.com/yueqianzhang/wex/tree/main/wex_ios/wex_ios/wex    
- Android Wex 位置：https://github.com/yueqianzhang/wex/tree/main/wex_android/app/src/main/java/plus/zyq/wex_android/wex    

WebView 无需导入任何依赖。    

## usage

### WebView

```javascript
// 方法名必选, 参数和返回均为可选，类型为JSONObject
wex.invoke('hello', {
    name: "WebView"
}, function(res) {
    console.log('Hello ' + res.name);
});
```

### iOS

```objectivec
@property (nonatomic, strong) Wex *wex;

self.wex = [Wex bridgeForWebView:self.webview];
[self.wex registerName:@"hello" handler:^(NSDictionary * _Nonnull params, WexResponseCallback  _Nonnull responseCallback) {
    responseCallback(@{@"name": [NSString stringWithFormat:@"Hello %@, I'M %@", params[@"name"], @"native"]});
}];
```

### Android

```java
private Wex wex;

wex = Wex.bridgeFor(webView);
wex.registerHandler("hello", new Wex.Handler() {
    @Override
    public void onReceive(JSONObject data, Wex.ResponseCallback responseCallback) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", String.format("Hello %s, I'M %s", data.optString("name"), "native"));
            responseCallback.onCallback(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
});
```

## NOTE
- JS 参数和返回值均为 JSONObject，可以不处理回调函数
- 目前只支持 JS 调用原生注册的方法
- 没经过详细测试，如果有问题请反馈，我们将第一时间修复，感谢

## TODO
- iOS CocoaPods & Android Gradle 集成
- 统一 iOS/Android JS 实现
- 增加 API 文档自动生成
- 方便各端调试
