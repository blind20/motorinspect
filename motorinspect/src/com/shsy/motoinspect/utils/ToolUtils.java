package com.shsy.motoinspect.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.apache.http.util.EncodingUtils;

import com.shsy.motoinspect.CommonConstants;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

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
	
	//�����������߳����б�URL
	public static String getZbzlCarList(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/getZ1CheckList";
	}
	//�������������б�
	public static String getZ1Device(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/getZ1Device";
	}
	
	//��������ǰ��/���ᵽλ
	public static String z1dw(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/z1dw";
	}
	
	//������������
	public static String upZ1(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/upZ1";
	}	
	
	
	//������������س����б�URL
	public static String getQDZCarList(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/getQDZCheckList";
	}
	//�����������������
	public static String upQDZ(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/upQDZ";
	}
	
	
	//�ȴ��������߳����б�URL
	public static String pullCarsUrl(Context con){
		
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/getCheckList";
	}
	
	
	//��������URL
	public static String pushCarOnlineUrl(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/pushVehOnLine";
	}
	
	
	//�û���½URL
	public static String loginUrl(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/user/login";
	}
	
	//��쳵�б�URL
	public static String outCarsUrl(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/getExternal";
	}
	//��̬�����б�
	public static String getExternalDCUrl(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/getExternalDC";
	}
	//�����б�
	public static String getExternalC1Url(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/getExternalC1";
	}
	
	//�ϴ������ƬURL
	public static String uploadPhotoUrl(Context con){
		if("".equals(getServerHost(con))){
			return "";
		}
		return getServerHost(con) + "/pda/uploadPhoto";
	}
	
	// �ϴ������ĿURL
	public static String externalUrl(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/external";
	}

	//�ϴ������ƬURL
	public static String uploadVideo(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/video/uploadVideo";
	}
		
	// �ϴ���̬������ĿURL
	public static String externalUrlDC(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/externalDC";
	}
	
	// �ϴ�������ĿURL
	public static String externalUrlC1(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/externalC1";
	}
	
	// �ϴ�·��URL
	public static String getExternalRURL(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/getExternalR";
	}	
	
	// ·��ʱ��URL
	public static String getRoadProcessURL(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/getRoadProcess";
	}
	
	//·�Զ���
	public static String roadProcessURL(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/roadProcess";
	}
	
	// ��ȡ��ǰ�û�url
	public static String getCurrentUserUrl(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/user/getCurrentUser";
	}
	
	// ��ȡ��λurl
	public static String getWorkPointsUrl(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/workpoint/getWorkPoints";
	}
	
	// ��ȡ��λ��λurl
	public static String getReStartUrl(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/workpoint/reStart";
	}

	//��ۼ�⿪ʼ
	public static String getProcessStartUrl (Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/processStart";
	}
	
	//������ȡƽ̨Ҫ������Ŀ
	public static String getChekcItemUrl (Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/getChekcItem";
	}	
	
	
	// ������Ƭ
	public static String getVehInOfHphmUrl(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/getVehInOfHphm";
	}	
	
	
	// ���쳵���б�
	public static String getCheckedList(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/getCheckedList";
	}
	
	// δ��ɼ�⳵���б�
	public static String getVehChecking(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/veh/getVehChecking";
	}
	
	// ��ȡ����������
	public static String getProcess(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/report/getProcess";
	}
	
	// ��ȡ������б�
	public static String getLines(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/getLines";
	}
	
	// ��ȡ������б�
	public static String getCheckEvents(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/report/getCheckEvents";
	}

	// ��ȡ������б�
	public static String relogin(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/relogin";
	}
	
	// ���߳����б�URL
	public static String getCheckQueueVeh(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/getCheckQueueVeh";
	}
	
	// ����URL
	public static String downLine(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/downLine";
	}

	// ����
	public static String reUpLine(Context con) {
		if ("".equals(getServerHost(con))) {
			return "";
		}
		return getServerHost(con) + "/pda/reUpLine";
	}
	

	@SuppressLint("SimpleDateFormat")
	public static String getCurDate() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}
	
	public static String getUUID(Context con){
		int first = new Random(10).nextInt(8) + 1;
        System.out.println(first);
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {//�п����Ǹ���
            hashCodeV = -hashCodeV;
        }
        // 0 ����ǰ�油��0
        // 4 ������Ϊ4
        // d �������Ϊ������
        return first + String.format("%014d", hashCodeV);
	}
	
	public static String getIMEI(){
		return "35" + //we make this look like a valid IMEI
	            Build.BOARD.length()%10+ Build.BRAND.length()%10 + 
	            Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 + 
	            Build.DISPLAY.length()%10 + Build.HOST.length()%10 + 
	            Build.ID.length()%10 + Build.MANUFACTURER.length()%10 + 
	            Build.MODEL.length()%10 + Build.PRODUCT.length()%10 + 
	            Build.TAGS.length()%10 + Build.TYPE.length()%10 + 
	            Build.USER.length()%10 ; //13 digits
	}
	
	 /** 
     * MD5 ���� 
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
	 * ��ȡ���е�����
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
	 * �����ļ���Ӧ�ñ��� /data/data/com.vechicle
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
	 * ��ȡ����Ӧ���ļ� /data/data/com.vechicle
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
