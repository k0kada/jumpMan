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

    private static final int HIT_MARGIN_LEFT = 30;
    private static final int HIT_MARGIN_RIGHT = 10;

    private static final float GRAVITY = 20.8f;
    private static final float WEIGHT = GRAVITY * 10;

    private final Paint paint = new Paint();

    private Bitmap bitmap;

    final Rect rect;
    final Rect hitRect;

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
        this.hitRect = new Rect(left, top, right, bottom);
        this.hitRect.left += HIT_MARGIN_LEFT;
        this.hitRect.right -= HIT_MARGIN_RIGHT;

        this.callback = callback;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, rect.left, rect.top, paint);
    }

    private float velocity = 0;

    public void jump(float power) {
        //加速度計算
        velocity = (power * WEIGHT);
    }

    public void stop() {
        velocity = 0;
    }

    public void move() {
        int distanceFromGround = callback.getDistanceFromGround(this);

        //地面を下に突き抜けた時
        if (velocity < 0 && velocity < -distanceFromGround) {
            velocity = -distanceFromGround;
        }

        rect.offset(0, Math.round(-1 * velocity));

        //地面から激突したら止まる
        if (distanceFromGround == 0) {
            return;
        } else if (distanceFromGround < 0) {
            rect.offset(0, distanceFromGround);
            return;
        }

        //下へ落下
        velocity -= GRAVITY;
    }
}
