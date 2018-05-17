package com.adj.kjc;

import java.io.*;
import java.util.HashMap;

public class Data {

    // 烤鸡翅数据文件目录
    final private static String OPERATE_DATA_PATH   = "/mnt/sdcard/Android/kjc/";
    // 配置文件路径
    final private static String SETTING_OBJECT_PATH = "/mnt/sdcard/Android/kjc/setting.obj";
    // 用于替换的照片路径
    final private static String REPLACE_IMG_PATH    = "/mnt/sdcard/Android/kjc/replace.jpg";
    // 用于切割的临时路径
    final private static String SCALE_TEMP_IMG_PATH    = "/mnt/sdcard/Android/kjc/temp.jpg";
    // XPOSED日志文件路径
    final private static String  KJC_LOG_PATH = "/mnt/sdcard/Android/kjc/error.log";

    // 存放开关状态的HashMap
    private static HashMap dataMap = new HashMap<String, Boolean>();

    // 静态代码块, 负责初始化
    static {
        // 默认状态
        dataMap.put("Hook", true);
        dataMap.put("Toast", true);
        dataMap.put("KjcLog", true);
        dataMap.put("XposedLog", true);
        dataMap.put("ImgUpdatedTime", "unknown");

        // 创建目录
        File fileDir = new File(OPERATE_DATA_PATH);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        // 创建配置文件
        File fileFile = new File(SETTING_OBJECT_PATH);
        if (!fileFile.exists()) {
            // 创建文件并更新默认状态到配置文件
            setAllStatus();
        } else {
            // 更新状态
            updateStatus();
        }
    }

    // 从文件更新状态至配置
    public static void updateStatus(){
        try {
            dataMap = (HashMap)Tool.fileToObject(SETTING_OBJECT_PATH);
        } catch (Exception e) {
            dataMap.put("Hook", true);
            dataMap.put("Toast", true);
            dataMap.put("KjcLog", true);
            dataMap.put("XposedLog", true);
            dataMap.put("IMG_UPDATE_TIME", "unknown");
            // 保存状态
            setAllStatus();
        }
    }

    // 更新所有配置至文件
    private static boolean setAllStatus(){
        try{
            Tool.objectToFile(dataMap, SETTING_OBJECT_PATH);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    // 更新Hook配置至文件
    public static boolean setHook(boolean Hook){
        dataMap.put("Hook", Hook);
        return setAllStatus();
    }

    // 更新Toast配置至文件
    public static boolean setToast(boolean Toast){
        dataMap.put("Toast", Toast);
        return setAllStatus();
    }

    // 更新XposedLog配置至文件
    public static boolean setLog(boolean XposedLog){
        dataMap.put("XposedLog", XposedLog);
        return setAllStatus();
    }

    // 更新KjcLog配置至文件
    public static boolean setKjcLog(boolean KjcLog){
        dataMap.put("KjcLog", KjcLog);
        return setAllStatus();
    }

    // 更新ImgUpdatedTime配置至文件
    public static boolean setImgUpdatedTime(String ImgUpdatedTime){
        dataMap.put("ImgUpdatedTime", ImgUpdatedTime);
        return setAllStatus();
    }

    public static boolean isHook() {
        return (boolean)dataMap.get("Hook");
    }

    public static boolean isToast() {
        return (boolean)dataMap.get("Toast");
    }

    public static boolean isXposedLog() {
        return (boolean)dataMap.get("XposedLog");
    }

    public static boolean isKjcLog() {
        return (boolean)dataMap.get("KjcLog");
    }

    public static String getImgUpdatedTime(){
        return (String) dataMap.get("ImgUpdatedTime");
    }

    public static String getOperateDataPath() {
        return OPERATE_DATA_PATH;
    }

    public static String getSettingObjectPath() {
        return SETTING_OBJECT_PATH;
    }

    public static String getReplaceImgPath() {
        return REPLACE_IMG_PATH;
    }
    
    public static String getKjcLogPath() {
        return KJC_LOG_PATH;
    }

    public static String getScaleTempImgPath() {
        return SCALE_TEMP_IMG_PATH;
    }
}
