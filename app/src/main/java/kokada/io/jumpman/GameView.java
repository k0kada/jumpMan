package kokada.io.jumpman;

//アプリケーションの環境情報とかをグローバル(Android OSの全域）で受け渡しするためのインターフェース
//アクティビティの起動とかブロードキャスト、インテントの受け取りといった他のアプリからの応答を行え、アンドロイド特有のリソース・クラスにアクセスすることも出来る。
import android.content.Context;
//Bitmapクラスはイメージに関するクラスです。外部から画像を読み込んだり、新しく作成したりします。
import android.graphics.Bitmap;
//BitmapFactoryクラスは外部ファイルやリソース、ストリームなどからBitmapクラスのオブジェクトを作成するためのクラスです。
import android.graphics.BitmapFactory;
//Canvasクラスは、文字、図形等を描画することができます。
import android.graphics.Canvas;
//MotionEventは、デバイスの種類に応じて、絶対的または、相対的な移動や その他のデータのいづれかを保持することができます。
import android.view.MotionEvent;
//Viewクラスは、ビューの土台となる機能を持っているクラスです。
import android.view.View;

/**
 * Created by kokada on 16/03/17.
 */
public class GameView extends View {

    private static final int GROUND_MOVE_TO_LEFT = 10;
    private static final int GROUND_HEIGHT = 50;
    private Ground ground;

    private Bitmap marioBitmap;
    private Mario mario;

    /**
     * 自機と地面との距離を計算
     */
    private final Mario.Callback marioCallback = new Mario.Callback() {
        /**
         *
         * @param mario
         * @return
         */
        @Override
        public int getDistanceFromGround(Mario mario) {
            //地面から左右ではみ出しているかの判定
            boolean horizontal = !(mario.rect.left >= ground.rect.right || mario.rect.right <= ground.rect.left);

            if (!horizontal) {
                return Integer.MAX_VALUE;
            }

            return ground.rect.top - mario.rect.bottom;
        }
    };

    /**
     * viewクラス継承
     * @param context
     */
    public GameView(Context context) {
        super(context);

        marioBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mario);
        mario = new Mario(marioBitmap, 0, 0, marioCallback);
    }

    /**
     * 画面描写
     * @param canvas
     */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if (ground == null) {
            int top = height - GROUND_HEIGHT;
            ground = new Ground(0, top, width, height);
        }
        mario.move();
        ground.move(GROUND_MOVE_TO_LEFT);
        mario.draw(canvas);
        ground.draw(canvas);

        //再度onDrawを実行
        invalidate();
    }

    //ミリ秒
    private static final long MAX_TOUCH_TIME = 500;
    //タッチした時刻
    private long touchDownStartTime;

    /**
     * タッチ判定
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDownStartTime = System.currentTimeMillis();
                return true;
            case MotionEvent.ACTION_UP:
                float time = System.currentTimeMillis() - touchDownStartTime;
                jumpMario(time);
                touchDownStartTime = 0;
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 自機をジャンプさせる
     * @param time
     */
    private void jumpMario(float time) {
        //地面に着地していなかったら
        if (marioCallback.getDistanceFromGround(mario) > 0) {
            return;
        }

        //タッチしていた時間を計算されせジャンプさせる(タッチ時間によってジャンプ距離が変わる)
        mario.jump(Math.min(time, MAX_TOUCH_TIME) / MAX_TOUCH_TIME);
    }
}
