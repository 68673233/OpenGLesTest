package com.library.openglestest.svm;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.Ml;
import org.opencv.ml.SVM;
import org.opencv.ml.TrainData;
import org.opencv.utils.Converters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

public class SvmTrain {

    //region 属性

    private Context context;

    private boolean success;
    private Mat classes;
    private Mat trainingData;
    private List<Mat> trainingImages=new ArrayList<>();//用来存放训练图像信息的容器
    private List<Integer> trainingLabels=new ArrayList<>(); //用来存放图像对应正负样本的值，正样本为1，负样本为0
    private SVM svm;

    //endregion

    public SvmTrain(Context context){
        this.context=context;
        this.success = OpenCVLoader.initDebug();
    }

    public String trainToXml(){
        if (!success) return null;
        String xmlPath=context.getFilesDir()+"/"+"num.xml";
        openFiles(this.context,1);
        openFiles(this.context,0);
        trainingData=new Mat(trainingImages.size(),trainingImages.get(0).cols(), CvType.CV_32FC1);
        for (int i = 0; i < trainingImages.size(); i++) {
            Mat temp =trainingImages.get(i);
            temp.copyTo(trainingData.row(i));
        }
        trainingData.convertTo(trainingData, CvType.CV_32FC1);
        if (classes==null) classes=new Mat();
        Converters.vector_int_to_Mat(trainingLabels).copyTo(classes);
        classes.convertTo(classes, CvType.CV_32SC1);

        SVM model = SVM.create();//以下是设置SVM训练模型的配置
        model.setType(SVM.C_SVC);
        model.setKernel(SVM.RBF);  //核函数
        model.setDegree(10);  //内核函数（POLY）的参数degree
        model.setGamma(8);  //（POLY/ RBF/ SIGMOID）的参数r
        model.setC(10);     //（C_SVC/ EPS_SVR/ NU_SVR）的参数C 惩罚因子
        model.setCoef0(1.0);  //（POLY/ SIGMOID）的参数coef0
        model.setNu(0.5);  //（NU_SVC/ ONE_CLASS/ NU_SVR）的参数 v
        model.setP(0.1);  //（EPS_SVR）的参数e
        model.setClassWeights(null); //C_SVC中的可选权重，赋给指定的类，乘以C。所以这些权重影响不同类别的错误分类惩罚项。权重越大，某一类别的误分类数据的惩罚项就越大
        //SVM的迭代训练过程的中止条件，解决部分受约束二次最优问题。您可以指定的公差和/或最大迭代次数。
        model.setTermCriteria(new TermCriteria(TermCriteria.MAX_ITER, 20000, 0.0001));

        TrainData tdata = TrainData.create(trainingData, Ml.ROW_SAMPLE, classes);
        //model->train(trainingData, ROW_SAMPLE, classes);
        model.train(tdata);
        model.save(xmlPath/*"car.xml"*/);//保存
        return xmlPath;
    }

    private void openFiles(Context context,int flag){
          File[] files=new File( context.getFilesDir()+"/"+flag+"/").listFiles();
          for (int i=0;i<files.length;i++){
              String pathFile=(files[i]).getAbsolutePath();
              Mat img= Imgcodecs.imread((files[i]).getAbsolutePath());
              Mat line_i=img.reshape(1,1);

              trainingImages.add(line_i);
              trainingLabels.add(flag);
          }
    }

    /** 识别图片 是否是0或1 */
    public boolean recognition(String xml, Bitmap bitmap){
        boolean flag=false;
        Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        SVM svm= SVM.load(xml);
        Mat mat=new Mat();
        Utils.bitmapToMat(bmp,mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGRA2BGR);

        Mat p = mat.reshape(1, 1);
        p.convertTo(p, CvType.CV_32FC1);
        //返回1为正确，0 为错误
        int response = (int)svm.predict(p);
        flag=response==1;

        return flag;
    }

    public boolean recognition(String xml,String pathFile){
        boolean flag=false;
        SVM svm=SVM.load(xml);
        Mat mat=Imgcodecs.imread(pathFile);
        Mat p=mat.reshape(1,1);
        p.convertTo(p,CvType.CV_32FC1);
        //返回1为正确，0 为错误
        float response=svm.predict(p);
        Log.i("mainactivity","response:"+response);
        flag=response==0;
        return flag;
    }
    public int recognitionFiles(String xml,String path){
        int count=0;
        SVM svm=SVM.load(xml);
        File file=new File(path);
        File[] files=file.listFiles();
        for (int i=0;i<files.length;i++) {
            Mat mat = Imgcodecs.imread(files[i].getAbsolutePath());
            Mat p = mat.reshape(1, 1);
            p.convertTo(p, CvType.CV_32FC1);
            //返回1为正确，0 为错误
            int response =(int) svm.predict(p);
            Log.i("mainactivity", "file:"+files[i].getAbsolutePath()+ "   response:" + response);
            if (response==0){
                count++;
            }
        }

        return count;
    }


}
