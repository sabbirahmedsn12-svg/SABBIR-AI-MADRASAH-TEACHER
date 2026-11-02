package com.sabbir.madrasah;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import org.json.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MainActivity extends Activity {

    private final String API_URL =
        "https://sabbir-madrasah-ai-server2.onrender.com/ask";

    private final String PREF = "sabbir_ai_chats";
    private final int BG = Color.rgb(33,33,33);
    private final int PANEL = Color.rgb(43,43,43);
    private final int WHITE = Color.WHITE;
    private final int MUTED = Color.rgb(180,180,180);

    private LinearLayout root, header, messages, inputArea;
    private ScrollView scroll;
    private EditText input;
    private TextView subjectText, titleText;
    private ImageView logoView;
    private ProgressBar progress;

    private ArrayList<JSONObject> chats = new ArrayList<>();
    private JSONObject currentChat;
    private String currentSubject = "সাধারণ প্রশ্ন";
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private final String[] subjects = {
        "সাধারণ প্রশ্ন",
        "সরফ",
        "নাহু",
        "ফিকহ",
        "উসুলুল ফিকহ",
        "বালাগাত",
        "মানতিক",
        "তাফসির",
        "হাদিস",
        "আরবি সাহিত্য",
        "ইলমুল ফারায়েজ",
        "আদব"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        getWindow().getDecorView().setSystemUiVisibility(0);

        loadChats();
        if (chats.isEmpty()) newChat();
        else currentChat = chats.get(0);

        buildScreen();
        showMessages();
    }

    private int dp(float value) {
        return (int)(value * getResources().getDisplayMetrics().density);
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    private TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        t.setIncludeFontPadding(true);
        return t;
    }

    private void buildScreen() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        setContentView(root);

        // Header
        header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(12), dp(7), dp(12), dp(7));
        header.setBackgroundColor(BG);
        root.addView(header,
            new LinearLayout.LayoutParams(-1, dp(62)));

        TextView history = text("☰", 25, WHITE);
        history.setGravity(Gravity.CENTER);
        header.addView(history,
            new LinearLayout.LayoutParams(dp(42), -1));
        history.setOnClickListener(v -> showHistory());

        logoView = new ImageView(this);
        logoView.setImageResource(R.drawable.logo);
        logoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        header.addView(logoView,
            new LinearLayout.LayoutParams(dp(38), dp(38)));

        LinearLayout heading = new LinearLayout(this);
        heading.setOrientation(LinearLayout.VERTICAL);
        heading.setPadding(dp(9), 0, 0, 0);

        titleText = text("SABBIR AI", 18, WHITE);
        titleText.setTypeface(null, Typeface.BOLD);
        heading.addView(titleText);

        TextView subtitle = text("MADRASAH TEACHER", 10, MUTED);
        heading.addView(subtitle);

        header.addView(heading,
            new LinearLayout.LayoutParams(0, -1, 1));

        TextView newButton = text("+", 30, WHITE);
        newButton.setGravity(Gravity.CENTER);
        header.addView(newButton,
            new LinearLayout.LayoutParams(dp(45), -1));
        newButton.setOnClickListener(v -> newChat());

        View line = new View(this);
        line.setBackgroundColor(Color.rgb(65,65,65));
        root.addView(line,
            new LinearLayout.LayoutParams(-1, dp(1)));

        // Subject selector
        LinearLayout subjectBar = new LinearLayout(this);
        subjectBar.setGravity(Gravity.CENTER_VERTICAL);
        subjectBar.setPadding(dp(16), dp(7), dp(16), dp(7));
        root.addView(subjectBar,
            new LinearLayout.LayoutParams(-1, dp(48)));

        subjectText = text("বিষয়: " + currentSubject + "  ▾",
            14, WHITE);
        subjectBar.addView(subjectText);
        subjectText.setOnClickListener(v -> chooseSubject());

        // Chat messages
        scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);

        messages = new LinearLayout(this);
        messages.setOrientation(LinearLayout.VERTICAL);
        messages.setPadding(dp(16), dp(12), dp(16), dp(18));

        scroll.addView(messages);
        root.addView(scroll,
            new LinearLayout.LayoutParams(-1, 0, 1));

        // Bottom input area
        View separator = new View(this);
        separator.setBackgroundColor(Color.rgb(65,65,65));
        root.addView(separator,
            new LinearLayout.LayoutParams(-1, dp(1)));

        inputArea = new LinearLayout(this);
        inputArea.setOrientation(LinearLayout.VERTICAL);
        inputArea.setPadding(dp(12), dp(10), dp(12), dp(8));
        inputArea.setBackgroundColor(BG);
        root.addView(inputArea,
            new LinearLayout.LayoutParams(-1, -2));

        LinearLayout inputBox = new LinearLayout(this);
        inputBox.setGravity(Gravity.CENTER_VERTICAL);
        inputBox.setPadding(dp(14), dp(5), dp(7), dp(5));
        inputBox.setBackground(shape(PANEL, 27));

        inputArea.addView(inputBox,
            new LinearLayout.LayoutParams(-1, -2));

        input = new EditText(this);
        input.setHint("আপনার প্রশ্ন লিখুন...");
        input.setHintTextColor(MUTED);
        input.setTextColor(WHITE);
        input.setTextSize(16);
        input.setSingleLine(false);
        input.setMaxLines(5);
        input.setMinLines(1);
        input.setBackgroundColor(Color.TRANSPARENT);
        input.setPadding(dp(2), dp(9), dp(5), dp(9));
        input.setInputType(
            android.text.InputType.TYPE_CLASS_TEXT |
            android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
        );

        inputBox.addView(input,
            new LinearLayout.LayoutParams(0, -2, 1));

        TextView send = text("↑", 25, WHITE);
        send.setGravity(Gravity.CENTER);
        send.setBackground(shape(Color.rgb(235,235,235), 50));
        send.setTextColor(Color.BLACK);
        inputBox.addView(send,
            new LinearLayout.LayoutParams(dp(42), dp(42)));
        send.setOnClickListener(v -> sendMessage());

        TextView footer = text(
            "SABBIR AI ভুল করতে পারে। গুরুত্বপূর্ণ বিষয় যাচাই করে নিন।",
            10, MUTED);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(7), 0, 0);
        inputArea.addView(footer);
    }

    private void newChat() {
        try {
            currentChat = new JSONObject();
            currentChat.put("id", System.currentTimeMillis());
            currentChat.put("title", "নতুন চ্যাট");
            currentChat.put("subject", "সাধারণ প্রশ্ন");
            currentChat.put("messages", new JSONArray());

            chats.add(0, currentChat);
            currentSubject = "সাধারণ প্রশ্ন";
            saveChats();

            if (root != null) {
                subjectText.setText("বিষয়: " + currentSubject + "  ▾");
                showMessages();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chooseSubject() {
        new AlertDialog.Builder(this)
            .setTitle("বিষয় নির্বাচন করুন")
            .setItems(subjects, (dialog, which) -> {
                currentSubject = subjects[which];
                try {
                    currentChat.put("subject", currentSubject);
                } catch (Exception e) {}
                subjectText.setText("বিষয়: " + currentSubject + "  ▾");
                saveChats();
            })
            .show();
    }

    private void showMessages() {
        messages.removeAllViews();

        if (currentChat == null) return;

        try {
            JSONArray arr = currentChat.getJSONArray("messages");

            if (arr.length() == 0) {
                showWelcome();
            }

            for (int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);
                addMessage(
                    item.optString("role"),
                    item.optString("text")
                );
            }
        } catch (Exception e) {
            showWelcome();
        }

        scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
    }

    private void showWelcome() {
        LinearLayout welcome = new LinearLayout(this);
        welcome.setOrientation(LinearLayout.VERTICAL);
        welcome.setGravity(Gravity.CENTER);
        welcome.setPadding(dp(12), dp(45), dp(12), dp(25));

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.logo);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        welcome.addView(logo,
            new LinearLayout.LayoutParams(dp(92), dp(92)));

        TextView title = text("SABBIR AI", 25, WHITE);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, dp(16), 0, dp(5));
        welcome.addView(title);

        TextView description = text(
            "আপনার মাদরাসার পড়াশোনার সহায়ক শিক্ষক",
            16, MUTED);
        description.setGravity(Gravity.CENTER);
        welcome.addView(description);

        TextView hint = text(
            "সরফ, নাহু, ফিকহ, হাদিস, তাফসিরসহ বিভিন্ন বিষয়ে প্রশ্ন করুন।",
            15, MUTED);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(dp(10), dp(15), dp(10), 0);
        welcome.addView(hint);

        messages.addView(welcome,
            new LinearLayout.LayoutParams(-1, -2));
    }

    private void addMessage(String role, String value) {
        boolean user = role.equals("user");

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setGravity(user ? Gravity.RIGHT : Gravity.LEFT);
        row.setPadding(0, dp(9), 0, dp(9));

        TextView message = text(value, 16, WHITE);
        message.setGravity(Gravity.TOP | Gravity.LEFT);
        message.setPadding(dp(15), dp(11), dp(15), dp(11));
        message.setLineSpacing(dp(5), 1.0f);

        if (user) {
            message.setBackground(shape(
                Color.rgb(48,48,48), 20));
            row.addView(message,
                new LinearLayout.LayoutParams(
                    -2, -2));
        } else {
            row.addView(message,
                new LinearLayout.LayoutParams(-1, -2));
        }

        messages.addView(row,
            new LinearLayout.LayoutParams(-1, -2));
    }

    private void sendMessage() {
        String question = input.getText().toString().trim();

        if (question.isEmpty()) return;

        if (currentChat == null) newChat();

        input.setText("");

        try {
            JSONArray arr = currentChat.getJSONArray("messages");

            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("text", question);
            arr.put(userMsg);

            if (arr.length() == 1) {
                currentChat.put("title",
                    question.length() > 35
                    ? question.substring(0, 35) + "..."
                    : question);
            }

            saveChats();
            showMessages();

        } catch (Exception e) {
            e.printStackTrace();
        }

        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
            .hideSoftInputFromWindow(input.getWindowToken(), 0);

        askAI(question);
    }

    private void askAI(String question) {
        TextView loading = text("SABBIR AI উত্তর তৈরি করছে...",
            14, MUTED);
        loading.setPadding(dp(12), dp(15), dp(12), dp(15));
        messages.addView(loading);

        scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));

        JSONObject chatReference = currentChat;

        executor.execute(() -> {
            String answer = "";

            try {
                URL url = new URL(API_URL);
                HttpURLConnection conn =
                    (HttpURLConnection)url.openConnection();

                conn.setRequestMethod("POST");
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(60000);
                conn.setDoOutput(true);
                conn.setRequestProperty(
                    "Content-Type", "application/json; charset=UTF-8");

                JSONObject body = new JSONObject();
                body.put("question", question);
                body.put("subject", currentSubject);

                JSONArray history = new JSONArray();
                JSONArray oldMessages =
                    chatReference.getJSONArray("messages");

                for (int i = 0; i < oldMessages.length(); i++) {
                    history.put(oldMessages.getJSONObject(i));
                }

                body.put("history", history);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                int status = conn.getResponseCode();

                InputStream stream = status >= 200 && status < 300
                    ? conn.getInputStream()
                    : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stream, "UTF-8"));

                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                conn.disconnect();

                JSONObject response =
                    new JSONObject(result.toString());

                answer = response.optString("answer",
                    response.optString("response",
                    response.optString("text", "")));

                if (status < 200 || status >= 300) {
                    answer = response.optString("error",
                        "সার্ভারে সমস্যা হয়েছে। কিছুক্ষণ পরে চেষ্টা করুন।");
                }

                if (answer.isEmpty()) {
                    answer = "AI থেকে উত্তর পাওয়া যায়নি। আবার চেষ্টা করুন।";
                }

            } catch (Exception e) {
                answer = "সংযোগে সমস্যা হয়েছে। ইন্টারনেট পরীক্ষা করে আবার চেষ্টা করুন।";
            }

            final String finalAnswer = answer;

            runOnUiThread(() -> {
                messages.removeView(loading);

                try {
                    JSONObject aiMsg = new JSONObject();
                    aiMsg.put("role", "assistant");
                    aiMsg.put("text", finalAnswer);

                    chatReference.getJSONArray("messages").put(aiMsg);
                    saveChats();

                    if (currentChat == chatReference) {
                        showMessages();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void showHistory() {
        if (chats.isEmpty()) {
            newChat();
            return;
        }

        String[] names = new String[chats.size()];

        for (int i = 0; i < chats.size(); i++) {
            names[i] = chats.get(i).optString("title", "নতুন চ্যাট");
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("আপনার চ্যাট")
            .setItems(names, (d, which) -> {
                currentChat = chats.get(which);
                currentSubject = currentChat.optString(
                    "subject", "সাধারণ প্রশ্ন");
                subjectText.setText(
                    "বিষয়: " + currentSubject + "  ▾");
                showMessages();
            })
            .setPositiveButton("নতুন চ্যাট",
                (d, which) -> newChat())
            .setNegativeButton("বন্ধ", null)
            .create();

        dialog.setOnShowListener(d -> {
            ListView list = dialog.getListView();
            list.setOnItemLongClickListener((parent, view, position, id) -> {
                confirmDelete(position);
                return true;
            });
        });

        dialog.show();
    }

    private void confirmDelete(int position) {
        new AlertDialog.Builder(this)
            .setTitle("চ্যাট মুছে ফেলবেন?")
            .setMessage("এই চ্যাটটি মুছে যাবে।")
            .setNegativeButton("না", null)
            .setPositiveButton("ডিলেট", (d, w) -> {
                JSONObject deleting = chats.get(position);
                chats.remove(position);

                if (currentChat == deleting) {
                    if (chats.isEmpty()) {
                        currentChat = null;
                        newChat();
                    } else {
                        currentChat = chats.get(0);
                    }
                    currentSubject = currentChat.optString(
                        "subject", "সাধারণ প্রশ্ন");
                    subjectText.setText(
                        "বিষয়: " + currentSubject + "  ▾");
                    showMessages();
                }

                saveChats();
            })
            .show();
    }

    private void saveChats() {
        try {
            JSONArray array = new JSONArray();
            for (JSONObject chat : chats) {
                array.put(chat);
            }

            getSharedPreferences(PREF, MODE_PRIVATE)
                .edit()
                .putString("data", array.toString())
                .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadChats() {
        try {
            String saved = getSharedPreferences(PREF, MODE_PRIVATE)
                .getString("data", "[]");

            JSONArray array = new JSONArray(saved);

            for (int i = 0; i < array.length(); i++) {
                chats.add(array.getJSONObject(i));
            }

        } catch (Exception e) {
            chats.clear();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
                                }
