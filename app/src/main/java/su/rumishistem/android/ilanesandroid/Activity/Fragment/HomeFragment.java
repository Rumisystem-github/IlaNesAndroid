package su.rumishistem.android.ilanesandroid.Activity.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import su.rumishistem.android.ilanesandroid.Activity.MainActivity;
import su.rumishistem.android.ilanesandroid.R;

public class HomeFragment extends Fragment {
	private MainActivity Parent;

	public HomeFragment(MainActivity ParentActivity) {
		this.Parent = ParentActivity;
	}

	@Override
	public View onCreateView(LayoutInflater Inflater, ViewGroup Container, Bundle SavedInstanceState) {
		return Inflater.inflate(R.layout.home_fragment, Container, false);
	}

	@Override
	public void onViewCreated(View V, Bundle SavedInstanceState) {
		super.onViewCreated(V, SavedInstanceState);
	}
}
