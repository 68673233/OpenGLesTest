package com.library.openglestest;

import android.content.Context;
import android.text.method.HideReturnsTransformationMethod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Tools {

    /**
     * 从assets中复制文件到data/data/中
     * @param context
     * @param assetsPathFile
     * @param dataPathFile
     * @return 返回复制成功的文件绝对路径
     */
   public static String copyData(Context context,String assetsPathFile,String dataPathFile) {
        InputStream in = null;
        FileOutputStream out = null;
        String path = context.getApplicationContext().getFilesDir()
                .getAbsolutePath() + "/"+dataPathFile; // data/data目录
        File file = new File(path);
        if (!file.exists()) {
            try {
                in = context.getAssets().open(assetsPathFile); // 从assets目录下复制
                out = new FileOutputStream(file);
                int length = -1;
                byte[] buf = new byte[1024];
                while ((length = in.read(buf)) != -1) {
                    out.write(buf, 0, length);
                }
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        return  path;
    }

}

