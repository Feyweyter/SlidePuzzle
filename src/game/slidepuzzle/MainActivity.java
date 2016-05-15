package game.slidepuzzle;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	private void startThread() {
		final Handler handler = new Handler();

		try {
			startAnimation();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		new Thread() {
			@Override
			public void run() {

				handler.postDelayed(new Runnable() {
					public void run() {
						Intent intent = new Intent(MainActivity.this,
								OptionsActivity.class);
						startActivity(intent);

					}
				}, 3500);

			}

		}.start();
	}

	private void startAnimation() throws InterruptedException {

		ImageView imageViewLake = (ImageView) findViewById(R.id.imageViewLake);

		ImageView imageViewLakeCut = (ImageView) findViewById(R.id.imageViewLakeCut);

		Animation anim = AnimationUtils
				.loadAnimation(this, R.anim.change_alpha);
		imageViewLake.setVisibility(View.VISIBLE);
		imageViewLakeCut.setVisibility(View.INVISIBLE);
		imageViewLake.startAnimation(anim);

		ImageView imageViewShip = (ImageView) findViewById(R.id.imageViewShip);
		Animation anim2 = AnimationUtils.loadAnimation(this,
				R.anim.change_position);
		imageViewShip.setVisibility(View.VISIBLE);
		imageViewShip.startAnimation(anim2);

	}

	public void onClickIcon(View view) {
		startThread();
	}

}