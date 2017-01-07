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
import com.shsy.motoinspect.entity.CarPhotoEntity;
import com.shsy.motoinspect.ui.activity.OuterInspectActivity;
import com.shsy.motoinspect.utils.DensityUtil;
import com.shsy.motoinspect.utils.Logger;
import com.shsy.motoinspect.utils.PictureUtil;
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
import okhttp3.Call;
import android.widget.GridView;
import android.widget.Toast;

public class OuterPhotoFrm extends BaseFragment {

	private GridView mGridView;
	private GridAdapter gridAdapter;
	
	private List<CarPhotoEntity> mInitList;
	private boolean isRecycle = false;
	
	private String timeStamp;
	
//	private String originPhotoFilePath;
//	private String uploadPhotoFilePath;
//	private String thumbnailPhotoFilePath;
	
	private Context context;
	private int mPosition;
	private String jylsh;
	
	//����ԭͼ
	private final static int OriginType = 0;
	//���������ϴ�ͼ
	private final static int UploadType = 1;
	//����ͼ
	private final static int ThumbnaiType = 2;
	
	private final static int UploadQuality =80;
	private final static int ThumbnailQuality =30;
	
	public static final int REQ_CAMERA_DATA = 100;
	public static final int REQ_SELECT_PHOTO = 101;
	
	private int mWhich = -1;
	
	public static final String PHOTO_IS_MUST = "1";
	public static final String PHOTO_NOT_MUST = "0";
	private ProgressDialog mProgressDlg ;
	
	/**
	 * �޲ι��캯������Ҫ,
	 * ��������ع�����
	 * 
	 */
	public OuterPhotoFrm() {
		
	}
	
	public OuterPhotoFrm(List<CarPhotoEntity> datas) {
		Logger.show("datas", datas.toString());
		mInitList = datas;
		
	}
	
	
	protected OnPhotoItemListener mListener;
	
	public interface OnPhotoItemListener{
		public void OnAddPhotoItem(CarPhotoEntity carPhotoEntity);
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
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnPhotoItemListener) mActivity;
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new ClassCastException(mActivity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}


