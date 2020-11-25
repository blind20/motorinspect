package com.shsy.motoinspect;

public class CommonConstants {
	
	private CommonConstants(){}

	public static final String SP_NAME = "sp_config";
	public static final boolean isShowLog = true;
	
	
	//压缩图片采样率
	public final static int OPTIONS_INSAMPLESIZE = 4;
	
	// http请求状态码标志
	public final static int STATAS_OK = 200;// 请求OK
	public final static int NO_RESPONSE = 400;// 请求无响应 找不到响应资源
	public final static int S_EXCEPTION = 500;// 服务器出错
	
	
	
	/**
	 * 请求返回状态
	 */
	public final static int STATAS_FAIL = 0;// 请求OK
	public final static int STATAS_SUCCESS = 1;// 请求无响应 找不到响应资源
	public final static int STATAS_INVALID = 600;// 失效
	
	
	//checkflag外观查验结果标记
	public final static int CHECKPASS = 1;
	public final static int CHECKFAIL = 2;
	public final static int NOTCHECK = 0;
	
	//外观检验fragment数量
	public final static int OUTERCHECKFMS = 3;
	public final static int OUTERFMS_FIRST = 21;
	public final static int OUTERFMS_SECOND = 40;
	
	
	public final static String DIALOGTYPE = "Dialog_Type";
	
	public final static String ISRECYCLE = "ISRECYCLE";
	
	
	// 不需要引车、待引车、正在引车三种状态
	public final static int NOTPULLCAR = -1;
	public final static int WAITPULLCAR = 0;
	public final static int DOPULLCAR = 1;
	
	
	public final static String BUNDLE_TO_OUTER = "outercheck";
	
	

	
	//个人设置页面跳转指定
	public final static String TO_SETTING = "settingsel";
	
	
	//sharepreference key
	public final static String IP = "ip";
	public final static String PORT = "port";
	public final static String USERNAME = "username";
	public final static String REALNAME = "realName";
	public final static String PWD = "password";
	public final static String JSESSIONID = "sessionid";
	//启动次数
	public final static String COUNT = "count";
	public final static String UUID = "uuid";
	
	
	//startActivityForResult request code
	public final static int REQUEST_LOGIN = 100;
	
	
	// 外观检测、底盘检测、动态底盘检测
	public final static int APPEARANCE  = 0;
	public final static int CHASSIS = 1;
	public final static int DYNAMIC = 2;
	public final static int PULLCAR = 3;
	public final static int RESET = 4;
	public final static int ROADTEST = 5;
	public final static int REPHOTO = 6;
	
	
	
	//getChekcItem  请求参数 type
	public final static String DTDPJYXM = "dtdpjyxm";
	public final static String WGJYXM = "wgjcxm";
	public final static String DPJYXM = "dpjyxm";
	public final static String WGJYZP = "wgjyzp";
	
	
	//选择三天内,一周内,两周内,一月内
	public final static int DAYS3  = 3;
	public final static int DAYS7 = 7;
	public final static int DAYS14 = 14;
	public final static int DAYS30 = 30;
	public final static int DAYS90 = 90;
	
	//滚筒线、平板线
	public final static String CYLINDERLINE  = "0";
	public final static String PLATELINE = "1";
	
	//系统设置
	//启用引车是否选线号
	public final static String IS_SELECT_LINE  = "is_select_line";
	public final static String STATION = "station";
}
