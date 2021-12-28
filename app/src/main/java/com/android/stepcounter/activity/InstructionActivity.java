package com.android.stepcounter.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.stepcounter.R;
import com.android.stepcounter.adpter.InstructionAdapter;
import com.android.stepcounter.model.InstructionModel;
import com.android.stepcounter.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;


public class InstructionActivity extends AppCompatActivity {
    Toolbar mToolbar;
    RecyclerView mRvInstruction;
    InstructionAdapter instructionAdapter;
    ArrayList<InstructionModel> instructionModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Instruction");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Gson gson = new Gson();

        try {
            InputStream stream = getApplicationContext().getAssets().open("instruction.txt");

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String tContents = new String(buffer);
            Type arrayListTypeToken = new TypeToken<ArrayList<InstructionModel>>() {
            }.getType();

            instructionModels = gson.fromJson(tContents, arrayListTypeToken);

            Logger.e(instructionModels.size() + "databse");

        } catch (IOException e) {
            // Handle exceptions here
        }

        mRvInstruction = findViewById(R.id.rvInstruction);
        instructionAdapter = new InstructionAdapter(this, instructionModels);
        mRvInstruction.setHasFixedSize(true);
        mRvInstruction.setLayoutManager(new LinearLayoutManager(this));
        mRvInstruction.setAdapter(instructionAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}