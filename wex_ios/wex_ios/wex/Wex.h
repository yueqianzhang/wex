//
//  Wex.h
//  wex_ios
//
//  Created by xwj-imac3 on 2020/11/26.
//

#import <Foundation/Foundation.h>
#import <WebKit/WebKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef void(^WexResponseCallback)(NSDictionary *resp);
typedef void(^WexHandler)(NSDictionary *data, WexResponseCallback responseCallback);

@interface Wex : NSObject

+ (instancetype)bridgeForWebView:(WKWebView *)webview;
- (void)registerName:(NSString *)name handler:(WexHandler)handler;

@end

NS_ASSUME_NONNULL_END
