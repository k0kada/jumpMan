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

    public Mario(Bitmap bitmap, int left, int top) {
        this.bitmap = bitmap;
        int right = left + bitmap.getWidth();
        int bottom = top + bitmap.getHeight();
        this.rect = new Rect(left, top, right, bottom);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, rect.left, rect.top, paint);
    }
}
