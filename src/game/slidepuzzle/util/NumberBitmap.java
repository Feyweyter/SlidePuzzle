package game.slidepuzzle.util;

import android.graphics.Bitmap;

public class NumberBitmap {

	private Bitmap bitmap;
	private int number;
	
	public NumberBitmap(Bitmap bitmap, int number){
		this.bitmap=bitmap;
		this.number=number;
	}
	
	public Bitmap getBitmap(){
		return this.bitmap;
	}
	
	public int getNumber(){
		return this.number;
	}
	
}
