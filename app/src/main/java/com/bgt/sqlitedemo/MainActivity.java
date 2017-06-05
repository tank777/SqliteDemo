package com.bgt.sqlitedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bgt.sqlitedemo.localdb.PostsDatabaseHelper;
import com.bgt.sqlitedemo.localdb.localdb_model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.button)
    Button button;
    PostsDatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        helper = PostsDatabaseHelper.getInstance(this);
    }

    @OnClick(R.id.button)
    public void onViewClicked() {

        if (helper != null) {

            User user = new User(editText.getText().toString());
            String message = helper.saveUser(user);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
