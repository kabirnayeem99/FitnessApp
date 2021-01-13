package ch.zli.eb.myfitnessjourney;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

public class ListActivity extends AppCompatActivity {

    // VIEW ELEMENT AS PROPERTY
    ListView goalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // ASSIGNING VIEW ELEMENT TO PROPERTY
        goalList = findViewById(R.id.goalListUi);
    }
}