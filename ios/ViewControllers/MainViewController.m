//
//  MainViewController.m
//  ppv1plus
//
//  Created by Programmer City on 1/6/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import "MainViewController.h"
#import "../FacialCaptureUX/LivenessViewController.h"
#import <MiSnapLiveness/MiSnapLiveness.h>
#import <React/RCTLog.h>
#import <React/RCTUtils.h>
#import <React/RCTConvert.h>
@interface Facial () <LivenessViewControllerDelegate>

@property (strong, nonatomic) IBOutlet UILabel *versionLabel;
@property (assign, nonatomic) BOOL showResults;
@property (strong, nonatomic) MiSnapLivenessCaptureParameters *captureParams;

//
@property (nonatomic, strong) UIImagePickerController *picker;
@property (nonatomic, strong) RCTResponseSenderBlock callback;
@property (nonatomic, strong) NSDictionary *defaultOptions;
@property (nonatomic, retain) NSDictionary *options;
@property (nonatomic, retain) NSMutableDictionary *response;
@property (nonatomic, strong) NSArray *customButtons;
@end

@implementation Facial

// To export a module named CalendarManager
RCT_EXPORT_MODULE();


RCT_EXPORT_METHOD(startFacial:(RCTResponseSenderBlock)callback)
{
  
    MiSnapLivenessCaptureParameters *captureParameters =
    [[MiSnapLivenessCaptureParameters alloc] init];
  
    // Configure any changes to defaults here. For example:
    //captureParameters.captureMode == kLivenessCaptureMode_Manual; // Default is kLivenessCaptureMode_Automatic
    
 self.callback = callback;
  dispatch_async(dispatch_get_main_queue(), ^{
    LivenessViewController *vc = [LivenessViewController
    instantiateFromStoryboard];
    vc.licenseKey = @"{\"signature\":\"u5vt7Mk6ai3Cnr11xRIMTCs1ptABIFnE\\/yxV2ZxmaVl\\/7jJm29y2FXk9L9pNZGhPHXtPwDL4MYpj8RK9BPJ6DParBefYLIIQ+a35\\/7JVDgCJoW7jt7Ae7dhXGIuPMExOCqt5t1pp\\/hKzT69t5LZ17c08ZrVR9PXjkQYIY799yXu\\/QxoOriEnOjg3aY68C2\\/fHQmYVg24CwEbcF2eVcxcDhKSNb34csu3Ni9eknFhQpl8xz+0lyBE0LcnWUfRMHCnAJ9FoPlP2ITmGAnr8XiPHjhZE5M4fEqJrKLWX6MBYO+esdNsxEF7jhMBFO73toouwL+tqMPKbLYJ2+0oyMdPjg==\",\"organization\":\"Daon\",\"signed\":{\"features\":[\"ALL\"],\"expiry\":\"2020-12-11 00:00:00\",\"applicationIdentifier\":\"com.cts.pagaphonects\"},\"version\":\"2.1\"}";
    vc.delegate = self;
    vc.captureParams = self-> _captureParams;

    if (self.navigationController) {
        [self.navigationController pushViewController:vc animated:YES];
    } else {
        // llamada de Reract-native
        [[[[[UIApplication sharedApplication] delegate] window] rootViewController] presentViewController:vc animated:YES completion:nil];
    }
  });
}


- (void)livenessCaptureSuccess:(MiSnapLivenessCaptureResults *)results
{

    // See MiSnapLivenessResultCode enum in MiSnapLivenessCaptureResults.h for all available result codes
    switch (results.livenessResultCode)
    {
        case kLiveness_Result_Spoof_Detected:
            // Handle auto or manual capture spoof detected
            break;
        case kLiveness_Result_Success_Video:
            // Handle auto capture passive and active liveness detected
            break;
        case kLiveness_Result_Success_Still_Camera:
            // Handle manual capture passive liveness detected and active liveness not detected
            break;
        case kLiveness_Result_Unverified:
            // Handle auto capture passive liveness NOT detected and active liveness detected
            break;
        case kLiveness_Result_Unverified_Still_Camera:
            // Handle manual capture passive liveness and active liveness NOT detected
            break;
}
    // These properties are non-nil when a capture succeeds
    UIImage *capturedImage = results.capturedImage;
    NSString *userExperienceData = results.uxpData;
    NSString *encodedImage = results.encodedImage;

    self.callback(@[[NSNull null], encodedImage]);
}

@end