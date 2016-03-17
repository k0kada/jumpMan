package kokada.io.jumpman;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
//座標など
import android.graphics.Rect;

/**
 * Created by kokada on 16/03/17.
 * 地面を作成
 */
public class Ground {
    //茶色
    private int COLOR = Color.rgb(153, 76, 0);
    private Paint paint = new Paint();

    final Rect rect;

    /**
     * Groundのコンストラクタ
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public Ground(int left, int top, int right, int bottom) {
        rect = new Rect(left, top, right, bottom);
        paint.setColor(COLOR);
    }

    /**
     * Rectオブジェクトの位置に茶色の四角形を描画する
     * @param canvas
     */
    public void draw(Canvas canvas) {
        canvas.drawRect(rect, paint);
    }

    /**
     * 地面を移動させる
     * @param moveToLeft
     */
    public void move(int moveToLeft) {
        rect.offset(-moveToLeft, 0);
    }
}
