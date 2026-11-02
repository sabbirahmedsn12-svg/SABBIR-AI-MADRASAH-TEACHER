package com.sabbir.madrasah;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {

    LinearLayout mainLayout;
    EditText questionInput;
    TextView answerText;
    TextView subjectText;

    int green = Color.rgb(18, 75, 55);
    int gold = Color.rgb(205, 164, 75);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScrollView scroll = new ScrollView(this);
        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(22, 25, 22, 25);
        mainLayout.setBackgroundColor(Color.rgb(246, 248, 243));

        scroll.addView(mainLayout);
        setContentView(scroll);

        createHeader();
        createSubjects();
        createQuestionBox();
        createAnswerBox();
    }

    void createHeader() {

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(22, 28, 22, 28);
        header.setGravity(Gravity.CENTER);
        header.setBackgroundColor(green);

        TextView title = new TextView(this);
        title.setText("SABBIR AI");
        title.setTextColor(gold);
        title.setTextSize(28);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = new TextView(this);
        subtitle.setText("MADRASAH TEACHER");
        subtitle.setTextColor(Color.WHITE);
        subtitle.setTextSize(17);
        subtitle.setGravity(Gravity.CENTER);

        TextView welcome = new TextView(this);
        welcome.setText("আপনার ডিজিটাল উস্তাদ");
        welcome.setTextColor(Color.WHITE);
        welcome.setTextSize(14);
        welcome.setGravity(Gravity.CENTER);
        welcome.setPadding(0, 12, 0, 0);

        header.addView(title);
        header.addView(subtitle);
        header.addView(welcome);

        mainLayout.addView(header);
    }

    void createSubjects() {

        addHeading("📚 ইলমি বিভাগ নির্বাচন করুন");

        String[] subjects = {
            "আরবি কিতাব",
            "নাহু",
            "সরফ",
            "ফিকহে হানাফি",
            "উসুলুল ফিকহ",
            "বালাগাত",
            "মানতিক",
            "আরবি সাহিত্য",
            "তাফসির",
            "হাদিস",
            "ইলমুল ফারায়েজ"
        };

        for (String subject : subjects) {

            Button button = new Button(this);
            button.setText(subject);
            button.setTextSize(15);
            button.setTextColor(green);
            button.setAllCaps(false);

            button.setOnClickListener(v -> {
                subjectText.setText("নির্বাচিত বিষয়: " + subject);
            });

            mainLayout.addView(button);
        }

    }

    void createQuestionBox() {

        addHeading("✍️ আপনার প্রশ্ন লিখুন");

        subjectText = new TextView(this);
        subjectText.setText("নির্বাচিত বিষয়: আরবি কিতাব");
        subjectText.setTextSize(14);
        subjectText.setTextColor(green);

        mainLayout.addView(subjectText);

        questionInput = new EditText(this);
        questionInput.setHint("আরবি ইবারত অথবা বাংলা প্রশ্ন লিখুন...");
        questionInput.setTextSize(16);
        questionInput.setMinLines(4);
        questionInput.setGravity(Gravity.TOP);
        questionInput.setPadding(15, 15, 15, 15);
        questionInput.setBackgroundColor(Color.WHITE);

        mainLayout.addView(questionInput);

        Button askButton = new Button(this);
        askButton.setText("AI উস্তাদকে প্রশ্ন করুন");
        askButton.setTextColor(Color.WHITE);
        askButton.setTextSize(16);
        askButton.setAllCaps(false);
        askButton.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(green)
        );

        askButton.setOnClickListener(v -> {

            String question = questionInput.getText().toString().trim();

            if (question.isEmpty()) {
                questionInput.setError("প্রথমে প্রশ্ন লিখুন");
                return;
            }

            answerText.setText(
                "আপনার প্রশ্ন গ্রহণ করা হয়েছে।\n\n" +
                "AI শিক্ষক সার্ভার সংযুক্ত করার পর এখানে " +
                "আরবি অনুবাদ, ব্যাখ্যা ও রেফারেন্সসহ উত্তর আসবে।"
            );

        });

        mainLayout.addView(askButton);
    }

    void createAnswerBox() {

        addHeading("📖 শিক্ষকের উত্তর");

        answerText = new TextView(this);
        answerText.setText(
            "আসসালামু আলাইকুম!\n\n" +
            "আমি SABBIR AI MADRASAH TEACHER।\n" +
            "আপনার ইলমি প্রশ্নের অপেক্ষায় আছি।"
        );

        answerText.setTextSize(16);
        answerText.setTextColor(Color.DKGRAY);
        answerText.setPadding(18, 20, 18, 20);
        answerText.setBackgroundColor(Color.WHITE);

        mainLayout.addView(answerText);
    }

    void addHeading(String text) {

        TextView heading = new TextView(this);
        heading.setText(text);
        heading.setTextSize(19);
        heading.setTextColor(green);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setPadding(0, 24, 0, 12);

        mainLayout.addView(heading);
    }
          }
