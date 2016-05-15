package game.slidepuzzle;

import com.actionbarsherlock.app.SherlockActivity;

import android.os.Bundle;
import android.view.View;

public class AboutGameActivity extends SherlockActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_game);
	}

	public void onClickBack(View view) {
		finish();
	}
}
