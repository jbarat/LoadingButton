package com.jbarat.loadingbutton.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.jbarat.LoadingButton;
import com.jbarat.loadingbutton.R;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LoadingButton loadingButton = (LoadingButton) findViewById(R.id.button);
		Button successButton = (Button) findViewById(R.id.button2);
		Button failButton = (Button) findViewById(R.id.button3);

		loadingButton.setOnClickListener(v -> {
			successButton.setVisibility(VISIBLE);
			failButton.setVisibility(VISIBLE);
		});

		successButton.setOnClickListener(v -> loadingButton.success());
		failButton.setOnClickListener(v -> loadingButton.failure());

		LoadingButton loadingButton2 = (LoadingButton) findViewById(R.id.button4);
		Button successButton2 = (Button) findViewById(R.id.button5);
		Button failButton2 = (Button) findViewById(R.id.button6);

		loadingButton2.setOnClickListener(v -> {
			successButton2.setVisibility(VISIBLE);
			failButton2.setVisibility(VISIBLE);
		});

		successButton2.setOnClickListener(v -> loadingButton2.success());
		failButton2.setOnClickListener(v -> loadingButton2.failure());
	}
}
