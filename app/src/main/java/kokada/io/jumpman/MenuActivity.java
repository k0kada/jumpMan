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
    Bundle stageId = new Bundle();

    private Button button1;
    private Button button2;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_menu);


            // ボタンコントロールのインスタンスを取得
            button1 = (Button)findViewById(R.id.stage1);
            button2 = (Button)findViewById(R.id.stage2);

            // ボタンクリックイベントを登録
            button1.setOnClickListener(this);
            button2.setOnClickListener(this);


        }

        // ボタンクリックイベント
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.stage1:
                    stageId.putString("stageId", "stage1");
                    break;
                case R.id.stage2:
                    stageId.putString("stageId", "stage2");
                    break;
            }


            // ゲームメイン画面に遷移
            Intent intent = new Intent( MenuActivity.this, MainActivity.class );
            System.out.println(stageId);
            intent.putExtras(stageId);
            startActivity( intent );
        }
}
