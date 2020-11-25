package com.shsy.motoinspect.ui.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.shsy.motoinspect.BaseFragment;
import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.adapter.GridAdapter;
import com.shsy.motoinspect.adapter.GridVideoAdapter;
import com.shsy.motoinspect.entity.CarListInfoEntity;
import com.shsy.motoinspect.entity.CarPhotoEntity;
import com.shsy.motoinspect.entity.VehCheckProcess;
import com.shsy.motoinspect.entity.VideoInfo;
import com.shsy.motoinspect.network.MyHttpUtils;
import com.shsy.motoinspect.ui.activity.MainActivity;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.ProgressDlgUtil;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motoinspect.utils.ToolUtils;
import com.shsy.motorinspect.R;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.DropBoxManager.Entry;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import okhttp3.Call;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class VideoFileFrm extends BaseFragment implements View.OnClickListener{
	
	private boolean isRecycle =false;
	
	private String TAG = getClass().getName();
	private int VIDEO_WITH_CAMERA =100;
	
	private FragmentTransaction ft;
	private CarListInfoEntity carInfo;
	private GridView mGridView;
	private GridVideoAdapter mGridAdapter;
	private List<VideoInfo> mList;
	private File mTempFile;
	private int mPosition;
	private String kssj;
	private TextView tv_note;
	
	
	@Override
	public int getLayoutResID() {
		return R.layout.frm_outer_photo;
	}

	@Override
	public void initParam() {
		initView();
		initDatas();
		viewSetAdapter();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if(bundle!=null){
			carInfo = bundle.getParcelable("carInfo");
			Logger.show("carInfo", "carInfo="+carInfo.getHphm());
			Logger.show("carInfo", "jylsh="+carInfo.getLsh());
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(savedInstanceState != null ){
			isRecycle = savedInstanceState.getBoolean("isRecycle");
			if(isRecycle){
				Logger.show(getTag(), "savedInstanceState");
			}
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
//		onRefresh();
	}
	

	
	
	private void initView() {
		mGridView = (GridView) mRootView.findViewById(R.id.grid_photo);
		tv_note = (TextView) mRootView.findViewById(R.id.tv_note);
		tv_note.setText("录制短视频时长不超过30秒");
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			
		}
	}
	
	
	private void initDatas() {
		mList =  new ArrayList<VideoInfo>();
		String[] names = getResources().getStringArray(R.array.videotypename);
		String[] codes =  getResources().getStringArray(R.array.videotypecode);
		for(int i=0;i<names.length;i++){
			VideoInfo videoInfo = new VideoInfo();
			videoInfo.setVideoName(names[i]);
			videoInfo.setVideoCode(codes[i]);
			mList.add(videoInfo);
		}
	}
	
	
	private void viewSetAdapter() {
		mGridAdapter = new GridVideoAdapter(mList,mActivity);
		mGridView.setAdapter(mGridAdapter);
		onItemClick();
		onItemLongClick();
	}
	

	private void onItemClick() {
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mPosition = position;
				kssj = ToolUtils.getCurDate();
				VideoInfo videoInfo = mList.get(position);
				if(!TextUtils.isEmpty(videoInfo.getVideoFilePath())){
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse("file://"+videoInfo.getVideoFilePath()), "video/mp4");
					startActivity(intent);
				}else{
					startVideoAty(videoInfo);
				}
				
			}
		});
	}
	
	
	protected void startVideoAty(VideoInfo videoInfo) {
		String timeStamp  = getCurrentTimeStamp();
		String path = createVideoPathByType(timeStamp,videoInfo.getVideoCode());
		File file = new File(path);
		if(file.exists()){
			file.delete();
		}
		mTempFile = file;
		Uri uri = Uri.fromFile(file);
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 120);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0 );
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	    startActivityForResult(intent, VIDEO_WITH_CAMERA);
	}
	
	
	/**
	 * 获取文件的方法：
	 * 1.Intent data数据为null,无法通过 getExtras()获得
	 * 2.通过filepath来获取
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == VIDEO_WITH_CAMERA){
			if(mTempFile!=null && !TextUtils.isEmpty(mTempFile.getAbsolutePath())&&mTempFile.length()>0){
				saveVideoToGallery(mActivity,mTempFile);
				MediaMetadataRetriever media = new MediaMetadataRetriever();
				media.setDataSource(mTempFile.getAbsolutePath());
				Bitmap bitmap = media.getFrameAtTime();
				VideoInfo videoInfo = mList.get(mPosition);
				videoInfo.setThumbnailBmp(bitmap);
				videoInfo.setVideoFilePath(mTempFile.getAbsolutePath());
				mGridAdapter.setData(mList);
				mGridAdapter.notifyDataSetChanged();
				
				uploadVideo(videoInfo,carInfo);
			}
		}
	}
	
	

	protected String createVideoPathByType(String timeStamp,String code) {
		String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
		String fileName = "";
		fileName = "Video_"+code+ "_" + timeStamp + ".mp4";
        
		return path + "/" + fileName;
    }
	
	
	private void uploadVideo(final VideoInfo videoInfo, CarListInfoEntity carInfo) {
		String url = ToolUtils.uploadVideo(mActivity);
		Map<String,String> params = getByVideoAndCarInfo(videoInfo,carInfo);
		for(Map.Entry<String, String> entry : params.entrySet()){
			Logger.show(TAG, entry.getKey()+"="+entry.getValue());
		}
		File file = new File(videoInfo.getVideoFilePath());
		ProgressDlgUtil.showProgressDialog(mActivity, "正在上传"+videoInfo.getVideoName()+"短视频...");
		MyHttpUtils.getInstance(mActivity)
		.postHttpVideo(url,file,params, new StringCallback() {
			
			@Override
			public void onResponse(String response, int id) {
				Logger.show("photoresponse", "photoresponse="+response);
				try {
					JSONObject jo = new JSONObject(response);
					Integer status = (Integer) jo.get("status");
					if(CommonConstants.STATAS_SUCCESS == status){
						tv_note.setText(videoInfo.getVideoName()+"上传成功");
					}else{
						tv_note.setText(videoInfo.getVideoName()+"上传失败");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				ProgressDlgUtil.dismissProgressDialog();
			}

			@Override
			public void onError(Call call, Exception e, int id) {
				ProgressDlgUtil.dismissProgressDialog();
				e.printStackTrace();
				tv_note.setText(videoInfo.getVideoName()+"上传失败,信息:"+e.getMessage());
			}
		});
	}
	
	
	
	private void onItemLongClick() {
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				mPosition = position;
				kssj = ToolUtils.getCurDate();
				final VideoInfo videoInfo = mList.get(position);
				if(!TextUtils.isEmpty(videoInfo.getVideoFilePath())){
					new AlertDialog.Builder(mActivity).setMessage("是否重新上传")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							uploadVideo(videoInfo,carInfo);
						}
					}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).show();
				}
				return false;
			}
		});
	}
	
	
	
	private Map<String, String> getByVideoAndCarInfo(VideoInfo videoInfo, CarListInfoEntity carInfo) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("jylsh", carInfo.getLsh());
		map.put("hphm",carInfo.getHphm());
		map.put("hpzl",carInfo.getHpzl());
		map.put("clsbdh",carInfo.getClsbdh());
		map.put("jyxm",videoInfo.getVideoCode());
		map.put("kssj",kssj);
		map.put("jssj",ToolUtils.getCurDate());
		map.put("jycs",Integer.toString(carInfo.getJycs()));
		map.put("jyzt","2");
		map.put("jcxdh",carInfo.getJcxdh());
		map.put("voideSate","1");
		return map;
	}

	private String getCurrentTimeStamp(){
		return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	}

	private boolean getSdcarState(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	public static void saveVideoToGallery(Context context, File file){
		Uri localUri = Uri.fromFile(file);
		Intent localIntent =new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
		context.sendBroadcast(localIntent);
	}

	
}
