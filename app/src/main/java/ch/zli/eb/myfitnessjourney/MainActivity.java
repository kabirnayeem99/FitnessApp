package ch.zli.eb.myfitnessjourney;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // VIEW ELEMENTS AS PROPERTIES
    ProgressBar progressBar;

    TextView stepsStatus;
    TextView todaysGoalDesc;

    Button historyButton;
    Button progressButton;
    Button currentButton;
    Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ASSIGNING VIEW ELEMENTS TO PROPERTIES
        progressBar = findViewById(R.id.progressbar);

        stepsStatus = findViewById(R.id.stepsStatus);
        todaysGoalDesc = findViewById(R.id.goalDesc);

        historyButton = findViewById(R.id.historyButton);
        progressButton = findViewById(R.id.progressButton);
        currentButton = findViewById(R.id.currentButton);
        createButton = findViewById(R.id.createButton);

    }
}