package lk.jiat.eshop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class SnowfallView extends View {

    private static final int NUM_SNOWFLAKES = 50;
    private static final int SNOWFLAKE_SIZE = 10;

    private final Paint paint = new Paint();
    private final Random random = new Random();

    private int[] snowflakeX;
    private int[] snowflakeY;

    public SnowfallView(Context context) {
        super(context);
        init();
    }

    public SnowfallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Reinitialize snowflake positions based on the new size
        if (w > 0 && h > 0) {
            snowflakeX = new int[NUM_SNOWFLAKES];
            snowflakeY = new int[NUM_SNOWFLAKES];

            for (int i = 0; i < NUM_SNOWFLAKES; i++) {
                snowflakeX[i] = random.nextInt(w);
                snowflakeY[i] = random.nextInt(h);
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Check if snowflake arrays are not null
        if (snowflakeX != null && snowflakeY != null) {
            for (int i = 0; i < NUM_SNOWFLAKES; i++) {
                canvas.drawCircle(snowflakeX[i], snowflakeY[i], SNOWFLAKE_SIZE, paint);

                // Simulate falling motion
                snowflakeY[i] += 5;
                if (snowflakeY[i] > getHeight()) {
                    // Reset snowflake to the top if it goes below the screen
                    snowflakeY[i] = 0;
                    snowflakeX[i] = random.nextInt(getWidth());
                }
            }
        }

        // Invalidate to trigger continuous redraw
        invalidate();
    }

}
