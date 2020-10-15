//
//  CallbackHolder.h
//  MitekIntegration
//
//  Created by Juan Manuel Perez Santos on 3/4/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>

NS_ASSUME_NONNULL_BEGIN

@interface CallbackHolder : NSObject

@property (nonatomic, strong) RCTResponseSenderBlock successCallback;
@property (nonatomic, strong) RCTResponseSenderBlock failureCallback;

-(void)sendSuccess:(NSArray *)args;
-(void)sendFailure:(NSArray *)args;

+(id)sharedInstance;

@end

NS_ASSUME_NONNULL_END
