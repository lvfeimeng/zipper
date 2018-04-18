package com.zipper.wallet.base;

import android.app.Activity;

import java.util.Stack;

/**
 * Activity管理类
 */

public class ActivityManager {

    private static final String TAG = "ActivityManager";

    public static Stack<Activity> activityStack;

    private static ActivityManager instance;

    private ActivityManager() {

    }

    /**
     * 单一实例
     */
    public static ActivityManager getInstance() {
        if (instance == null) {
            instance = new ActivityManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.push(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity getCurrentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

//    /**
//     * 结束当前Activity（堆栈中最后一个压入的）
//     */
//    public void finishActivity() {
//        Activity activity = activityStack.lastElement();
//        finishActivity(activity);
//    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        int flag = -1;
        for (int i = 0; i < activityStack.size(); i++) {
            if (activityStack.get(i).getClass().equals(cls)) {
                flag = i;
                break;
            }
        }
        if(flag>-1) {
            finishActivity(activityStack.get(flag));
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0; i < activityStack.size(); i++) {
            if (null != activityStack.get(i) && i < activityStack.size()) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 保留一些activity,其他的关闭
     */
    public void retainActivity(Stack<Activity> stack) {
        activityStack.retainAll(stack);
    }

    /**
     * 判断是否包含某个activity
     *
     * @param cls
     * @return
     */
    public boolean contains(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }


//        /**
//         * 退出应用程序
//         */
//        public void appExit(Context context) {
//            try {
//                finishAllActivity();
//                android.app.ActivityManager activityManager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//                activityManager.restartPackage(context.getPackageName());
//                System.exit(0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
}