	/**
	 * activity����������
	 * ����,����Ϊ��
	 * 
	 */
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
		initViewsByJylsh();
	}


	private void initViewsByJylsh() {
		jylsh = getArguments().getString("jylsh");
		String url = ToolUtils.getChekcItemUrl(mActivity);
		Map<String, String> headers = new HashMap<String, String>();
		final String session = (String) SharedPreferenceUtils.get(mActivity, CommonConstants.JSESSIONID, "");
		headers.put("Cookie", "JSESSIONID="+session);
		
		OkHttpUtils.post()
		.url(url)
		.headers(headers)
		.addParams("jylsh", jylsh)
		.addParams("type", CommonConstants.WGJYZP)
		.build()
		.execute(new StringCallback() {
			
			@Override
			public void onResponse(String response, int id) {
				try {
					if(TextUtils.isEmpty(response)){
						ToastUtils.showToast(mActivity, "�����쳣", Toast.LENGTH_LONG);
						return;
					}
					
					String cyzp = response.toString();
					String[] zps = cyzp.split(",");
					
					Logger.show("zpszps", "zpszps");
					if(!TextUtils.isEmpty(cyzp)){
						mInitList = markMustUpload(zps);
						viewSetAdapter();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onError(Call call, Exception e, int id) {
				ToastUtils.showToast(mActivity, "������ѡ����", Toast.LENGTH_LONG);
				mInitList = initPhotoGrid();
				viewSetAdapter();
			}
		});
	}
	

	@Override
	public int getLayoutResID() {
		return R.layout.frm_outer_photo;
	}
	
	
	
	private List<CarPhotoEntity> markMustUpload(String[] cyzp) {
		List<CarPhotoEntity> list = initPhotoGrid();
		for(CarPhotoEntity carPhotoEntity : list){
			String photoTypeCode = carPhotoEntity.getPhotoTypeCode();
			for(String code: cyzp){
				if(code.equals(photoTypeCode)){
					carPhotoEntity.setIsMustFlag(OuterPhotoFrm.PHOTO_IS_MUST);
					break;
				}
			}
		}
		return list;
	}

	private void findView() {
		mGridView = (GridView) mRootView.findViewById(R.id.grid_photo);
		mProgressDlg = new ProgressDialog(mActivity);
		mProgressDlg.setMessage("����ƽָ̨���������Ƭ,��ȴ�");
		mProgressDlg.show();
	}
	
	
	private List<CarPhotoEntity> initPhotoGrid() {
		List<CarPhotoEntity> list = TakePhotoUtil.initFullPhotoList(mActivity);
		return list;
	}
	
	
	
	/**
	 * ����Adapter
	 * 
	 */
	private void viewSetAdapter() {
		mProgressDlg.dismiss();
		gridAdapter = new GridAdapter(mActivity, mInitList, 0);
		mGridView.setAdapter(gridAdapter);
		
		Logger.show("mInitList", mInitList.toString());
		
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
						timeStamp = getCurrentTimeStamp();
						startCaptureAty(timeStamp);
//						String txt = getResources().getString(R.string.select_photo_type);
//						MyDialogFragment myDialog = MyDialogFragment.newInstance(MyDialogFragment.DLG_PHOTO_TYPE);
//						myDialog.setTargetFragment(OuterPhotoFrm.this, OuterPhotoFrm.REQ_SELECT_PHOTO);
//						myDialog.show(getFragmentManager(), txt);
					}else{
						ToastUtils.showToast(mActivity, getActivity().getString(R.string.sd_disable), Toast.LENGTH_LONG);
					}
				}
				
			}
		});
	}
	
	
	
	private void startCaptureAty(String timeStamp) {
		Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String originPath = create3PhotoPathByType(timeStamp, OuterPhotoFrm.OriginType);
		File photoFile = new File(originPath);
		Uri fileUri = Uri.fromFile(photoFile);
		photoIntent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
	    startActivityForResult(photoIntent, OuterPhotoFrm.REQ_CAMERA_DATA);
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
	    if( requestCode == OuterPhotoFrm.REQ_CAMERA_DATA ) {
	    	Logger.show("onActivityResult", "which"+mWhich);
	    	
	    	String[] pathArray = createUploadAndThumbnailFile(timeStamp, OuterPhotoFrm.UploadQuality);
	    	//û������,ֱ�ӷ���
	    	if(pathArray ==null){
	    		return;
	    	}
	        
	        CarPhotoEntity carPhoto = mInitList.get(mPosition);
	        carPhoto.setThumbnailBmp(BitmapFactory.decodeFile(pathArray[1]));
	        carPhoto.setUploadPhotoFilePath(pathArray[0]);
	        carPhoto.setThumbnailPhotoFilePath(pathArray[1]);
	        mListener.OnAddPhotoItem(carPhoto);
	        
	        gridAdapter.setData(mInitList);
	        gridAdapter.notifyDataSetChanged();
	    }
	    
	    if(requestCode == OuterPhotoFrm.REQ_SELECT_PHOTO){
	    	mWhich = data.getIntExtra(MyDialogFragment.RES_SELECT_PHOTO, -1);
	    	timeStamp = getCurrentTimeStamp();
			startCaptureAty(timeStamp);
	    }
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
		
		String originFilepath = create3PhotoPathByType(timeStamp, OuterPhotoFrm.OriginType);
		String uploadFilepath = create3PhotoPathByType(timeStamp, OuterPhotoFrm.UploadType);
		String thumbnailFilepath = create3PhotoPathByType(timeStamp, OuterPhotoFrm.ThumbnaiType);
		
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
			thumbnailBmp.compress(Bitmap.CompressFormat.JPEG, OuterPhotoFrm.ThumbnailQuality, thumbnailfos);
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
	 * ��ҪӲ����(hardcode) /sdcard/; ʹ�� Environment.getExternalStorageDirectory().getPath() ���sdcard·��
	*��ǰ��Android(4.1֮ǰ�İ汾)�У�SDcard��·��ͨ����/sdcard�����ߡ�/mnt/sdcard������ʾ��
	*����Jelly Beanϵͳ���޸�Ϊ�ˡ�/storage/sdcard0�����Ժ���ܻ����ж��SDcard�������
	*ĿǰΪ�˱��ֺ�֮ǰ����ļ��ݣ�sdcard·������linkӳ�䡣
	*Ϊ��ʹ���Ĵ�����ӽ�׳�����ܹ������Ժ��Android�汾���µ��豸��
	*��ͨ��Environment.getExternalStorageDirectory().getPath()����ȡsdcard·����
	*�������Ҫ��sdcard�б����ض����͵����ݣ����Կ���ʹ��Environment.getExternalStoragePublicDirectory(String type)������
	*�ú������Է����ض����͵�Ŀ¼��Ŀǰ֧���������ͣ�
	*DIRECTORY_DCIM //��������ͼƬ����Ƶ�����λ��
	*DIRECTORY_DOWNLOADS //�����ļ������λ��
	*DIRECTORY_MOVIES //��Ӱ�����λ�ã� ���� ͨ��google play���صĵ�Ӱ
	*DIRECTORY_MUSIC //���ֱ����λ��
	*DIRECTORY_PICTURES //���ص�ͼƬ�����λ��
	*/	

	/**
	 * ����ʱ����õ�ԭͼ��Сͼ��ͼ������·��
	 * @param timeStamp
	 */
	protected String create3PhotoPathByType(String timeStamp,int type) {
		
		String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
		String fileName = "";
		switch(type){
			case OuterPhotoFrm.OriginType:
				fileName = "Origin_Veh_" + timeStamp + ".jpg";
				break;
			case OuterPhotoFrm.UploadType:
				fileName = "Small_Veh_" + timeStamp + ".jpg";
				break;
			case OuterPhotoFrm.ThumbnaiType:
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
