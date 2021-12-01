package com.example.deadline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddRemindersActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    EditText mEditTitle, mEditDescription, mEditDate, mEditTime;
    Spinner mSpinnerSubjects;
    Button mButtonAddReminder;
    //creating the object of type DrawView
    //in order to get the reference of the View
    private DrawView mPaint;
    //creating objects of type button
    private ImageButton mResetButton, mDetectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminders);

        //getting the reference of the views from their ids
        mPaint = findViewById(R.id.draw_view);

        mResetButton = findViewById(R.id.refreshImgButton);
        mDetectButton = findViewById(R.id.detectImgButton);

        mEditTitle = findViewById(R.id.titleReminder);
        mEditDescription = findViewById(R.id.descriptionReminder);
        mEditDate = findViewById(R.id.datePickerReminder);
        mEditTime = findViewById(R.id.timePickerReminder);

        mSpinnerSubjects = findViewById(R.id.spinnerSubjects);

        //reset draw view
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPaint.reset();
            }
        });

        //detect image
        mDetectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runTextRecognition();
            }
        });

        //pass the height and width of the custom view to the init method of the DrawView object
        ViewTreeObserver vto = mPaint.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                mPaint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = mPaint.getMeasuredWidth();
                int height = mPaint.getMeasuredHeight();
                mPaint.init(height, width);
            }
        });

        //pick date and time for reminder
        mEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        mEditTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        //set subjects for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSubjects.setAdapter(adapter);

        mButtonAddReminder = (Button) findViewById(R.id.addReminder);
        mButtonAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //info of reminder
                String title = mEditTitle.getText().toString();
                String subject = mSpinnerSubjects.getSelectedItem().toString();
                String date = mEditDate.getText().toString();
                String time = mEditTime.getText().toString();

                String description1 = mEditDescription.getText().toString() + " : " + subject;
                String description2 = time + " - " + date;


                if(!TextUtils.isEmpty(mEditTitle.getText().toString())){

                    DatabaseClass databaseClass = new DatabaseClass(AddRemindersActivity.this);
                    Integer ID = databaseClass.addReminders(title, description1, description2);
//                    showToast(ID);
                    //set alarm
                    setAlarm(ID, title, subject, date, time);

                    Intent intent = new Intent(AddRemindersActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }else {

                    Toast.makeText(AddRemindersActivity.this, "Please fill title", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void runTextRecognition() {
        int rotationDegree = 0;
        Bitmap bitmap = mPaint.detect();
        InputImage image = InputImage.fromBitmap(bitmap, rotationDegree);

        TextRecognizer textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        mDetectButton.setEnabled(false);
        textRecognizer.process(image)
                .addOnSuccessListener(
                        new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text texts) {
                                mDetectButton.setEnabled(true);
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                mDetectButton.setEnabled(true);
                                e.printStackTrace();
                            }
                        });

    }

    private void processTextRecognitionResult(Text texts) {
        mEditTitle = findViewById(R.id.titleReminder);
        String detectedText = "";

        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            showToast("No text found");
            return;
        }

        for (int i = 0; i < blocks.size(); i++) {
            List<Text.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    String detected_word = elements.get(k).getText().toString();
                    detectedText = detectedText + detected_word;
                }
            }
        }
        //make switch case
        detectedText = detectedText.toUpperCase();
        switch (detectedText){
            case "BT":
                detectedText = "BÀI TẬP";
                break;
            case "TT":
                detectedText = "THYẾT TRÌNH";
                break;
            case "BTL":
                detectedText = "BÀI TẬP LỚN";
                break;
            case "BC":
                detectedText = "BÁO CÁO";
                break;
            default:
                break;
        }

        mEditTitle.setText(detectedText);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mEditTime = findViewById(R.id.timePickerReminder);
        mEditTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String day_string = String.valueOf(dayOfMonth);
        if (day_string.length() == 1){
            day_string = "0" + day_string;
        }

        String month_string = String.valueOf(month + 1);
        if (month_string.length() == 1){
            month_string = "0" + month_string;
        }

        String datePicker = day_string + "/" + month_string + "/" + String.valueOf(year);

        mEditDate = findViewById(R.id.datePickerReminder);
        mEditDate.setText(datePicker);
    }


    private void setAlarm(Integer ID, String title, String subject, String date, String time){
        String year, month, day, hour, minute;

        //get time and date value
        day = date.substring(0, 2);
        month = date.substring(3, 5);
        year = date.substring(6, 10);

        hour = time.substring(0, 2);
        minute = time.substring(3, 5);

        //create a calendar object
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.valueOf(year));
        calendar.set(Calendar.MONTH, Integer.valueOf(month) - 1); //note
        calendar.set(Calendar.DATE, Integer.valueOf(day));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour)); //for 24-hours
        calendar.set(Calendar.MINUTE, Integer.valueOf(minute));
        calendar.set(Calendar.SECOND, 0);

        //create an intent to show notification
        Intent intent = new Intent(AddRemindersActivity.this, NotificationAlarm.class);
        intent.putExtra("title", title);
        intent.putExtra("subject", subject);
        intent.putExtra("ID", ID);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(AddRemindersActivity.this, ID,
                intent, PendingIntent.FLAG_UPDATE_CURRENT); // keep it but replace its extra data with what is in this new Intent

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

//        Toast.makeText(AddRemindersActivity.this,"Inserted successfully",Toast.LENGTH_SHORT).show();
//        showToast(String.valueOf(ID));
    }
}
