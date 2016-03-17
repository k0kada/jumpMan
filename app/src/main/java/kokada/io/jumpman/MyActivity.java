package kokada.io.jumpman;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MyActivity extends AppCompatActivity implements GameView.GameOverCallback {
    private GameView gameView;

    /**
     * ゲームオーバーの文面
     */
    @Override
    public void onGameOver() {
        Toast.makeText(this, "Game Over", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        gameView.setCallback(this);
        setContentView(gameView);
    }

}
