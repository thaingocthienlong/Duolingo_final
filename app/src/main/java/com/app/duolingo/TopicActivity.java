package com.app.duolingo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.duolingo.adapter.TopicAdapter;
import com.app.duolingo.models.Course;
import com.app.duolingo.models.CourseWord;
import com.app.duolingo.models.Topic;
import com.app.duolingo.models.Word;
import com.app.duolingo.models.WordTopic;
import com.app.duolingo.services.DatabaseService;
import com.app.duolingo.utils.DialogUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Objects;

public class TopicActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    private TopicAdapter topicAdapter;
    private DatabaseService databaseService;
    private ImageButton btnAdd;
    private ImageButton btnBack;
    private String folderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        databaseService = new DatabaseService();
        folderId = getIntent().getStringExtra("folderId");
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
        Dialog dialog = new Dialog(TopicActivity.this);
        View view = LayoutInflater.from(TopicActivity.this).inflate(R.layout.dialog_add_topic, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText edtName = view.findViewById(R.id.edtName);
        EditText edtDescription = view.findViewById(R.id.edtDescription);
        TextView no = view.findViewById(R.id.btnNo);
        TextView yes = view.findViewById(R.id.btnYes);
        no.setOnClickListener(v -> dialog.dismiss());

        yes.setOnClickListener(v -> {
            dialog.dismiss();
            String t = edtName.getText().toString().trim();
            String d = edtDescription.getText().toString().trim();
            if (!t.isEmpty() && !d.isEmpty()) {
                Topic topic = new Topic();
                topic.setTitle(t);
                topic.setDescription(d);
                addTopic(topic);
            }
        });
    }

    private void addTopic(Topic topic) {
        DialogUtil.progressDlgShow(this, "Loading");
        databaseService.pushTopicToFirebase(this, topic, folderId, new DatabaseService.OnListener() {
            @Override
            public void onSuccess() {
                DialogUtil.progressDlgHide();
                Toast.makeText(TopicActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                DialogUtil.progressDlgHide();
                Toast.makeText(TopicActivity.this, "Failure!!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initView() {
        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);
        btnBack = findViewById(R.id.btnBack);

    }

    private void initList() {
        topicAdapter = new TopicAdapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(topicAdapter);
        topicAdapter.setOnListener(new TopicAdapter.OnListener() {
            @Override
            public void onClickItem(Topic topic) {
                Intent intent = new Intent(TopicActivity.this, TopicDetailsActivity.class);
                intent.putExtra("folderId", folderId);
                intent.putExtra("topicId", topic.getId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(Topic topic) {
                showLongClickMenu(topic);
            }
        });
        databaseService.loadTopicFromFirebase(this, folderId, new DatabaseService.OnLoadTopicListener() {
            @Override
            public void onSuccess(List<Topic> topics) {
                topicAdapter.addList(topics);
            }

            @Override
            public void onFailure() {
                Toast.makeText(TopicActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showLongClickMenu(Topic topic) {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(TopicActivity.this);
        bottomDialog.setCancelable(true);
        bottomDialog.show();
        bottomDialog.setContentView(R.layout.layout_bottom_sheet);
        View btnAdd = bottomDialog.findViewById(R.id.tvAddtoCourse);
        View btnDelete = bottomDialog.findViewById(R.id.tvDelete);
        View btnEdit = bottomDialog.findViewById(R.id.tvEdit);
        Objects.requireNonNull(btnAdd).setOnClickListener(v -> {
            bottomDialog.dismiss();
            showDialogAddToCourse(topic);
        });
        Objects.requireNonNull(btnDelete).setOnClickListener(v -> {
            bottomDialog.dismiss();
            showDialogDelete(topic);
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomDialog.dismiss();
                showDialogEdit(topic);
            }
        });
        View btnCancel = bottomDialog.findViewById(R.id.cancel);
        Objects.requireNonNull(btnCancel).setOnClickListener(v -> bottomDialog.dismiss());
    }

    private void showDialogAddToCourse(Topic topic) {
        String courseName = topic.getTitle();
        String courseDescription = topic.getDescription();

        if (!courseName.isEmpty() && !courseDescription.isEmpty()) {
            createCourseWithTopicWords(courseName, courseDescription, topic);
            Toast.makeText(TopicActivity.this, "Topic added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(TopicActivity.this, "Course name and description are required", Toast.LENGTH_SHORT).show();
        }
    }

    private void createCourseWithTopicWords(String name, String description, Topic topic) {
        Course newCourse = new Course();
        newCourse.setName(name);
        newCourse.setDescription(description);
        newCourse.setTopicId(topic.getId());

        databaseService.pushCourseToFirebase(newCourse, new DatabaseService.OnListener() {
            @Override
            public void onSuccess() {
                databaseService.loadWordTopicFromFirebase(TopicActivity.this, folderId, topic.getId(), new DatabaseService.OnLoadWordTopicListener() {
                    @Override
                    public void onSuccess(List<WordTopic> wordTopics) {
                        for (WordTopic wordTopic : wordTopics) {
                            createWordAndAddToCourse(wordTopic, newCourse.getId());
                        }
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(TopicActivity.this, "Failed to load words for topic.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure() {
                Toast.makeText(TopicActivity.this, "Failed to create course", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createWordAndAddToCourse(WordTopic wordTopic, String courseId) {
        Word newWord = new Word();
        newWord.setEnglish(wordTopic.getEnglish());
        newWord.setMeaning(wordTopic.getMeaning());
        newWord.setPronounce(wordTopic.getPronounce());
        newWord.setSound("");

        databaseService.pushWordToFirebase(newWord, new DatabaseService.Callback<String>() {
            @Override
            public void onResult(String wordId) {
                CourseWord courseWord = new CourseWord();
                courseWord.setCourseId(courseId);
                courseWord.setWordId(wordId);
                databaseService.addCourseWord(courseWord, new DatabaseService.OnListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure() {
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(TopicActivity.this, "Failed to create word", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogEdit(Topic topic) {
        Dialog dialog = new Dialog(TopicActivity.this);
        View view = LayoutInflater.from(TopicActivity.this).inflate(R.layout.dialog_add_topic, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText edtName = view.findViewById(R.id.edtName);
        EditText edtDescription = view.findViewById(R.id.edtDescription);
        edtName.setText(topic.getTitle());
        edtDescription.setText(topic.getDescription());

        TextView no = view.findViewById(R.id.btnNo);
        TextView yes = view.findViewById(R.id.btnYes);
        no.setOnClickListener(v -> dialog.dismiss());

        yes.setOnClickListener(v -> {
            dialog.dismiss();
            String t = edtName.getText().toString().trim();
            String d = edtDescription.getText().toString().trim();
            if (!t.isEmpty() && !d.isEmpty()) {
                topic.setTitle(t);
                topic.setDescription(d);
                updateTopic(topic);
            }
        });
    }

    private void updateTopic(Topic topic) {
        DialogUtil.progressDlgShow(TopicActivity.this, "Loading");
        databaseService.updateTopic(TopicActivity.this, topic, folderId, new DatabaseService.OnListener() {
            @Override
            public void onSuccess() {
                DialogUtil.progressDlgHide();
                Toast.makeText(TopicActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                DialogUtil.progressDlgHide();
                Toast.makeText(TopicActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showDialogDelete(Topic topic) {
        Dialog dialog = new Dialog(TopicActivity.this);
        View view = LayoutInflater.from(TopicActivity.this).inflate(R.layout.dialog_delete, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView no = view.findViewById(R.id.btnNo);
        TextView yes = view.findViewById(R.id.btnYes);
        no.setOnClickListener(v -> dialog.dismiss());

        yes.setOnClickListener(v -> {
            dialog.dismiss();
            DialogUtil.progressDlgShow(TopicActivity.this, "Loading");
            databaseService.removedTopic(TopicActivity.this, folderId, topic.getId(), new DatabaseService.OnListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(TopicActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                    DialogUtil.progressDlgHide();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(TopicActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                    DialogUtil.progressDlgHide();
                }
            });

        });
    }


}