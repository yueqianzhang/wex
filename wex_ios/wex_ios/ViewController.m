//
//  ViewController.m
//  wex_ios
//
//  Created by qian on 2020/11/24.
//

#import "ViewController.h"
#import <WebKit/WebKit.h>
#import "Wex.h"

@interface ViewController ()

@property (weak, nonatomic) IBOutlet WKWebView *webview;
@property (nonatomic, strong) Wex *wex;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.wex = [Wex bridgeForWebView:self.webview];
    [self.wex registerName:@"nativeSayHello" handler:^(NSDictionary * _Nonnull params, WexResponseCallback  _Nonnull responseCallback) {
        responseCallback(@{@"xxx": [NSString stringWithFormat:@"Hello %@, I'M %@", params[@"name"], @"native"]});
    }];

    NSURL *url = [NSBundle.mainBundle URLForResource:@"index" withExtension:@"html"];
    [self.webview loadFileURL:url allowingReadAccessToURL:url];
}


@end
