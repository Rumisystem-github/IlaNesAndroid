package su.rumishistem.android.ilanesandroid.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import su.rumishistem.android.ilanesandroid.Activity.CommentView;
import su.rumishistem.android.ilanesandroid.Activity.UserView;
import su.rumishistem.android.ilanesandroid.Module.UserIconManager;
import su.rumishistem.android.ilanesandroid.R;

public class CommentListAdapter extends BaseAdapter {
	private Context CTX;
	private JsonNode ItemList;
	private String IllustID;
	private LayoutInflater Inflater;

	public CommentListAdapter(Context CTX, JsonNode ItemList, String IllustID) {
		this.CTX = CTX;
		this.ItemList = ItemList;
		this.IllustID = IllustID;
		this.Inflater = LayoutInflater.from(CTX);
	}

	@Override
	public int getCount() {
		return ItemList.size();
	}

	@Override
	public Object getItem(int I) {
		return ItemList.get(I);
	}

	@Override
	public long getItemId(int I) {
		return I;
	}

	@Override
	public View getView(int I, View ConvertView, ViewGroup Parent) {
		JsonNode Row = ItemList.get(I);

		if (ConvertView == null) {
			ConvertView = Inflater.inflate(R.layout.comment_item, Parent, false);
		}

		ImageView UserIcon = ConvertView.findViewById(R.id.user_icon);
		TextView UserName = ConvertView.findViewById(R.id.user_name);
		TextView Text = ConvertView.findViewById(R.id.text);
		TextView ReplyCount = ConvertView.findViewById(R.id.reply_count);

		UserName.setText(Row.get("ACCOUNT").get("NAME").asText());
		Text.setText(Row.get("TEXT").asText());
		ReplyCount.setText("返信(" + Row.get("REPLY_COUNT").asText() + ")");

		//アイコン
		new Thread(new Runnable() {
			@Override
			public void run() {
				Bitmap Icon = UserIconManager.Get(Row.get("ACCOUNT").get("UID").asText());
				((android.app.Activity) CTX).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						UserIcon.setImageBitmap(Icon);
					}
				});
			}
		}).start();

		//ユーザーを開くやつ
		UserIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				OpenUserView(Row.get("ACCOUNT").get("UID").asText());
			}
		});
		UserName.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				OpenUserView(Row.get("ACCOUNT").get("UID").asText());
			}
		});

		ReplyCount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					Intent INT = new Intent(CTX, CommentView.class);
					INT.putExtra("ID", Row.get("ID").asText());
					INT.putExtra("ILLUST_ID", IllustID);
					INT.putExtra("DATA", new ObjectMapper().writeValueAsString(Row));
					CTX.startActivity(INT);
				} catch (Exception EX) {
					EX.printStackTrace();
				}
			}
		});

		return ConvertView;
	}

	private void OpenUserView(String UID) {
		Intent INT = new Intent(CTX, UserView.class);
		INT.putExtra("UID", UID);
		CTX.startActivity(INT);
	}
}
