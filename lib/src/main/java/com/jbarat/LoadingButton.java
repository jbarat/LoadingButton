/*
 * Copyright (C) 2017 Jozsef Barat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jbarat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

import static android.animation.ValueAnimator.ofFloat;
import static com.jbarat.LoadingButton.State.Fail;
import static com.jbarat.LoadingButton.State.InProgress;
import static com.jbarat.LoadingButton.State.InProgressAfterFailResult;
import static com.jbarat.LoadingButton.State.InProgressAfterSuccessResult;
import static com.jbarat.LoadingButton.State.Initial;
import static com.jbarat.LoadingButton.State.Success;

public class LoadingButton extends FrameLayout {

    enum State {Initial, InProgress, InProgressAfterSuccessResult, InProgressAfterFailResult, Fail, Success}

    private static final int CIRCLE_TO_TEXT_DURATION_DEFAULT = 500;

    private State state;

    private LoadingButtonInitialOnClickListener loadingButtonInitialOnClickListener;
    private LoadingButtonSuccessOnClickListener loadingButtonSuccessOnClickListener;
    private LoadingButtonFailOnClickListener loadingButtonFailOnClickListener;

    private ImageView circleResult;
    private ProgressBar progress;
    private Button button;
    private ImageView circle;
    private TextView resultText;

    private int circleToTextDuration;
    private int failColor;
    private int successColor;

    private String successText;
    private String failText;

    private float originalResultTextX;
    private float originalCircleResultX;
    private float newCircleResultX;

    public LoadingButton(Context context) {
        super(context);
        init(context, null);
    }

    public LoadingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoadingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setOnInitialClickListener(LoadingButtonInitialOnClickListener listener) {
        this.loadingButtonInitialOnClickListener = listener;
    }

    public void setOnSuccessClickListener(LoadingButtonSuccessOnClickListener listener) {
        this.loadingButtonSuccessOnClickListener = listener;
    }

    public void setOnFailOnClickListener(LoadingButtonFailOnClickListener listener) {
        this.loadingButtonFailOnClickListener = listener;
    }

    public void success() {
        if (!isInProgressOrFinished()) {
            state = InProgressAfterSuccessResult;

            ValueAnimator va = ofFloat(0, 1);
            va.setDuration(circleToTextDuration);
            va.addUpdateListener(new CircleAndResultCircleAlphaAnimatorUpdateListener());
            va.addListener(new CircleToResultCircleWithTextAnimatorListener());
            va.start();

            progress.setVisibility(GONE);
            circleResult.setImageResource(R.drawable.ic_check_circle);
            circleResult.setColorFilter(successColor);
            resultText.setText(successText);
        }
    }

    public void failure() {
        if (!isInProgressOrFinished()) {
            state = InProgressAfterFailResult;

            ValueAnimator va = ofFloat(0, 1);
            va.setDuration(circleToTextDuration);
            va.addUpdateListener(new CircleAndResultCircleAlphaAnimatorUpdateListener());
            va.addListener(new CircleToResultCircleWithTextAnimatorListener());
            va.start();

            progress.setVisibility(GONE);
            circleResult.setImageResource(R.drawable.ic_cancel_circle);
            circleResult.setColorFilter(failColor);
            resultText.setText(failText);
        }
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

        button.setOnClickListener(new ButtonOnClickListener());
        circleResult.setOnClickListener(new ResultOnClickListener());
        resultText.setOnClickListener(new ResultOnClickListener());

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

        failColor= typedArray.getColor(R.styleable.LoadingButton_lb_failure_color,Color.RED);
        successColor= typedArray.getColor(R.styleable.LoadingButton_lb_success_color,Color.GREEN);

        circleToTextDuration = typedArray.getInteger(R.styleable.LoadingButton_lb_anim_duration, CIRCLE_TO_TEXT_DURATION_DEFAULT);
        typedArray.recycle();
    }

    private void returnToInProgress() {
        state = InProgress;

        ValueAnimator va = ofFloat(1, 0);
        va.setDuration(circleToTextDuration);
        va.addUpdateListener(new MoveResultTextAndCircleAnimatorUpdateListener());
        va.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                ValueAnimator va = ofFloat(1, 0);
                va.setDuration(circleToTextDuration);
                va.addUpdateListener(new CircleAndResultCircleAlphaAnimatorUpdateListener());
                va.addListener(new ShowProgressBarOnFinishAnimationListener());
                va.start();
            }

        });
        va.start();
    }

    private boolean isInProgressOrFinished() {
        return state == Success || state == Fail || state == InProgressAfterSuccessResult || state == InProgressAfterFailResult;
    }

    private class ButtonOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (state == Initial) {
                state = InProgress;
                button.setText("");
                loadingButtonInitialOnClickListener.onClick(LoadingButton.this);

                Animator circularReveal = ViewAnimationUtils.createCircularReveal(button,
                        (int) button.getPivotX(),
                        (int) button.getPivotY(),
                        button.getWidth(),
                        0);
                circularReveal.addListener(new ShowProgressBarOnFinishAnimationListener());
                circularReveal.setDuration(800).start();
            }
        }
    }

    private class ShowProgressBarOnFinishAnimationListener extends AnimatorListenerAdapter {

        @Override
        public void onAnimationEnd(Animator animation) {
            button.setVisibility(GONE);
            progress.setVisibility(VISIBLE);
            progress.setProgress(0);
        }
    }

    private class CircleAndResultCircleAlphaAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            circle.setAlpha(1 - value);
            circleResult.setAlpha(value);
        }
    }

    private class CircleToResultCircleWithTextAnimatorListener extends AnimatorListenerAdapter {

        @Override
        public void onAnimationEnd(Animator animation) {
            originalResultTextX = resultText.getX();
            originalCircleResultX = circleResult.getX();
            final float circleHalfWidth = circle.getHeight() / 2;
            // TODO 56 is a magic number
            newCircleResultX = (circleResult.getPivotX() - circleHalfWidth) -
                    (originalResultTextX - circleHalfWidth) + 56;

            ValueAnimator va = ofFloat(0, 1);
            va.setDuration(circleToTextDuration);
            va.addListener(new ResultToFinalStateAnimatorListener());
            va.addUpdateListener(new MoveResultTextAndCircleAnimatorUpdateListener());
            va.start();
        }
    }

    private class ResultToFinalStateAnimatorListener extends AnimatorListenerAdapter {

        @Override
        public void onAnimationEnd(Animator animation) {
            if (state == InProgressAfterFailResult) {
                state = Fail;
            } else if (state == InProgressAfterSuccessResult) {
                state = Success;
            }
        }
    }

    private class ResultOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (state == Success || state == Fail) {
                if (state == Success) {
                    loadingButtonSuccessOnClickListener.onClick(LoadingButton.this);
                } else {
                    loadingButtonFailOnClickListener.onClick(LoadingButton.this);
                }

                LoadingButton.this.returnToInProgress();
            }
        }
    }

    private class MoveResultTextAndCircleAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            resultText.setAlpha(value);
            resultText.setX(originalResultTextX + (circle.getHeight() / 2 * value));
            circleResult.setX(originalCircleResultX - (newCircleResultX * value));
        }
    }
}
