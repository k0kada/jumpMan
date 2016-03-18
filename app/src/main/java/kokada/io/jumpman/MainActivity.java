package kokada.io.jumpman;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;

public class MainActivity extends AppCompatActivity implements GameView.GameOverCallback {

    private GameView gameView;

    /**
     * ゲームオーバーの文面
     */
    @Override
    public void onGameOver() {
        final Bundle savedInstanceState = new Bundle();

        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setTitle("Game Over");
        alertDlg.setMessage("やり直しますか？");
        alertDlg.setPositiveButton("やり直す", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //再起動
                onDestroy();
                onCreate(savedInstanceState);
            }
        });

        alertDlg.setNegativeButton("メニューへ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //メニューへ遷移
                startActivity(new Intent(MainActivity.this, MenuActivity.class));
                //Activity停止
                finish();
            }
        });

        //ダイアログ表示
        alertDlg.create().show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        String stageId = i.getStringExtra("stageId");

        switch (stageId) {
            case "stage1":
            break;

        }
        gameView = new GameView(this);
        gameView.setCallback(this);
        setContentView(gameView);
    }

}
