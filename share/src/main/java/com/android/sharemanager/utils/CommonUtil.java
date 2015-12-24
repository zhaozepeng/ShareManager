package com.android.sharemanager.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

	private static final int DEF_DIV_SCALE = 10;

	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * CommonUser = 1;// 普通微店主
	 * 
	 * YunSupplier = 2;// 云商通批发商
	 * 
	 * BatchSupplier = 3;// 批发号供应商
	 * 
	 * ErpSupplier = 4;// ERP供应商
	 * 
	 * OrdinaryVerifier = 5;// 见习认证员
	 * 
	 * HighGradeVerifier = 6;// 高级认证员
	 * 
	 * BatchVerifier = 7;// 批发号认证员
	 * 
	 * BatchVerifierport = 8;// 认证点
	 */
	public static boolean getIdentity(int identityCardCode, int index) {
		// identityCardCode = 5;
		// <<表示左移, 左移一位表示原来的值乘2.
		// num 指定要移位值a 移动的位数。
		return (identityCardCode >> index) % 2 == 1;
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 根据列表内容设置高度
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		totalHeight += listView.getPaddingTop() + listView.getPaddingBottom();

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		totalHeight += (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		params.height = totalHeight;
		listView.setLayoutParams(params);
	}

	/**
	 * 将为null的字符串变成""
	 * 
	 * @param value
	 * @return
	 */
	public static String setNull2Empty(String value) {
		if (value == null) {
			value = "";
		}
		return value;
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */

	public static double round(Double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}

		BigDecimal b = null == v ? new BigDecimal("0.00") : new BigDecimal(
				Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static PackageInfo getPackageInfo(Context mContext) throws Exception {
		PackageManager packageManager = mContext.getPackageManager();
		// getPackageName()是你当前类的包名,0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(
				mContext.getPackageName(), 0);
		return packInfo;
	}

	/*
	 * 跳转到设置网络界面
	 */
	public static void openSetting(Activity activity) {
		Intent intentNetwork = new Intent(Settings.ACTION_SETTINGS);
		activity.startActivity(intentNetwork);
	}

	/**
	 * 判定输入汉字
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
	}

	/**
	 * 检测String是否全是中文
	 * 
	 * @param name
	 * @return
	 */
	public static boolean checkNameChese(String name) {
		boolean res = true;
		char[] cTemp = name.toCharArray();
		for (int i = 0; i < name.length(); i++) {
			if (!isChinese(cTemp[i])) {
				res = false;
				break;
			}
		}
		return res;
	}


	/**
	 *
	 * @param bitmap
	 *            原图
	 * @param edgeLength
	 *            希望得到的正方形部分的边长
	 * @return 缩放截取正中部分后的位图。
	 */
	public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
		if (null == bitmap || edgeLength <= 0) {
			return null;
		}

		Bitmap result = bitmap;
		int widthOrg = bitmap.getWidth();
		int heightOrg = bitmap.getHeight();

		if (widthOrg >= edgeLength && heightOrg >= edgeLength) {
			// 压缩到一个最小长度是edgeLength的bitmap
			int longerEdge = edgeLength * Math.max(widthOrg, heightOrg) / Math
					.min(widthOrg, heightOrg);
			int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
			int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
			Bitmap scaledBitmap;

			try {
				scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth,
						scaledHeight, true);
			} catch (OutOfMemoryError e) {
				return null;
			}

			// 从图中截取正中间的正方形部分。
			int xTopLeft = (scaledWidth - edgeLength) / 2;
			int yTopLeft = (scaledHeight - edgeLength) / 2;

			try {
				result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft,
						edgeLength, edgeLength);
				if (scaledBitmap != bitmap)
					scaledBitmap.recycle();
			} catch (OutOfMemoryError e) {
				return null;
			}
		}

		return result;
	}

	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = null;
		try {
			output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			final float roundPx = pixels;
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);
		} catch (OutOfMemoryError er) {
			er.printStackTrace();
		}
		return output;
	}

	// 获取应用版本号
	public static int getAppVersion(Context context) {
		PackageManager manager;
		PackageInfo info = null;
		manager = context.getPackageManager();

		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return info.versionCode;
	}


	// 根据指定尺寸缩放本地图片
	public static Bitmap getImageThumbnailGetResources(Resources resources,
			int reId, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.ARGB_8888;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeResource(resources, reId, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;

		if (width <= 0 || height <= 0) {
			options.inSampleSize = 1;
		} else {

			int beWidth = w / width;
			int beHeight = h / height;
			int be = 1;
			if (beWidth < beHeight) {
				be = beWidth;
			} else {
				be = beHeight;
			}
			if (be <= 0) {
				be = 1;
			}
			options.inSampleSize = be;
		}
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeResource(resources, reId, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		// ThumbnailUtils.OPTIONS_RECYCLE_INPUT 回收原bitmap
		// bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
		// ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	// 根据指定尺寸缩放目标图片
	public static Bitmap getImageThumbnailAndReturnCorrectImg(String imagePath,
			int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.ARGB_8888;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;

		if (width <= 0 || height <= 0) {
			options.inSampleSize = 1;
		} else {
			int beWidth = w / width;
			int beHeight = h / height;
			int be = 1;
			if (beWidth < beHeight) {
				be = beWidth;
			} else {
				be = beHeight;
			}
			if (be <= 0) {
				be = 1;
			}
			options.inSampleSize = be;
		}

		try {
			// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
			bitmap = BitmapFactory.decodeFile(imagePath, options);

			// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
			// ThumbnailUtils.OPTIONS_RECYCLE_INPUT 回收原bitmap
			// bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
			// ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		} catch (Throwable e) {
			e.printStackTrace();
			bitmap = null;
		}
		return bitmap;
	}

	// 根据指定尺寸缩放目标图片
	public static Bitmap getImageThumbnail(String imagePath, int width,
			int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;

		if (width <= 0 || height <= 0) {
			options.inSampleSize = 1;
		} else {
			int beWidth = w / width;
			int beHeight = h / height;
			int be = 1;
			if (beWidth < beHeight) {
				be = beWidth;
			} else {
				be = beHeight;
			}
			if (be <= 0) {
				be = 1;
			}
			options.inSampleSize = be;
		}

		try {
			// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
			bitmap = BitmapFactory.decodeFile(imagePath, options);
			// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
			// ThumbnailUtils.OPTIONS_RECYCLE_INPUT 回收原bitmap
			// bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
			// ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		} catch (Throwable e) {
			e.printStackTrace();
			bitmap = null;
		}
		return bitmap;
	}

	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[inStream.available()];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {

			outStream.write(buffer, 0, len);
		}

		byte[] byteArray = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return byteArray;
	}

	public static String formatDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return dateFormat.format(date);
	}

	public static String formatNow() {
		Calendar cal = Calendar.getInstance();
		return formatDate(cal.getTime());
	}

	/**
	 * @param context
	 *            上下文
	 * @param packageName
	 *            需要匹配包名
	 * @return true/false
	 */
	public static boolean isAvilible(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		// List<String> pName = new ArrayList<String>();// 用于存储所有已安装程序的包名
		// 从pinfo中将包名字逐一取出，压入pName list中
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				// pName.add(pn);
				if (pn.equalsIgnoreCase(packageName))
					return true;
			}
		}
		return false;
		// return pName.contains(packageName);//
		// 判断pName中是否有目标程序的包名，有TRUE，没有FALSE
	}

	public static boolean checkApkExist(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName))
			return false;
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			return info != null;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 将字符串转换成ASCII码
	 *
	 * @param cnStr
	 * @return
	 */
	public static String getCnASCII(String cnStr) {
		StringBuffer strBuf = new StringBuffer();
		// 将字符串转换成字节序列
		byte[] bGBK = cnStr.getBytes();
		for (int i = 0; i < bGBK.length; i++) {
			// 将每个字符转换成ASCII码
			strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
		}
		return strBuf.toString();
	}

	/**
	 * 取首字母 如果是字母则返回大写字母 否则返回#
	 *
	 * @param str
	 * @return
	 */
	public static String getFirstChar(String str) {
		if (TextUtils.isEmpty(str)) {
			return "#";
		}
		String key = str.substring(0, 1).toUpperCase();
		if (key.matches("[A-Z]")) {
			return key;
		}
		return "#";
	}

	/**
	 * 判断是否是手机号
	 *
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		boolean flag = false;
		try {
			Pattern p = Pattern.compile("^1\\d{10}$");
			Matcher m = p.matcher(mobiles);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
			return flag;
		}
		return flag;
	}

	/**
	 * 格式化时间（输出类似于 刚刚, 4分钟前, 一小时前, 昨天这样的时间）
	 *
	 * @param time
	 *            需要格式化的时间 如"2014-07-14 19:01:45"
	 * @param pattern
	 *            输入参数time的时间格式 如:"yyyy/MM/dd HH:mm:ss"
	 *            如果为空则默认使用"yyyy/MM/dd HH:mm:ss"格式
	 * @return time为null，或者时间格式不匹配，输出空字符""
	 */
	public static String formatDisplayTime(String time, String pattern) {
		String display = "";
		int tMin = 60 * 1000;
		int tHour = 60 * tMin;
		int tDay = 24 * tHour;
		int effectiveTime = 2 * tDay;// 最长有效时间 2天

		if (time != null) {
			try {
				Date tDate = new SimpleDateFormat(pattern).parse(time);
				Date today = new Date();
				SimpleDateFormat todayDf = new SimpleDateFormat("yyyy/MM/dd");
				Date yesterday = new Date(todayDf.parse(todayDf.format(today))
						.getTime());
				Date beforeYes = new Date(yesterday.getTime() - tDay);
				if (tDate != null) {
					SimpleDateFormat halfDf = new SimpleDateFormat("yyyy/MM/dd");
					long dTime = today.getTime() - tDate.getTime();
					if (dTime < tDay && tDate.after(yesterday)) {
						display = "今天";
					} else if (dTime <= effectiveTime) {
						display = todayDf.format(tDate);
					} else {
						display = "已过期";
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return display;
	}

	/**
	 * 格式化时间（输出时间字符串对应的日期）
	 *
	 * @param time
	 *            需要格式化的时间 如"2014-07-14 19:01:45"
	 * @param pattern
	 *            输入参数time的时间格式 如:"yyyy-MM-dd HH:mm:ss"
	 *            如果为空则默认使用"yyyy-MM-dd HH:mm:ss"格式
	 * @return time为null，或者时间格式不匹配，输出空字符""
	 */
	public static String formatDisplayTimeToDate(String time, String pattern) {
		String display = "";
		int tMin = 60 * 1000;
		int tHour = 60 * tMin;
		int tDay = 24 * tHour;

		if (time != null) {
			try {
				Date tDate = new SimpleDateFormat(pattern).parse(time);
				Date today = new Date();
				SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
				Date yesterday = new Date(todayDf.parse(todayDf.format(today))
						.getTime());
				if (tDate != null) {
					SimpleDateFormat halfDf = new SimpleDateFormat("yyyy-MM-dd");
					display = todayDf.format(tDate);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return display;
	}

	/**
	 * 提供精确的加法运算。
	 *
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * @return 两个参数的和
	 */
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();

	}

	public static String addToStr(double v1, double v2) {
		// return v1+v2;
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).stripTrailingZeros().toPlainString();

	}

	/**
	 * 提供精确的减法运算。
	 *
	 * @param v1
	 *            被减数
	 * @param v2
	 *            减数
	 * @return 两个参数的差
	 */
	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 提供精确的乘法运算。
	 *
	 * @param v1
	 *            被乘数
	 * @param v2
	 *            乘数
	 * @return 两个参数的积
	 */
	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
	 *
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2) {
		return div(v1, v2, DEF_DIV_SCALE);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 *
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 *
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 根据需要显示的行数来动态改变ListView的高度
	 *
	 * @param listView
	 * @param lineNum
	 */
	public static void setListViewHeightBasedOnLineNum(ListView listView,
			int lineNum) {

		ListAdapter listAdapter = listView.getAdapter();

		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;

		int num = lineNum == 0 ? listAdapter.getCount() : lineNum;

		for (int i = 0; i < num; i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();

		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		listView.setLayoutParams(params);
	}

	/**
	 * 将毫秒转换成 hh:mm:ss输出
	 *
	 * @param mss
	 * @return
	 */
	public static String formatDuring(long mss) {
		String hh, mm, ss;

		long days = mss / (1000 * 60 * 60 * 24);
		long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;
		if (days > 0) {
			hours += days * 24;
		}
		if (hours < 10) {
			hh = "0" + hours;
		} else {
			hh = "" + hours;
		}
		if (minutes < 10) {
			mm = "0" + minutes;
		} else {
			mm = "" + minutes;
		}
		if (seconds < 10) {
			ss = "0" + seconds;
		} else {
			ss = "" + seconds;
		}

		return hh + ":" + mm + ":" + ss;
	}

	/**
	 * 日期字符串转date类型
	 *
	 * @param date
	 * @return
	 */
	public static Date stringToDate(String date) {
		SimpleDateFormat formatDate = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date time = null;
		try {
			time = formatDate.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}

	/**
	 * 判断网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static boolean isNetworkWifi(Context context){
		if (!isNetworkConnected(context)){
		}
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return info.getType() == ConnectivityManager.TYPE_WIFI;
	}
	
	public static int[] getScreenHeightAndWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = activity.getResources().getDisplayMetrics();
		activity.getWindow().getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		int[] data = new int[2];
		data[0] = dm.widthPixels;
		data[1] = dm.heightPixels;

		return data;

	}


	/**
	 * 手机图片uri转path
	 * @param imageUri
	 * @return
	 */
	public static String uriToPath(Context activity,Uri imageUri) {
		String targetPath = "";
		if (!imageUri.toString().startsWith("file://")) {// file开头的路径不需要再进行转换
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = activity.getContentResolver().query(imageUri, filePathColumn, null, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				targetPath = cursor.getString(columnIndex);
				cursor.close();
			}
		} else {
			targetPath = imageUri.toString().substring(7, imageUri.toString().length());
		}
		return targetPath;
	}


	/**
	 * md5加密
	 */
	public static String md5(String string) {
		byte[] hash;
		try {
			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Huh, MD5 should be supported?", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Huh, UTF-8 should be supported?", e);
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10) hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}
		return hex.toString();
	}
}
