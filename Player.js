/**
 * Created by lmy2534290808 on 2017/12/2.
 */
import React, {
    Component,
    PropTypes
} from 'react';
import {
    requireNativeComponent,
    View,
    Text,
    Image,
    ActivityIndicator,
} from 'react-native';
class Player extends Component {
    constructor(props, context) {
        super(props, context);
        this.state={
            indicatorVisible:false
        }
        this._onLoading = this._onLoading.bind(this);
        this._onPaused = this._onPaused.bind(this);
        this._onShutdown = this._onShutdown.bind(this);
        this._onPlayerError = this._onPlayerError.bind(this);
        this._onPlaying = this._onPlaying.bind(this);
        this._onBufferingStart=this._onBufferingStart.bind(this);
        this._onBufferingEnd=this._onBufferingEnd.bind(this);
    }
    _onLoading(event) {
        this.props.onLoading && this.props.onLoading(event.nativeEvent);
    }
    _onPaused(event) {
        this.props.onPaused && this.props.onPaused(event.nativeEvent);
    }
    _onShutdown(event) {
        this.props.onShutdown && this.props.onShutdown(event.nativeEvent);
    }
    _onPlayerError(event) {
        this.props.onPlayerError && this.props.onPlayerError(event.nativeEvent);
    }
    _onPlaying(event) {
        this.props.onPlaying && this.props.onPlaying(event.nativeEvent);
    }
    setNativeProps(nativeProps){
        this._root.setNativeProps(nativeProps)
    }
    _onBufferingStart(event){
        this.setState({indicatorVisible:true})
    }
    _onBufferingEnd(event){
        this.setState({indicatorVisible:false})
    }
    _renderLoadingView(){
      let {indicatorVisible}=this.state;
      if(indicatorVisible){
          return  <View  style={{position:'absolute',top:0,left:0,right:0,bottom:0,
              backgroundColor:'transparent',
              justifyContent:"center",alignItems:'center'}}>
              <ActivityIndicator size={'large'}/>
              <Text style={{color:'black'}}>加载中</Text>
          </View>
      }else{
          return null;
      }
    }
    render() {
        let {style,...props}=this.props;
        const nativeProps = Object.assign({},props);
        Object.assign(nativeProps, {
            onLoading: this._onLoading,
            onPaused: this._onPaused,
            onShutdown: this._onShutdown,
            onPlayerError: this._onPlayerError,
            onPlaying: this._onPlaying,
            onBufferingEnd:this._onBufferingEnd,
            onBufferingStart:this._onBufferingStart,
        });
        return (
            <Image source={require('./thumbnail.png')} style={[{justifyContent:'center',alignItems:'center'},style]}>
            <RCTPlayerAndroid {...nativeProps}
                              style={{position:'absolute',top:0,left:0,right:0,bottom:0,backgroundColor:'transparent'}}
                ref={ref=>this._root=ref}
            />
                {this._renderLoadingView()}
            </Image>
        )
    }
}
Player.propTypes = {
    source: PropTypes.shape({                          // 是否符合指定格式的物件
        uri: PropTypes.string.isRequired,
        timeout: PropTypes.number, //Android only
        mediaCodec: PropTypes.oneOf([0,1,2]), //Android only
        liveStreaming: PropTypes.bool, //Android only
    }).isRequired,
    paused:PropTypes.bool,
    rotation:PropTypes.number,//设置角度
    muted:PropTypes.bool, //iOS only
    aspectRatio: PropTypes.oneOf([0, 1, 2, 3, 4]),
    onLoading: PropTypes.func,
    onPaused: PropTypes.func,
    onShutdown: PropTypes.func,
    onPlayerError: PropTypes.func,
    onPlaying: PropTypes.func,
    ...View.propTypes,
}
const RCTPlayerAndroid = requireNativeComponent('RCTPlayerAndroid', Player);
module.exports = Player;