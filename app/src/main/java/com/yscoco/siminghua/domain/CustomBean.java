package com.yscoco.siminghua.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZhangZeZhi on 2018-10-29.
 */

public class CustomBean implements Serializable {
    private String name;
    private int typeCode;
    private boolean player;
    private long times;
    private List<Integer> lineList;
    private List<Integer> secondLineList;

    public String getName() {
        return name;
    }

    public CustomBean setName(String name) {
        this.name = name;
        return this;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public CustomBean setTypeCode(int typeCode) {
        this.typeCode = typeCode;
        return this;
    }

    public List<Integer> getLineList() {
        return lineList;
    }

    public CustomBean setLineList(List<Integer> lineList) {
        this.lineList = lineList;
        return this;
    }

    public boolean isPlayer() {
        return player;
    }

    public CustomBean setPlayer(boolean player) {
        this.player = player;
        return this;
    }

    public long getTimes() {
        return times;
    }

    public CustomBean setTimes(long times) {
        this.times = times;
        return this;
    }

    public List<Integer> getSecondLineList() {
        return secondLineList;
    }

    public CustomBean setSecondLineList(List<Integer> secondLineList) {
        this.secondLineList = secondLineList;
        return this;
    }

}
