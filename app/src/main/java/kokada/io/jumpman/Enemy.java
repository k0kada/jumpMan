package kokada.io.jumpman;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;



/**
 * Created by kokada on 16/03/17.
 * 敵表示クラス
 */
public class Enemy {
    private static final int BLOOL_SIZE = 100;

    private static final Rect BITMAP_SRC_RUNNING = new Rect(0, 0, BLOOL_SIZE, BLOOL_SIZE);

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
        int getDistanceFromGround(Enemy enemy);
    }
    private final Callback callback;

    public Enemy(Bitmap bitmap, int left, int top, Callback callback) {
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

    public void move(int moveToLeft) {
        rect.offset(-moveToLeft, 0);
        hitRect.left -= moveToLeft;
        hitRect.right -= moveToLeft;

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
