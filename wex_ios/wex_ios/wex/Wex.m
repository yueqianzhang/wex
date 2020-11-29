//
//  Wex.m
//  wex_ios
//
//  Created by xwj-imac3 on 2020/11/26.
//

#import "Wex.h"

@interface Wex ()  <WKScriptMessageHandler>

@property (nonatomic, weak) WKWebView *webView;
@property (nonatomic, strong) NSMutableDictionary<NSString *, WexHandler> *handlers;

@end

@implementation Wex

+ (instancetype)bridgeForWebView:(WKWebView *)webview {
    Wex *instance = [[Wex alloc] init];
    instance.webView = webview;
    instance.handlers = [NSMutableDictionary dictionary];
    
    WKUserContentController *userController = webview.configuration.userContentController;
    WKUserScript *userScript = [[WKUserScript alloc] initWithSource:@"var wex=(function(){var callbacks={};function invoke(name,data,callback){var cid;if(callback){cid=new Date().getTime().toString();callbacks[cid]=callback}window.webkit.messageHandlers.wexBridge.postMessage({cid:cid,name:name,data:data})}function nativeCallback(cid,res){if(!cid)return;var callback=callbacks[cid];callback(res);callback=undefined;}return{invoke:invoke,nativeCallback:nativeCallback}}());" injectionTime:WKUserScriptInjectionTimeAtDocumentStart forMainFrameOnly:NO];
    [userController addUserScript:userScript];
    [userController addScriptMessageHandler:instance name:@"wexBridge"];
    
    return instance;
}

- (void)registerName:(NSString *)name handler:(WexHandler)handler {
    self.handlers[name] = handler;
}

#pragma mark - Private methods

- (void)userContentController:(WKUserContentController *)userContentController didReceiveScriptMessage:(WKScriptMessage *)message {
    if (![message.body isKindOfClass:NSDictionary.class]) return;
    WexHandler handler = self.handlers[message.body[@"name"]];
    if (!handler) return;
    
    NSDictionary *data = message.body[@"data"] ?: @{};
    WexResponseCallback callback = ^(NSDictionary *resp) {
        NSString *cid = message.body[@"cid"];
        [self.webView evaluateJavaScript:[NSString stringWithFormat:@"wex.nativeCallback(%@, %@);",
                                          cid, [self jsonString:resp]] completionHandler:nil];
    };
    
    handler(data, callback);
}

#pragma mark - Utils

- (NSString *)jsonString:(NSDictionary *)dictionary {
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dictionary options:0 error:&error];
    if (error) return @"";
    return [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
}

@end
