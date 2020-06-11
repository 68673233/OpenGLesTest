package com.library.openglestest;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.FaceDetector;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.library.openglestest.svm.SVMUtils;
import com.library.openglestest.svm.SvmTrain;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region opengl 图片处理
        Button btn = findViewById(R.id.btn);
        btn.setText("opengl 滤波");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, GrayActivity.class);
                startActivity(intent);
            }
        });
        //endregion

        final ImageView image = findViewById(R.id.image);
        image.setImageResource(R.drawable.renlan);
        final ImageView image2 = findViewById(R.id.image2);

        //region opencv 图像处理
        Button btnBi = findViewById(R.id.btnBi);
        btnBi.setText("opencv滤波");
        btnBi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = initLoadOpenCVLibs();
                if (bitmap != null) image2.setImageBitmap(bitmap);
            }
        });
        //endregion

        //region  测试递归双边滤波算法 https://blog.csdn.net/garfielder007/article/details/50581221
        Button btnT = findViewById(R.id.btnT);
        btnT.setText("快速双边滤波");
        btnT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = fastBi(0.03, 10f);
                if (bitmap != null) image2.setImageBitmap(bitmap);
            }
        });
        //endregion

        //region 人脸识别
        Button btnFace = findViewById(R.id.btnface);
        btnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Bitmap bitmap=face();
                 if (bitmap!=null) image2.setImageBitmap(bitmap);
            }
        });
        //endregion

        //region fft
        Button btnFFT=findViewById(R.id.btn_fft);
        btnFFT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Bitmap bitmap=fft();
                 if (bitmap!=null) image2.setImageBitmap(bitmap);

            }
        });
        //endregion

        //region svm训练
        String trainXmlPathFile=this.getFilesDir()+"/num.xml";
        //region
//        SVMUtils.delFiles(this,this.getFilesDir()+"/0");
//        SVMUtils.delFiles(this,this.getFilesDir()+"/1");
        //endregion

          //将正本图片进行分隔保存
//         SVMUtils.spliteBitmap(this);
//        SVMUtils.spliteBitmap(this,0);
//        SVMUtils.spliteBitmap(this,1);
        SvmTrain svmTrain=new SvmTrain(this);
//        //训练成xml
//        trainXmlPathFile= svmTrain.trainToXml();
//        //查看保存的路径是否存在
//        File[] file=this.getFilesDir().listFiles();
        //加载文件进行测试

        Bitmap bitmap=Bitmap.createBitmap(20,20, Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        Paint paint=new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
//        canvas.drawLine(10,0,10,20,paint);
        canvas.drawPaint(paint);
        paint.setColor(Color.WHITE);
        canvas.drawRect(4,4,14,14,paint);
image2.setImageBitmap(bitmap);
       // boolean b= svmTrain.recognition(trainXmlPathFile,bitmap);

//        boolean b=svmTrain.recognition(trainXmlPathFile,this.getFilesDir()+"/0/1.jpg");
//        int count=svmTrain.recognitionFiles(trainXmlPathFile,this.getFilesDir()+"/2");
       // Toast.makeText(this,"结果："+b,Toast.LENGTH_SHORT).show();
        //endregion
    }

    /**
     * 明天 打印出来频域，相位等信息
     * 另外把黑白转成彩色怎么搞的。
     * @return
     */
    private Bitmap fft(){
        Mat src=new Mat();
        Mat des=new Mat();

        Bitmap bitmap1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.renlan);
        Bitmap bitmap = bitmap1.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2BGR);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);

        int m= Core.getOptimalDFTSize(src.rows());
        int n=Core.getOptimalDFTSize(src.cols());
        //扩充图像
        Core.copyMakeBorder(src,des,0,m-src.rows(),0,n-src.cols(),Core.BORDER_CONSTANT, Scalar.all(0));
        Log.d(TAG, "fft() called:"+des.toString()+",srcRow:"+src.rows()+",cols:"+src.cols());
        List<Mat> planes =new ArrayList<>();//添加维度，用于存储傅立叶变换的结果

        Mat dess=new Mat();
        des.convertTo(dess,CvType.CV_32F);
        planes.add(dess);
        planes.add(Mat.zeros(des.size(), CvType.CV_32F));
        Mat complexI=new Mat();
        Core.merge(planes,complexI);//合并通道
        Log.d(TAG, "fft() called complexI:"+complexI.toString());
        //离散傅立叶变换
        Core.dft(complexI,complexI/*,Core.DFT_INVERSE*/);
        //将存储在complexI的结果分解到planes[0],planes[1]中
        Core.split(complexI,planes);
        //计算幅值  log(1 + sqrt(Re(DFT(I))^2 + Im(DFT(I))^2))
        Core.magnitude(planes.get(0),planes.get(1),planes.get(0));

        //0是实部 1是虚部
        Mat magnitudeImage = planes.get(0);
        Core.add(magnitudeImage,Scalar.all(1),magnitudeImage);
        Log.d(TAG, "fft() called:"+Arrays.toString(magnitudeImage.get(magnitudeImage.rows()/2,magnitudeImage.cols()/2)));
        Core.log(magnitudeImage,magnitudeImage);//用对数表示
        Log.d(TAG, "fft() called:"+Arrays.toString(magnitudeImage.get(magnitudeImage.rows()/2,magnitudeImage.cols()/2)));
