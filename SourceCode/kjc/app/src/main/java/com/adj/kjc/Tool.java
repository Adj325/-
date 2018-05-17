package com.adj.kjc;

import java.io.*;
import java.util.Date;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import java.text.SimpleDateFormat;
import android.graphics.BitmapFactory;

public class Tool {

    /**
     * 将obj输出到outputFile
     *
     * @ori https://blog.csdn.net/u010982856/article/details/52300298
     */
    public static void objectToFile(Object obj, String outputFile) {
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(outputFile));
            oos = new ObjectOutputStream(fos);
            //开始写入
            oos.writeObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    /**
     * 从outputFile读取数据给obj
     *
     * @ori https://blog.csdn.net/u010982856/article/details/52300298
     */
    public static Object fileToObject(String fileName) {

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);
            Object object = ois.readObject();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return null;
    }

    // 工具 - 复制文件
    public static void copyFile(String srcFile, String desFile ) throws Exception{
        InputStream streamFrom = new FileInputStream(srcFile);
        OutputStream streamTo = new FileOutputStream(desFile);
        byte buffer[] = new byte[1024];
        int len;
        while ((len = streamFrom.read(buffer)) > 0){
            streamTo.write(buffer, 0, len);
        }
        streamFrom.close();
        streamTo.close();
    }

    /**
     * 切割照片
     *
     * @ori:  https://blog.csdn.net/bingo_1214/article/details/45028485
     * @ori2: https://blog.csdn.net/qq_31028313/article/details/55251370
     */
    // 工具 - 切割照片
    public static void scalePicture(String srcPath, String dstPath, int desWidth,int desHeight) throws Exception {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeFile(srcPath);
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        float scaleWidth = (float) desWidth/srcWidth, scaleHeight = (float)desHeight/srcHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        bitmap = Bitmap.createBitmap(bitmap, 0,0, srcWidth, srcHeight, matrix,true);
        // 保存到本地
        File filePic = new File(Data.getReplaceImgPath());
        FileOutputStream fos = new FileOutputStream(filePic);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();

    }

    // 工具 - 获取当前时间
    public static String getTime(){
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("hh:mm:ss");
        return ft.format(dNow);
    }

    // 工具 - 输出日志信息
    public static void kjcLog(String logs){
        try{
            FileWriter fw = new FileWriter(Data.getKjcLogPath(),true);
            fw.write(logs);
            fw.close();
        }catch (Exception e){}
    }
}
