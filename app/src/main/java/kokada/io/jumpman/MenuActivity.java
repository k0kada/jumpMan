package kokada.io.jumpman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by kokada on 16/03/19.
 */
public class MenuActivity extends Activity implements View.OnClickListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_menu);
            // ボタンコントロールのインスタンスを取得
            Button button = (Button)findViewById(R.id.GameStartButton);

            // ボタンクリックイベントを登録
            button.setOnClickListener(this);
        }

        // ボタンクリックイベント
        public void onClick(View v) {
            // ゲームメイン画面に遷移
            Intent intent = new Intent( MenuActivity.this, MyActivity.class );
            startActivity( intent );
        }
}
