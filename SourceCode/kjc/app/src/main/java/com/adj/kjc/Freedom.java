package com.adj.kjc;

import android.widget.Toast;
import android.content.Context;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Freedom implements IXposedHookLoadPackage {
    private ClassLoader ultimateClassLoader;
    private Context toastContext;

    // 日志输出
    private void log(String logs){

        Data.updateStatus();

        if(Data.isXposedLog()){
            XposedBridge.log(logs+"\r\n");
        }

        if(Data.isKjcLog()){
            Tool.kjcLog(logs+"\r\n");
        }

        if(Data.isToast()){
            toast(logs, 0);
        }
    }

    // toast信息提示
    private void toast(String meg, int time){
        Toast.makeText(toastContext, meg, time).show();
    }

    // Js引擎追踪所有js调用
    private void hookJsEngine(){

        XposedHelpers.findAndHookMethod("org.apache.cordova.engine.SystemExposedJsApi", ultimateClassLoader, "exec", int.class, String.class, String.class, String.class, String.class, new XC_MethodHook(){

            @Override
            protected void beforeHookedMethod(MethodHookParam param){

                if(Data.isHook()){
                    // 验证本地照片时, 覆盖照片
                    if(param.args[1].toString().equalsIgnoreCase("Face") &&
                            param.args[2].toString().equalsIgnoreCase("verify")){

                        if(param.args[4].toString().contains("jpg")){
                            // 按指定模式在字符串查找
                            String pattern = "(\\d+)";
                            // 创建 Pattern 对象
                            Pattern r = Pattern.compile(pattern);
                            // 现在创建 matcher 对象
                            Matcher m = r.matcher(param.args[4].toString());
                            // 跳过学号
                            m.find();
                            if (m.find()) {
                                String picNo = m.group();
                                toast("验证图片id: "+picNo, 1);

                                try{
                                    Tool.copyFile(Data.getReplaceImgPath(), "/data/data/com.mybofeng.assist/files/"+picNo+".jpg");
                                    toast("已经覆盖本地验证图片!", 1);
                                }catch (Exception e){
                                    toast("无法覆盖本地验证图片!\r\n"+e.toString(), 1);
                                }
                            }
                        }
                    }else if(param.args[1].toString().equalsIgnoreCase("File") &&
                            param.args[2].toString().equalsIgnoreCase("remove")){
                        log("通过js引擎, 拦截File-remove\n禁止删除文件!");
                        String tmp = param.args[4].toString();
                        param.args[4] = "[]";
                        log(tmp+" -> "+param.args[4]);
                    }
                    String service = param.args[1].toString(), action = param.args[2].toString(), arguments = param.args[4].toString();
                    String content = String.format("%s: %s, %s\n%s\n",
                            Tool.getTime(), service, action, arguments);
                    log(content);
                }
            }
        });
    }

    // 程序入口
    @Override
    public void handleLoadPackage(LoadPackageParam loadPackageParam){

        // 针对"垃圾软件"进行拦截
        if (loadPackageParam.packageName.equals("com.mybofeng.assist")) {
            // 拦截TxAppEntry的c方法, 通过其取得应用脱壳后的context
            XposedHelpers.findAndHookMethod("com.tencent.StubShell.TxAppEntry", loadPackageParam.classLoader,
                "c", Context.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param){

                        // 获取脱壳后的context
                        Context context = (Context) param.args[0];

                        // 获得toast使用的Context
                        toastContext = context.getApplicationContext();
                        // 获取脱壳后的classLoader
                        ultimateClassLoader = context.getClassLoader();

                        // JS引擎追踪
                        hookJsEngine();

                        // 告知用户拦截效果
                        toast("Nice!", 0);
                        String tip = String.format("Hook: %s\r\nToast: %s\r\nKjcLog: %s\nXposedLog: %s\r\nImgUpdatedTime: %s",
                                ""+Data.isHook(), ""+Data.isToast(), ""+Data.isKjcLog(), ""+Data.isXposedLog(), Data.getImgUpdatedTime());
                        toast(tip, 1);
                    }
                }
            );
        }
    }

}

