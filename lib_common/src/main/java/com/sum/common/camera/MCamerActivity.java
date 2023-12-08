package com.sum.common.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.sum.common.R;
import com.sum.framework.log.LogUtil;

/**
 * @author why
 * @date 2023/12/3 17:25
 */
public class MCamerActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private CameraSurfaceView mCameraSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);
        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        button = (Button) findViewById(R.id.takePic);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraSurfaceView.takePicture();

                mCameraSurfaceView.setOnPathChangedListener(path -> {
                            LogUtil.INSTANCE.d("-----拍摄的照片路径：" + path);

                            Intent intent = new Intent();
                            intent.putExtra("path", path);


                            setResult(RESULT_OK, intent);
                            finish();
                        }

                );
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.takePic) {
            mCameraSurfaceView.takePicture();
        }
    }


//    @Override
//    public void autoFocus() {
//        mCameraSurfaceView.setAutoFocus();
//    }
}
