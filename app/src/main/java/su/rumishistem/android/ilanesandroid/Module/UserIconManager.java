package su.rumishistem.android.ilanesandroid.Module;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import java.util.concurrent.CountDownLatch;

public class UserIconManager {
	private static LruCache<String, Bitmap> Cache;
	private static boolean InitOK = false;

	public static void Init() {
		final int MaxMemory = (int)(Runtime.getRuntime().maxMemory()) / 10;
		final int CacheSize = MaxMemory / 8;

		InitOK = true;

		Cache = new LruCache<String, Bitmap>(CacheSize) {
			@Override
			protected int sizeOf(String Key, Bitmap BMP) {
				return BMP.getByteCount() / 1024;
			}
		};
	}

	public static void Clear(String UID) {
		//初期化
		if (!InitOK) {
			Init();
		}

		Cache.remove(Integer.toHexString(UID.hashCode()));
	}

	//ユーザーIDを入れろ
	public static Bitmap Get(String UID) {
		String Key = Integer.toHexString(UID.hashCode());

		//初期化
		if (!InitOK) {
			Init();
		}

		//キャッシュから取得
		Bitmap CachedBMP = Cache.get(Key);
		if (CachedBMP != null) {
			return CachedBMP;
		}

		//無いのでサーバーから持ってくる
		CountDownLatch CDL = new CountDownLatch(1);

		try {
			Bitmap[] Icon = {null};

			new Thread(new Runnable() {
				@Override
				public void run() {
					byte[] ReturnData = API.GetIcon(UID);
					Icon[0] = BitmapFactory.decodeByteArray(ReturnData, 0, ReturnData.length);
					CDL.countDown();
				}
			}).start();
			CDL.await();

			Cache.put(Key, Icon[0]);

			return Icon[0];
		} catch (InterruptedException EX) {
			return null;
		}
	}
}