//        //如果有奇数行或列，则对频谱进行裁剪
        magnitudeImage = magnitudeImage.adjustROI(0,0,magnitudeImage.cols() & -2,magnitudeImage.rows() & -2);

        int cx = magnitudeImage.cols()/2;
        int cy = magnitudeImage.rows()/2;
        Mat q0=new Mat(magnitudeImage, new Rect(0,0,cx,cy));
        Mat q1=new Mat(magnitudeImage,new Rect(cx,0,cx,cy));
        Mat q2=new Mat(magnitudeImage,new Rect(0,cy,cx,cy));
        Mat q3=new Mat(magnitudeImage,new Rect(cx,cy,cx,cy));
        Mat tmp=new Mat();
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);
        q1.copyTo(tmp);
        q2.copyTo(q1);
        tmp.copyTo(q2);
        Core.normalize(magnitudeImage,magnitudeImage,0,1,Core.NORM_MINMAX);//归一化
        Mat invDFT,invDFTcvt;
        Core.idft(complexI, des, Core.DFT_SCALE | Core.DFT_REAL_OUTPUT );//离散傅立叶逆变换

        des.convertTo(des, CvType.CV_8U);
        bitmap = Bitmap.createBitmap(des.cols(), des.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(des, bitmap);

        //归一化后，颜色值变为0~1，不能正常显示，要乘以颜色值255
        Core.multiply(magnitudeImage,Scalar.all(255),magnitudeImage);
        Log.d(TAG, "fft() called:"+Arrays.toString(magnitudeImage.get(cy,cx)));
        magnitudeImage.convertTo(magnitudeImage,CvType.CV_8U);
        Log.d(TAG, "fft() called:"+Arrays.toString(magnitudeImage.get(cy,cx)));
        Utils.matToBitmap(magnitudeImage,bitmap);
        int[] buff=new int[bitmap.getWidth()*bitmap.getHeight()];
        bitmap.getPixels(buff,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());
        Log.d(TAG, "fft() called+"+ Arrays.toString(buff));
        return bitmap;
    }

    private Bitmap initLoadOpenCVLibs() {
        boolean success = OpenCVLoader.initDebug();
        if (success) {
            Log.d("main", "Load Library successfully......");
            Mat src = new Mat();
            Mat des = new Mat();

            Bitmap bitmap1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.renlan);
            Bitmap bitmap = bitmap1.copy(Bitmap.Config.ARGB_8888, true);
            Utils.bitmapToMat(bitmap, src);
            //由于bitmap是RGBA的，转换成Mat也是带了alpha，但是bilateralFilter不能有alpha，所以做一下颜色空间转换
            Imgproc.cvtColor(src, src, Imgproc.COLOR_BGRA2BGR);//解决bilateralFilter时报错smooth.cpp:3145: error: (-215) (src.type() == CV_8UC1 || src.type() == CV_8UC3) && src.data != dst.data

            //双边滤波
//            Imgproc.bilateralFilter(src,des,10,75,75);
            Imgproc.bilateralFilter(src, des, 30, 100, 5);
            //高斯滤波
//            Imgproc.GaussianBlur(src, des, new Size(15, 15), 3, 3);
            //中值滤波
//            Imgproc.medianBlur(src, des, 3);
//            Imgproc.cvtColor(src, des, Imgproc.COLOR_RGBA2GRAY);
            Utils.matToBitmap(des, bitmap);
            return bitmap;
        }
        return null;
    }

    private Bitmap fastBi(double sigma_spatial, double sigma_range) {
        final int Red = 0;
        final int Green = 1;
        final int Blue = 2;

        Bitmap temBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.renlan);
        Bitmap bitmap = temBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int[] srcColor = new int[bitmap.getWidth() * bitmap.getHeight()];
        int[] desColor = srcColor.clone();
        bitmap.getPixels(srcColor, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());


        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        final int nChannel = 4;
        double[] yp = new double[100];
        double alpha = Math.exp(-Math.sqrt(2.0) / (sigma_spatial * width));//filter kernel size
        double inv_alpha = (1 - alpha);
        double[] range_table = new double[256];
        for (int i = 0; i <= 255; i++)
            range_table[i] = Math.exp(-(double) i / (sigma_range * 255));

        for (int i = 0; i < height; i++) {
            desColor[i * width] = srcColor[i * width];
            for (int j = 1; j < width; j++) {
                double weight = range_table[euro_dist_rgb_max(desColor[i * width + j], desColor[i * width + j - 1])];
                double alpha_ = weight * alpha;

                double yccR = inv_alpha * MColor.intToColor(srcColor[i * width + j]).Red() + alpha_ * yp[Red];
                double yccG = inv_alpha * MColor.intToColor(srcColor[i * width + j]).Green() + alpha_ * yp[Green];
                double yccB = inv_alpha * MColor.intToColor(srcColor[i * width + j]).Blue() + alpha_ * yp[Blue];
                desColor[i * width + j] = MColor.ColorToInt(MColor.intToColor(srcColor[i * width + j]).Alpha(), (int) yccR, (int) yccG, (int) yccB);
                yp[Red] = yccR;
                yp[Green] = yccG;
                yp[Blue] = yccB;
            }

            int w1 = width - 1;
            desColor[i * width + w1] = MColor.mulColor(desColor[i * width + w1], 0.5f);
            for (int x = width - 2; x >= 0; x--) {
                double weight = range_table[euro_dist_rgb_max(srcColor[i * width + x], srcColor[i * width + x + 1])];
                double alpha_ = weight * alpha;
                double yccR = inv_alpha * MColor.intToColor(srcColor[i * width + x]).Red() + alpha_ * yp[Red];
                double yccG = inv_alpha * MColor.intToColor(srcColor[i * width + x]).Green() + alpha_ * yp[Green];
                double yccB = inv_alpha * MColor.intToColor(srcColor[i * width + x]).Blue() + alpha_ * yp[Blue];
                desColor[i * width + x] = MColor.ColorToInt(MColor.intToColor(srcColor[i * width + x]).Alpha(), (int) yccR, (int) yccG, (int) yccB);
                yp[Red] = yccR;
                yp[Green] = yccG;
                yp[Blue] = yccB;
            }


        }

        bitmap.setPixels(desColor, 0, width, 0, 0, bitmap.getWidth(), bitmap.getHeight());

        return bitmap;
    }

    /**
     * 人脸识别
     * @return
     */
    private Bitmap face() {
        boolean success = OpenCVLoader.initDebug();
        if (success) {
            Mat mat_gray = new Mat();
            Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.renlan);
            Utils.bitmapToMat(bmp, mat_gray);
            Imgproc.cvtColor(mat_gray, mat_gray, Imgproc.COLOR_BGRA2BGR);
            Imgproc.cvtColor(mat_gray, mat_gray, Imgproc.COLOR_BGR2GRAY);

            CascadeClassifier face_cascade = new CascadeClassifier();
//            String faceXml = "file:///android_asset/haarcascade_frontalface_alt.xml";
//            String faceXml= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES )+"/haarcascade_frontalface_alt.xml";
            String faceXml=this.getFilesDir()+"/haarcascade_frontalface_alt.xml";
            File file=new File(faceXml);
            if (!file.exists()) {
                faceXml = Tools.copyData(this, "haarcascade_frontalface_alt.xml", "haarcascade_frontalface_alt.xml");
            }
            Log.i("mainactivity","Path:"+faceXml);
            if (!face_cascade.load(faceXml)) {
                Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                return null;
            }
            MatOfRect matOfRect = new MatOfRect();
            face_cascade.detectMultiScale(mat_gray, matOfRect, 1.1, 2, 0 /*| CV_HAAR_SCALE_IMAGE*/, new Size(30, 30));
            List<Rect> list = matOfRect.toList();

            Utils.matToBitmap(mat_gray, bmp);
            Bitmap bitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Color.RED);
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            for (int i = 0; i < list.size(); i++) {
                canvas.drawRect(list.get(i).x, list.get(i).y, list.get(i).width + list.get(i).x, list.get(i).height + list.get(i).y, paint);
//                canvas.drawRect(list.get(i),paint);
            }
            return bitmap;
        }
        return null;
    }

        private byte[] InputStreamToByte(InputStream is) throws IOException {
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                bytestream.write(ch);
            }
            byte imgdata[] = bytestream.toByteArray();
            bytestream.close();
            return imgdata;
    }

    private int euro_dist_rgb_max(int a, int b) {
        MColor A = MColor.intToColor(a);
        MColor B = MColor.intToColor(b);
        int r = Math.abs(A.Red() - B.Red());
        int g = Math.abs(A.Green() - B.Green());
        int blue = Math.abs(A.Blue() - B.Blue());
        return Math.max(Math.max(r, g), blue);
    }

    static class MColor {
        private int mAlpha, mRed, mGreen, mBlue;

        public MColor() {
        }

        public MColor(int color) {
            mAlpha = (color & 0xff000000) >> 24;
            mRed = (color & 0xff0000) >> 16;
            mGreen = (color & 0x00ff00) >> 8;
            mBlue = (color & 0x0000ff);
        }

        public static int ColorToInt(int alpha, int red, int green, int blue) {
            return Color.argb(alpha, red, green, blue);
        }

        public static int mulColor(int color, double mul) {
            double mAlpha = ((color & 0xff000000) >> 24) * mul;
            double mRed = ((color & 0xff0000) >> 16) * mul;
            double mGreen = ((color & 0x00ff00) >> 8) * mul;
            double mBlue = ((color & 0x0000ff)) * mul;
            return ColorToInt((int) mAlpha, (int) mRed, (int) mGreen, (int) mBlue);
        }

        private static MColor intToColor(int color) {
            return new MColor(color);
        }

        public int Alpha() {
            return mAlpha;
        }

        public int Red() {
            return mRed;
        }

        public int Green() {
            return mGreen;
        }

        public int Blue() {
            return mBlue;
        }
    }


}
