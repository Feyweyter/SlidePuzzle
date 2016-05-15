package game.slidepuzzle.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class ImageUtil {

	private Bitmap bitmapOriginal = null;
	private Bitmap bitmapNew = null;
	private int sideInPx = 400;
	ContentResolver contenetResolver = null;

	public ImageUtil(Bitmap bitMapImg, int sideInPx) {
		this.sideInPx = sideInPx;
		this.bitmapOriginal = bitMapImg;
	}

	public ImageUtil(Bitmap bitMapImg) {
		this.bitmapOriginal = bitMapImg;
	}

	public ImageUtil(ContentResolver contenetResolver, int sideInPx) {
		this.contenetResolver = contenetResolver;
		this.sideInPx = sideInPx;
	}

	public ImageUtil(ContentResolver contenetResolver) {
		this.contenetResolver = contenetResolver;
	}

	public Bitmap decodeBounds(Uri uri) {
		InputStream inputStream = null;
		try {
			inputStream = contenetResolver.openInputStream(uri);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(inputStream, null, options);
	}

	public List<Bitmap> cropBitmapOnNSquares(Bitmap bm, int N, Context context)
			throws IOException {
		int sideSquare = 0;
		sideSquare = (int) Math.ceil((float) bm.getWidth() / (float) N);
		List<Bitmap> squares = new ArrayList<Bitmap>();

		Bitmap bitmapPiece = null;
		int[] pixels;

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				bitmapPiece = Bitmap.createBitmap(sideSquare, sideSquare,
						Bitmap.Config.ARGB_8888);
				pixels = new int[sideSquare * sideSquare];
				bm.getPixels(pixels, 0, sideSquare, j * sideSquare, i
						* sideSquare, sideSquare, sideSquare);
				bitmapPiece.setPixels(pixels, 0, sideSquare, 0, 0, sideSquare,
						sideSquare);
				squares.add(bitmapPiece);
			}
		}

		for (int i = 0; i < squares.size(); i++) {
			saveToInternalStorage(squares.get(i), i + 1, context);
		}
		return squares;

	}

	public String saveToInternalStorage(Bitmap bitmapImage, int i,
			Context context) throws IOException {
		ContextWrapper cw = new ContextWrapper(context);
		File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
		File mypath = new File(directory, "w" + i + ".png");

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(mypath);
			bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fos.close();
		}
		return directory.getAbsolutePath();
	}

	public Bitmap cropBitmap(Bitmap bm) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		boolean isVertical = false;
		int minSideLenght = 0;
		int balance = 0;
		if (width < height) {
			minSideLenght = width;
			balance = (height - minSideLenght) / 2;
			isVertical = true;

		} else {
			minSideLenght = height;
			balance = (width - minSideLenght) / 2;
		}

		bitmapNew = Bitmap.createBitmap(minSideLenght, minSideLenght,
				Bitmap.Config.ARGB_8888);
		int[] pixels = new int[minSideLenght * minSideLenght];

		if (isVertical) {
			bm.getPixels(pixels, 0, minSideLenght, 0, balance, minSideLenght,
					minSideLenght);
		} else {
			bm.getPixels(pixels, 0, minSideLenght, balance, 0, minSideLenght,
					minSideLenght);
		}
		bitmapNew.setPixels(pixels, 0, minSideLenght, 0, 0, minSideLenght,
				minSideLenght);

		return bitmapNew;
	}
}
