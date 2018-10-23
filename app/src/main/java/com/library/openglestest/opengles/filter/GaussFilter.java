package com.library.openglestest.opengles.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.library.openglestest.R;
import com.library.openglestest.opengles.GLCanvas;
import com.library.openglestest.opengles.GLHelper;
import com.library.openglestest.opengles.Tools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class GaussFilter {
    private static final int TEXTURE_ID = 0;
    private static final int TEXTURE_WIDTH = 1;
    private static final int TEXTURE_HEIGHT = 2;


    private String currVertex= Tools.VertexShader;
    private String currFragment=Tools.FragmentShader_Gauss;

    private int attribPosition;
    private int attribTexCoord;
    private int hMatrix;
    private int uChangeColor,toneChangeColor;
    private FloatBuffer vertex;
    private ShortBuffer index;
    private int textureId;
    private float[] quadVertex = new float[]{
            0f, 0f, 0.0f, // Position 0
            0,  0f, // TexCoord 0           0，1
            0f, (-1.0f), 0.0f, // Position 1
            0f,  1f, // TexCoord 1             0，0
            1.0f, (-1.0f), 0.0f, // Position 2
            1.0f,  1f, // TexCoord 2         1 0
            1.0f,0f, 0.0f, // Position 3
            1.0f,  0f, // TexCoord 3           1，1
    };
    private short[] quadIndex = new short[]{
            (short) (0), // Position 0
            (short) (1), // Position 1
            (short) (2), // Position 2
            (short) (2), // Position 2
            (short) (3), // Position 3
            (short) (0), // Position 0
    };

    private Context context;
    public GaussFilter(Context context){
        this.context=context;
        loadVertex();
        initShader();
        loadTexture(R.drawable.lk);
    }
    public void drawSelf(){
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.textureId);
        vertex.position(0);
// load the position
        GLES20.glVertexAttribPointer(attribPosition,
                3, GLES20.GL_FLOAT,
                false, 20, vertex);
        vertex.position(3);
// load the texture coordinate
        GLES20.glVertexAttribPointer(attribTexCoord,
                2, GLES20.GL_FLOAT,
                false, 20, vertex);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT,
                index);
    }

    private void loadVertex(){
        // float size = 4
        this.vertex = ByteBuffer.allocateDirect(quadVertex.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        this.vertex.put(quadVertex).position(0);
        // short size = 2
        this.index = ByteBuffer.allocateDirect(quadIndex.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        this.index.put(quadIndex).position(0);
    }

    private void  initShader(){
//            String vertexSource = Tools.readFromAssets("VertexShaderMatrix.glsl");
        String vertexSource = Tools.readFromAssets(currVertex);
        String fragmentSource = Tools.readFromAssets(currFragment);
        // Load the shaders and get a linked program
        int program = GLHelper.loadProgram(vertexSource, fragmentSource);
        // Get the attribute locations
        attribPosition = GLES20.glGetAttribLocation(program, "a_position");
        attribTexCoord = GLES20.glGetAttribLocation(program, "a_texCoord");
        int uniformTexture = GLES20.glGetUniformLocation(program, "u_samplerTexture");
        switch (currVertex){
            case Tools.VertexShader:break;
            case Tools.VertexShaderMatrix:
                hMatrix = GLES20.glGetUniformLocation(program, "u_MVPMatrix");
                break;
        }
        switch (currFragment){
            case Tools.FragmentShader:break;
            case Tools.FragmentShader_Grad:
                uChangeColor =GLES20.glGetUniformLocation(program,"u_ChangeColor");
                break;
            case Tools.FragmentShader_Tone:
                toneChangeColor=GLES20.glGetUniformLocation(program,"u_ToneChangeColor");
                break;
        }
        GLES20.glUseProgram(program);
        GLES20.glEnableVertexAttribArray(attribPosition);
        GLES20.glEnableVertexAttribArray(attribTexCoord);
        // Set the sampler to texture unit 0
        GLES20.glUniform1i(uniformTexture, 0);
    }

    int[] loadTexture(int drawableId) {
        int[] textureId = new int[1];
        // Generate a texture object
        GLES20.glGenTextures(1, textureId, 0);
        int[] result = null;
        if (textureId[0] != 0) {
            this.textureId = textureId[0];
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
            } finally {
            }
            result = new int[3];
            result[TEXTURE_ID] = textureId[0]; // TEXTURE_ID
            result[TEXTURE_WIDTH] = bitmap.getWidth(); // TEXTURE_WIDTH
            result[TEXTURE_HEIGHT] = bitmap.getHeight(); // TEXTURE_HEIGHT
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE);
            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();

        } else {
            throw new RuntimeException("Error loading texture.");
        }
        return result;
    }

}
