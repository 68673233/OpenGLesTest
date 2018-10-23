package com.library.openglestest.opengles;

public interface IFilter {
    public void setGrayFilterColorData(float[] grayFilterColorData);

    /**
     * 设置色调 暖色R：1  G：1  B：0
     *         冷色R：0  G：0  B：1
     * @param FileterColorData
     */
    public void setToneFilterColorData(float[] FileterColorData);
}
