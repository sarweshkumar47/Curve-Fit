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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.makesense.labs.curvefitexample.listItem.ActivityItem;
import com.makesense.labs.curvefitexample.listItem.ExampleListRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        ExampleListRecyclerViewAdapter.RecyclerViewClickListener {

    private ExampleListRecyclerViewAdapter exampleListRecyclerViewAdapter;
    List<ActivityItem> exampleActivityList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        exampleListRecyclerViewAdapter = new ExampleListRecyclerViewAdapter(this,
                exampleActivityList, this);
        recyclerView.setAdapter(exampleListRecyclerViewAdapter);

        prepareExampleActivityList();
    }

    private void prepareExampleActivityList() {
        exampleActivityList.add(new ActivityItem("BasicDemo"));
        exampleActivityList.add(new ActivityItem("AddMultipleLatLngPointsExample"));
        exampleActivityList.add(new ActivityItem("DrawCurveAndPolylineTogetherExample"));
        exampleActivityList.add(new ActivityItem("HandleOrientationChangeExample"));
        exampleActivityList.add(new ActivityItem("TestLibraryFeatures"));
        exampleListRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityItemClick(int position) {
        switch (position) {
            case 0:
                startActivity(new Intent(this, BasicDemoActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, AddMultipleLatLngPointsExampleActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, DrawCurveAndPolylineTogetherExampleActivity.class));
                break;
            case 3:
                startActivity(new Intent(this, HandleOrientationChangeExampleActivity.class));
                break;
            case 4:
                startActivity(new Intent(this, TestLibraryFeaturesActivity.class));
                break;
        }
    }
}