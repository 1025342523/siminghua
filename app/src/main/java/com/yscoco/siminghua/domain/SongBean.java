package com.yscoco.siminghua.domain;

import com.lzx.musiclibrary.aidl.model.SongInfo;

import java.io.Serializable;

/**
 * Created by ZhangZeZhi on 2018\11\22 0022.
 */

public class SongBean implements Serializable {

    private SongInfo songInfo;
    private boolean isSelected;
    private boolean isPlay;

    public SongInfo getSongInfo() {
        return songInfo;
    }

    public SongBean setSongInfo(SongInfo songInfo) {
        this.songInfo = songInfo;
        return this;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public SongBean setSelected(boolean selected) {
        isSelected = selected;
        return this;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public SongBean setPlay(boolean play) {
        isPlay = play;
        return this;
    }

}
