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

@interface Facial () <LivenessViewControllerDelegate>

@property (strong, nonatomic) IBOutlet UILabel *versionLabel;
@property (assign, nonatomic) BOOL showResults;
@property (strong, nonatomic) MiSnapLivenessCaptureParameters *captureParams;

@end

@implementation Facial

// To export a module named CalendarManager
RCT_EXPORT_MODULE();


RCT_EXPORT_METHOD(startFacial:(NSString *)name location:(NSString *)location)
{
  
  LivenessViewController *vc = [LivenessViewController
  instantiateFromStoryboard];
  vc.licenseKey = @"your_license_key_here";
  vc.delegate = self;
  vc.captureParams = _captureParams;

  if (self.navigationController) {
      [self.navigationController pushViewController:vc animated:YES];
  } else {
      [self presentViewController:vc animated:YES completion:nil];
  }
  
  RCTLogInfo(@"Pretending to create an event %@ at %@", name, location);
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
}

@end


