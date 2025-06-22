package su.rumishistem.android.ilanesandroid.Module;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetAPIHost {
	private static final String HTTPHost = "https://ilanes.rumiserver.com/api/";
	private static final String HTTPLocalHost = "http://192.168.0.125:4004/api/";

	private static final String AccountHTTPHost = "https://account.rumiserver.com/api/";
	private static final String AccountHTTPLocalHost = "http://192.168.0.125:3000/";

	public static String HTTP() {
		try {
			//外部
			URL RequestURL = new URL(HTTPHost);
			HttpURLConnection Connection = (HttpURLConnection) RequestURL.openConnection();
			Connection.getResponseCode();

			return HTTPHost;
		} catch (Exception EX) {
			//ローカル
			return HTTPLocalHost;
		}
	}

	public static String AccountHTTP() {
		try {
			//外部
			URL RequestURL = new URL(AccountHTTPHost);
			HttpURLConnection Connection = (HttpURLConnection) RequestURL.openConnection();
			Connection.getResponseCode();

			return AccountHTTPHost;
		} catch (Exception EX) {
			//ローカル
			return AccountHTTPLocalHost;
		}
	}
}
