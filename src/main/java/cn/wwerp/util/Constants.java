package cn.wwerp.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Constants {
	
	private final static Logger log = LoggerFactory.getLogger(Constants.class);
	
	public static final int SUCCESS = 0;
	public static final int ERR_WRONGRECVS = -101;
	
	public static boolean smsVer = true;
	public static final String VERSION = "幼信 1.0";
	
	public static final int YXON_TEACHER_ID = 1000000000;
	public static final String SESSION_ATTR_USER     = "User";
	public static final String SESSION_ATTR_SCHOOLID = "SchoolId";
	
	public static final String REQ_ATTR_STULIST = "StuList"; 
	public static final String REQ_ATTR_TCHLIST = "TchList";
	
	public static final int USER_TYPE_STUDENT = 1;  //用户类型定义
	public static final int USER_TYPE_TEACHER = 2;
	public static final int USER_TYPE_KG      = 5;

	public static final int TYPE_SMS          =  6;
	
	public static final int TYPE_USER         =  0;  //对应App Headline 类型定义
	public static final int TYPE_HOMEWORK     =  10;
	public static final int TYPE_NOTICE       =  20;
	public static final int TYPE_NOTICE_TCH   =  21;
	public static final int TYPE_FOOD         =  30;
	public static final int TYPE_SIGNRECORD   =  40;
	public static final int TYPE_KGNEWS       =  50;
	public static final int TYPE_NOTICE_EMERGENCY  =  100;
	public static final int TYPE_YXON              =  1000;
	
	public static final int ACT_TYPE_ADD      =  1;
	public static final int ACT_TYPE_MODIFY   =  2;
	public static final int ACT_TYPE_DELETE   =  3;
	
	public static final int UID_YXON          =  10000;
	
	public static final int RECVTYPE_GRADE = 1;  //按年级
	public static final int RECVTYPE_CLASS = 2;  //按班级
	public static final int RECVTYPE_STU   = 3;  //按学生
	public static final int RECVTYPE_DEP   = 4;  //按部门
	public static final int RECVTYPE_POS   = 5;  //按职位
	public static final int RECVTYPE_TCH   = 6;  //按老师
	public static final int RECVTYPE_TEAM  = 7;  //按小组
	public static final int RECVTYPE_ALLSTU= 11; //所有学生
			
	public static final int RESPCODE_LOGINFAIL    = 10001;
	public static final int RESPCODE_ERRACCOUNT   = 10101;
	public static final int RESPCODE_ERRINFO      = 10102;
	public static final int RESPCODE_ERRSMSCODE   = 10103;
	
	public static final int RESPCODE_ERRTIMES     = 10103;
	
	public static final int RESPCODE_ERR_APPKEY_USED  = 10201;
	  
	public static final int GENDER_M          = 1;
	public static final int GENDER_F          = 2;
	
	public static final int SIGN_IN           = 1;   //刷卡模式定义
	public static final int SIGN_OUT          = 2;
	public static final int SIGN_AUTO         = 3;
	public static final int SIGN_AUTO_IN      = 4;
	public static final int SIGN_AUTO_OUT     = 5;
	
	public static final int STATUS_INSCHOOL   = 1; //在校       学生状态定义
	public static final int STATUS_OUTSCHOOL  = 2; //离校
	public static final int STATUS_GRADUATE   = 3; //毕业
	
	public static final int OPT_BIND          = 1;
	public static final int OPT_DISCARD       = 2;
	public static final int OPT_LOSS          = 3;
	public static final int OPT_UNLOSS        = 4;
		
	public static final int CARDSTATUS_OK         = 1;
	public static final int CARDSTATUS_LOSS       = 2;
	public static final int CARDSTATUS_DISCARD    = 3;
	
	public static final int STATUS_DELETED        = 4; //删除状态

	public static final int FOOD_BREAKFAST        = 1;  //食谱定义
	public static final int FOOD_LUNCH            = 2;
	public static final int FOOD_EXTRA            = 3;
	public static final int FOOD_DINNER           = 4;
	public static final int FOOD_MORNINGEXTRA     = 5;
	
	public static final int SMSSENDDELAYTIME_30   = 30;
	public static final int SMSSENDDELAYTIME_60   = 60;
	public static final int SMSSENDDELAYTIME_1   = 1;
	
	public static final String WILDCARD_STU_NAME = "%stuname%";
	public static final String WILDCARD_TCH_NAME = "%teaname%";
	
	public static final String VerifyCode = "VerifyCode";
	
	public static String rasPublicKey = "";
	
	public static String createCardSignKey = "1234567890";
	
	public static int maxStrLenForApp = 80;
	
	public static int unreadTimeoutDay = 7;
	public static long unreadTimeout = unreadTimeoutDay * 24 * 3600000L;
	
	public static List<Integer> sendFoodMsgTime = Arrays.asList(8,10, 12, 15, 18); //定时发送食谱的时间点，对应早餐、早间加餐、午餐、加餐、晚餐
	
	public static int timeoutSmsCode = 90;

	public static final List<String> TwoCharacterSurnames = Arrays.asList("欧阳", "上官", "司马", "东方", "诸葛", "令狐", "南宫", "慕容", 
					"公孙", "司徒", "皇甫", "夏候", "万俟", "宇文", "轩辕", "东郭", "南门", "西门", "尉迟");
	
	public static enum Name {
		Gender,
		StuType,
		StuStatus,
		TchStatus,
		Position,
		Relation,
		CardStatus,
		KgStatus,
		UserStatus,
		Education,
		Professional,
		Political,
		Subject,
		ClassRole,
		Grade
	}

	private static Map<String, Map<Integer, String>> items = new HashMap<String, Map<Integer, String>>();
	private static Map<String, Map<String, Integer>> extValueItems = new HashMap<String, Map<String, Integer>>();
	private static Map<String, String> textTemplets = new HashMap<String, String>();
	
	private static FileMonitor fileMonitor;
	static {
		try {
			String xml = Util.locateFile("constants.xml");
			if (xml != null) {
				fileMonitor = new FileMonitor(xml, 60000);
				fileMonitor.addListener(new FileMonitor.Listener() {
					
					@Override
					public void onFileMofity(String file, long ts) {
						new FileSystemXmlApplicationContext(file);
						log.info(" cfg update! textTemplets:" + textTemplets);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setTextTemplets(Map<String, String> textTemplets) {
		Constants.textTemplets = textTemplets;
	}

	public static String getTextTemplet(String key) {
		return textTemplets.get(key);
	}
	
	public static Map<Integer, String> getItems(Name name) {
		return getItems(name.toString());
	}
	
	public static Map<Integer, String> getItems(String name) {
		Map<Integer, String> map = items.get(name);
		if (map == null)
			map = Collections.emptyMap();
		return map;
	}
	
	public static String getValue(Name name, int key) {
		return getValue(name.toString(), key);
	}
	
	public static String getValue(String name, int key) {
		String v = getItems(name).get(key);
		return (v == null ? "" : v);
	}
	
	public static int getKey(Name name, String value) {
		return getKey(name.toString(), value);
	}
	
	public static int getKey(String name, String value) {
		return getKey(name, value, 0);
	}
	
	public static int getKey(Name name, String value, int defaultKey) {
		return getKey(name.toString(), value, defaultKey);
	}
	
	public static int getKey(String name, String value, int defaultKey) {
		if (value == null)
			return defaultKey;
		Map<Integer, String> map = getItems(name);
		for (Map.Entry<Integer, String> entry : map.entrySet()) {
			if (value.equals(entry.getValue()) || value.indexOf(entry.getValue()) >= 0)
				return entry.getKey();
		}
		
		Map<String, Integer> exts = extValueItems.get(name);
		if (exts != null) {
			for (Map.Entry<String, Integer> e : exts.entrySet()) {
				if (value.indexOf(e.getKey()) >= 0)
					return e.getValue();
			}
		}
		return defaultKey;
	}
	
	public void setItems(Map<String, Map<Integer, String>> items) {
		Constants.items = items;
		for (Map.Entry<String, Map<Integer, String>> entry : Constants.items.entrySet()) {
			String name = entry.getKey();
			for (Map.Entry<Integer, String> e : entry.getValue().entrySet()) {
				String[] ss = e.getValue().split(",");
				if (ss.length > 1) {
					e.setValue(ss[0]);
					
					for (int i = 1; i < ss.length; i++) {
						Map<String, Integer> exts = extValueItems.get(name);
						if (exts == null) {
							exts = new HashMap<String, Integer>();
							extValueItems.put(name, exts);
						}
						exts.put(ss[i], e.getKey());
					}
				}
			}
		}
	}

	public void setRasPublicKey(String rasPublicKey) {
		Constants.rasPublicKey = rasPublicKey;
	}
	
	public static void setCreateCardSignKey(String createCardSignKey) {
		Constants.createCardSignKey = createCardSignKey;
	}
	
	public static JsonBuilder createSuccessResp() {
		return new JsonBuilder().append("code", SUCCESS);
	}
	
	public static void setUnreadTimeout(long unreadTimeout) {
		Constants.unreadTimeout = unreadTimeout;
	}
	
	public static boolean isUnreadTimeout(long time) {
		return (time < System.currentTimeMillis() - Constants.unreadTimeout);
	}
	
	public static void setSmsVer(boolean isSmsVer) {
		Constants.smsVer = isSmsVer;
	}

	public static boolean isSmsVer() {
		return smsVer;
	}
	
	public static void setSendFoodMsgTime(List<Integer> sendFoodMsgTime) {
		Constants.sendFoodMsgTime = sendFoodMsgTime;
	}

	public static void setTimeoutSmsCode(int timeoutSmsCode) {
		Constants.timeoutSmsCode = timeoutSmsCode;
	}
}
