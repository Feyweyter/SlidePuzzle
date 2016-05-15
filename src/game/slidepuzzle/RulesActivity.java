package game.slidepuzzle;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RulesActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rules);
	}

	
	public void onClickBack(View view) {
		finish();
	}
	
	public void onClickNewGame(View view) {
		Intent intent = new Intent(RulesActivity.this, NewGameActivity.class);
		startActivity(intent);
	}
}
