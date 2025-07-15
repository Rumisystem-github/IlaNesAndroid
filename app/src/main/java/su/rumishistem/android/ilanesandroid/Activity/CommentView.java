package su.rumishistem.android.ilanesandroid.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

import su.rumishistem.android.ilanesandroid.Adapter.CommentListAdapter;
import su.rumishistem.android.ilanesandroid.Module.API;
import su.rumishistem.android.ilanesandroid.Module.IPCHTTP;
import su.rumishistem.android.ilanesandroid.Module.ListViewHeightBasedOnChildren;
import su.rumishistem.android.ilanesandroid.R;

public class CommentView extends AppCompatActivity {
	private Context CTX;
	private String Token;
	private String CommentID = null;
	private String IllustID = null;
	private JsonNode CommentData = null;

	@Override
	protected void onStart() {
		super.onStart();

		CTX = this;

		try {
			Intent INT = getIntent();
			CommentID = INT.getStringExtra("ID");
			IllustID = INT.getStringExtra("ILLUST_ID");
			CommentData = new ObjectMapper().readTree(INT.getStringExtra("DATA"));
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}

	@Override
	protected void onCreate(@Nullable Bundle SavedInstanceState) {
		super.onCreate(SavedInstanceState);

		setContentView(R.layout.comment_view);

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
							JsonNode Return = API.RunPost("Comment?ILLUST=" + IllustID + "&REPLY=" + CommentID, new ObjectMapper().writeValueAsString(Body), Token);
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

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							ListView CurrentCommentListView = findViewById(R.id.current_comment);
							CommentListAdapter CurrentCommentAdapter = new CommentListAdapter(CTX, new ObjectMapper().readTree(new ObjectMapper().writeValueAsString(new JsonNode[]{CommentData})), IllustID);
							CurrentCommentListView.setAdapter(CurrentCommentAdapter);
							ListViewHeightBasedOnChildren.set(CurrentCommentListView);
						} catch (Exception EX) {
							EX.printStackTrace();
						}
					}
				});

				RefreshCommentList();
			}
		}).start();
	}

	private void RefreshCommentList() {
		JsonNode ReplyList = API.RunGet("Comment?REPLY=" + CommentID + "&PAGE=1", Token);
		if (ReplyList.get("STATUS").asBoolean()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ListView ReplyListView = findViewById(R.id.comment_list);
					CommentListAdapter ReplyCommentAdapter = new CommentListAdapter(CTX, ReplyList.get("LIST"), IllustID);
					ReplyListView.setAdapter(ReplyCommentAdapter);
					ListViewHeightBasedOnChildren.set(ReplyListView);
				}
			});
		} else {
			//TODO:失敗時の処理
		}
	}
}
