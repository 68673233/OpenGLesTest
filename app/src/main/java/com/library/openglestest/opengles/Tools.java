package com.library.openglestest.opengles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by liwb on 2017/6/7.
 */

public class Tools {
    //region 滤镜
    //普通顶点着色器
    public final static String VertexShader="VertexShader.glsl";
    //带变形的顶点着色器
    public final static String VertexShaderMatrix="VertexShaderMatrix.glsl";

    //普通显示图像片段着色器
    public final static String FragmentShader="FragmentShader.glsl";
    //片段着色器 之转换灰度
    public final static String FragmentShader_Grad="FragmentShader_Grad.glsl";
    public final static String FragmentShader_Tone="FragmentShader_Tone.glsl";
    public final static String FragmentShader_Gauss="FragmentShader_Gauss.glsl";
    //endregion

        public  static String readFromAssets(String s){
            String tem;
            if (s.equals(VertexShader)){
                tem="uniform mat4 u_MVPMatrix;\n" +
                        "  \n" +
                        "attribute vec4 a_position;\n" +
                        "attribute vec2 a_texCoord;\n" +
                        "  \n" +
                        "varying vec2 v_texCoord;\n" +
                        "  \n" +
                        " void main() \n" +
                        " {\n" +
                        "    gl_Position = a_position;\n" +
                        "    v_texCoord  = a_texCoord;   \n" +
                        " }";
            }
            else if (s.equals(VertexShaderMatrix)){
                tem=    "attribute vec4 a_position;" +
                        "uniform mat4 u_MVPMatrix;"+
                        "attribute  vec2 a_texCoord;"+
                        "varying  vec2 v_texCoord;"+
                        "void main() {" +
                        "  gl_Position = u_MVPMatrix*a_position;" +
                        "  v_texCoord=a_texCoord;"+
                        "}";
            }
            else if (s.equals(FragmentShader)){
               tem="precision lowp float;       \n" +
                       "  \n" +
                       "varying vec2 v_texCoord;                       \n" +
                       "uniform sampler2D u_samplerTexture;\n" +
                       "  \n" +
                       "void main()                                          \n" +
                       "{                                                    \n" +
                       "  gl_FragColor = texture2D(u_samplerTexture, v_texCoord);\n" +
                       "}";
            }else if (s.equals(FragmentShader_Grad)){
                tem="precision mediump float;\n" +
                        "\n" +
                        "//在片元着色器这里添加这个 sampler2D 表示我们要添加2D贴图\n" +
                        "uniform sampler2D u_samplerTexture;\n" +
                        "//定义一个u_ChangeColor,因为颜色的变量是RGB,所以使用vec3\n" +
                        "uniform vec3 u_ChangeColor;\n" +
                        "\n" +
                        "varying vec2 v_texCoord;\n" +
                        "\n" +
                        "void main(){\n" +
                        "    //得到2d color\n" +
                        "    vec4 nColor=texture2D(u_samplerTexture,v_texCoord);\n" +
                        "   //黑白图片\n" +
                        "    float c= nColor.r*u_ChangeColor.r+nColor.g*u_ChangeColor.g+nColor.b*u_ChangeColor.b;\n" +
                        "    gl_FragColor = vec4(c,c,c,nColor.a);\n" +
                        "}";
            }else if (s.equals(FragmentShader_Tone)){
                tem="precision mediump float;\n" +
                        "\n" +
                        "//在片元着色器这里添加这个 sampler2D 表示我们要添加2D贴图\n" +
                        "uniform sampler2D u_samplerTexture;\n" +
                        "//定义一个u_ChangeColor,因为颜色的变量是RGB,所以使用vec3\n" +
                        "uniform vec3 u_ToneChangeColor;\n" +
                        "varying vec2 v_texCoord;\n" +
                        "\n" +
                        "//modifyColor.将color限制在rgb\n" +
                        "void modifyColor(vec4 color){\n" +
                        "    color.r=max(min(color.r,1.0),0.0);\n" +
                        "    color.g=max(min(color.g,1.0),0.0);\n" +
                        "    color.b=max(min(color.b,1.0),0.0);\n" +
                        "    color.a=max(min(color.a,1.0),0.0);\n" +
                        "}\n" +
                        "\n" +
                        "void main(){\n" +
                        "    //得到2d color\n" +
                        "    vec4 nColor=texture2D(u_samplerTexture,v_texCoord);\n" +
                        "    //简单色彩处理，冷暖色调、增加亮度、降低亮度等\n" +
                        "        vec4 deltaColor=nColor+vec4(u_ToneChangeColor,0.0);\n" +
                        "        modifyColor(deltaColor);\n" +
                        "        gl_FragColor=deltaColor;\n" +
                        "}";
            }else if (s.equals(FragmentShader_Gauss)){
                tem="precision mediump float;\n" +
                        "\n" +
                        "//在片元着色器这里添加这个 sampler2D 表示我们要添加2D贴图\n" +
                        "uniform sampler2D u_samplerTexture;\n" +
                        "\n" +
                        "varying vec2 v_texCoord;\n" +
                        "\n" +
                        "void main(){\n" +
                        "    vec4 color = vec4(0.0);\n" +
                        "    int coreSize=3;\n" +
                        "    int halfSize=coreSize/2;\n" +
                        "    float texelOffset = 0.01;\n" +
                        "    //创建我们计算好的卷积核\n" +
                        "    float kernel[9];\n" +
                        "    kernel[6] = 1.0; kernel[7] = 2.0; kernel[8] = 1.0;\n" +
                        "    kernel[3] = 2.0; kernel[4] = 4.0; kernel[5] = 2.0;\n" +
                        "    kernel[0] = 1.0; kernel[1] = 2.0; kernel[2] = 1.0;\n" +
                        "    int index = 0;\n" +
                        "    //每一块都进行图像卷积。\n" +
                        "    for(int y=0;y<coreSize;y++)\n" +
                        "    {\n" +
                        "        for(int x = 0;x<coreSize;x++)\n" +
                        "        {\n" +
                        "            vec4 currentColor = texture2D(u_samplerTexture,v_texCoord+vec2(float((-1+x))*texelOffset,float((-1+y))*texelOffset));\n" +
                        "            color += currentColor*kernel[index];\n" +
                        "            index++;\n" +
                        "        }\n" +
                        "    }\n" +
                        "    //归一处理\n" +
                        "    color/=16.0;\n" +
                        "\n" +
                        "    gl_FragColor=color;\n" +
                        "}";
            }

            else    tem="";
            return tem;
        }

        public  static InputStream readFromAsserts(String pathFile){
            InputStream in=null;
            File f = new File(pathFile);
            if (f.exists())
                try {
                    in =new FileInputStream(f);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            return in;
        }

}
