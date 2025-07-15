package su.rumishistem.android.ilanesandroid.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import su.rumishistem.android.ilanesandroid.Adapter.CommentListAdapter;
import su.rumishistem.android.ilanesandroid.Adapter.IllustListAdapter;
import su.rumishistem.android.ilanesandroid.Adapter.UserIllustListAdapter;
import su.rumishistem.android.ilanesandroid.Module.API;
import su.rumishistem.android.ilanesandroid.Module.IPCHTTP;
import su.rumishistem.android.ilanesandroid.Module.IllustImageManager;
import su.rumishistem.android.ilanesandroid.Module.ListViewHeightBasedOnChildren;
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

		Button CommentButton = findViewById(R.id.comment_button);
		CommentButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View V) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							EditText CommentText = findViewById(R.id.comment_text);
							String Text = CommentText.getText().toString();

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									CommentButton.setActivated(false);
									CommentText.setActivated(false);
								}
							});

							HashMap<String, String> Body = new HashMap<>();
							Body.put("TEXT", Text);
							JsonNode Return = API.RunPost("Comment?ILLUST=" + ID, new ObjectMapper().writeValueAsString(Body), Token);
							if (Return.get("STATUS").asBoolean()) {
								//成功
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										CommentText.setText("");
									}
								});

								RefreshCommentList();
							} else {
								//エラー
							}

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									CommentButton.setActivated(true);
									CommentText.setActivated(true);
								}
							});
						} catch (Exception EX) {
							EX.printStackTrace();
						}
					}
				}).start();
			}
		});

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
						UserIconImageView.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								OpenUserView(User.get("UID").asText());
							}
						});

						//ユーザー名
						TextView UserNameTextView = findViewById(R.id.user_name);
						UserNameTextView.setText(User.get("NAME").asText());
						UserNameTextView.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								OpenUserView(User.get("UID").asText());
							}
						});

						//おすすめ
						IllustListAdapter Adapter = new IllustListAdapter(CTX, Osusume);
						ListView OsusumeListView = findViewById(R.id.OsusumeList);
						OsusumeListView.setAdapter(Adapter);
						ListViewHeightBasedOnChildren.set(OsusumeListView);
					}
				});

				//コメント
				RefreshCommentList();
			}
		}).start();
	}

	private void RefreshCommentList() {
		JsonNode CommentResult = API.RunGet("Comment?ILLUST=" + ID + "&PAGE=1", Token);
		if (CommentResult.get("STATUS").asBoolean()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ListView CommentList = findViewById(R.id.CommentList);
					CommentListAdapter Adapter = new CommentListAdapter(CTX, CommentResult.get("LIST"), ID);
					CommentList.setAdapter(Adapter);
					ListViewHeightBasedOnChildren.set(CommentList);
				}
			});
		} else {
			//TODO:失敗時の処理
		}
	}

	private void OpenUserView(String UID) {
		Intent INT = new Intent(CTX, UserView.class);
		INT.putExtra("UID", UID);
		CTX.startActivity(INT);
	}
}
