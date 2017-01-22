package com.jbarat.loadingbutton.demo;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jbarat.LoadingButton;
import com.jbarat.loadingbutton.R;

import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		View content = findViewById(R.id.activity_main);
		LoadingButton loadingButton = (LoadingButton) findViewById(R.id.button);
		Button successButton = (Button) findViewById(R.id.button2);
		Button failButton = (Button) findViewById(R.id.button3);

		loadingButton.setOnInitialClickListener(v -> {
			successButton.setVisibility(VISIBLE);
			failButton.setVisibility(VISIBLE);
		});

		successButton.setOnClickListener(v -> loadingButton.success());
		failButton.setOnClickListener(v -> loadingButton.failure());

		loadingButton.setOnSuccessClickListener(view -> Snackbar.make(content, "Success Clicked", LENGTH_SHORT).show());
		loadingButton.setOnFailOnClickListener(view -> Snackbar.make(content, "Fail Clicked", LENGTH_SHORT).show());
	}
}
