package game.slidepuzzle;

import game.slidepuzzle.util.DialogShowImage;
import game.slidepuzzle.util.ImageUtil;
import game.slidepuzzle.util.SlicedImageView;

import java.util.LinkedList;

import com.actionbarsherlock.app.SherlockActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;

public class SlideImageActivity extends SherlockActivity {
	SlicedImageView gameView = null;

	private Chronometer mChronometer;

	private DialogShowImage showImage;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slide_image);
		mChronometer = (Chronometer) findViewById(R.id.chronometer1);
		mChronometer.setBase(SystemClock.elapsedRealtime());
		mChronometer.start();
		Bitmap bitmapImage = null;

		Bundle extras = getIntent().getExtras();
		Uri uri = (Uri) extras.getParcelable("DownloadedImage");
		ContentResolver contenetResolver = getContentResolver();
		ImageUtil cdi = new ImageUtil(contenetResolver);
		Bitmap bitmapNew = cdi.decodeBounds(uri);
		bitmapImage = cdi.cropBitmap(bitmapNew);

		gameView = (SlicedImageView) findViewById(R.id.gameboard);
		if (bitmapImage != null) {
			gameView.setImage(bitmapImage);
		}
		bitmapImage = null;

		@SuppressWarnings({ "deprecation", "unchecked" })
		final LinkedList<Integer> tileOrder = (LinkedList<Integer>) getLastNonConfigurationInstance();
		gameView.setFragmentManager(getFragmentManager());
		gameView.setChronometer(mChronometer);
		if (tileOrder != null) {
			gameView.setImageSquareOrder(tileOrder);
		}
	}

	private Bitmap getImageFromResource(int id) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeResource(getResources(), id, options);
	}

	public void onClickRefresh(View view) {
		mChronometer.stop();
		mChronometer.setBase(SystemClock.elapsedRealtime());
		gameView.fillTiles();
		mChronometer.start();
	}

	@SuppressLint("NewApi")
	public void onClickGetImage(View view) {
		Bitmap original = gameView.getOriginalBitmap();
		showImage = new DialogShowImage(getApplicationContext(), original);
		showImage.show(getFragmentManager(), "showImage");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
