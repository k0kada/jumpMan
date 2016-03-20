package kokada.io.jumpman;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by kokada on 16/03/17.
 */
public class Stage4View extends SurfaceView implements SurfaceHolder.Callback {

    //ジャンプゲージ
    private static final float POWER_GAUGE_HEIGHT = 30;
    private static final Paint PAINT_POWER_GAUGE = new Paint();

    static {
        PAINT_POWER_GAUGE.setColor(Color.RED);
    }

    //スコア表示
    private static final float SCORE_TEXT_SIZE = 80.0f;
    private long score;
    private static final Paint paintScore = new Paint();

    //地面設定
    private static final int GROUND_MOVE_TO_LEFT = 20;
    private static final int GROUND_HEIGHT = 50;

    private static final int ADD_GROUND_COUNT = 5;

    private static final int GROUND_WIDTH = 340;
    private static final int GROUND_BLOCK_HEIGHT = 100;

    private Ground lastGround;

    private final List<Ground> groundList = new ArrayList<>();
    private final Random rand = new Random(System.currentTimeMillis());

    private Bitmap marioBitmap;
    private Mario mario;

    private static final int ENEMY_MOVE_TO_LEFT = 30;
    private static final int ENEMY_APPEAR_FROM_LEFT = 1800;
    private Bitmap enemyBitmap;
    private Enemy enemy;

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
                boolean horizontal = !(mario.hitRect.left >= ground.rect.right || mario.hitRect.right <= ground.rect.left);
                //自機と敵の幅が重なっているかの判定
                boolean enemyHorizontal = (mario.hitRect.left <= enemy.hitRect.right && enemy.hitRect.left <= mario.hitRect.right) || (enemy.hitRect.left <= mario.hitRect.right && mario.hitRect.left <= enemy.hitRect.right);
                //敵の再出現判定
                boolean enemyRespawn = enemy.hitRect.right < 0;

                if (enemyHorizontal) {
                    //敵にぶつかったら死亡
                    int distanceFromEnemy = enemy.hitRect.top - mario.hitRect.bottom;
                    if (distanceFromEnemy < 0) {
                        gameOver();
                    }
                }

                //敵が画面左を過ぎたらリスポーンする
                if (enemyRespawn) {
                    enemy = new Enemy(enemyBitmap, ENEMY_APPEAR_FROM_LEFT, 0, enemyCallback);
                }

                //はみ出していなかったら地面(ブロック)までの距離を返す
                if (horizontal) {

                    //gameover判定
                    int distanceFromGround = ground.rect.top - mario.hitRect.bottom;


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

    /**
     * 敵と地面の距離
     */
    private final Enemy.Callback enemyCallback = new Enemy.Callback() {
        /**
         *
         * @param enemy
         * @return
         */
        @Override
        public int getDistanceFromGround(Enemy enemy) {
            int width = getWidth();
            int height = getHeight();
            //拡張for文
            for (Ground ground : groundList) {

                //今までいた地面(ブロック)から外れたら
                if (!ground.isShown(width, height)) {
                    continue;
                }

                //地面(ブロック)から左右ではみ出しているかの判定
                boolean horizontal = !(enemy.hitRect.left >= ground.rect.right || enemy.hitRect.right <= ground.rect.left);
                //はみ出していなかったら地面(ブロック)までの距離を返す
                if (horizontal) {

                    //敵と地面の距離
                    int distanceFromGround = ground.rect.top - enemy.hitRect.bottom;

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
    public Stage4View(Context context) {
        super(context);

        marioBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mario);
        mario = new Mario(marioBitmap, 0, 0, marioCallback);

        enemyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
        enemy = new Enemy(enemyBitmap, ENEMY_APPEAR_FROM_LEFT, 0, enemyCallback);

        //スコア設定
        paintScore.setColor(Color.BLACK);
        paintScore.setTextSize(SCORE_TEXT_SIZE);
        paintScore.setAntiAlias(true);

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

                //地面が生成されるごとにスコアを足す
                if (isGameOver.get() != true) {
                    score += 150;
                }

                //地面の高さをランダムに生成
                int groundHeight = rand.nextInt(height / GROUND_BLOCK_HEIGHT) * GROUND_BLOCK_HEIGHT / 2 + GROUND_HEIGHT;

                int left = lastGround.rect.right;
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

        //マリオ表示
        mario.move();
        mario.draw(canvas);

        //敵表示
        enemy.move(ENEMY_MOVE_TO_LEFT);
        enemy.draw(canvas);

        //タッチ時間に応じてパワーゲージを表示
        if (touchDownStartTime > 0) {
            float elapsedTime = System.currentTimeMillis() - touchDownStartTime;
            canvas.drawRect(0, 0, width * (elapsedTime / MAX_TOUCH_TIME), POWER_GAUGE_HEIGHT, PAINT_POWER_GAUGE);
        }

        //スコア表示
        canvas.drawText("Score:" + score, 0, SCORE_TEXT_SIZE, paintScore);
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
