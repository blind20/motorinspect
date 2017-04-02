package com.shsy.motoinspect.ui.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.shsy.motoinspect.BaseFragment;
import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.adapter.GridAdapter;
import com.shsy.motoinspect.entity.CarListInfoEntity;
import com.shsy.motoinspect.entity.CarPhotoEntity;
import com.shsy.motoinspect.network.MyHttpUtils;
import com.shsy.motoinspect.ui.activity.OuterInspectActivity;
import com.shsy.motoinspect.utils.DensityUtil;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.PictureUtil;
import com.shsy.motoinspect.utils.ProgressDlgUtil;
import com.shsy.motoinspect.utils.SharedPreferenceUtils;
import com.shsy.motoinspect.utils.TakePhotoUtil;
import com.shsy.motoinspect.utils.ToastUtils;
import com.shsy.motoinspect.utils.ToolUtils;
import com.shsy.motorinspect.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
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

public class OuterPhotoFrm2 extends BaseFragment {

	private GridView mGridView;
	private GridAdapter gridAdapter;
	private TextView tv_note;
	private String sTxtNote;
	
	private List<CarPhotoEntity> mInitList;
	private boolean isRecycle = false;
	
	private String timeStamp;
	
	
	private Context context;
	private int mPosition;
	
	//����ԭͼ
	private final static int OriginType = 0;
	//���������ϴ�ͼ
	private final static int UploadType = 1;
	//����ͼ
	private final static int ThumbnaiType = 2;
	
	private final static int UploadQuality =80;
	private final static int ThumbnailQuality =30;
	
	public static final int REQ_CAMERA_DATA = 100;
	public static final int REQ_LONG_CLICK = 101;
	
