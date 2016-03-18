package kokada.io.jumpman;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class MyActivity extends AppCompatActivity implements GameView.GameOverCallback {

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
        alertDlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //再起動
                onDestroy();
                onCreate(savedInstanceState);
            }
        });
        alertDlg.create().show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        gameView.setCallback(this);
        setContentView(gameView);
    }


}
