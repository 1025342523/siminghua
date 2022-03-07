package com.ys.module.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;


public class ActivityCollectorUtils {

    public static ActivityCollectorUtils getInstance() {
        return new ActivityCollectorUtils();
    }

    public List<Activity> activities = new ArrayList<>();

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public void finishAll() {
        Activity activity = null;
        if (activities == null || (activities.size() == 0)) {
            return;
        }

        for (int i = (activities.size() - 1); i >= 0; i--) {
            activity = activities.get(i);
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
            activities.remove(i);
        }
    }

    public void finishOther() {
        Activity activity = null;
        if (activities == null || (activities.size() == 0)) {
            return;
        }
        for (int i = (activities.size() - 2); i >= 0; i--) {
            activity = activities.get(i);
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
            activities.remove(i);
        }
    }
}