	private int mWhich = -1;
	private CarListInfoEntity carInfo;
	
	
	/**
	 * �޲ι��캯������Ҫ,
	 * ��������ع�����
	 * 
	 */
	public OuterPhotoFrm2() {
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		carInfo = getArguments().getParcelable("carInfo");
		mInitList = new ArrayList<CarPhotoEntity>();
		sTxtNote = mActivity.getResources().getString(R.string.is_must_photo);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		if(savedInstanceState != null ){
			isRecycle = savedInstanceState.getBoolean("isRecycle");
			if(isRecycle){
				ArrayList list = savedInstanceState.getParcelableArrayList("minitlist");
				mInitList = (List<CarPhotoEntity>) list.get(0);
			}
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
	}


	/*@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		isRecycle = true;
		ArrayList list = new ArrayList();
		list.add(mInitList);
		outState.putBoolean("isRecycle", isRecycle);
		outState.putParcelableArrayList("minitlist", list);
	}*/
	

	@Override
	public void initParam() {
		findView();
		initView();
	}

	
	private void findView() {
		mGridView = (GridView) mRootView.findViewById(R.id.grid_photo);
		tv_note = (TextView) mRootView.findViewById(R.id.tv_note);
	}
	

	private void initView(){
		mInitList = initPhotoGrid();
		viewSetAdapter();
	}
	

	@Override
	public int getLayoutResID() {
		return R.layout.frm_outer_photo;
	}
	
	
	private List<CarPhotoEntity> initPhotoGrid() {
		List<CarPhotoEntity> list = TakePhotoUtil.initChassisPhotoList(mActivity);
		return list;
	}
	
	
	
	/**
	 * ����Adapter
	 * 
	 */
	private void viewSetAdapter() {
		gridAdapter = new GridAdapter(mActivity, mInitList, 0);
		mGridView.setAdapter(gridAdapter);
		onItemClick();
		onItemLongClick();
	}
	
	private void onItemClick() {
		mGridView.setOnItemClickListener(new OnItemClickListener(){
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mPosition = position;
				if(!TextUtils.isEmpty(mInitList.get(position).getUploadPhotoFilePath())){
					//�鿴��Ƭ
					viewImgHasPhoto(position);
					
				}else{
					//������Ƭ
					//�ж�SD��״̬
					if(getSdcarState()){
						sTxtNote = mActivity.getResources().getString(R.string.is_must_photo);
						tv_note.setText(sTxtNote);
						timeStamp = getCurrentTimeStamp();
						startCaptureAty(timeStamp);
					}else{
						ToastUtils.showToast(mActivity, getActivity().getString(R.string.sd_disable), Toast.LENGTH_LONG);
					}
				}
			}
		});
	}
	
	
	private void onItemLongClick() {
		mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				mPosition = position;
				MyDialogFragment myDialog = MyDialogFragment.newInstance(MyDialogFragment.DLG_LIST_TYPE,
																			"","",
																			OuterPhotoFrm2.REQ_LONG_CLICK);
				myDialog.setTargetFragment(OuterPhotoFrm2.this, OuterPhotoFrm2.REQ_LONG_CLICK);
				myDialog.show(getFragmentManager(), "");
				return false;
			}
		});
	}
	
	
	
	private void startCaptureAty(String timeStamp) {
		Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String originPath = create3PhotoPathByType(timeStamp, OuterPhotoFrm2.OriginType);
		File photoFile = new File(originPath);
		Uri fileUri = Uri.fromFile(photoFile);
		photoIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
	    startActivityForResult(photoIntent, OuterPhotoFrm2.REQ_CAMERA_DATA);
	}
	
	
	private void viewImgHasPhoto(int position) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://"+mInitList.get(position).getUploadPhotoFilePath()), "image/*");
		startActivity(intent);
	}
	 
	
	

	/**
	 * ��ȡ��Ƭ�ķ�����
	 * 1.Intent data����Ϊnull,�޷�ͨ��
	 * (Bitmap) data.getExtras().get("data")�����Ƭ
	 * 2.ͨ��filepath����ȡ��Ƭ
	 * 
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if( requestCode == OuterPhotoFrm2.REQ_CAMERA_DATA ) {
	    	Logger.show("onActivityResult", "which"+mWhich);
	    	
	    	String[] pathArray = createUploadAndThumbnailFile(timeStamp, OuterPhotoFrm2.UploadQuality);
	    	//û������,ֱ�ӷ���
	    	if(pathArray ==null){
	    		return;
	    	}
	        
	        CarPhotoEntity carPhoto = mInitList.get(mPosition);
	        carPhoto.setThumbnailBmp(BitmapFactory.decodeFile(pathArray[1]));
	        carPhoto.setUploadPhotoFilePath(pathArray[0]);
	        carPhoto.setThumbnailPhotoFilePath(pathArray[1]);
	        
	        gridAdapter.setData(mInitList);
	        gridAdapter.notifyDataSetChanged();
	        
	        uploadPhoto(carPhoto,carInfo);
	    }
	    
	    if(requestCode == OuterPhotoFrm2.REQ_LONG_CLICK){
	    	Logger.show("reupload", "====reupload=====");
	    	CarPhotoEntity carPhoto = mInitList.get(mPosition);
	        uploadPhoto(carPhoto,carInfo);
	    }
	}
	
	private void uploadPhoto(final CarPhotoEntity carPhoto,CarListInfoEntity carInfo) {
		String url = ToolUtils.uploadPhotoUrl(mActivity);
		Map<String, String> params = packZpInfoByArgu(carPhoto,carInfo);
		File file = new File(carPhoto.getUploadPhotoFilePath());
		ProgressDlgUtil.showProgressDialog(mActivity, "�����ϴ�"+carPhoto.getPhotoTypeName()+"��Ƭ...");
		MyHttpUtils.getInstance(mActivity)
		.postHttpFile(url,file,params, new StringCallback() {
			
			@Override
			public void onResponse(String response, int id) {
				Logger.show("photoresponse", "photoresponse="+response);
				try {
					JSONObject jo = new JSONObject(response);
					Integer state = (Integer) jo.get("state");
					if(CommonConstants.STATAS_SUCCESS == state){
						sTxtNote = carPhoto.getPhotoTypeName()+"��Ƭ�ϴ��ɹ�";
					}else{
						sTxtNote = carPhoto.getPhotoTypeName()+"��Ƭ�ϴ�ʧ��,�볤����Ƭ�����ϴ�";
					}
					ProgressDlgUtil.dismissProgressDialog();
					ToastUtils.showToast(mActivity, sTxtNote, Toast.LENGTH_SHORT);
				} catch (JSONException e) {
					e.printStackTrace();
					ProgressDlgUtil.dismissProgressDialog();
					sTxtNote = carPhoto.getPhotoTypeName()+"��Ƭ�ϴ�ʧ��,�볤����Ƭ�����ϴ�(���ݸ�ʽ�쳣)";
					ToastUtils.showToast(mActivity, sTxtNote, Toast.LENGTH_LONG);
				}
				tv_note.setText(sTxtNote);
			}

			@Override
			public void onError(Call call, Exception e, int id) {
				ProgressDlgUtil.dismissProgressDialog();
				e.printStackTrace();
				sTxtNote = carPhoto.getPhotoTypeName()+"��Ƭ�ϴ�ʧ��,�볤����Ƭ�����ϴ�(��������)";
				tv_note.setText(sTxtNote);
				ToastUtils.showToast(mActivity, sTxtNote, Toast.LENGTH_LONG);
			}
		});
	}
	
	
	private Map<String, String> packZpInfoByArgu(CarPhotoEntity carPhoto,CarListInfoEntity carInfo){
		Map<String,String> map = new HashMap<String,String>();
		map.put("jyjgbh",carInfo.getJyjgbh());
		map.put("jcxdh",carInfo.getJcxdh());
		map.put("jylsh", carInfo.getLsh());
		map.put("hphm",carInfo.getHphm());
		map.put("hpzl",carInfo.getHpzl());
		map.put("clsbdh",carInfo.getClsbdh());
		map.put("jycs",Integer.toString(carInfo.getJycs()));
		map.put("pssj",ToolUtils.getCurDate() );
		map.put("jyxm","DC");
		map.put("zpzl",carPhoto.getPhotoTypeCode());
		return map;
	}
	

	/**
	 * ʵ��
	 * 1.ɾ������������Ƭ
	 * 2.����ѹ����ͼƬ�ļ�
	 * @param timeStamp ���ɵ�ԭͼ,��Ҫɾ��
	 * @param compressQuality ��Ҫ�����ϴ���ͼƬ
	 * @param compressBmp
	 * @return ����:����ͼƬ·��
	 * 
	 */
	private String[] createUploadAndThumbnailFile(String timeStamp,int compressQuality) {
		
		String originFilepath = create3PhotoPathByType(timeStamp, OuterPhotoFrm2.OriginType);
		String uploadFilepath = create3PhotoPathByType(timeStamp, OuterPhotoFrm2.UploadType);
		String thumbnailFilepath = create3PhotoPathByType(timeStamp, OuterPhotoFrm2.ThumbnaiType);
		
		Bitmap uploadBmp = compressBitmapByInsample(originFilepath, CommonConstants.OPTIONS_INSAMPLESIZE);
		if(uploadBmp == null){
			return null;
		}
		
		int screenW = (context.getResources().getDisplayMetrics().widthPixels 
				- DensityUtil.dip2px(context,20))/4;
		Bitmap thumbnailBmp = ThumbnailUtils.extractThumbnail(uploadBmp, screenW, screenW);
		
		// ����·���½��ϴ�ͼƬ\����ͼ,��������ɾ�����½�
		PictureUtil.createFileIfNonOrDeleteIfExists(uploadFilepath);
		PictureUtil.createFileIfNonOrDeleteIfExists(thumbnailFilepath);
		
		//����ԭͼ·��ɾ��ԭͼ
//		File originFile = new File(originFilepath);
//		originFile.delete();
		PictureUtil.createFileIfNonOrDeleteIfExists(originFilepath);
		
		try {
			FileOutputStream uploadfos = new FileOutputStream(new File(uploadFilepath));
			FileOutputStream thumbnailfos = new FileOutputStream(new File(thumbnailFilepath));
			
			uploadBmp.compress(Bitmap.CompressFormat.JPEG, compressQuality, uploadfos);
			thumbnailBmp.compress(Bitmap.CompressFormat.JPEG, OuterPhotoFrm2.ThumbnailQuality, thumbnailfos);
			uploadfos.flush();
			thumbnailfos.flush();
			uploadfos.close();
			thumbnailfos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		PictureUtil.galleryAddPhoto(context,originFilepath);
		PictureUtil.galleryAddPhoto(context,uploadFilepath);
		PictureUtil.galleryAddPhoto(context,thumbnailFilepath);
		
		return new String[]{uploadFilepath,thumbnailFilepath};
	}
	
	/**
	 * ��ԭͼ����ѹ���õ��ϴ�ͼƬ
	 * @param imagePath
	 * @param scale
	 * @return
	 */
	private Bitmap compressBitmapByInsample(String imagePath,int scale){
		
		//��·��������ͼƬ������null
		if(!new File(imagePath).exists()){
			return null;
		}
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);
		
		options.inSampleSize = scale;
		options.inPurgeable = true;
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
		
		return PictureUtil.drawTextToBitmap(bitmap, ToolUtils.getCurDate());
	}
	

	/**
	 * ����ʱ����õ�ԭͼ��Сͼ��ͼ������·��
	 * @param timeStamp
	 */
	protected String create3PhotoPathByType(String timeStamp,int type) {
		
		String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
		String fileName = "";
		switch(type){
			case OuterPhotoFrm2.OriginType:
				fileName = "Origin_Veh_" + timeStamp + ".jpg";
				break;
			case OuterPhotoFrm2.UploadType:
				fileName = "Small_Veh_" + timeStamp + ".jpg";
				break;
			case OuterPhotoFrm2.ThumbnaiType:
				fileName = "Thumbnai_Veh_" + timeStamp + ".jpg";
				break;
		}
        
		return path + "/" + fileName;
    }
	
	
	private String getCurrentTimeStamp(){
		return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	}
	

	private boolean getSdcarState(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	
}
