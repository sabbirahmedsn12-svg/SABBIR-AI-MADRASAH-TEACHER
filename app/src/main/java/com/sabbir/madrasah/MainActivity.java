package com.sabbir.madrasah;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {

    LinearLayout root;
    String selectedBook = "আরবি কিতাব";
    int green = Color.rgb(18, 75, 55);
    int gold = Color.rgb(205, 164, 75);
    int cream = Color.rgb(247, 248, 242);

    String[][] books = {
        {"সরফ", "মীযানুস সরফ", "মুনশাইব", "ইলমুস সীগাহ", "শাফিয়া", "পাঞ্জ গঞ্জ"},
        {"নাহু", "নাহুমীর", "হেদায়াতুন নাহু", "কাফিয়া", "শরহে জামি", "মুখতাসারুল মা'আনী"},
        {"ফিকহে হানাফি", "নূরুল ইযাহ", "মুখতাসারুল কুদুরি", "কানযুদ দাকায়েক", "আল-হেদায়া", "শরহে বেকায়া", "ফাতাওয়া আলমগীরী"},
        {"উসুলুল ফিকহ", "উসুলুশ শাশী", "নূরুল আনওয়ার", "হাসামী", "মুসাল্লামুস সুবুত"},
        {"বালাগাত", "মুখতাসারুল মাআনী", "তালখীসুল মিফতাহ", "মুতাওয়াল"},
        {"মানতিক", "সুল্লামুল উলুম", "মীর কুতবি", "তাহযীব", "কুতবি"},
        {"তাফসির", "তাফসিরে জালালাইন", "তাফসিরে বায়যাবী", "মাদারিকুত তানযীল", "তাফসিরে ইবনে কাসীর"},
        {"হাদিস", "মিশকাতুল মাসাবীহ", "সহীহ বুখারি", "সহীহ মুসলিম", "সুনানে আবু দাউদ", "তিরমিযী", "নাসাঈ", "ইবনে মাজাহ"},
        {"আরবি সাহিত্য", "মাকামাতে হারিরী", "দিওয়ানুল হামাসা", "কালিলা ওয়া দিমনা"},
        {"ইলমুল ফারায়েজ", "সিরাজী", "শরহে সিরাজী"}
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHome();
    }

    GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(null, Typeface.BOLD);
        return t;
    }

    void setup() {
        ScrollView scroll = new ScrollView(this);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 18, 18, 25);
        root.setBackgroundColor(cream);
        scroll.addView(root);
        setContentView(scroll);
    }

    void showHome() {
        setup();

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setGravity(Gravity.CENTER);
        header.setPadding(20, 30, 20, 30);
        header.setBackground(shape(green, 25));

        TextView title = text("SABBIR AI", 30, gold, true);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = text("MADRASAH TEACHER", 17, Color.WHITE, true);
        subtitle.setGravity(Gravity.CENTER);

        TextView welcome = text("আপনার ডিজিটাল ইলমি উস্তাদ", 14, Color.WHITE, false);
        welcome.setGravity(Gravity.CENTER);

        header.addView(title);
        header.addView(subtitle);
        header.addView(welcome);
        root.addView(header);

        TextView heading = text("📚 কিতাব ও ইলমি বিভাগ", 21, green, true);
        heading.setPadding(0, 25, 0, 15);
        root.addView(heading);

        for (String[] category : books) {
            TextView section = text("◆ " + category[0], 18, green, true);
            section.setPadding(5, 17, 5, 8);
            root.addView(section);

            for (int i = 1; i < category.length; i++) {
                String book = category[i];
                Button b = new Button(this);
                b.setText("📖  " + book + "     ›");
                b.setTextSize(15);
                b.setTextColor(green);
                b.setAllCaps(false);
                b.setGravity(Gravity.CENTER_VERTICAL);
                b.setBackground(shape(Color.WHITE, 16));
                b.setPadding(15, 5, 15, 5);

                LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(-1, 58);
                lp.setMargins(0, 5, 0, 5);
                root.addView(b, lp);

                b.setOnClickListener(v -> {
                    selectedBook = book;
                    showChat();
                });
            }
        }

        TextView note = text(
            "নোট: কিতাবের তালিকা প্রাথমিক। নির্ভরযোগ্য কিতাবের তথ্য যুক্ত করে লাইব্রেরি আরও সমৃদ্ধ করা হবে।",
            13, Color.DKGRAY, false
        );
        note.setPadding(5, 20, 5, 10);
        root.addView(note);
    }

    void showChat() {
        setup();

        Button back = new Button(this);
        back.setText("‹  কিতাবের তালিকায় ফিরে যান");
        back.setAllCaps(false);
        back.setTextColor(green);
        back.setBackgroundColor(Color.TRANSPARENT);
        back.setGravity(Gravity.CENTER_VERTICAL);
        root.addView(back);
        back.setOnClickListener(v -> showHome());

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(18, 22, 18, 22);
        header.setGravity(Gravity.CENTER);
        header.setBackground(shape(green, 20));

        TextView bookTitle = text(selectedBook, 23, gold, true);
        bookTitle.setGravity(Gravity.CENTER);

        TextView sub = text("SABBIR AI MADRASAH TEACHER", 13, Color.WHITE, false);
        sub.setGravity(Gravity.CENTER);

        header.addView(bookTitle);
        header.addView(sub);
        root.addView(header);

        TextView greeting = text(
            "আসসালামু আলাইকুম!\n\n" +
            "আপনি এখন " + selectedBook + " বিভাগে আছেন।\n" +
            "আপনার প্রশ্ন লিখুন। ইনশাআল্লাহ সহজ বাংলায় ব্যাখ্যা দেওয়ার ব্যবস্থা করা হবে।",
            16, green, false
        );
        greeting.setPadding(18, 22, 18, 22);
        greeting.setBackground(shape(Color.WHITE, 18));
        LinearLayout.LayoutParams gp = new LinearLayout.LayoutParams(-1, -2);
        gp.setMargins(0, 20, 0, 15);
        root.addView(greeting, gp);

        TextView label = text("✍️ আপনার ইলমি প্রশ্ন", 18, green, true);
        label.setPadding(0, 15, 0, 10);
        root.addView(label);

        EditText question = new EditText(this);
        question.setHint("আরবি ইবারত অথবা প্রশ্ন লিখুন...");
        question.setTextSize(16);
        question.setMinLines(4);
        question.setGravity(Gravity.TOP);
        question.setPadding(15, 15, 15, 15);
        question.setBackground(shape(Color.WHITE, 15));
        root.addView(question);

        Button ask = new Button(this);
        ask.setText("AI উস্তাদকে প্রশ্ন করুন");
        ask.setTextColor(Color.WHITE);
        ask.setTextSize(16);
        ask.setAllCaps(false);
        ask.setBackground(shape(green, 18));

        LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(-1, 58);
        ap.setMargins(0, 15, 0, 15);
        root.addView(ask, ap);

        TextView answer = text(
            "📖 শিক্ষকের উত্তর\n\n" +
            "এখানে AI-এর উত্তর, অনুবাদ, ই'রাব, ব্যাখ্যা ও কিতাবের রেফারেন্স দেখানো হবে।",
            16, Color.DKGRAY, false
        );
        answer.setPadding(18, 20, 18, 20);
        answer.setBackground(shape(Color.WHITE, 18));
        root.addView(answer);

        ask.setOnClickListener(v -> {
            String q = question.getText().toString().trim();
            if (q.isEmpty()) {
                question.setError("প্রশ্ন লিখুন");
                return;
            }

            answer.setText(
                "আপনার প্রশ্ন:\n" + q +
                "\n\nবিষয়: " + selectedBook +
                "\n\nAI সার্ভার এখনো সংযুক্ত করা হয়নি। পরবর্তী ধাপে Gemini AI সংযুক্ত করা হবে।"
            );
        });
    }
                       }
