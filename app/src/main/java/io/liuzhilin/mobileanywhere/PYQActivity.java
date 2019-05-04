package io.liuzhilin.mobileanywhere;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import io.liuzhilin.mobileanywhere.adapter.PYQAdapter;

public class PYQActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pyq);
        RecyclerView recyclerView = findViewById(R.id.pyq_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new PYQAdapter(this));
    }

    public static void startActivity(Context context){
        Intent intent = new Intent(context,PYQActivity.class);
        context.startActivity(intent);
    }
}
