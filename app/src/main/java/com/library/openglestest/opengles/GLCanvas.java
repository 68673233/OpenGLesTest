package com.library.openglestest.opengles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import com.library.openglestest.R;
import com.library.openglestest.opengles.filter.GaussFilter;
import com.library.openglestest.opengles.filter.GrayFilter;
import com.library.openglestest.opengles.filter.ToneFilter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class GLCanvas {

    private Context context;
    private GLSurfaceView glSurfaceView;
    private GLSurfaceViewRender render;
    private int renderMode;



    public GLCanvas(Context context, int renderMode,Class<?> clasz) {
        this.context = context;
        this.renderMode = renderMode;

        glSurfaceView = new GLSurfaceView(context);
        glSurfaceView.setEGLContextClientVersion(2);
        render = new GLSurfaceViewRender(context,clasz);
        glSurfaceView.setRenderer(render);
        glSurfaceView.setRenderMode(renderMode);
    }
    public abstract void onDrawFrame(IFilter filter);

    public GLSurfaceView getView(){return this.glSurfaceView;}
    public GLSurfaceView.Renderer getRender(){ return this.render;}

    class GLSurfaceViewRender implements GLSurfaceView.Renderer,IFilter {

        private Context context;
//        GrayFilter grayFilter;
        Class<?> clasz;
        Object object;
        private GLSurfaceViewRender(Context context,Class<?> clasz) {
            this.context = context;
            this.clasz=clasz;
        }



        //region  renderer接口
        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            // 设置背景颜色
            gl10.glClearColor(0.0f, 0f, 1f, 0.5f);
            if (clasz.getName().contains("GrayFilter") ) object=new GrayFilter(context);
            else if (clasz.getName().contains("ToneFilter")) object=new ToneFilter(context);
            else if (clasz.getName().contains("GaussFilter")) object=new GaussFilter(context);

            GLES20.glEnable(GLES20.GL_TEXTURE_2D);
            // Active the texture unit 0
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//            loadVertex();
//            initShader();
//            loadTexture((R.drawable.lk));
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            gl10.glViewport(0, 0, width, height);

        }


        @Override
        public void onDrawFrame(GL10 gl10) {
               GLCanvas.this.onDrawFrame(this);
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            if (object instanceof GrayFilter) ((GrayFilter)object).drawSelf();
            else if (object instanceof ToneFilter) ((ToneFilter)object).drawSelf();
            else if (object instanceof GaussFilter) ((GaussFilter)object).drawSelf();
        }
        //endregion

        //region 方法
//        private static final int TEXTURE_ID = 0;
//        private static final int TEXTURE_WIDTH = 1;
//        private static final int TEXTURE_HEIGHT = 2;
//
//        int attribPosition;
//        int attribTexCoord;
//        int uChangeColor,toneChangeColor;
//        int hMatrix;
//        private int textureId;
//        private FloatBuffer vertex;
//        private ShortBuffer index;
//        private float[] quadVertex = new float[]{
//                0f, 0f, 0.0f, // Position 0
//                0,  0f, // TexCoord 0           0，1
//                0f, (-1.0f), 0.0f, // Position 1
//                0f,  1f, // TexCoord 1             0，0
//                1.0f, (-1.0f), 0.0f, // Position 2
//                1.0f,  1f, // TexCoord 2         1 0
//                1.0f,0f, 0.0f, // Position 3
//                1.0f,  0f, // TexCoord 3           1，1
//        };
//        private short[] quadIndex = new short[]{
//                (short) (0), // Position 0
//                (short) (1), // Position 1
//                (short) (2), // Position 2
//                (short) (2), // Position 2
//                (short) (3), // Position 3
//                (short) (0), // Position 0
//        };
//
//        private void loadVertex(){
//            // float size = 4
//            this.vertex = ByteBuffer.allocateDirect(quadVertex.length * 4)
//                    .order(ByteOrder.nativeOrder())
//                    .asFloatBuffer();
//            this.vertex.put(quadVertex).position(0);
//            // short size = 2
//            this.index = ByteBuffer.allocateDirect(quadIndex.length * 2)
//                    .order(ByteOrder.nativeOrder())
//                    .asShortBuffer();
//            this.index.put(quadIndex).position(0);
//        }
//        private void  initShader(){
////            String vertexSource = Tools.readFromAssets("VertexShaderMatrix.glsl");
//            String vertexSource = Tools.readFromAssets(currVertex);
//            String fragmentSource = Tools.readFromAssets(currFragment);
//            // Load the shaders and get a linked program
//            int program = GLHelper.loadProgram(vertexSource, fragmentSource);
//            // Get the attribute locations
//            attribPosition = GLES20.glGetAttribLocation(program, "a_position");
//            attribTexCoord = GLES20.glGetAttribLocation(program, "a_texCoord");
//            int uniformTexture = GLES20.glGetUniformLocation(program, "u_samplerTexture");
//            switch (currVertex){
//                case GLCanvas.VertexShader:break;
//                case GLCanvas.VertexShaderMatrix:
//                                hMatrix = GLES20.glGetUniformLocation(program, "u_MVPMatrix");
//                    break;
//            }
//            switch (currFragment){
//                case GLCanvas.FragmentShader:break;
//                case GLCanvas.FragmentShader_Grad:
//                    uChangeColor =GLES20.glGetUniformLocation(program,"u_ChangeColor");
//                    break;
//                case GLCanvas.FragmentShader_Tone:
//                    toneChangeColor=GLES20.glGetUniformLocation(program,"u_ToneChangeColor");
//                    break;
//            }
//            GLES20.glUseProgram(program);
//            GLES20.glEnableVertexAttribArray(attribPosition);
//            GLES20.glEnableVertexAttribArray(attribTexCoord);
//
//            // Set the sampler to texture unit 0
//            GLES20.glUniform1i(uniformTexture, 0);
//        }
//
//        int[] loadTexture(int drawableId) {
//
//            int[] textureId = new int[1];
//            // Generate a texture object
//            GLES20.glGenTextures(1, textureId, 0);
//
//            int[] result = null;
//            if (textureId[0] != 0) {
//                this.textureId = textureId[0];
//                //InputStream is = Tools.readFromAsserts(path);
//                Bitmap bitmap;
//                try {
//                    //bitmap = BitmapFactory.decodeStream(is);
//                    bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
//                } finally {
////                    try {
////                       // is.close();
////                    } catch (IOException e) {
////                        throw new RuntimeException("Error loading Bitmap.");
////                    }
//                }
//                result = new int[3];
//                result[TEXTURE_ID] = textureId[0]; // TEXTURE_ID
//                result[TEXTURE_WIDTH] = bitmap.getWidth(); // TEXTURE_WIDTH
//                result[TEXTURE_HEIGHT] = bitmap.getHeight(); // TEXTURE_HEIGHT
//                // Bind to the texture in OpenGL
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
//                // Set filtering
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
//                        GLES20.GL_LINEAR);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
//                        GLES20.GL_NEAREST);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
//                        GLES20.GL_CLAMP_TO_EDGE);
//                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
//                        GLES20.GL_CLAMP_TO_EDGE);
//                // Load the bitmap into the bound texture.
//                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
//
//                // Recycle the bitmap, since its data has been loaded into OpenGL.
//                bitmap.recycle();
//
//            } else {
//                throw new RuntimeException("Error loading texture.");
//            }
//            return result;
//        }

        //endregion

        //region 滤镜参数设置
        @Override
        public void setGrayFilterColorData(float[] grayFilterColorData) {
              if (object instanceof GrayFilter) ((GrayFilter)object).setGrayFilterColorData(grayFilterColorData);
        }

        @Override
        public void setToneFilterColorData(float[] FileterColorData) {
             if (object instanceof ToneFilter) ((ToneFilter)object).setToneFilterColorData(FileterColorData);
        }

        //endregion


    }
    //region


    //endregion
}
