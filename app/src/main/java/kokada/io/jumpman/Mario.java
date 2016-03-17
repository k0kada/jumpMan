package kokada.io.jumpman;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
//短形描写
import android.graphics.RectF;


/**
 * Created by kokada on 16/03/17.
 * 自機表示クラス
 */
public class Mario {
    private static final int BLOOL_SIZE = 100;

    private static final Rect BITMAP_SRC_JUMPING = new Rect(0, BLOOL_SIZE * 2, BLOOL_SIZE, BLOOL_SIZE * 3);
    private static final Rect BITMAP_SRC_RUNNING = new Rect(BLOOL_SIZE * 3, 0, BLOOL_SIZE * 4, BLOOL_SIZE);

    private static final int HIT_MARGIN_LEFT = 30;
    private static final int HIT_MARGIN_RIGHT = 10;

    private static final float GRAVITY = 20.8f;
    private static final float WEIGHT = GRAVITY * 10;

    private final Paint paint = new Paint();

    private Bitmap bitmap;

    final RectF rect;
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
        int right = left + BLOOL_SIZE;
        int bottom = top + BLOOL_SIZE;
        this.rect = new RectF(left, top, right, bottom);
        this.hitRect = new Rect(left, top, right, bottom);
        this.hitRect.left += HIT_MARGIN_LEFT;
        this.hitRect.right -= HIT_MARGIN_RIGHT;

        this.callback = callback;
    }

    public void draw(Canvas canvas) {
        Rect src = BITMAP_SRC_RUNNING;
        if (velocity != 0) {
            src = BITMAP_SRC_JUMPING;
        }

        canvas.drawBitmap(bitmap, src, rect, paint);
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
        hitRect.offset(0, Math.round(-1 * velocity));

        //地面との距離が0なら地面の上に止まる
        if (distanceFromGround == 0) {
            return;
        } else if (distanceFromGround < 0) {
            rect.offset(0, distanceFromGround);
            hitRect.offset(0, distanceFromGround);
            return;
        }

        //下へ落下
        velocity -= GRAVITY;
    }
}
