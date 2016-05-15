package game.slidepuzzle.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.widget.ImageView;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class ImageSquareView extends ImageView {

	public Coordinate coordinate;
	public int originalIndex;
	public int numberOfDrags;
	private boolean empty;

	public ImageSquareView(Context context, int originalIndex) {
		super(context);
		this.originalIndex = originalIndex;
	}

	public void setOriginalIndex(int originalIndex) {
		this.originalIndex = originalIndex;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
		if (empty) {
			setBackgroundDrawable(null);
			setAlpha(0);
		}
	}

	public boolean isInRowOrColumnOf(ImageSquareView otherTile) {
		return (coordinate.getAxisWith(otherTile.coordinate));
	}

	public boolean isToRightOf(ImageSquareView imageSquare) {
		return coordinate.isToRightOfSquare(imageSquare.coordinate);
	}

	public boolean isToLeftOf(ImageSquareView imageSquare) {
		return coordinate.isToLeftOfSquare(imageSquare.coordinate);
	}

	public boolean isAbove(ImageSquareView imageSquare) {
		return coordinate.isAboveSquare(imageSquare.coordinate);
	}

	public boolean isBelow(ImageSquareView imageSquare) {
		return coordinate.isBelowSquare(imageSquare.coordinate);
	}

	public void setXY(float x, float y) {
		if (Build.VERSION.SDK_INT >= 11) {
			setX(x);
			setY(y);
		} else {
			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) getLayoutParams();
			params.leftMargin = (int) x;
			params.topMargin = (int) y;
			setLayoutParams(params);
		}
	}

	public float getXPos() {
		if (Build.VERSION.SDK_INT >= 11) {
			return getX();
		} else {
			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) getLayoutParams();
			return params.leftMargin;
		}
	}

	public float getYPos() {
		if (Build.VERSION.SDK_INT >= 11) {
			return getY();
		} else {
			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) getLayoutParams();
			return params.topMargin;
		}
	}

}
