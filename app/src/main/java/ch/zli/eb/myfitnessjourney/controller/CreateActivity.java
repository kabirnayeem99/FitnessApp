package ch.zli.eb.myfitnessjourney.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.zli.eb.myfitnessjourney.R;
import ch.zli.eb.myfitnessjourney.model.Goal;

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

    // GOAL LIST VON SHARED PREFERENCES AND GOAL WHICH IS CREATE UPON SUBMISSION OF FORM
    Goal userGoal;
    ArrayList<Goal> goalList;

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


    public void createGoalButton(View v) throws ParseException {

        boolean validation = true;

        // REGEX FOR TIME INPUT
        String timeFormat = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
        Pattern timePattern = Pattern.compile(timeFormat);
        Matcher timeMatcher = timePattern.matcher(timeInput.getText().toString());

        // VALIDATION PROCESS NAME -> TIME -> CHECKBOXES -> START DATE -> END DATE
        if (nameInput.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            validation = false;
        } else if (!timeMatcher.matches()) {
            Toast.makeText(this, "Please enter a valid time. Format: hh:mm", Toast.LENGTH_LONG).show();
            validation = false;
        } else if (checkNo.isChecked() && checkYes.isChecked()) {
            Toast.makeText(this, "Please choose if you want reminders", Toast.LENGTH_LONG).show();
            validation = false;
        } else if (!checkYes.isChecked() && !checkNo.isChecked()) {
            Toast.makeText(this, "Please choose one option", Toast.LENGTH_LONG).show();
            validation = false;
        } else if (!validateDateInputs()) {
            Toast.makeText(this, "Please enter valid dates", Toast.LENGTH_LONG).show();
            validation = false;
        } else if (validation) {
            createGoalObject();
        }
    }

    private void createGoalObject() throws ParseException {
        // REQUIRED DATE FORMAT
        DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        dateFormatter.setLenient(false);

        String name = nameInput.getText().toString().trim();
        Date startDate = dateFormatter.parse(startDateInput.getText().toString());
        Date endDate = dateFormatter.parse(endDateInput.getText().toString());
        boolean reminders = checkYes.isChecked();
        LocalTime time = LocalTime.parse(timeInput.getText().toString().concat(":00"));

        userGoal = new Goal(name, time, reminders, startDate, endDate, false);
    }

    // VALIDATES START DATE AND END DATE INPUT
    public boolean validateDateInputs() throws ParseException {
        // REQUIRED DATE FORMAT
        DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        dateFormatter.setLenient(false);

        Date startDate;
        Date endDate;
        Date date = dateFormatter.parse(dateFormatter.format(new Date()));

        try {
            startDate = dateFormatter.parse(startDateInput.getText().toString());
            endDate = dateFormatter.parse(endDateInput.getText().toString());
        } catch(Exception e) {
            return false;
        }

        if (endDate.compareTo(startDate) > 0 || endDate.compareTo(startDate) == 0) {
            return true;
        } else {
            return false;
        }
    }
}