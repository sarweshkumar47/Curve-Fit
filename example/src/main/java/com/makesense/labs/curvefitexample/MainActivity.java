/*
 * Copyright 2018 Sarweshkumar C R
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.makesense.labs.curvefitexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button firstExampleButton = findViewById(R.id.firstExampleButton);
        Button secondExampleButton = findViewById(R.id.secondExampleButton);
        Button thirdExampleButton = findViewById(R.id.thirdExampleButton);

        firstExampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent firstExampleActivity = new Intent(MainActivity.this,
                        FirstExampleActivity.class);
                startActivity(firstExampleActivity);
            }
        });

        secondExampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondExampleActivity = new Intent(MainActivity.this,
                        SecondExampleActivity.class);
                startActivity(secondExampleActivity);
            }
        });

        thirdExampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent thirdExampleActivity = new Intent(MainActivity.this,
                        ThirdExampleActivity.class);
                startActivity(thirdExampleActivity);
            }
        });
    }
}
