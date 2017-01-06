package com.shsy.motoinspect.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;

public class PictureUtil {


	/**
	 * 把bitmap转换成String
	 * 
	 * @param filePath
	 * @return
	 */
	public static String bitmapToString(Bitmap bmp) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);
		byte[] b = baos.toByteArray();
		
		return Base64.encodeToString(b, Base64.DEFAULT);
	}

	/**
	 * 计算图片的缩放值
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
	

	
	

	/**
	 * 获取保存图片的目录
	 * 
	 * @return
	 */
	public static File getAlbumDir() {
		File dir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				getAlbumName());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * 获取保存 隐患检查的图片文件夹名称
	 * 
	 * @return
	 */
	public static String getAlbumName() {
		return "VechilePic";
	}
	
	
	 /**
     * 添加文字到图片，类似水印文字。
     * @param gContext
     * @param gResId
     * @param gText
     * @return
     */ 
    public static Bitmap drawTextToBitmap(Bitmap bitmap, String gText) { 
   
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig(); 
        // set default bitmap config if none 
        if (bitmapConfig == null) { 
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888; 
        } 
        bitmap = bitmap.copy(bitmapConfig, true); 
   
        Canvas canvas = new Canvas(bitmap); 
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); 
        // text color - #3D3D3D 
        paint.setColor(Color.rgb(177,68,80)); 
        // text size in pixels 
        paint.setTextSize(45.0f);
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE); 
   
        // draw text to the Canvas center 
        Rect bounds = new Rect(); 
        paint.getTextBounds(gText, 0, gText.length(), bounds); 
        int x = (bitmap.getWidth() - bounds.width())/10*9 ; 
        int y = (bitmap.getHeight() + bounds.height())/10*9; 
        canvas.drawText(gText, x , y, paint); 
   
        return bitmap; 
    }
    

	/**
	 * broadcast让拍摄照片的立刻在手机相册中浏览
	 * @param file 要显示的照片文件
	 */
	public static void galleryAddPhoto(Context con,File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        con.sendBroadcast(mediaScanIntent);
    }
	
	/**
	 * 重载
	 */
	public static void galleryAddPhoto(Context context, String path) {
		
		if(!new File(path).exists()){
			return;
		}
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(path);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		context.sendBroadcast(mediaScanIntent);
	}
    
	/**
	 * 根据路径删除图片
	 * @param path
	 */
	public static void createFileIfNonOrDeleteIfExists(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}
	
}
