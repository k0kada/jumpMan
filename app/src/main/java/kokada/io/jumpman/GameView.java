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
import android.graphics.Color;
/**
 * スレッド間通信のための仕組み。(Handlerインスタンスを生成したスレッドへイベントを送るための仕組み)
 */
import android.os.Handler;
import android.view.MotionEvent;
/*
Surfaceのピクセルを実際にいじったり、Surfaceの変化を監視する人のためのインターフェイス。
SurfaceViewにはgetHolder()メソッドが用意されていて、そのSurfaceViewのホルダーのインスタンスを取得できる。
 */
import android.view.SurfaceHolder;
/*
SurfaceViewは、viewクラスを継承したクラスです。
Viewクラスよりも高速に描画ができ、ゲームプログラムに適しています。
SurfaceViewは、UIスレッドから独立して処理を行うビューです。
リアルタイムで処理を行うためには、UIスレッドから、独立したスレッドを起動を行う必要があります。
SurfaceViewは、アプリケーションのスレッドと行が処理のスレッドが独立している為、定期的な処理に向いています。
 */
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/*
原子的な更新が可能な boolean 値です。原子変数のプロパティーの詳細は、java.util.concurrent.atomic パッケージ仕様を参照してください。
AtomicBoolean は、原子更新フラグなどのアプリケーションで使用されます。 Boolean の代替として使用することはできません。
 */
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by kokada on 16/03/17.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private static final int GROUND_MOVE_TO_LEFT = 10;
    private static final int GROUND_HEIGHT = 50;

    private static final int ADD_GROUND_COUNT = 5;

    private static final int GROUND_WIDTH = 340;
    private static final int GROUND_BLOCK_HEIGHT = 100;

    private Ground lastGround;

    private final List<Ground> groundList = new ArrayList<>();
    private final Random rand = new Random(System.currentTimeMillis());

    private Bitmap marioBitmap;
    private Mario mario;

    /**
     * 自機と地面との距離
     */
    private final Mario.Callback marioCallback = new Mario.Callback() {
        /**
         * 自機と地面との距離を返す
         * @param mario
         * @return
         */
        @Override
        public int getDistanceFromGround(Mario mario) {
            int width = getWidth();
            int height = getHeight();

            //拡張for文
            for (Ground ground : groundList) {

                //今までいた地面(ブロック)から外れたら
                if (!ground.isShown(width, height)) {
                    continue;
                }

                //地面(ブロック)から左右ではみ出しているかの判定
                boolean horizontal = !(mario.rect.left >= ground.rect.right || mario.rect.right <= ground.rect.left);
                //はみ出していなかったら地面(ブロック)までの距離を返す
                if (horizontal) {
                    //gameover判定
                    int distanceFromGround = ground.rect.top - mario.rect.bottom;
                    //自機が地面の下に行ったらゲームオーバー
                    if (distanceFromGround < 0) {
                        gameOver();
                        return Integer.MAX_VALUE;
                    }

                    return distanceFromGround;
                }
            }

            return Integer.MAX_VALUE;
        }
    };

    private static final long DRAW_INTERVAL = 1000 / 80;

    /**
     * 画面描写
     */
    private class DrawTread extends Thread {
        private final AtomicBoolean isFinished = new AtomicBoolean(false);
        //描写止める
        public void finish() {
            isFinished.set(true);
        }

        //描写開始
        @Override
        public void run() {
            SurfaceHolder holder = getHolder();

            while (!isFinished.get()) {

                if (holder.isCreating()) {
                    continue;
                }
                Canvas canvas = holder.lockCanvas();
                if (canvas == null) {
                    continue;
                }
                drawGame(canvas);

                holder.unlockCanvasAndPost(canvas);

                synchronized (this) {
                    try {
                        //描写間隔
                        wait(DRAW_INTERVAL);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    private DrawTread drawTread;

    /**
     * 描写を始める
     * @return
     */
    public void startDrawThread() {
        stopDrawThread();

        drawTread = new DrawTread();
        drawTread.start();
    }

    /**
     * 描写を止める判定
     * @return
     */
    public boolean stopDrawThread() {
        if (drawTread == null) {
            return false;
        }
        drawTread.finish();
        drawTread = null;

        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        startDrawThread();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holderstart) {
        startDrawThread();
    }

    //SurfaceViewの描画スレッド内からUIを変更するために使用するHandlerを追加
    private final Handler handler = new Handler();

    /**
     * GameViewのイベントを外部に伝えるためのCallbackインターフェイスを追加
     */
    public interface GameOverCallback {
        void onGameOver();
    }

    private GameOverCallback gameOverCallback;

    public void setCallback (GameOverCallback callback) {
        gameOverCallback = callback;
    }

    private final AtomicBoolean isGameOver = new AtomicBoolean();

    private void gameOver() {
        if (isGameOver.get()) {
            return;
        }

        //フラグをtrueにセットする
        isGameOver.set(true);
        //自機の加速度を0にする
        mario.stop();

        handler.post(new Runnable() {
            @Override
            public void run() {
                gameOverCallback.onGameOver();
            }
        });
    }

    /**
     * viewクラス継承
     * @param context
     */
    public GameView(Context context) {
        super(context);

        marioBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mario);
        mario = new Mario(marioBitmap, 0, 0, marioCallback);

        getHolder().addCallback(this);
    }

    /**
     * キャンバス描写
     * @param canvas
     */
    private void drawGame(Canvas canvas) {
        canvas.drawColor(Color.YELLOW);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if (lastGround == null) {
            int top = height - GROUND_HEIGHT;
            lastGround = new Ground(0, top, width, height);
            groundList.add(lastGround);
        }

        if (lastGround.isShown(width, height)) {
            for (int i = 0; i < ADD_GROUND_COUNT; i++) {
                int left = lastGround.rect.right;

                //地面の高さをランダムに生成
                int groundHeight = rand.nextInt(height / GROUND_BLOCK_HEIGHT) * GROUND_HEIGHT / 2 + GROUND_HEIGHT;
                System.out.println(groundHeight);
                int top = height - groundHeight;
                int right = left + GROUND_WIDTH;
                lastGround = new Ground(left, top, right, height);
                groundList.add(lastGround);
            }
        }

        for (int i = 0; i < groundList.size(); i++) {
            Ground ground = groundList.get(i);

            if (ground.isAvailable()) {
                ground.move(GROUND_MOVE_TO_LEFT);
                if (ground.isShown(width, height)) {
                    ground.draw(canvas);
                }
            } else {
                groundList.remove(ground);
                i--;
            }
        }

        mario.move();
        mario.draw(canvas);
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
                //タッチ時間を判定して自機をジャンプさせる
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
