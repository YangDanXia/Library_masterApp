package com.example.library_master;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/*
 * 这个类通过访问传入的path
 * 并将对应的图片下载到本地
 * 最后将图片以bitmap的形式返回
 */
public class DownLoad_Picture {

	public static Bitmap download(String path)
	{
		URL url = null;
		Bitmap bitmap = null;
		try {
			url = new URL(path);
			InputStream is = url.openStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bitmap;
	}

}
