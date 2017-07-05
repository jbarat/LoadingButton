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
package com.jbarat.loadingbutton.demo;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jbarat.LoadingButton;
import com.jbarat.loadingbutton.R;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    private Button successButton;
    private Button failButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View content = findViewById(R.id.activity_main);
        LoadingButton loadingButton = (LoadingButton) findViewById(R.id.loadingButton);
        successButton = (Button) findViewById(R.id.successButton);
        failButton = (Button) findViewById(R.id.failButton);

        loadingButton.setOnInitialClickListener(v -> {
            successButton.setVisibility(VISIBLE);
            failButton.setVisibility(VISIBLE);
        });

        successButton.setOnClickListener(v -> {
            loadingButton.success();
            hideButtons();
        });
        failButton.setOnClickListener(v -> {
            loadingButton.failure();
            hideButtons();
        });

        loadingButton.setOnSuccessClickListener(view -> {
            Snackbar.make(content, "Success Clicked", LENGTH_SHORT).show();
            showButtons();
        });
        loadingButton.setOnFailOnClickListener(view -> {
            Snackbar.make(content, "Fail Clicked", LENGTH_SHORT).show();
            showButtons();
        });
    }

    private void showButtons() {
        successButton.setVisibility(VISIBLE);
        failButton.setVisibility(VISIBLE);
    }

    private void hideButtons() {
        successButton.setVisibility(INVISIBLE);
        failButton.setVisibility(INVISIBLE);
    }
}
