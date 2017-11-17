package uk.co.appsbystudio.geoshare.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import uk.co.appsbystudio.geoshare.Application;
import uk.co.appsbystudio.geoshare.R;

public class BitmapUtils {

    public static Bitmap bitmapCanvas(Bitmap profileImage, int w, int h, boolean selected, int alpha, String address) {
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bmp);

        Bitmap mapMarker = BitmapFactory.decodeResource(Application.getContext().getResources(), R.drawable.map_marker_point_shadow);
        Bitmap scaledMarker = Bitmap.createScaledBitmap(mapMarker, 116, 155, false);

        if (selected) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setAlpha(alpha);
            paint.setAntiAlias(true);

            RectF rect = new RectF(58, 16, canvas.getWidth()- (canvas.getHeight()-20)/2, canvas.getHeight()  - 20);
            RectF rectF = new RectF(58, 16, canvas.getWidth(), canvas.getHeight()  - 20);
            canvas.drawRect(rect, paint);
            canvas.drawRoundRect(rectF, (canvas.getHeight()-20)/2, (canvas.getHeight()-20)/2, paint);

            String[] split = address.split("\n");

            Paint greenPaint = new Paint();
            greenPaint.setColor(Application.getContext().getResources().getColor(R.color.colorPrimary));
            greenPaint.setStyle(Paint.Style.FILL);
            greenPaint.setTextSize(32);
            greenPaint.setAlpha(alpha);
            greenPaint.setAntiAlias(true);

            canvas.drawText(StringUtils.ellipsize(split[0], 22), 120, 48, greenPaint);
            canvas.drawText(StringUtils.ellipsize(split[1], 22), 120, 84, greenPaint);
            canvas.drawText(StringUtils.ellipsize(split[2], 22), 120, 122, greenPaint);
        }
        canvas.drawBitmap(scaledMarker, 0, 0, null);
        if (profileImage != null) canvas.drawBitmap(getCroppedBitmap(profileImage), 18, 22, null);

        return bmp;
    }

    private static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return Bitmap.createScaledBitmap(output, 80, 80, false);
    }
}
