package game.slidepuzzle.util;

import game.slidepuzzle.R;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class DialogShowImage extends DialogFragment implements OnClickListener {

	private Bitmap original = null;

	public DialogShowImage(Context context, Bitmap bm) {
		this.original = bm;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_show_image, null);

		ImageView imageView = (ImageView) v.findViewById(R.id.downloadedImage);
		imageView.setImageBitmap(original);
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, 0);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub

	}

}
