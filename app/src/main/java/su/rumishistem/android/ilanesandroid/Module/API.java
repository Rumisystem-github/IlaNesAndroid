package su.rumishistem.android.ilanesandroid.Module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class API {
	public static JsonNode RunGet(String Path, String Token) {
		try {
			HttpURLConnection Connection = OpenConnection(GetAPIHost.HTTP() + Path);
			Connection.setRequestMethod("GET");
			Connection.setRequestProperty("Accept", "application/json");

			//トークン
			if (Token != null) {
				Connection.setRequestProperty("TOKEN", Token);
			}

			//応答を取得
			int Code = Connection.getResponseCode();
			InputStream IS = (Code < HttpsURLConnection.HTTP_BAD_REQUEST)
					?Connection.getInputStream()
					:Connection.getErrorStream();

			//読む
			StringBuilder SB = new StringBuilder();
			BufferedReader BR = new BufferedReader(new InputStreamReader(IS, "UTF-8"));
			String Line;
			while ((Line = BR.readLine()) != null) {
				SB.append(Line.trim());
			}

			return new ObjectMapper().readTree(SB.toString());
		} catch (Exception EX) {
			EX.printStackTrace();
			throw new Error("接続失敗");
		}
	}

	public static JsonNode RunPost(String Path, String Body, String Token) {
		try {
			return SendBodyHTTP(GetAPIHost.HTTP() + Path, Body.getBytes(StandardCharsets.UTF_8), "POST", new HashMap<String, String>(){
				{
					put("Content-Type", "application/json; charset=UTF-8");
					put("Accept", "application/json");

					//トークン
					if (Token != null) {
						put("TOKEN", Token);
					}
				}
			});
		} catch (Exception EX) {
			try {
				HashMap<String, Object> Return = new HashMap<>();
				Return.put("STATUS", false);
				Return.put("ERR", "ANDROID_APP_ERR");
				return new ObjectMapper().readTree(new ObjectMapper().writeValueAsString(Return));
			} catch (Exception EX2) {
				throw new Error("は。");
			}
		}
	}

	public static JsonNode RunPostByte(String Path, byte[] Body, String Token) {
		try {
			return SendBodyHTTP(GetAPIHost.HTTP() + Path, Body, "POST", new HashMap<String, String>(){
				{
					put("Content-Type", "application/json; charset=UTF-8");
					put("Accept", "application/json");

					//トークン
					if (Token != null) {
						put("TOKEN", Token);
					}
				}
			});
		} catch (Exception EX) {
			try {
				HashMap<String, Object> Return = new HashMap<>();
				Return.put("STATUS", false);
				Return.put("ERR", "ANDROID_APP_ERR");
				return new ObjectMapper().readTree(new ObjectMapper().writeValueAsString(Return));
			} catch (Exception EX2) {
				throw new Error("は。");
			}
		}
	}

	public static JsonNode RunPatch(String Path, String Body, String Token) {
		try {
			return SendBodyHTTP(GetAPIHost.HTTP() + Path, Body.getBytes(StandardCharsets.UTF_8), "PATCH", new HashMap<String, String>(){
				{
					put("Content-Type", "application/json; charset=UTF-8");
					put("Accept", "application/json");

					//トークン
					if (Token != null) {
						put("TOKEN", Token);
					}
				}
			});
		} catch (Exception EX) {
			try {
				HashMap<String, Object> Return = new HashMap<>();
				Return.put("STATUS", false);
				Return.put("ERR", "ANDROID_APP_ERR");
				return new ObjectMapper().readTree(new ObjectMapper().writeValueAsString(Return));
			} catch (Exception EX2) {
				throw new Error("は。");
			}
		}
	}

	public static JsonNode SendBodyHTTP(String Path, byte[] Body, String Method, HashMap<String, String> Header) {
		try {
			HttpURLConnection Connection = OpenConnection(GetAPIHost.HTTP() + Path);
			Connection.setRequestMethod(Method);
			Connection.setDoOutput(true);

			for (String Key:Header.keySet()) {
				Connection.setRequestProperty(Key, Header.get(Key));
			}

			//JSONを送りつける
			OutputStream OS = Connection.getOutputStream();
			OS.write(Body);
			OS.flush();

			//応答を取得
			int Code = Connection.getResponseCode();
			InputStream IS = (Code < HttpsURLConnection.HTTP_BAD_REQUEST)
					?Connection.getInputStream()
					:Connection.getErrorStream();

			//読む
			StringBuilder SB = new StringBuilder();
			BufferedReader BR = new BufferedReader(new InputStreamReader(IS, "UTF-8"));
			String Line;
			while ((Line = BR.readLine()) != null) {
				SB.append(Line.trim());
			}

			return new ObjectMapper().readTree(SB.toString());
		} catch (Exception EX) {
			EX.printStackTrace();
			throw new Error("接続失敗");
		}
	}

	public static byte[] GetIcon(String UID) {
		try {
			HttpURLConnection Connection = OpenConnection(GetAPIHost.AccountHTTP() + "Icon?UID=" + URLEncoder.encode(UID));
			Connection.setRequestMethod("GET");

			//応答を取得
			int Code = Connection.getResponseCode();
			InputStream IS = (Code < HttpsURLConnection.HTTP_BAD_REQUEST)
					?Connection.getInputStream()
					:Connection.getErrorStream();

			//読む
			ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
			byte[] Temp = new byte[4096];
			int Length;
			while ((Length = IS.read(Temp)) != -1) {
				BAOS.write(Temp, 0, Length);
			}

			return BAOS.toByteArray();
		} catch (Exception EX) {
			EX.printStackTrace();
			throw new Error("接続失敗");
		}
	}

	public static byte[] GetThumbnail(String ID) {
		try {
			JsonNode Result = RunGet("GetThumbnail?ID=" + URLEncoder.encode(ID), "");
			if (!Result.get("STATUS").asBoolean()) {
				throw new Error("接続失敗");
			}

			String FileURL = Result.get("URL").asText();
			FileURL = FileURL.replace("https://ilanes.rumiserver.com/", GetAPIHost.HTTP().replace("/api/", "/"));

			HttpURLConnection Connection = OpenConnection(FileURL);
			Connection.setRequestMethod("GET");

			//応答を取得
			int Code = Connection.getResponseCode();
			InputStream IS = (Code < HttpsURLConnection.HTTP_BAD_REQUEST)
					?Connection.getInputStream()
					:Connection.getErrorStream();

			//読む
			ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
			byte[] Temp = new byte[4096];
			int Length;
			while ((Length = IS.read(Temp)) != -1) {
				BAOS.write(Temp, 0, Length);
			}

			return BAOS.toByteArray();
		} catch (Exception EX) {
			EX.printStackTrace();
			throw new Error("接続失敗");
		}
	}

	public static byte[] GetImage(String ID, int Page, String Token) {
		try {
			JsonNode Result = RunGet("GetIMAGE?ID=" + URLEncoder.encode(ID) + "&PAGE=" + Page, Token);
			if (!Result.get("STATUS").asBoolean()) {
				throw new Error("接続失敗");
			}

			String FileURL = Result.get("URL").asText();
			FileURL = FileURL.replace("https://ilanes.rumiserver.com/", GetAPIHost.HTTP().replace("/api/", "/"));

			HttpURLConnection Connection = OpenConnection(FileURL);
			Connection.setRequestMethod("GET");

			if (Token != null) {
				Connection.setRequestProperty("TOKEN", Token);
			}

			//応答を取得
			int Code = Connection.getResponseCode();
			InputStream IS = (Code < HttpsURLConnection.HTTP_BAD_REQUEST)
					?Connection.getInputStream()
					:Connection.getErrorStream();

			//読む
			ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
			byte[] Temp = new byte[4096];
			int Length;
			while ((Length = IS.read(Temp)) != -1) {
				BAOS.write(Temp, 0, Length);
			}

			return BAOS.toByteArray();
		} catch (Exception EX) {
			EX.printStackTrace();
			throw new Error("接続失敗");
		}
	}

	private static HttpURLConnection OpenConnection(String Path) throws IOException {
		URL RequestURL = new URL(Path);
		System.out.println("HTTP:" + RequestURL.toString());

		HttpURLConnection Connection = (HttpURLConnection) RequestURL.openConnection();
		return Connection;
	}
}
