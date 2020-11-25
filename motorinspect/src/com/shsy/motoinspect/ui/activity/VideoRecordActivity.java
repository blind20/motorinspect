package com.shsy.motoinspect.ui.activity;

import java.io.IOException;
import java.util.List;

import com.shsy.motoinspect.BaseActivity;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motorinspect.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class VideoRecordActivity extends BaseActivity  implements SurfaceHolder.Callback{
	private static final String TAG = "VideoRecordActivity";
    private SurfaceView mSurfaceview;
    private Button mBtnStartStop;
    private Button mBtnPlay;
    private boolean mStartedFlg = false;//是否正在录像
    private MediaRecorder mRecorder;
    private SurfaceHolder mSurfaceHolder;
    private Camera camera;
    private String path;
    private TextView textView;
    private int text = 0;
    
    private android.os.Handler handler = new android.os.Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            text++;
            textView.setText(text+"");
            handler.postDelayed(this,1000);
        }
    };

	@Override
	public int getLayoutResID() {
		return R.layout.video_record;
	}

	@Override
	public void findView() {
		mSurfaceview = (SurfaceView) findViewById(R.id.surfaceview);
        mBtnStartStop = (Button) findViewById(R.id.btnStartStop);
        mBtnPlay = (Button) findViewById(R.id.btnPlayVideo);
        textView = (TextView)findViewById(R.id.text);
	}

	@Override
	public void initParam() {
		path = getIntent().getStringExtra("videopath");
		mBtnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mStartedFlg) {
                    handler.postDelayed(runnable,1000);
                    if (mRecorder == null) {
                        mRecorder = new MediaRecorder();
                    }

//                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                    
					if (camera != null) {
//						camera.setDisplayOrientation(90);
//						camera.unlock();
						mRecorder.setCamera(camera);
					}

                    try {
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                        mRecorder.setVideoSize(640, 480);
                        mRecorder.setVideoFrameRate(30);
                        mRecorder.setVideoEncodingBitRate(1 * 1024 * 1024);
                        mRecorder.setOrientationHint(90);
                        mRecorder.setMaxDuration(480 * 1000);
                        mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

                        if (path != null) {
                            mRecorder.setOutputFile(path);
                            mRecorder.prepare();
                            mRecorder.start();
                            mStartedFlg = true;
//                            mBtnStartStop.setText("Stop");
                            mBtnStartStop.setBackgroundResource(R.drawable.recordstop);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    //stop
                    if (mStartedFlg) {
                        try {
                            handler.removeCallbacks(runnable);
                            mRecorder.stop();
                            mRecorder.reset();
                            mRecorder.release();
                            mRecorder = null;
//                            mBtnStartStop.setText("Start");
                            mBtnStartStop.setBackgroundResource(R.drawable.recordstart);
                            if (camera != null) {
                                camera.release();
                                camera = null;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mStartedFlg = false;
                }
            }
        });

        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	Intent intent = new Intent();
            	setResult(100, intent);
            	finish();
            }
        });

        SurfaceHolder holder = mSurfaceview.getHolder();
        holder.addCallback(this);
        // setType必须设置，要不出错.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    	try {
			if(surfaceHolder!=null){
				mSurfaceHolder = surfaceHolder;
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
			    if (camera != null) {
			        camera.setDisplayOrientation(90);
			        camera.setPreviewDisplay(surfaceHolder);
			        Parameters param = camera.getParameters();
			        int[] size = getPreviewSize();
			        param.setPictureSize(size[0], size[1]);
			        param.setJpegQuality(100);
			        param.setPictureFormat(PixelFormat.JPEG);
			        param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			        camera.setParameters(param);
			    }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    	if(surfaceHolder!=null){
    		mSurfaceHolder = surfaceHolder;
    		camera.startPreview();
    		camera.cancelAutoFocus();
    		/**
    		 * 关键代码 该操作必须在开启预览之后进行（最后调用），否则会黑屏，并提示该操作的下一步出错
    		 * 只有执行该步骤后才可以使用MediaRecorder进行录制
    		 * 否则会报 MediaRecorder(13280): start failed: -19
    		 */
    		camera.unlock();
    	}
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mSurfaceview = null;
        mSurfaceHolder = null;
        handler.removeCallbacks(runnable);
        if (mRecorder != null) {
            mRecorder.release(); 
            mRecorder = null;
            Log.d(TAG, "surfaceDestroyed release mRecorder");
        }
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
    
    private int[] getPreviewSize(){
    	int bestPreviewWidth = 1280;
    	int bestPreviewHeight = 720;
    	
    	int mCameraPreviewWidth;
    	int mCameraPreviewHeight;
    	int diffs = Integer.MAX_VALUE;
    	WindowManager windowManager  = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    	Display display = windowManager.getDefaultDisplay();
    	Point screenResolution  = new Point(display.getWidth(), display.getHeight());
    	List<Size> availablePreviewSizes  = camera.getParameters().getSupportedPreviewSizes();

    	for(Size previewSize: availablePreviewSizes ){
    		mCameraPreviewWidth = previewSize.width;
    		mCameraPreviewHeight = previewSize.height;
    		int newDiffs = Math.abs(mCameraPreviewWidth - screenResolution.y) 
    				+ Math.abs(mCameraPreviewHeight - screenResolution.x);
    		if (newDiffs == 0) {
                bestPreviewWidth = mCameraPreviewWidth;
                bestPreviewHeight = mCameraPreviewHeight;
                break;
            }
            if (diffs > newDiffs) {
                bestPreviewWidth = mCameraPreviewWidth;
                bestPreviewHeight = mCameraPreviewHeight;
                diffs = newDiffs;
            }
    	}
		return new int[]{bestPreviewWidth,bestPreviewHeight};
    }
    

}
