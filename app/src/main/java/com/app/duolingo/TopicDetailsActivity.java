package com.app.duolingo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.duolingo.adapter.TopicAdapter;
import com.app.duolingo.adapter.WordTopicAdapter;
import com.app.duolingo.models.Topic;
import com.app.duolingo.models.Word;
import com.app.duolingo.models.WordTopic;
import com.app.duolingo.services.DatabaseService;
import com.app.duolingo.utils.DialogUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TopicDetailsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    private WordTopicAdapter wordTopicAdapter;
    private DatabaseService databaseService;
    private ImageButton btnAdd;
    private ImageButton btnBack;

    private String folderId;
    private String topicId;

    private TextToSpeech textToSpeech;
    private boolean isReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_details);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(TopicDetailsActivity.this, "Language not supported.", Toast.LENGTH_SHORT).show();
                    } else {
                        isReady = true;
                    }
                } else {
                    Toast.makeText(TopicDetailsActivity.this, "TextToSpeech initialization failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        databaseService = new DatabaseService();
        folderId = getIntent().getStringExtra("folderId");
        topicId = getIntent().getStringExtra("topicId");
        initView();
        initList();


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTopicDialog();
            }
        });


    }

    private void addTopicDialog() {
        Dialog dialog = new Dialog(TopicDetailsActivity.this);
        View view = LayoutInflater.from(TopicDetailsActivity.this).inflate(R.layout.dialog_add_word_to_topic, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText edtEnglish = view.findViewById(R.id.edtEnglish);
        EditText edtMeaning = view.findViewById(R.id.edtMeaning);
        EditText edtPronounce = view.findViewById(R.id.edtPronounce);
        TextView no = view.findViewById(R.id.btnNo);
        TextView yes = view.findViewById(R.id.btnYes);
        no.setOnClickListener(v -> dialog.dismiss());

        yes.setOnClickListener(v -> {
            dialog.dismiss();
            String e = edtEnglish.getText().toString().trim();
            String m = edtMeaning.getText().toString().trim();
            String p = edtPronounce.getText().toString().trim();
            if (!e.isEmpty() && !m.isEmpty() && !p.isEmpty()) {
                WordTopic wordTopic = new WordTopic();
                wordTopic.setEnglish(e);
                wordTopic.setMeaning(m);
                wordTopic.setPronounce(p);
                addWordTopic(wordTopic);

            }
        });
    }

    private void addWordTopic(WordTopic wordTopic) {
        DialogUtil.progressDlgShow(this, "Loading");
        databaseService.pushWordTopicToFirebase(this, wordTopic, folderId, topicId, new DatabaseService.OnListener() {
            @Override
            public void onSuccess() {
                DialogUtil.progressDlgHide();
            }

            @Override
            public void onFailure() {
                DialogUtil.progressDlgHide();
                Toast.makeText(TopicDetailsActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);
        btnBack = findViewById(R.id.btnBack);

    }

    private void showLongClickMenu(WordTopic wordTopic) {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(TopicDetailsActivity.this);
        bottomDialog.setCancelable(true);
        bottomDialog.show();
        bottomDialog.setContentView(R.layout.layout_bottom_sheet);
        View btnDelete = bottomDialog.findViewById(R.id.tvDelete);
        View btnEdit = bottomDialog.findViewById(R.id.tvEdit);
        Objects.requireNonNull(btnDelete).setOnClickListener(v -> {
            bottomDialog.dismiss();
            showDialogDelete(wordTopic);
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomDialog.dismiss();
                showDialogEdit(wordTopic);
            }
        });
        View btnCancel = bottomDialog.findViewById(R.id.cancel);
        Objects.requireNonNull(btnCancel).setOnClickListener(v -> bottomDialog.dismiss());
    }

    private void showDialogEdit(WordTopic wordTopic) {
        Dialog dialog = new Dialog(TopicDetailsActivity.this);
        View view = LayoutInflater.from(TopicDetailsActivity.this).inflate(R.layout.dialog_add_word_to_topic, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView title = view.findViewById(R.id.title);
        title.setText("Edit word");
        EditText edtEnglish = view.findViewById(R.id.edtEnglish);
        EditText edtMeaning = view.findViewById(R.id.edtMeaning);
        EditText edtPronounce = view.findViewById(R.id.edtPronounce);
        TextView no = view.findViewById(R.id.btnNo);
        TextView yes = view.findViewById(R.id.btnYes);

        edtEnglish.setText(wordTopic.getEnglish());
        edtMeaning.setText(wordTopic.getMeaning());
        edtPronounce.setText(wordTopic.getPronounce());


        no.setOnClickListener(v -> dialog.dismiss());

        yes.setOnClickListener(v -> {
            dialog.dismiss();
            String e = edtEnglish.getText().toString().trim();
            String m = edtMeaning.getText().toString().trim();
            String p = edtPronounce.getText().toString().trim();
            if (!e.isEmpty() && !m.isEmpty() && !p.isEmpty()) {
                wordTopic.setEnglish(e);
                wordTopic.setMeaning(m);
                wordTopic.setPronounce(p);
                updateWord(wordTopic);

            }
        });
    }

    private void updateWord(WordTopic wordTopic) {
        DialogUtil.progressDlgShow(TopicDetailsActivity.this,"Loading");
        databaseService.updateWordTopic(TopicDetailsActivity.this, wordTopic, folderId, topicId, new DatabaseService.OnListener() {
            @Override
            public void onSuccess() {
                DialogUtil.progressDlgHide();
                Toast.makeText(TopicDetailsActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                DialogUtil.progressDlgHide();
                Toast.makeText(TopicDetailsActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showDialogDelete(WordTopic wordTopic) {
        Dialog dialog = new Dialog(TopicDetailsActivity.this);
        View view = LayoutInflater.from(TopicDetailsActivity.this).inflate(R.layout.dialog_delete, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        TextView no = view.findViewById(R.id.btnNo);
        TextView yes = view.findViewById(R.id.btnYes);
        no.setOnClickListener(v -> dialog.dismiss());

        yes.setOnClickListener(v -> {
            dialog.dismiss();
            DialogUtil.progressDlgShow(TopicDetailsActivity.this, "Loading");
            databaseService.removedWordTopic(TopicDetailsActivity.this, folderId, topicId, wordTopic.getId(), new DatabaseService.OnListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(TopicDetailsActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                    DialogUtil.progressDlgHide();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(TopicDetailsActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                    DialogUtil.progressDlgHide();
                }
            });

        });
    }


    private void initList() {
        wordTopicAdapter = new WordTopicAdapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(wordTopicAdapter);
        wordTopicAdapter.setOnListener(new WordTopicAdapter.OnListener() {
            @Override
            public void onSpeech(WordTopic wordTopic) {
                if (isReady) {
                    textToSpeech.speak(wordTopic.getEnglish(), TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    Toast.makeText(TopicDetailsActivity.this, "Launching reader, please try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(WordTopic wordTopic) {
                showLongClickMenu(wordTopic);
            }
        });
        databaseService.loadWordTopicFromFirebase(this, folderId, topicId, new DatabaseService.OnLoadWordTopicListener() {
            @Override
            public void onSuccess(List<WordTopic> wordTopics) {
                wordTopicAdapter.addList(wordTopics);
            }

            @Override
            public void onFailure() {
                Toast.makeText(TopicDetailsActivity.this, "onFailure", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}