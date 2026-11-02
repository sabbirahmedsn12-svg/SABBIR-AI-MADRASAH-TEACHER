package com.sabbir.madrasah;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    private final String SERVER_URL =
            "https://sabbir-madrasah-ai-server2.onrender.com/ask";

    private final int GREEN = Color.rgb(0, 85, 52);
    private final int GOLD = Color.rgb(218, 174, 75);
    private final int CREAM = Color.rgb(250, 247, 235);

    private LinearLayout root, messages;
    private EditText input;
    private ScrollView chatScroll;
    private String selectedBook = "";
    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private final String[][] books = {
        {"সারফ", "মিজানুস সরফ", "মুনশাইব", "ইলমুস সিগাহ", "শাফিয়া", "পাঞ্জ গঞ্জ"},
        {"নাহু", "নাহুমীর", "হেদায়াতুন নাহু", "কাফিয়া", "শরহে জামি", "মুখতাসারুল মা'আনি"},
        {"ফিকহ", "নূরুল ইযাহ", "মুখতাসারুল কুদুরী", "কানযুদ দাকায়েক", "আল হিদায়া", "শরহে বেকায়া", "ফাতাওয়া আলমগীরী"},
        {"উসুল", "উসুলুশ শাশী", "নূরুল আনওয়ার", "হুসামী"},
        {"বালাগাত", "মুখতাসারুল মা'আনি", "তালখীসুল মিফতাহ", "মুতাওয়াল"},
        {"মানতিক", "সুল্লামুল উলূম", "মীর কুতবী", "তাহযীব", "কুতবী"},
        {"তাফসির", "তাফসিরে জালালাইন", "বায়যাবী", "মাদারিক", "ইবনে কাসীর"},
        {"হাদিস", "মিশকাত", "সহিহ বুখারী", "সহিহ মুসলিম", "আবু দাউদ", "তিরমিযী", "নাসাঈ", "ইবনে মাজাহ"},
        {"আরবি সাহিত্য", "মাকামাতে হারিরী", "দিওয়ানে হামাসা", "কালিলা ওয়া দিমনা"},
        {"ফারায়েজ", "সিরাজী", "শরহে সিরাজী"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHome();
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setIncludeFontPadding(true);
        t.setLineSpacing(4, 1.15f);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setPadding(12, 12, 12, 12);
        return t;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    private void setupRoot() {
        root = column();
        root.setBackgroundColor(CREAM);
        setContentView(root);
    }

    private void addLogo() {
        LinearLayout logo = column();
        logo.setGravity(Gravity.CENTER);
        logo.setPadding(12, 18, 12, 18);
        logo.setBackground(shape(GREEN, 22));

        TextView book = text("📖", 45, GOLD, true);
        logo.addView(book);

        TextView name = text("SABBIR AI", 28, Color.WHITE, true);
        logo.addView(name);

        TextView subtitle = text(
                "MADRASAH TEACHER", 16, GOLD, true);
        logo.addView(subtitle);

        TextView tagline = text(
                "জ্ঞান • আমল • সফলতা", 15, Color.WHITE, true);
        logo.addView(tagline);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        -1, -2);
        p.setMargins(14, 14, 14, 14);
        root.addView(logo, p);
    }

    private void showHome() {
        setupRoot();
        addLogo();

        TextView heading = text(
                "আপনার কিতাব নির্বাচন করুন",
                21, GREEN, true);
        root.addView(heading);

        ScrollView scroll = new ScrollView(this);
        LinearLayout list = column();
        list.setPadding(12, 4, 12, 20);

        for (String[] category : books) {
            TextView title = text(
                    "📚 " + category[0],
                    19, Color.WHITE, true);
            title.setBackground(shape(GREEN, 12));

            LinearLayout.LayoutParams tp =
                    new LinearLayout.LayoutParams(-1, -2);
            tp.setMargins(0, 12, 0, 5);
            list.addView(title, tp);

            for (int i = 1; i < category.length; i++) {
                final String bookName = category[i];

                TextView item = text(
                        "📖  " + bookName + "   ›",
                        17, GREEN, true);
                item.setGravity(Gravity.CENTER_VERTICAL);
                item.setBackground(shape(Color.WHITE, 12));

                LinearLayout.LayoutParams ip =
                        new LinearLayout.LayoutParams(-1, -2);
                ip.setMargins(0, 4, 0, 4);
                list.addView(item, ip);

                item.setOnClickListener(v -> openChat(bookName));
            }
        }

        scroll.addView(list);
        root.addView(scroll,
                new LinearLayout.LayoutParams(-1, 0, 1));
    }

    private void openChat(String book) {
        selectedBook = book;
        showChat();
        addMessage("আসসালামু আলাইকুম। " + book +
                " সম্পর্কে আপনার প্রশ্ন লিখুন।", false);
    }

    private void showChat() {
        setupRoot();

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(8, 8, 8, 8);
        header.setBackgroundColor(GREEN);

        TextView back = text("‹", 32, Color.WHITE, true);
        back.setOnClickListener(v -> showHome());
        header.addView(back,
                new LinearLayout.LayoutParams(55, -2));

        TextView title = text(
                "📖 " + selectedBook, 18, Color.WHITE, true);
        header.addView(title,
                new LinearLayout.LayoutParams(0, -2, 1));

        root.addView(header);

        chatScroll = new ScrollView(this);
        messages = column();
        messages.setPadding(10, 10, 10, 10);
        chatScroll.addView(messages);

        root.addView(chatScroll,
                new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout bottom = new LinearLayout(this);
        bottom.setPadding(8, 8, 8, 8);
        bottom.setGravity(Gravity.CENTER_VERTICAL);
        bottom.setBackgroundColor(Color.WHITE);

        input = new EditText(this);
        input.setHint("আপনার প্রশ্ন লিখুন...");
        input.setTextSize(16);
        input.setSingleLine(false);
        input.setMinHeight(55);
        input.setMaxLines(4);
        input.setIncludeFontPadding(true);
        input.setPadding(12, 10, 12, 10);
        input.setBackground(shape(CREAM, 12));

        bottom.addView(input,
                new LinearLayout.LayoutParams(0, -2, 1));

        TextView send = text(
                "পাঠান", 16, Color.WHITE, true);
        send.setBackground(shape(GREEN, 12));
        bottom.addView(send,
                new LinearLayout.LayoutParams(90, -2));

        send.setOnClickListener(v -> sendQuestion());

        root.addView(bottom);
    }

    private void addMessage(String message, boolean user) {
        TextView bubble = text(
                message, 16,
                user ? Color.WHITE : GREEN, false);

        bubble.setGravity(Gravity.START);
        bubble.setBackground(shape(
                user ? GREEN : Color.WHITE, 14));

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(
                        -1, -2);
        p.setMargins(0, 6, 0, 6);

        messages.addView(bubble, p);
        chatScroll.post(() ->
                chatScroll.fullScroll(View.FOCUS_DOWN));
    }

    private void sendQuestion() {
        String question = input.getText().toString().trim();

        if (question.isEmpty()) {
            Toast.makeText(this,
                    "প্রশ্ন লিখুন", Toast.LENGTH_SHORT).show();
            return;
        }

        addMessage(question, true);
        input.setText("");

        TextView loading = text(
                "উত্তর তৈরি হচ্ছে...", 15, GREEN, false);
        messages.addView(loading);

        executor.execute(() -> {
            String answer = askServer(question);

            runOnUiThread(() -> {
                messages.removeView(loading);
                addMessage(answer, false);
            });
        });
    }

    private String askServer(String question) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(SERVER_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(60000);
            connection.setDoOutput(true);
            connection.setRequestProperty(
                    "Content-Type", "application/json; charset=UTF-8");

            JSONObject data = new JSONObject();
            data.put("question", question);
            data.put("subject", selectedBook);

            OutputStream out = connection.getOutputStream();
            out.write(data.toString().getBytes("UTF-8"));
            out.close();

            int code = connection.getResponseCode();

            InputStream stream = code >= 200 && code < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stream, "UTF-8"));

            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();

            JSONObject response =
                    new JSONObject(result.toString());

            if (response.has("answer")) {
                return response.getString("answer");
            }

            return "দুঃখিত, সার্ভার থেকে উত্তর পাওয়া যায়নি।";

        } catch (Exception e) {
            return "সংযোগে সমস্যা হয়েছে। ইন্টারনেট পরীক্ষা করে আবার চেষ্টা করুন।";
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
    }
