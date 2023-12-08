package com.sum.common.camera;

/**
 * @author why
 * @date 2023/12/3 16:41
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.sum.framework.log.LogUtil;
import com.sum.framework.utils.mmkv.FileStorageUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;


/**
 * Created by LK-RYX on 2018/4/28.
 */

public class CameraSurfaceView2 extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback {

    private static final String TAG = "CameraSurfaceView";

    private Context mContext;
    private SurfaceHolder holder;
    private Camera mCamera;

    private int mScreenWidth;
    private int mScreenHeight;

    private File mFile;
    private OnPathChangedListener onPathChangedListener;

    public OnPathChangedListener getOnPathChangedListener() {
        return onPathChangedListener;
    }

    public void setOnPathChangedListener(OnPathChangedListener onPathChangedListener) {
        this.onPathChangedListener = onPathChangedListener;
    }

    public CameraSurfaceView2(Context context) {
        this(context, null);
    }

    public CameraSurfaceView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraSurfaceView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        getScreenMetrix(context);
//        topView = new CameraTopRectView(context, attrs);

        initView();

    }

    //拿到手机屏幕大小
    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;

    }

    private void initView() {
        holder = getHolder();//获得surfaceHolder引用
        holder.addCallback(this);
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置类型

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        if (mCamera == null) {

            mCamera = Camera.open();

            try {
                mCamera.setPreviewDisplay(holder);//摄像头画面显示在Surface上
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        LogUtil.INSTANCE.d("------surfaceChanged");

        setCameraParams(mCamera, width, height);
        mCamera.startPreview();
//        mCamera.takePicture(null, null, jpeg);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        LogUtil.INSTANCE.d("------surfaceDestroyed");

        mCamera.stopPreview();//停止预览
        mCamera.release();//释放相机资源
        mCamera = null;
        holder = null;
    }

    @Override
    public void onAutoFocus(boolean success, Camera Camera) {
        if (success) {
            LogUtil.INSTANCE.d("------onAutoFocus success=" + success);
            System.out.println(success);
        }
    }


    private void setCameraParams(Camera camera, int width, int height) {
        //LogUtil.d("------setCameraParams  width="+width+"  height="+height);
        Camera.Parameters parameters = mCamera.getParameters();
        // 获取摄像头支持的PictureSize列表
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        /**从列表中选取合适的分辨率*/
        Camera.Size picSize = getProperSize(pictureSizeList, ((float) height / width));
        if (null == picSize) {
            Log.i(TAG, "null == picSize");
            picSize = parameters.getPictureSize();
        }

        // 根据选出的PictureSize重新设置SurfaceView大小
        float w = picSize.width;
        float h = picSize.height;
        parameters.setPictureSize(picSize.width, picSize.height);
        this.setLayoutParams(new ConstraintLayout.LayoutParams((int) (height * (h / w)), height));

        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();

        Camera.Size preSize = getProperSize(previewSizeList, ((float) height) / width);
        if (null != preSize) {
            LogUtil.INSTANCE.d("----------preSize.width=" + preSize.width + " --- preSize.height=" + preSize.height);
            parameters.setPreviewSize(preSize.width, preSize.height);
        }

        parameters.setJpegQuality(100); // 设置照片质量
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }

        mCamera.cancelAutoFocus();//自动对焦。
        mCamera.setDisplayOrientation(90);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
        mCamera.setParameters(parameters);

    }

    /**
     * 从列表中选取合适的分辨率
     * 默认w:h = 4:3
     * <p>注意：这里的w对应屏幕的height
     * h对应屏幕的width<p/>
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        Log.i(TAG, "screenRatio=" + screenRatio);
        Camera.Size result = null;
        for (Camera.Size size : pictureSizeList) {
            float currentRatio = ((float) size.width) / size.height;
            if (currentRatio - screenRatio == 0) {
                result = size;
                break;
            }
        }

        if (null == result) {
            for (Camera.Size size : pictureSizeList) {
                float curRatio = ((float) size.width) / size.height;
                if (curRatio == 4f / 3) {// 默认w:h = 4:3
                    result = size;
                    break;
                }
            }
        }

        return result;
    }


    // 拍照瞬间调用
    private Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            LogUtil.INSTANCE.d("---------执行到这里了吗+1：shutter");
        }
    };

    // 获得没有压缩过的图片数据
    private Camera.PictureCallback raw = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera Camera) {

            LogUtil.INSTANCE.d("---------执行到这里了吗+：raw");
        }
    };

    //创建jpeg图片回调数据对象
    private Camera.PictureCallback jpeg = new Camera.PictureCallback() {

        private Bitmap bitmap;

        @Override
        public void onPictureTaken(byte[] data, Camera Camera) {


            BufferedOutputStream bos = null;
            Bitmap bm = null;
            if (data != null) {

            }

            try {
                // 获得图片
                bm = BitmapFactory.decodeByteArray(data, 0, data.length);

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

//                  图片存储前旋转
                    Matrix m = new Matrix();
                    int height = bm.getHeight();
                    int width = bm.getWidth();
                    m.setRotate(90);
                    //旋转后的图片
                    bitmap = Bitmap.createBitmap(bm, 0, 0, width, height, m, true);

                    LogUtil.INSTANCE.d("---------执行到这里了吗+3");

                    String photo =  String.valueOf(new Date().getTime() + ".jpg");
//                    File file = new File(FileStorageUtil.INSTANCE.getPath() + File.separator + "DIC");

                    mFile = new File(FileStorageUtil.INSTANCE.getAppTempPath() + File.separator +  photo);
//                    if (!mFile.exists()) {
//                        mFile.mkdirs();
//                    }
                    LogUtil.INSTANCE.d("---------自定义照片保存路径：" + mFile.getPath());

                    bos = new BufferedOutputStream(new FileOutputStream(mFile));

//                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
//                            data.length);
//                    Bitmap sizeBitmap = Bitmap.createScaledBitmap(bitmap,
//                            topView.getViewWidth(), topView.getViewHeight(), true);
//                    bm = Bitmap.createBitmap(sizeBitmap, topView.getRectLeft(),
//                            topView.getRectTop(),
//                            topView.getRectRight() - topView.getRectLeft(),
//                            topView.getRectBottom() - topView.getRectTop());// 截取


                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中


                } else {
                    Toast.makeText(mContext, "没有检测到内存卡", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(bos != null) {
                        bos.flush();//输出
                        bos.close();//关闭
                    }
                    bm.recycle();// 回收bitmap空间
                    mCamera.stopPreview();// 关闭预览
                    mCamera.startPreview();// 开启预览
                    if (onPathChangedListener != null) {
                        LogUtil.INSTANCE.d("------监听路径：" + mFile.getPath());
                        onPathChangedListener.onValueChange(mFile.getPath());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    public void takePicture() {
        //设置参数,并拍照
        setCameraParams(mCamera, getWidth(), getHeight());
        // 当调用camera.takePiture方法后，camera关闭了预览，这时需要调用startPreview()来重新开启预览
        mCamera.takePicture(null, null, jpeg);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public void setAutoFocus() {
        mCamera.autoFocus(this);
    }


    public interface OnPathChangedListener {
        void onValueChange(String path);
    }
}

