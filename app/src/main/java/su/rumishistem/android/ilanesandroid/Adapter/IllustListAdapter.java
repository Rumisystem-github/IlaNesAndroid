package su.rumishistem.android.ilanesandroid.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

import su.rumishistem.android.ilanesandroid.Module.IllustThumbnailManager;
import su.rumishistem.android.ilanesandroid.Module.UserIconManager;
import su.rumishistem.android.ilanesandroid.R;

public class IllustListAdapter extends BaseAdapter {
	private Context CTX;
	private JsonNode ItemList;
	private LayoutInflater Inflater;

	public IllustListAdapter(Context CTX, JsonNode ItemList) {
		this.CTX = CTX;
		this.ItemList = ItemList;
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
			ConvertView = Inflater.inflate(R.layout.illust_item, Parent, false);
		}

		ImageView Thumbnail = ConvertView.findViewById(R.id.thumbnail);
		TextView Title = ConvertView.findViewById(R.id.title);
		ImageView UserIcon = ConvertView.findViewById(R.id.user_icon);
		TextView UserName = ConvertView.findViewById(R.id.user_name);

		Title.setText(Row.get("TITLE").asText());
		UserIcon.setImageBitmap(UserIconManager.Get(Row.get("ACCOUNT").get("UID").asText()));
		UserName.setText(Row.get("ACCOUNT").get("NAME").asText());

		Bitmap Original = IllustThumbnailManager.Get(Row.get("ID").asText());

		if (!(Original == null || Original.getWidth() <= 0 || Original.getHeight() <= 0)) {
			int TargetWidth = 180;
			int TargetHeight = 195;
			int Width = Original.getWidth();
			int Height = Original.getHeight();
			float Scale = Math.min((float)TargetWidth / Width, (float)TargetHeight / Height);
			int ScaledWidth = Math.round(Width * Scale);
			int ScaledHeight = Math.round(Height * Scale);

			if (!(ScaledWidth <= 0 || ScaledHeight <= 0)) {
				Bitmap ScaledBitmap = Bitmap.createScaledBitmap(Original, ScaledWidth, ScaledHeight, true);
				Thumbnail.setImageBitmap(ScaledBitmap);
			}
		}

		return ConvertView;
	}
}
