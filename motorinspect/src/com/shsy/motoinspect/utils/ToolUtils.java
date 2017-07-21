package com.shsy.motoinspect.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import com.shsy.motoinspect.CommonConstants;
import com.shsy.motoinspect.entity.CarPhotoEntity;
import com.shsy.motoinspect.ui.fragment.OuterCheckFrm;
import com.shsy.motoinspect.ui.fragment.OuterPhotoFrm;
import com.shsy.motorinspect.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class ToolUtils {

	public Context context;
	public final static String PREF_NAME = "sharepref_values";
	
	public static String getServerHost(Context con) {
		
		String ip = (String) SharedPreferenceUtils.get(con, CommonConstants.IP, "");
		String port = (String) SharedPreferenceUtils.get(con, CommonConstants.PORT, "");
		
		if("".equals(ip)||"".equals(port)){
			return "";
		}
		return "http://" + ip + ":" + port +"/veh";
	}
	
	
	//等待引车上线车辆列表URL
	public static String pullCarsUrl(Context con){
		
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/getCheckList";
	}
	
	
	//引车上线URL
	public static String pushCarOnlineUrl(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/pushVehOnLine";
	}
	
	
	//用户登陆URL
	public static String loginUrl(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/user/login";
	}
	
	//外检车列表URL
	public static String outCarsUrl(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/getExternal";
	}
	//动态底盘列表
	public static String getExternalDCUrl(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/getExternalDC";
	}
	//底盘列表
	public static String getExternalC1Url(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/getExternalC1";
	}
	
	//上传外检照片URL
	public static String uploadPhotoUrl(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/uploadPhoto";
	}
	
	// 上传外检项目URL
	public static String externalUrl(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/external";
	}

	// 上传动态底盘项目URL
	public static String externalUrlDC(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/externalDC";
	}
	
	// 上传底盘项目URL
	public static String externalUrlC1(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/externalC1";
	}
	
	// 上传路试URL
	public static String getExternalRURL(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/getExternalR";
	}	
	
	// 路试时间URL
	public static String getRoadProcessURL(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/getRoadProcess";
	}
	
	//路试动作
	public static String roadProcessURL(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/roadProcess";
	}
	
	// 获取当前用户url
	public static String getCurrentUserUrl(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/user/getCurrentUser";
	}
	
	// 获取工位url
	public static String getWorkPointsUrl(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/workpoint/getWorkPoints";
	}
	
	// 获取复位工位url
	public static String getReStartUrl(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/workpoint/reStart";
	}

	//外观检测开始
	public static String getProcessStartUrl (Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/processStart";
	}
	
	//联网获取平台要求检测项目
	public static String getChekcItemUrl (Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/getChekcItem";
	}	
	
	
	// 补拍照片
	public static String getVehInOfHphmUrl(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/getVehInOfHphm";
	}	
	
	

	
	

	@SuppressLint("SimpleDateFormat")
	public static String getCurDate() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}
	
	public static String deviceImei(){
		Context context = ContextUtil.getInstance();
		String devImei;
		TelephonyManager tm = (TelephonyManager)context.getSystemService(
				Context.TELEPHONY_SERVICE);
		try {
			devImei = tm.getDeviceId();
		} catch (Exception e) {
			devImei = "";
		}
		return devImei;
	}
	
	public static String imei2active(){
		Log.i("imei2active", "imei2active:"+ getEncodeStr(deviceImei()));
		return getEncodeStr(deviceImei());
	}
	
	 /** 
     * MD5 加密 
     */  
	private static String getMd5Str(String str) {  
        MessageDigest messageDigest = null;  
        try {  
            messageDigest = MessageDigest.getInstance("MD5");  
            messageDigest.reset();  
            messageDigest.update(str.getBytes("UTF-8"));  
        } catch (NoSuchAlgorithmException e) {  
            System.out.println("NoSuchAlgorithmException caught!");  
            System.exit(-1);  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        byte[] byteArray = messageDigest.digest();  
        StringBuffer md5StrBuff = new StringBuffer();  
        for (int i = 0; i < byteArray.length; i++) {              
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)  
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));  
            else  
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));  
        }  
        return md5StrBuff.toString();  
    }
	
	
	public static String getEncodeStr(String str){
		String sEncode = getMd5Str(str);
		StringBuffer buffer = new StringBuffer(sEncode);
		sEncode = buffer.reverse().toString().substring(0, 10);
		return sEncode;
	}
	
	public static InputStream string2IpInputStream(String str){
		ByteArrayInputStream inputstream = new ByteArrayInputStream(str.getBytes());
		return inputstream;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static Date stringToDate(String dateStr,String format) {
//      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
        return date;
    }
	
	public static String dateToString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String str = format.format(date);
		return str;
    }
	
	public static String dateToStringHMS(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = format.format(date);
		return str;
    }
	
	
	public static String getLableByCode(Context con,String code,int nLables, int nCodes){
		String[] lables = con.getResources().getStringArray(nLables);
		String[] codes = con.getResources().getStringArray(nCodes);
		String lable = null;
		for(int i=0;i<codes.length;i++){
			if(code.equals(codes[i])){
				lable = lables[i];
				break;
			}
		}
		return lable;
	}
	
	
	public static String getCodeByLable(Context con,String lable, int nLables, int nCodes){
		String[] lables = con.getResources().getStringArray(nLables);
		String[] codes = con.getResources().getStringArray(nCodes);
		String code = null;
		for(int i=0;i<lables.length;i++){
			if(lable.equals(lables[i])){
				code = codes[i];
				break;
			}
		}
		return code;
	}
	
	/**
	 * 读取流中的数据
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] read(InputStream inStream) throws Exception{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while( (len = inStream.read(buffer)) != -1){
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}
	
	public static String readFileByRandomAccess(String fileName) {
        RandomAccessFile randomFile = null;
        String content = "";
        try {
            randomFile = new RandomAccessFile(fileName, "r");
            randomFile.seek(0);
            byte[] bytes = new byte[10];
            int byteread = 0;
            while ((byteread = randomFile.read(bytes)) != -1) {
            	content += new String(bytes,0,byteread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e1) {
                }
            }
        }
        return content;
    }
	
	/**
	 * 保存文件到应用本地 /data/data/com.vechicle
	 * @param str
	 * @param fileName
	 */
	public static void saveFile(String str,String fileName){
		FileOutputStream fos = null ;
		Context context = ContextUtil.getInstance();
		try {
			fos = context.openFileOutput(fileName, Activity.MODE_PRIVATE);
			fos.write(str.getBytes());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fos = null;
			}
		}
	}
	
	/**
	 * 读取本地应用文件 /data/data/com.vechicle
	 * @param str
	 * @param fileName
	 */
	public static String readFile(String fileName){
		FileInputStream fis = null;
		String str = null;
		Context context = ContextUtil.getInstance();
		try {
			fis = context.openFileInput(fileName);
			int lenth = fis.available();
			byte[] buffer = new byte[lenth];
			fis.read(buffer);
			str = EncodingUtils.getString(buffer, "UTF-8");
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fis = null;
			}
		}
		return str;
	}
	
}
