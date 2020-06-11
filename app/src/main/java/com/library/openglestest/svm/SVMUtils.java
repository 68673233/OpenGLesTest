package com.library.openglestest.svm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.library.openglestest.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class SVMUtils {

    public static void spliteBitmap(Context context,int num){
        final int b=20;
        int filenum=0;
        Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.digits);
        File file0 = new File(context.getFilesDir()+"/"+num);
        if (!file0.exists()){ file0.mkdir();}

        int col=bitmap.getWidth()/b,row=num*5;
        for(int i=row;i<row+5;i++){
            int offsetRow=i*b;
            for(int j=0;j<col;j++){
                int offsetCol=j*b;
                String fileName= String.format(context.getFilesDir()+"/%d/%d.jpg",num,filenum++);
                Bitmap bmp=Bitmap.createBitmap(bitmap,offsetCol,offsetRow,b,b);
                saveImg(bmp,fileName,context);
            }
        }

    }

    /** 0~9的数据分别放到目录0和1中，第一行放0，第二行放1 */
    public static void spliteBitmap(Context context){
        Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),R.drawable.digits);
        int  filename = 0,filenum=0;
        int b=20;
        int row=bitmap.getHeight()/b;
        int col=bitmap.getWidth()/b;
        for(int i=0;i<row;i++){
            int offsetRow=i*b;
            if(i%5==0&&i!=0)
            {
                filename++;
                filenum=0;
            }
            for(int j=0;j<col;j++){
                int offsetCol = j*b; //列上的偏移量
                String fileName= String.format(context.getFilesDir()+"/%d/%d.jpg",filename,filenum++);
                Bitmap bmp=Bitmap.createBitmap(bitmap,offsetCol,offsetRow,b,b);
                saveImg(bmp,fileName,context);
            }
        }
    }

    public static boolean saveImg(Bitmap bitmap, String pathFile, Context context) {
        try {
            File file0 = new File(context.getFilesDir()+"/0");
            if (!file0.exists()){ file0.mkdir();}
            File file1 = new File(context.getFilesDir()+"/1");
            if (!file1.exists()){file1.mkdir();}

            File mFile=new File(pathFile);
            if (mFile.exists()) {
                //Toast.makeText(context, "该图片已存在!", Toast.LENGTH_SHORT).show();
                return false;
            }
            FileOutputStream outputStream = new FileOutputStream(mFile);     //构建输出�?
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);  //compress到输出outputStream
            Uri uri = Uri.fromFile(mFile);                                  //获得图片的uri
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)); //发�?�广播�?�知更新图库，这样系统图库可以找到这张图�?
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void delFiles(Context context,String path){
        File file=new File(path);
        File[] files=file.listFiles();
        for(File f:files){
            boolean delete = f.delete();
        }
    }

}
