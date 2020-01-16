import React,{Component} from 'react';
import {Image, View, NativeModules,Platform,SafeAreaView,Button} from 'react-native'



export default class Test extends Component {

  state={
    imageLoad:''
  }


  //Inicia los sdk
  blessStart=()=>{
    if(Platform.OS ==='android'){
      //Esta es una promesa que regresa la iamgen en base64
      NativeModules.Facial.startFacial().then(res=>{
        // console.log(`data:image/jpeg;base64,${res}`)
        let value = JSON.parse(res)
        let imageLoad = value.image
        this.setState({imageLoad})
        console.log('ress',value)
      }).catch(err=>console.log('err',err));
    }else{

      //esto es apenas la promesa para poder utilizar lo en IOS
      var onFacial = NativeModules.Facial;
      onFacial.startFacial('test ', 'Test conexion');
    }

   
    
  }
  
  render() {
      let {blessStart} = this
      let { imageLoad} = this.state
    return(
      
          <SafeAreaView style={{flex:1,justifyContent:'center',alignItems:'center'}}>
            <Image source={{uri:imageLoad}} style={{width:100,height:100}}/>
            <Button onPress={blessStart} title="Iniciar Facial"/>
          </SafeAreaView>
     
    )
  }
}