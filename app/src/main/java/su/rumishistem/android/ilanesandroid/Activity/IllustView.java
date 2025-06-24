package su.rumishistem.android.ilanesandroid.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;

import su.rumishistem.android.ilanesandroid.Module.API;
import su.rumishistem.android.ilanesandroid.Module.IPCHTTP;
import su.rumishistem.android.ilanesandroid.Module.IllustImageManager;
import su.rumishistem.android.ilanesandroid.Module.UserIconManager;
import su.rumishistem.android.ilanesandroid.R;

public class IllustView extends AppCompatActivity {
	private Context CTX;
	private String ID;
	private String Token;

	@Override
	protected void onStart() {
		super.onStart();

		CTX = this;

		Intent INT = getIntent();
		ID = INT.getStringExtra("ID");
	}

	@Override
	protected void onCreate(@Nullable Bundle SavedInstanceState) {
		super.onCreate(SavedInstanceState);

		setContentView(R.layout.illust_view);

		new Thread(new Runnable() {
			@Override
			public void run() {
				Token = IPCHTTP.getToken();

				JsonNode Result = API.RunGet("ILLUST?ID=" + ID, Token);
				JsonNode Illust = Result.get("ILLUST");
				JsonNode User = Result.get("ACCOUNT");
				JsonNode Setting = Result.get("SETTING");
				JsonNode Osusume = Result.get("OSUSUME");

				List<ImageView> ImageViewList = new ArrayList<>();
				for (int I = 0; I < Illust.get("PAGE").asInt(); I++) {
					ImageView IV = new ImageView(CTX);
					IV.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT
					));
					IV.setImageBitmap(IllustImageManager.Get(ID, I));
					ImageViewList.add(IV);
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//画像
						LinearLayout LL = findViewById(R.id.illust_list);
						for (ImageView IV: ImageViewList) {
							LL.addView(IV);
						}

						//タイトル
						TextView TitleTextView = findViewById(R.id.title);
						TitleTextView.setText(Illust.get("TITLE").asText());

						//タイトル
						TextView DescTextView = findViewById(R.id.desc);
						DescTextView.setText(Illust.get("DESC").asText());

						//ユーザーのアイコン
						ImageView UserIconImageView = findViewById(R.id.user_icon);
						UserIconImageView.setImageBitmap(UserIconManager.Get(User.get("UID").asText()));

						//ユーザー名
						TextView UserNameTextView = findViewById(R.id.user_name);
						UserNameTextView.setText(User.get("NAME").asText());
					}
				});
			}
		}).start();
	}
}
