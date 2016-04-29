package ismartdev.mn.darkhanwindow.text;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class Medium extends TextView {

	public Medium(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public Medium(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Medium(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
					"Roboto-Medium.ttf");
			setTypeface(tf);
		}
	}
}