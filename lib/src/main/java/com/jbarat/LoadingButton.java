package com.jbarat;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jbarat.lib.R;

import static android.animation.ValueAnimator.AnimatorUpdateListener;
import static android.animation.ValueAnimator.ofFloat;
import static com.jbarat.LoadingButton.State.Fail;
import static com.jbarat.LoadingButton.State.InProgress;
import static com.jbarat.LoadingButton.State.Initial;
import static com.jbarat.LoadingButton.State.Success;

public class LoadingButton extends FrameLayout {

	private ImageView circleResult;
	private ProgressBar progress;
	private Button button;
	private ImageView circle;
	private TextView resultText;

	private State state;

	private String successText;
	private String failText;

	public LoadingButton(Context context) {
		super(context);
		init(context, null);
	}

	public LoadingButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.loading_button, this, true);

		button = (Button) findViewById(R.id.loading_button);
		circle = (ImageView) findViewById(R.id.circle);
		circleResult = (ImageView) findViewById(R.id.circle_result);
		progress = (ProgressBar) findViewById(R.id.progress);
		resultText = (TextView) findViewById(R.id.result);

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (state == Initial) {
					state = InProgress;
					button.setText("");
					callOnClick();

					Animator circularReveal = ViewAnimationUtils.createCircularReveal(button,
							(int) button.getPivotX(),
							(int) button.getPivotY(),
							button.getWidth(),
							0);
					circularReveal.addListener(new AnimatorListener() {
						@Override
						public void onAnimationStart(Animator animation) {

						}

						@Override
						public void onAnimationEnd(Animator animation) {
							button.setVisibility(GONE);
							progress.setVisibility(VISIBLE);
							progress.setProgress(0);
						}

						@Override
						public void onAnimationCancel(Animator animation) {

						}

						@Override
						public void onAnimationRepeat(Animator animation) {

						}
					});
					circularReveal.setDuration(800).start();
				}
			}
		});

		circle.setMinimumHeight(button.getHeight());
		circle.setMinimumWidth(button.getWidth());

		state = Initial;

		initAttributes(context, attrs);
	}

	private void initAttributes(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton);

		int primaryColor = typedArray.getColor(R.styleable.LoadingButton_lb_color, Color.GRAY);
		button.setBackgroundColor(primaryColor);
		circle.setColorFilter(primaryColor);

		int progressColor = typedArray.getColor(R.styleable.LoadingButton_lb_progress_color, Color.BLUE);
		progress.getIndeterminateDrawable().setColorFilter(progressColor, PorterDuff.Mode.MULTIPLY);

		String initText = typedArray.getString(R.styleable.LoadingButton_lb_init_text);
		button.setText(initText);

		successText = typedArray.getString(R.styleable.LoadingButton_lb_success_text);
		failText = typedArray.getString(R.styleable.LoadingButton_lb_failure_text);

		typedArray.recycle();
	}

	public LoadingButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void success() {
		if (state != Success) {
			state = Success;

			ValueAnimator va = ofFloat(0, 1);
			va.setDuration(500);
			va.addUpdateListener(new AnimatorUpdateListener() {
				public void onAnimationUpdate(ValueAnimator animation) {
					float value = (float) animation.getAnimatedValue();
					circle.setAlpha(1 - value);
					circleResult.setAlpha(value);
				}
			});
			va.addListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					resultText.setText(successText);
					final float originalResultTextX = resultText.getX();
					final float originalCircleResultX = circleResult.getX();
					final float circleHalfWidth = circle.getHeight() / 2;
					// TODO 56 is a magic number
					final float newCircleResultX = (circleResult.getPivotX() - circleHalfWidth) -
							(originalResultTextX - circleHalfWidth) + 56;

					ValueAnimator va = ofFloat(0, 1);
					va.setDuration(500);
					va.addUpdateListener(new AnimatorUpdateListener() {
						public void onAnimationUpdate(ValueAnimator animation) {
							float value = (float) animation.getAnimatedValue();
							resultText.setAlpha(value);
							resultText.setX(originalResultTextX + (circleHalfWidth * value));
							circleResult.setX(originalCircleResultX - (newCircleResultX * value));
						}
					});
					va.start();
				}

				@Override
				public void onAnimationCancel(Animator animation) {

				}

				@Override
				public void onAnimationRepeat(Animator animation) {

				}
			});
			va.start();

			progress.setVisibility(GONE);
			circleResult.setImageResource(R.drawable.ic_check_circle);
			circleResult.setColorFilter(Color.GREEN);
		}
	}

	public void failure() {
		if (state != Fail) {
			state = Fail;

			ValueAnimator va = ofFloat(0, 1);
			va.setDuration(500);
			va.addUpdateListener(new AnimatorUpdateListener() {
				public void onAnimationUpdate(ValueAnimator animation) {
					float value = (float) animation.getAnimatedValue();
					circle.setAlpha(1 - value);
					circleResult.setAlpha(value);
				}
			});

			va.addListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					resultText.setText(failText);
					final float originalResultTextX = resultText.getX();
					final float originalCircleResultX = circleResult.getX();
					final float circleHalfWidth = circle.getHeight() / 2;
					// TODO 56 is a magic number
					final float newCircleResultX = (circleResult.getPivotX() - circleHalfWidth) -
							(originalResultTextX - circleHalfWidth) + 56;

					ValueAnimator va = ofFloat(0, 1);
					va.setDuration(500);
					va.addUpdateListener(new AnimatorUpdateListener() {
						public void onAnimationUpdate(ValueAnimator animation) {
							float value = (float) animation.getAnimatedValue();
							resultText.setAlpha(value);
							resultText.setX(originalResultTextX + (circleHalfWidth * value));
							circleResult.setX(originalCircleResultX - (newCircleResultX * value));
						}
					});
					va.start();
				}

				@Override
				public void onAnimationCancel(Animator animation) {

				}

				@Override
				public void onAnimationRepeat(Animator animation) {

				}
			});
			va.start();

			progress.setVisibility(GONE);
			circleResult.setImageResource(R.drawable.ic_cancel_circle);
			circleResult.setColorFilter(Color.RED);
		}
	}

	enum State {Initial, InProgress, Fail, Success}
}
