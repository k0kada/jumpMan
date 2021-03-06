package kokada.io.jumpman;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;

public class MainActivity extends AppCompatActivity
        implements Stage1View.GameOverCallback,Stage2View.GameOverCallback, Stage31View.GameOverCallback, Stage4View.GameOverCallback {

    private Stage1View stage1View;
    private Stage2View stage2View;
    private Stage31View stage3View;
    private Stage4View stage4View;

    /**
     * ゲームオーバーの文面
     */
    @Override
    public void onGameOver() {
        final Bundle savedInstanceState = new Bundle();

        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setTitle("Game Over");
        alertDlg.setMessage("やり直しますか？");
        alertDlg.setCancelable(false);

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
                stage1View = new Stage1View(this);
                stage1View.setCallback(this);
                setContentView(stage1View);
                break;

            case "stage2":
                stage2View = new Stage2View(this);
                stage2View.setCallback(this);
                setContentView(stage2View);
                break;

            case "stage3":
                stage3View = new Stage31View(this);
                stage3View.setCallback(this);
                setContentView(stage3View);
                break;

            case "stage4":
                stage4View = new Stage4View(this);
                stage4View.setCallback(this);
                setContentView(stage4View);
                break;
        }

    }

}
