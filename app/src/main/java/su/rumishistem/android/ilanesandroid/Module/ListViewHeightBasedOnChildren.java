package su.rumishistem.android.ilanesandroid.Module;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewHeightBasedOnChildren {
	public static void set(ListView LV) {
		ListAdapter Adapter = LV.getAdapter();
		if (Adapter == null) return;

		int TotalHeight = 0;
		for (int I = 0; I < Adapter.getCount(); I++) {
			View Item = Adapter.getView(I, null, LV);
			Item.measure(
					View.MeasureSpec.makeMeasureSpec(LV.getWidth(), View.MeasureSpec.UNSPECIFIED),
					View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
			);
			TotalHeight += Item.getMeasuredHeight();
		}

		ViewGroup.LayoutParams Params = LV.getLayoutParams();
		Params.height = TotalHeight + (LV.getDividerHeight() * (LV.getCount() - 1));
		LV.setLayoutParams(Params);
		LV.requestLayout();
	}
}
