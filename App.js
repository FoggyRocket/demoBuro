import React,{Component} from 'react';
import {Image, View, NativeModules,Platform,SafeAreaView,Button,StyleSheet} from 'react-native'



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
      NativeModules.Facial.startFacial((error, events) => {
        if (error) {
          console.log('error',error);
          
        } else {
         console.log('succe')
         let imageLoad = {uri:`data:image/jpeg;base64,${events}`}
         
         this.setState({imageLoad})
        }
      });
    }
  }

  bntIneIOS=(type)=>{
    const cardRead =  NativeModules.ReaderCardID
    if(type === "front"){
        cardRead.startReaderFront(
          (cardFront) => {
            let imageLoad = {uri:'data:image/jpeg;base64,' + cardFront}
             this.setState({imageLoad})
          },
          (error)=>{
            console.log('error',error);
          })
      }else{
        cardRead.startReaderBack(
          (cardFront) => {
            let imageLoad = {uri:'data:image/jpeg;base64,' + cardFront}
            this.setState({imageLoad})
          },
          (error)=>{
            console.log('error',error);
          })
      }
  }
  btnAndroid=(type)=>{
    if(type === 'front'){
      NativeModules.ReaderCardID.startReaderFront().then(infoCard=>{
      
        let value = JSON.parse(infoCard)
        let imageLoad = {uri:value.image}
        this.setState({imageLoad})
      }).catch(err=>{
        console.log('errror',err)
      })
    }else{
      NativeModules.ReaderCardID.startReaderBack().then(infoCard=>{
      
        let value = JSON.parse(infoCard)
        let imageLoad = {uri:value.image}
        this.setState({imageLoad})
      }).catch(err=>{
        console.log('errror',err)
      })
    }
  }
  
  render() {
      let {blessStart,bntIneIOS,btnAndroid} = this
      let { imageLoad} = this.state
    return(
      
          <SafeAreaView style={{flex:1,justifyContent:'center',alignItems:'center'}}>
            <Image source={imageLoad} style={{width:200,height:200}}/>
            <Button style={styles.btnStyles} onPress={blessStart} title="Selfie"/>
            <Button style={styles.btnStyles} onPress={()=>Platform.OS === "ios" ? bntIneIOS('front') : btnAndroid('front')} title="Ine Front"/>
            <Button style={styles.btnStyles} onPress={()=>Platform.OS === "ios" ? bntIneIOS('back') : btnAndroid('back')} title="Ine Back"/>

          </SafeAreaView>
     
    )
  }
}

const styles = StyleSheet.create({
  btnStyles:{
    marginTop:5,
    marginBottom:5,
  }
})