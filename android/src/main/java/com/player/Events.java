package com.player;

/**
 * Created by lmy2534290808 on 2017/12/2.
 */

public enum Events {
    LOADING("onLoading"),
    PAUSE("onPaused"),
    SHUTDOWN("onShutdown"),
    ERROR("onPlayerError"),
    PLAYING("onPlaying"),
    BUFFERING_START("onBufferingStart"),
    BUFFERING_END("onBufferingEnd");

    private final String mName;

    Events(final String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return mName;
    }
}
