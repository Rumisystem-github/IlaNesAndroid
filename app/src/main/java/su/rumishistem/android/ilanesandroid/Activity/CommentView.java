package su.rumishistem.android.ilanesandroid.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.android.ilanesandroid.Adapter.CommentListAdapter;
import su.rumishistem.android.ilanesandroid.Module.API;
import su.rumishistem.android.ilanesandroid.Module.IPCHTTP;
import su.rumishistem.android.ilanesandroid.Module.ListViewHeightBasedOnChildren;
import su.rumishistem.android.ilanesandroid.R;

public class CommentView extends AppCompatActivity {
	private Context CTX;
	private String Token;
	private String ID = null;
	private JsonNode CommentData = null;

	@Override
	protected void onStart() {
		super.onStart();

		CTX = this;

		try {
			Intent INT = getIntent();
			ID = INT.getStringExtra("ID");
			CommentData = new ObjectMapper().readTree(INT.getStringExtra("DATA"));
		} catch (Exception EX) {
			EX.printStackTrace();
		}
	}

	@Override
	protected void onCreate(@Nullable Bundle SavedInstanceState) {
		super.onCreate(SavedInstanceState);

		setContentView(R.layout.comment_view);

		new Thread(new Runnable() {
			@Override
			public void run() {
				Token = IPCHTTP.getToken();

				//リプライを取得
				JsonNode ReplyList = API.RunGet("Comment?REPLY="+ID+"&PAGE=1", Token);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							ListView CurrentCommentListView = findViewById(R.id.current_comment);
							CommentListAdapter CurrentCommentAdapter = new CommentListAdapter(CTX, new ObjectMapper().readTree(new ObjectMapper().writeValueAsString(new JsonNode[]{CommentData})));
							CurrentCommentListView.setAdapter(CurrentCommentAdapter);
							ListViewHeightBasedOnChildren.set(CurrentCommentListView);

							ListView ReplyListView = findViewById(R.id.comment_list);
							CommentListAdapter ReplyCommentAdapter = new CommentListAdapter(CTX, ReplyList.get("LIST"));
							ReplyListView.setAdapter(ReplyCommentAdapter);
							ListViewHeightBasedOnChildren.set(ReplyListView);
						} catch (Exception EX) {
							EX.printStackTrace();
						}
					}
				});
			}
		}).start();
	}
}
