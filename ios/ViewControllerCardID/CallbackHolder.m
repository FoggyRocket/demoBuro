//
//  CallbackHolder.m
//  MitekIntegration
//
//  Created by Juan Manuel Perez Santos on 3/4/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

#import "CallbackHolder.h"

@implementation CallbackHolder

#pragma mark LifeCycle

@synthesize successCallback;
@synthesize failureCallback;

-(void)sendSuccess:(NSArray *)args{

  if (self.successCallback != nil) {
    self.successCallback(args);
  }

}

-(void)sendFailure:(NSArray *)args{

  if(self.failureCallback != nil){
    self.failureCallback(args);
  }

}

#pragma mark Singleton
+(id)sharedInstance{

  static CallbackHolder *instance = nil;

  @synchronized (self) {
    if (instance == nil) {
      instance = [[self alloc] init];
    }
  }

  return instance;
}

- (instancetype)init
{
  if (self = [super init]) {
  }
  return self;
}

@end
