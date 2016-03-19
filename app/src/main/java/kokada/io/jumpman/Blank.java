package kokada.io.jumpman;

import android.graphics.Canvas;

/**
 * Created by kokada on 16/03/19.
 */
public class Blank extends Ground {

    public Blank(int left, int top, int right, int bottom) {
        super(left, top, right, bottom);
    }

    @Override
    public void draw(Canvas canvas) {

    }

    /**
     * 穴じゃない地面
     * @return
     */
    @Override
    public boolean isSolid() {
        return false;
    }
}
