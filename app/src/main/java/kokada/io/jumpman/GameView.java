package kokada.io.jumpman;

/**
 * Created by kokada on 16/03/17.
 */

/*
 * アプリケーションの環境情報とかをグローバル(Android OSの全域）で受け渡しするためのインターフェース
 アクティビティの起動とかブロードキャスト、インテントの受け取りといった他のアプリからの応答を行え、アンドロイド特有のリソース・クラスにアクセスすることも出来る。
 */
import android.content.Context;
/**
 * Bitmapクラスはイメージに関するクラスです。外部から画像を読み込んだり、新しく作成したりします。
 */
import android.graphics.Bitmap;
/**
 * BitmapFactoryクラスは外部ファイルやリソース、ストリームなどからBitmapクラスのオブジェクトを作成するためのクラスです。
 */
import android.graphics.BitmapFactory;
/**
 *Canvasクラスは、文字、図形等を描画することができます。
 */
import android.graphics.Canvas;
/**
 * Paintオブジェクトは、描画する場合に使用されるペンのようなものです。
 ペンのように描画する文字や図形の色や線の太さ等を指定することができます。
 */
import android.graphics.Paint;
/**
 * Viewクラスは、ビューの土台となる機能を持っているクラスです。
 */
import android.view.View;


public class GameView extends View {
    private static final Paint PAINT = new Paint();
    private Bitmap marioBitmap;

    /**
     * viewクラス継承
     * @param context
     */
    public GameView(Context context) {
        super(context);

        marioBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mario);
    }

    /**
     * マリオ画像読み込み
     * @param canvas
     */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(marioBitmap, 0, 0, PAINT);
    }
}
