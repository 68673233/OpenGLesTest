package com.library.openglestest;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.library.openglestest.opengles.GLCanvas;
import com.library.openglestest.opengles.IFilter;
import com.library.openglestest.opengles.filter.GaussFilter;
import com.library.openglestest.opengles.filter.GrayFilter;
import com.library.openglestest.opengles.filter.ToneFilter;

public class GrayActivity extends AppCompatActivity {

    float[] rgb=new float[]{0,0,0};
    SeekBar sbR;
    SeekBar sbG;
    SeekBar sbB;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gray);

        LinearLayout ll=findViewById(R.id.ll);
        ll.addView(genGLCanvas().getView());

        sbR=findViewById(R.id.pR);
        sbG=findViewById(R.id.pG);
        sbB=findViewById(R.id.pB);

        SeekBarOnChange sbc=new SeekBarOnChange();
        sbR.setOnSeekBarChangeListener(sbc);
        sbG.setOnSeekBarChangeListener(sbc);
        sbB.setOnSeekBarChangeListener(sbc);
    }

    class SeekBarOnChange implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (seekBar==GrayActivity.this.sbR) GrayActivity.this.rgb[0]=(seekBar.getProgress()/100.0f);
            else if (seekBar==GrayActivity.this.sbG) GrayActivity.this.rgb[1]=(seekBar.getProgress()/100.0f);
            else if (seekBar==GrayActivity.this.sbB) GrayActivity.this.rgb[2]=(seekBar.getProgress()/100.0f);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    private GLCanvas genGLCanvas(){
        GLCanvas canvas=new GLCanvas(this, GLSurfaceView.RENDERMODE_CONTINUOUSLY, GaussFilter.class) {
            @Override
            public void onDrawFrame(IFilter filter) {
                if (rgb[0]==0 || rgb[1]==0 || rgb[2]==0)return;
//                filter.setGrayFilterColorData( rgb);
                filter.setToneFilterColorData(rgb);
            }
        };
        return canvas;
    }
}
