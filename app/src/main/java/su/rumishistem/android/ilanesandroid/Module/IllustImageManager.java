package su.rumishistem.android.ilanesandroid.Module;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import java.util.concurrent.CountDownLatch;

public class IllustImageManager {
	public static Bitmap Get(String ID, int Page) {
		String Token = IPCHTTP.getToken();
		String Key = ID + "-" + Page;

		CountDownLatch CDL = new CountDownLatch(1);

		try {
			Bitmap[] Image = {null};

			new Thread(new Runnable() {
				@Override
				public void run() {
					byte[] ReturnData = API.GetImage(ID, Page, Token);
					Image[0] = BitmapFactory.decodeByteArray(ReturnData, 0, ReturnData.length);
					CDL.countDown();
				}
			}).start();
			CDL.await();

			if (Key != null && Image[0] != null) {
				return Image[0];
			} else {
				return null;
			}
		} catch (InterruptedException EX) {
			return null;
		}
	}
}
