package com.sabbir.madrasah;

import android.app.*;
import android.os.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.content.*;
import android.widget.*;
import org.json.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class MainActivity extends Activity {

    private final String SERVER_URL =
        "https://sabbir-madrasah-ai-server2.onrender.com/ask";

    private final int BG = Color.rgb(33,33,33);
    private final int PANEL = Color.rgb(42,42,42);
    private final int WHITE = Color.WHITE;
    private final int MUTED = Color.rgb(180,180,180);

    private LinearLayout root, header, messages, bottom;
    private ScrollView chatScroll;
    private EditText input;
    private TextView bookLabel;
    private JSONObject currentChat;

    private JSONArray chats;
    private SharedPreferences prefs;
    private final ExecutorService executor =
        Executors.newSingleThreadExecutor();

    private final String[] books = {
        "সাধারণ প্রশ্ন",
        "মিজানুস সরফ",
        "মুনশাইব",
        "শাফিয়া",
        "নাহুমীর",
        "হেদায়াতুন নাহু",
        "কাফিয়া",
        "শরহে জামি",
        "মুখতাসারুল মা'আনি",
        "নূরুল ইযাহ",
        "মুখতাসারুল কুদুরী",
        "কানযুদ দাকায়েক",
        "আল হিদায়া",
        "শরহে বেকায়া",
        "উসুলুশ শাশী",
        "নূরুল আনওয়ার",
        "সুল্লামুল উলূম",
        "মীর কুতবী",
        "তাফসিরে জালালাইন",
        "মিশকাত",
        "সহিহ বুখারী",
        "সহিহ মুসলিম",
        "সিরাজী",
        "মাকামাতে হারিরী"
    };

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        prefs = getSharedPreferences("sabbir_chats", MODE_PRIVATE);

        try {
            chats = new JSONArray(
                prefs.getString("chats", "[]")
            );
        } catch (Exception e) {
            chats = new JSONArray();
        }

        newChat();
    }

    private GradientDrawable bg(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    private TextView makeText(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setIncludeFontPadding(true);
        t.setLineSpacing(5, 1.15f);
        t.setPadding(12, 12, 12, 12);
        return t;
    }

    private LinearLayout vertical() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    private LinearLayout horizontal() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    private void saveChats() {
        prefs.edit().putString("chats", chats.toString()).apply();
    }

    private void newChat() {
        currentChat = new JSONObject();

        try {
            currentChat.put("id", System.currentTimeMillis());
            currentChat.put("title", "নতুন চ্যাট");
            currentChat.put("book", "সাধারণ প্রশ্ন");
            currentChat.put("messages", new JSONArray());
            chats.put(currentChat);
            saveChats();
        } catch (Exception e) {}

        showScreen();
    }

    private void showScreen() {
        root = vertical();
        root.setBackgroundColor(BG);
        setContentView(root);

        header = horizontal();
        header.setPadding(8, 6, 8, 6);
        header.setBackgroundColor(BG);

        TextView history = makeText("☰", 25, WHITE);
        history.setGravity(Gravity.CENTER);
        header.addView(history,
            new LinearLayout.LayoutParams(48, 52));
        history.setOnClickListener(v -> showHistory());

        TextView name = makeText("SABBIR AI", 19, WHITE);
        name.setTypeface(null, Typeface.BOLD);
        header.addView(name,
            new LinearLayout.LayoutParams(0, -2, 1));

        bookLabel = makeText("কিতাব ▾", 13, WHITE);
        bookLabel.setGravity(Gravity.CENTER);
        bookLabel.setBackground(bg(PANEL, 18));
        header.addView(bookLabel);
        bookLabel.setOnClickListener(v -> chooseBook());

        TextView fresh = makeText("＋", 27, WHITE);
        fresh.setGravity(Gravity.CENTER);
        header.addView(fresh,
            new LinearLayout.LayoutParams(48, 52));
        fresh.setOnClickListener(v -> newChat());

        root.addView(header);

        View line = new View(this);
        line.setBackgroundColor(Color.rgb(65,65,65));
        root.addView(line,
            new LinearLayout.LayoutParams(-1, 1));

        chatScroll = new ScrollView(this);
        chatScroll.setFillViewport(true);

        messages = vertical();
        messages.setPadding(15, 15, 15, 15);
        chatScroll.addView(messages);

        root.addView(chatScroll,
            new LinearLayout.LayoutParams(-1, 0, 1));

        loadMessages();

        bottom = horizontal();
        bottom.setPadding(10, 8, 10, 10);
        bottom.setBackgroundColor(BG);

        input = new EditText(this);
        input.setTextColor(WHITE);
        input.setHintTextColor(MUTED);
        input.setHint("যেকোনো প্রশ্ন লিখুন...");
        input.setTextSize(16);
        input.setMinHeight(52);
        input.setMaxLines(5);
        input.setGravity(Gravity.CENTER_VERTICAL);
        input.setIncludeFontPadding(true);
        input.setPadding(16, 10, 16, 10);
        input.setBackground(bg(PANEL, 24));

        bottom.addView(input,
            new LinearLayout.LayoutParams(0, -2, 1));

        TextView send = makeText("↑", 25, WHITE);
        send.setGravity(Gravity.CENTER);
        send.setTypeface(null, Typeface.BOLD);
        send.setBackground(bg(Color.rgb(16,163,127), 50));

        LinearLayout.LayoutParams sp =
            new LinearLayout.LayoutParams(48, 48);
        sp.setMargins(8, 0, 0, 0);
        bottom.addView(send, sp);

        send.setOnClickListener(v -> sendQuestion());

        root.addView(bottom);
    }

    private void loadMessages() {
        messages.removeAllViews();

        try {
            JSONArray arr = currentChat.getJSONArray("messages");

            if (arr.length() == 0) {
                TextView welcome = makeText(
                    "SABBIR AI MADRASAH TEACHER\n\nআজ কী শিখতে চান?",
                    21, WHITE
                );
                welcome.setGravity(Gravity.CENTER);
                messages.addView(welcome,
                    new LinearLayout.LayoutParams(-1, 250));
            }

            for (int i = 0; i < arr.length(); i++) {
                JSONObject m = arr.getJSONObject(i);
                addBubble(
                    m.getString("content"),
                    m.getString("role").equals("user"),
                    false
                );
            }
        } catch (Exception e) {}
    }

    private void addBubble(String value, boolean user, boolean scroll) {
        LinearLayout row = horizontal();
        row.setGravity(user ? Gravity.RIGHT : Gravity.LEFT);

        TextView bubble = makeText(value, 16, WHITE);
        bubble.setGravity(Gravity.START);
        bubble.setBackground(bg(
            user ? Color.rgb(48,48,48) : BG, 16
        ));

        row.addView(bubble,
            new LinearLayout.LayoutParams(-1, -2));

        LinearLayout.LayoutParams rp =
            new LinearLayout.LayoutParams(-1, -2);
        rp.setMargins(0, 5, 0, 5);
        messages.addView(row, rp);

        if (scroll) {
            chatScroll.post(() ->
                chatScroll.fullScroll(View.FOCUS_DOWN));
        }
    }

    private void saveMessage(String role, String content) {
        try {
            JSONArray arr = currentChat.getJSONArray("messages");

            JSONObject m = new JSONObject();
            m.put("role", role);
            m.put("content", content);
            arr.put(m);

            if (role.equals("user") &&
                currentChat.getString("title").equals("নতুন চ্যাট")) {
                String title = content.length() > 28
                    ? content.substring(0, 28) + "..."
                    : content;
                currentChat.put("title", title);
            }

            saveChats();
        } catch (Exception e) {}
    }

    private void sendQuestion() {
        String question = input.getText().toString().trim();

        if (question.isEmpty()) return;

        input.setText("");
        addBubble(question, true, true);
        saveMessage("user", question);

        TextView loading = makeText("উত্তর তৈরি হচ্ছে...", 15, MUTED);
        messages.addView(loading);

        final String book;
        final String history;

        try {
            book = currentChat.getString("book");
            history = currentChat.getJSONArray("messages").toString();
        } catch (Exception e) {
            messages.removeView(loading);
            return;
        }

        executor.execute(() -> {
            String answer = askServer(question, book, history);

            runOnUiThread(() -> {
                messages.removeView(loading);
                addBubble(answer, false, true);
                saveMessage("assistant", answer);
            });
        });
    }

    private String askServer(String question, String book, String history) {
        HttpURLConnection con = null;

        try {
            URL url = new URL(SERVER_URL);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(30000);
            con.setReadTimeout(60000);
            con.setDoOutput(true);
            con.setRequestProperty(
                "Content-Type", "application/json; charset=UTF-8"
            );

            JSONObject data = new JSONObject();
            data.put("question", question);
            data.put("subject", book);
            data.put("history", new JSONArray(history));

            OutputStream out = con.getOutputStream();
            out.write(data.toString().getBytes("UTF-8"));
            out.close();

            int code = con.getResponseCode();

            InputStream stream = code >= 200 && code < 300
                ? con.getInputStream()
                : con.getErrorStream();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, "UTF-8")
            );

            StringBuilder result = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();

            JSONObject response = new JSONObject(result.toString());

            if (response.has("answer")) {
                return response.getString("answer");
            }

            return "দুঃখিত, উত্তর পাওয়া যায়নি।";

        } catch (Exception e) {
            return "সার্ভারের সঙ্গে সংযোগ হচ্ছে না। ইন্টারনেট পরীক্ষা করুন।";
        } finally {
            if (con != null) con.disconnect();
        }
    }

    private void chooseBook() {
        new AlertDialog.Builder(this)
            .setTitle("কিতাব নির্বাচন করুন")
            .setItems(books, (d, which) -> {
                try {
                    currentChat.put("book", books[which]);
                    saveChats();
                    bookLabel.setText(books[which] + " ▾");
                } catch (Exception e) {}
            })
            .show();
    }

    private void showHistory() {
        Dialog dialog = new Dialog(this);

        LinearLayout panel = vertical();
        panel.setPadding(15, 15, 15, 15);
        panel.setBackgroundColor(BG);

        TextView title = makeText("চ্যাটসমূহ", 22, WHITE);
        title.setTypeface(null, Typeface.BOLD);
        panel.addView(title);

        Button create = new Button(this);
        create.setText("＋ নতুন চ্যাট");
        panel.addView(create);
        create.setOnClickListener(v -> {
            dialog.dismiss();
            newChat();
        });

        Button deleteAll = new Button(this);
        deleteAll.setText("সব চ্যাট মুছুন");
        panel.addView(deleteAll);
        deleteAll.setOnClickListener(v ->
            confirmDeleteAll(dialog)
        );

        ScrollView scroll = new ScrollView(this);
        LinearLayout list = vertical();

        for (int i = chats.length() - 1; i >= 0; i--) {
            final int index = i;
            try {
                JSONObject chat = chats.getJSONObject(i);

                LinearLayout item = horizontal();
                item.setPadding(5, 5, 5, 5);

                TextView chatTitle = makeText(
                    "💬 " + chat.getString("title"),
                    15, WHITE
                );
                item.addView(chatTitle,
                    new LinearLayout.LayoutParams(0, -2, 1));

                TextView delete = makeText("🗑", 19, WHITE);
                item.addView(delete);

                list.addView(item);
                item.setOnClickListener(v -> {
                    currentChat = chat;
                    dialog.dismiss();
                    showScreen();
                });

                delete.setOnClickListener(v ->
                    confirmDelete(index, dialog)
                );

            } catch (Exception e) {}
        }

        scroll.addView(list);
        panel.addView(scroll,
            new LinearLayout.LayoutParams(-1, 0, 1));

        dialog.setContentView(panel);
        Window w = dialog.getWindow();
        if (w != null) {
            w.setBackgroundDrawableResource(android.R.color.transparent);
            w.setLayout(
                (int)(getResources().getDisplayMetrics().widthPixels * 0.88),
                -1
            );
            w.setGravity(Gravity.LEFT);
        }
        dialog.show();
        w = dialog.getWindow();
        if (w != null) {
            w.setLayout(
                (int)(getResources().getDisplayMetrics().widthPixels * 0.88),
                -1
            );
            w.setGravity(Gravity.LEFT);
        }
    }

    private void confirmDelete(int index, Dialog dialog) {
        new AlertDialog.Builder(this)
            .setTitle("চ্যাট ডিলেট করবেন?")
            .setMessage("এই চ্যাটটি মুছে যাবে।")
            .setNegativeButton("না", null)
            .setPositiveButton("ডিলেট", (d, w) -> {
                chats.remove(index);
                saveChats();

                if (chats.length() == 0) {
                    dialog.dismiss();
                    newChat();
                } else {
                    try {
                        currentChat = chats.getJSONObject(0);
                    } catch (Exception e) {}
                    dialog.dismiss();
                    showHistory();
                }
            })
            .show();
    }

    private void confirmDeleteAll(Dialog dialog) {
        new AlertDialog.Builder(this)
            .setTitle("সব চ্যাট মুছবেন?")
            .setMessage("সব পুরোনো চ্যাট মুছে যাবে।")
            .setNegativeButton("না", null)
            .setPositiveButton("হ্যাঁ, মুছুন", (d, w) -> {
                chats = new JSONArray();
                saveChats();
                dialog.dismiss();
                newChat();
            })
            .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
                                          }
            
