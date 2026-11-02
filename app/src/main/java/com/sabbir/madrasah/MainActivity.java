package com.sabbir.madrasah;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.*;
import org.json.JSONObject;
import org.json.JSONArray;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    int green = Color.rgb(18, 75, 55);
    int gold = Color.rgb(205, 164, 75);
    int cream = Color.rgb(247, 248, 242);

    String selectedBook = "আরবি কিতাব";

    LinearLayout root, messages;
    EditText questionInput;
    Button sendButton;
    ScrollView chatScroll;

    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());

    JSONArray history = new JSONArray();

    String SERVER_URL =
        "https://sabbir-madrasah-ai-server2.onrender.com/ask";

    String[][] books = {
        {"সরফ", "মীযানুস সরফ", "মুনশাইব", "ইলমুস সীগাহ", "শাফিয়া", "পাঞ্জ গঞ্জ"},
        {"নাহু", "নাহুমীর", "হেদায়াতুন নাহু", "কাফিয়া", "শরহে জামি"},
        {"ফিকহে হানাফি", "নূরুল ইযাহ", "মুখতাসারুল কুদুরি", "কানযুদ দাকায়েক", "আল-হেদায়া", "শরহে বেকায়া", "ফাতাওয়া আলমগীরী"},
        {"উসুলুল ফিকহ", "উসুলুশ শাশী", "নূরুল আনওয়ার", "হাসামী"},
        {"বালাগাত", "মুখতাসারুল মাআনী", "তালখীসুল মিফতাহ", "মুতাওয়াল"},
        {"মানতিক", "সুল্লামুল উলুম", "মীর কুতবি", "তাহযীব"},
        {"তাফসির", "তাফসিরে জালালাইন", "তাফসিরে বায়যাবী", "মাদারিকুত তানযীল"},
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

    TextView makeText(String value, int size, int color, boolean bold) {
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
        history = new JSONArray();
        setup();

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setGravity(Gravity.CENTER);
        header.setPadding(20, 30, 20, 30);
        header.setBackground(shape(green, 25));

        TextView title = makeText("SABBIR AI", 30, gold, true);
        title.setGravity(Gravity.CENTER);

        TextView sub = makeText(
            "MADRASAH TEACHER", 17, Color.WHITE, true
        );
        sub.setGravity(Gravity.CENTER);

        TextView welcome = makeText(
            "আপনার ডিজিটাল ইলমি উস্তাদ", 14, Color.WHITE, false
        );
        welcome.setGravity(Gravity.CENTER);

        header.addView(title);
        header.addView(sub);
        header.addView(welcome);
        root.addView(header);

        TextView heading = makeText(
            "📚 কিতাব ও ইলমি বিভাগ", 21, green, true
        );
        heading.setPadding(0, 25, 0, 15);
        root.addView(heading);

        for (String[] category : books) {

            TextView section = makeText(
                "◆ " + category[0], 18, green, true
            );
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

                LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(-1, 58);
                lp.setMargins(0, 5, 0, 5);

                root.addView(b, lp);

                b.setOnClickListener(v -> {
                    selectedBook = book;
                    history = new JSONArray();
                    showChat();
                });
            }
        }
    }

    void showChat() {
        setup();

        Button back = new Button(this);
        back.setText("‹  কিতাবের তালিকায় ফিরে যান");
        back.setAllCaps(false);
        back.setTextColor(green);
        back.setBackgroundColor(Color.TRANSPARENT);
        root.addView(back);

        back.setOnClickListener(v -> showHome());

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setGravity(Gravity.CENTER);
        header.setPadding(15, 20, 15, 20);
        header.setBackground(shape(green, 20));

        TextView bookTitle = makeText(
            selectedBook, 23, gold, true
        );
        bookTitle.setGravity(Gravity.CENTER);

        TextView sub = makeText(
            "SABBIR AI MADRASAH TEACHER",
            13, Color.WHITE, false
        );
        sub.setGravity(Gravity.CENTER);

        header.addView(bookTitle);
        header.addView(sub);
        root.addView(header);

        chatScroll = new ScrollView(this);

        messages = new LinearLayout(this);
        messages.setOrientation(LinearLayout.VERTICAL);
        messages.setPadding(4, 15, 4, 15);

        chatScroll.addView(messages);

        LinearLayout.LayoutParams cp =
            new LinearLayout.LayoutParams(-1, 0, 1);

        root.addView(chatScroll, cp);

        addMessage(
            "আসসালামু আলাইকুম!\nআমি আপনার ডিজিটাল ইলমি উস্তাদ।\n" +
            selectedBook + " সম্পর্কে আপনার প্রশ্ন লিখুন।",
            false
        );

        questionInput = new EditText(this);
        questionInput.setHint("আপনার প্রশ্ন লিখুন...");
        questionInput.setTextSize(16);
        questionInput.setMinLines(2);
        questionInput.setMaxLines(5);
        questionInput.setGravity(Gravity.TOP);
        questionInput.setPadding(15, 12, 15, 12);
        questionInput.setBackground(shape(Color.WHITE, 18));

        root.addView(questionInput);

        sendButton = new Button(this);
        sendButton.setText("➤  AI উস্তাদকে পাঠান");
        sendButton.setTextColor(Color.WHITE);
        sendButton.setTextSize(16);
        sendButton.setAllCaps(false);
        sendButton.setBackground(shape(green, 18));

        LinearLayout.LayoutParams bp =
            new LinearLayout.LayoutParams(-1, 55);
        bp.setMargins(0, 10, 0, 5);

        root.addView(sendButton, bp);

        sendButton.setOnClickListener(v -> sendQuestion());
    }

    void addMessage(String value, boolean user) {

        TextView bubble = makeText(
            (user ? "আপনি\n\n" : "📖 AI উস্তাদ\n\n") + value,
            16,
            user ? Color.WHITE : green,
            false
        );

        bubble.setPadding(16, 15, 16, 15);
        bubble.setBackground(
            shape(user ? green : Color.WHITE, 18)
        );

        LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(
                -1, -2
            );

        lp.setMargins(
            user ? 35 : 0,
            6,
            user ? 0 : 35,
            10
        );

        messages.addView(bubble, lp);

        chatScroll.post(() ->
            chatScroll.fullScroll(ScrollView.FOCUS_DOWN)
        );
    }

    void sendQuestion() {

        String question =
            questionInput.getText().toString().trim();

        if (question.isEmpty()) {
            questionInput.setError("প্রশ্ন লিখুন");
            return;
        }

        if (question.length() > 12000) {
            questionInput.setError("প্রশ্নটি সংক্ষিপ্ত করুন");
            return;
        }

        addMessage(question, true);

        questionInput.setText("");
        sendButton.setEnabled(false);
        sendButton.setText("উত্তর তৈরি হচ্ছে...");

        TextView loading = makeText(
            "AI উস্তাদ উত্তর তৈরি করছেন, অপেক্ষা করুন...",
            14, green, false
        );

        messages.addView(loading);

        executor.execute(() -> {

            String result = null;
            String error = null;

            try {

                URL url = new URL(SERVER_URL);
                HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setConnectTimeout(30000);
                connection.setReadTimeout(120000);
                connection.setDoOutput(true);

                connection.setRequestProperty(
                    "Content-Type", "application/json; charset=UTF-8"
                );

                JSONObject body = new JSONObject();

                body.put("question", question);
                body.put("subject", selectedBook);
                body.put("history", history);

                OutputStream os = connection.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                int status = connection.getResponseCode();

                InputStream stream =
                    status >= 200 && status < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream();

                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stream, "UTF-8")
                );

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                JSONObject data =
                    new JSONObject(response.toString());

                if (status >= 200 && status < 300) {

                    result = data.optString(
                        "answer", "উত্তর পাওয়া যায়নি।"
                    );

                } else {

                    error = data.optString(
                        "error", "সার্ভারে সমস্যা হয়েছে।"
                    );
                }

                connection.disconnect();

            } catch (Exception e) {
                error = "সংযোগে সমস্যা হয়েছে। ইন্টারনেট পরীক্ষা করে আবার চেষ্টা করুন।";
            }

            final String finalResult = result;
            final String finalError = error;

            handler.post(() -> {

                messages.removeView(loading);

                sendButton.setEnabled(true);
                sendButton.setText("➤  AI উস্তাদকে পাঠান");

                if (finalResult != null) {

                    try {
                        JSONObject userItem = new JSONObject();
                        userItem.put("role", "user");
                        userItem.put("text", question);
                        history.put(userItem);

                        JSONObject modelItem = new JSONObject();
                        modelItem.put("role", "model");
                        modelItem.put("text", finalResult);
                        history.put(modelItem);

                    } catch (Exception ignored) {}

                    addMessage(finalResult, false);

                } else {

                    addMessage(
                        "দুঃখিত!\n" + finalError,
                        false
                    );
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
    }
