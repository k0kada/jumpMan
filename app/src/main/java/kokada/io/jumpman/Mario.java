package kokada.io.jumpman;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


/**
 * Created by kokada on 16/03/17.
 * 自機表示クラス
 */
public class Mario {
    private final Paint paint = new Paint();

    private Bitmap bitmap;

    final Rect rect;

    /**
     * 自機と地面の距離を呼び出す
     */
    public interface Callback {
        int getDistanceFromGround(Mario mario);
    }
    private final Callback callback;

    public Mario(Bitmap bitmap, int left, int top, Callback callback) {
        this.bitmap = bitmap;
        int right = left + bitmap.getWidth();
        int bottom = top + bitmap.getHeight();
        this.rect = new Rect(left, top, right, bottom);
        this.callback = callback;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, rect.left, rect.top, paint);
    }

    public void move() {
        int distanceFromGround = callback.getDistanceFromGround(this);
        //地面から激突したら止まる
        if (distanceFromGround == 0) {
            return;
        } else if (distanceFromGround < 0) {
            rect.offset(0, distanceFromGround);
            return;
        }

        //下へ落下
        rect.offset(0, 5);
    }
}
