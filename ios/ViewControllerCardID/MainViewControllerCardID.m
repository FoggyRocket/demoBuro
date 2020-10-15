
#import <UIKit/UIKit.h>
#import "CallbackHolder.h"
#import <MiSnapSDK/MiSnapSDK.h>
#import "MainViewControllerCardID.h"

@implementation ReaderCardID

RCT_EXPORT_MODULE(ReaderCardID);


RCT_EXPORT_METHOD(startReaderFront:(RCTResponseSenderBlock)successCallback error:(RCTResponseSenderBlock)failureCallback){

  [[CallbackHolder sharedInstance] setSuccessCallback:successCallback];
  [[CallbackHolder sharedInstance] setFailureCallback:failureCallback];

  [self performSelectorOnMainThread:@selector(presentMitekUI) withObject:nil waitUntilDone:NO];
  
}
RCT_EXPORT_METHOD(startReaderBack:(RCTResponseSenderBlock)successCallback error:(RCTResponseSenderBlock)failureCallback){

  [[CallbackHolder sharedInstance] setSuccessCallback:successCallback];
  [[CallbackHolder sharedInstance] setFailureCallback:failureCallback];

  [self performSelectorOnMainThread:@selector(presentMitekUI) withObject:nil waitUntilDone:NO];
  
}
-(void)presentMitekUIBack{

  UIViewController *topController = [UIApplication sharedApplication].keyWindow.rootViewController;

  NSMutableDictionary* parameters = [NSMutableDictionary
                                     dictionaryWithDictionary:[MiSnapSDKViewControllerUX2 defaultParametersForIdCardBack]];

  [parameters setObject:@"test" forKey:kMiSnapServerType];
  [parameters setObject:@"0.0" forKey:kMiSnapServerVersion];
  [parameters setObject:@"Mobile Deposit Check Front" forKey:kMiSnapShortDescription];

  MiSnapSDKViewControllerUX2* myViewController = [MiSnapSDKViewControllerUX2 instantiateFromStoryboard];
  myViewController.delegate = self;

  [myViewController setupMiSnapWithParams:parameters];

  [topController presentViewController:myViewController animated:true completion:nil];
}
-(void)presentMitekUI{

  UIViewController *topController = [UIApplication sharedApplication].keyWindow.rootViewController;

  NSMutableDictionary* parameters = [NSMutableDictionary
                                     dictionaryWithDictionary:[MiSnapSDKViewControllerUX2 defaultParametersForIdCardFront]];

  [parameters setObject:@"test" forKey:kMiSnapServerType];
  [parameters setObject:@"0.0" forKey:kMiSnapServerVersion];
  [parameters setObject:@"Mobile Deposit Check Front" forKey:kMiSnapShortDescription];

  MiSnapSDKViewControllerUX2* myViewController = [MiSnapSDKViewControllerUX2 instantiateFromStoryboard];
  myViewController.delegate = self;

  [myViewController setupMiSnapWithParams:parameters];

  [topController presentViewController:myViewController animated:true completion:nil];
}

//Método ejecutado al haber una captura exitosa
-(void)miSnapFinishedReturningEncodedImage:(NSString *)encodedImage
                             originalImage:(UIImage *)originalImage
                                andResults:(NSDictionary *)results {

  UIViewController *topController = [UIApplication sharedApplication].keyWindow.rootViewController;

  [topController dismissViewControllerAnimated:YES completion:^{

    [[CallbackHolder sharedInstance] sendSuccess:@[encodedImage]];

  }];

}

//Método ejecutado al ocurrir un error
-(void)miSnapCancelledWithResults:(NSDictionary *)results{

  [[CallbackHolder sharedInstance] sendFailure:@[@"Se canceló la captura de la imagen"]];

}

@end
