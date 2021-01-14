package ch.zli.eb.myfitnessjourney.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import ch.zli.eb.myfitnessjourney.R;

public class CreateActivity extends AppCompatActivity {

    // VIEW ELEMENTS AS PROPERTIES
    EditText nameInput;
    EditText timeInput;

    CheckBox checkYes;
    CheckBox checkNo;

    EditText startDateInput;
    EditText endDateInput;

    Button createButton;
    Button clearButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // ASSIGNING VIEW ELEMENTS TO PROPERTIES
        nameInput = findViewById(R.id.nameInput);
        timeInput = findViewById(R.id.timeInput);

        checkYes = findViewById(R.id.checkYes);
        checkNo = findViewById(R.id.checkNo);

        startDateInput = findViewById(R.id.startDate);
        endDateInput = findViewById(R.id.endDate);

        createButton = findViewById(R.id.createNewButton);
        clearButton = findViewById(R.id.clearButton);

    }

    // HANDLER FOR clearButton
    public void clearInputFields(View v) {
        nameInput.getText().clear();
        timeInput.getText().clear();

        if (checkYes.isChecked()) {
            checkYes.toggle();
        }
        if (checkNo.isChecked()) {
            checkNo.toggle();
        }

        startDateInput.getText().clear();
        endDateInput.getText().clear();
    }

}