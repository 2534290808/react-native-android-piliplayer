## react-native-android-piliplayer [![npm version](https://badge.fury.io/js/react-native-android-piliplayer.svg)](https://badge.fury.io/js/react-native-android-piliplayer)
## 第一步
工程目录下运行 npm install --save react-native-android-piliplayer 或者 yarn add react-native-android-piliplayer(已经安装了yarn)
## 第二步
运行 react-native link react-native-android-piliplayer
## 第三部使用
在工程中导入：
```bash
import React, { Component } from 'react';
import {AppRegistry,StyleSheet,Text, View,Dimensions,Button,ToastAndroid,LayoutAnimation,Image} from 'react-native';
import Player from 'react-native-piliplayer';
let {width,height}=Dimensions.get('window')
export default class player extends Component {
  constructor(){
    super();
    this.state={
        paused:false,
    }
  }
  showToast(text){
      ToastAndroid.show(text,ToastAndroid.SHORT)
  }
  render() {
    return (
      <View style={styles.container}>
        <Player
            paused={this.state.paused}
            ref={ref=>this.player=ref}
            source={{uri:'uri',liveStreaming:false,mediaCodec:1}}
            style={{width,height:200,top:400}} rotation={0}
            onLoading={()=>{}} onPaused={()=>{}}
            onPlaying={()=>{}} onShutdown={()=>{}}/>
      </View>
    );
  }
}
```
<br/>
注意在使用中时应设置组件的宽高;

## 属性：
```bash
 source: PropTypes.shape({                          // 是否符合指定格式的
        uri: PropTypes.string.isRequired,//视频路径
        timeout: PropTypes.number, //视频准备时间
        mediaCodec: PropTypes.oneOf([0,1,2]), //视频解码类型,0:软解 1：硬解码， 2：自动
        liveStreaming: PropTypes.bool, /视频是否为直播流
    }).isRequired,
    paused:PropTypes.bool,//视频是否暂停
    rotation:PropTypes.number,//设置角度
    aspectRatio: PropTypes.oneOf([0, 1, 2, 3, 4]),// 视频模式
    /**
         *  ASPECT_RATIO_ORIGIN = 0;
         *  ASPECT_RATIO_FIT_PARENT = 1
         *  ASPECT_RATIO_PAVED_PARENT = 2
         *  ASPECT_RATIO_16_9 = 3
         *  ASPECT_RATIO_4_3 = 4
         */
    onLoading: PropTypes.func,//视频加载
    onPaused: PropTypes.func,//视频暂停了
    onShutdown: PropTypes.func,//视频播放完成
    onPlayerError: PropTypes.func,//视频播放错误
    onPlaying: PropTypes.func,//视频播放中
    ...View.propTypes,
    
