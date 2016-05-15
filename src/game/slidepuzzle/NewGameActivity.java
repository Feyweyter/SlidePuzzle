package game.slidepuzzle;

import game.slidepuzzle.util.ImageUtil;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class NewGameActivity extends SherlockActivity {

	private ImageView image;
	private Bitmap bitMapImg = null;
	private static final int REQUEST = 1;
	private int sideInPx = 200;
	Uri selectedImageURI = null;
	boolean isSelected = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_game);

		image = (ImageView) findViewById(R.id.downloadedImage);
	}

	protected Bitmap decodeBoundsBitmapFactory(int id) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(), id, options);
		int imageHeight = options.outHeight;
		int imageWidth = options.outWidth;
		System.out.println(imageHeight + " " + imageWidth);
		int minSide = imageWidth < imageHeight ? imageWidth : imageHeight;
		if (minSide > sideInPx) {
			options.inSampleSize = Math.round(minSide / sideInPx);
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeResource(getResources(), id, options);
		}
		return BitmapFactory.decodeResource(getResources(), id);
	}

	public void onClickDownloadImage(View view) {
		Intent i = new Intent(Intent.ACTION_PICK);
		i.setType("image/*");
		startActivityForResult(i, REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST && resultCode == RESULT_OK) {
			selectedImageURI = data.getData();

			if (selectedImageURI != null) {
				ContentResolver contenetResolver = getContentResolver();
				ImageUtil cdi = new ImageUtil(contenetResolver);
				Bitmap bitmapNew = cdi.decodeBounds(selectedImageURI);
				bitmapNew = cdi.cropBitmap(bitmapNew);
				isSelected = true;
				image.setImageBitmap(bitmapNew);
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onClickGetSlideGame(View view) {
		Intent intent = new Intent(NewGameActivity.this,
				SlideImageActivity.class);

		if (isSelected) {
			Bundle extras = new Bundle();
			extras.putParcelable("DownloadedImage", selectedImageURI);
			intent.putExtras(extras);

			if (bitMapImg != null) {
				bitMapImg.recycle();
				bitMapImg = null;
			}

			startActivity(intent);
			finish();
		} else {
			Toast.makeText(getApplicationContext(), R.string.select_image,
					Toast.LENGTH_LONG).show();
		}

	}

	public void onClickBack(View view) {
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Bitmap bm = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_launcher, new BitmapFactory.Options());
		image.setImageBitmap(bm);
		isSelected = false;
	}
}
