package com.app.duolingo.services;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.app.duolingo.models.Course;
import com.app.duolingo.models.CourseWord;
import com.app.duolingo.models.Folder;
import com.app.duolingo.models.Point;
import com.app.duolingo.models.Progress;
import com.app.duolingo.models.Topic;
import com.app.duolingo.models.Word;
import com.app.duolingo.models.WordTopic;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseService {

    private DatabaseReference databaseReference;

    public DatabaseService() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public interface OnListener {
        void onSuccess();

        void onFailure();
    }

    public interface OnLoadFolderListener {
        void onSuccess(List<Folder> folders);

        void onFailure();
    }

    public interface OnLoadTopicListener {
        void onSuccess(List<Topic> topics);

        void onFailure();
    }

    public interface OnLoadWordTopicListener {
        void onSuccess(List<WordTopic> wordTopics);

        void onFailure();
    }

    public void addCourseWord(CourseWord courseWord, OnListener onListener) {
        String key = databaseReference.child("course_words").push().getKey();
        courseWord.setId(key);

        if (key != null) {
            databaseReference.child("course_words").child(key).setValue(courseWord)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            onListener.onSuccess();
                        } else {
                            onListener.onFailure();
                        }
                    })
                    .addOnFailureListener(e -> onListener.onFailure());
        } else {
            onListener.onFailure();
        }
    }

    public void pushWordToFirebase(Word word, Callback<String> callback) {
        String key = databaseReference.child("words").push().getKey();
        word.setId(key);

        if (key != null) {
            databaseReference.child("words").child(key).setValue(word)
                    .addOnSuccessListener(aVoid -> {
                        callback.onResult(key);
                    })
                    .addOnFailureListener(e -> {
                        callback.onError(e);
                    });
        } else {
            callback.onError(new Exception("Failed to generate unique key for word"));
        }
    }

    public void pushCourseToFirebase(Course course, OnListener onListener) {
        String key = databaseReference.child("courses").push().getKey();
        course.setId(key);

        if (key != null) {
            databaseReference.child("courses").child(key).setValue(course)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            onListener.onSuccess();
                        } else {
                            onListener.onFailure();
                        }
                    })
                    .addOnFailureListener(e -> onListener.onFailure());
        } else {
            onListener.onFailure();
        }
    }


    public void updateWordTopic(Activity activity, WordTopic wordTopic, String folderId, String topicId, OnListener onListener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("folder").child(folderId).child("topic")
                .child(topicId).child("word").child(wordTopic.getId());
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("english", wordTopic.getEnglish());
        updateMap.put("meaning", wordTopic.getMeaning());
        updateMap.put("pronounce", wordTopic.getPronounce());

        databaseReference.updateChildren(updateMap)
                .addOnCompleteListener(task -> {
                    if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                        return;
                    }
                    if (task.isSuccessful()) {
                        onListener.onSuccess();
                    } else {
                        onListener.onFailure();
                    }
                });
    }
    public void updateTopic(Activity activity, Topic topic, String folderId, OnListener onListener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("folder").child(folderId).child("topic")
                .child(topic.getId());
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("title", topic.getTitle());
        updateMap.put("description", topic.getDescription());

        databaseReference.updateChildren(updateMap)
                .addOnCompleteListener(task -> {
                    if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                        return;
                    }
                    if (task.isSuccessful()) {
                        onListener.onSuccess();
                    } else {
                        onListener.onFailure();
                    }
                });
    }
    public void updateFolder(Activity activity, Folder folder, OnListener onListener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("folder").child(folder.getId());
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("title", folder.getTitle());

        databaseReference.updateChildren(updateMap)
                .addOnCompleteListener(task -> {
                    if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                        return;
                    }
                    if (task.isSuccessful()) {
                        onListener.onSuccess();
                    } else {
                        onListener.onFailure();
                    }
                });
    }


    public void removedWordTopic(Activity activity, String folderId, String topicId, String wordId, OnListener onListener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("folder").child(folderId).child("topic")
                .child(topicId).child("word").child(wordId);
        databaseReference.removeValue()
                .addOnCompleteListener(task -> {
                    if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                        return;
                    }
                    if (task.isSuccessful()) {
                        onListener.onSuccess();
                    } else {
                        onListener.onFailure();
                    }
                });
    }

    public void removedTopic(Activity activity, String folderId, String topicId, OnListener onListener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("folder").child(folderId).child("topic")
                .child(topicId);
        databaseReference.removeValue()
                .addOnCompleteListener(task -> {
                    if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                        return;
                    }
                    if (task.isSuccessful()) {
                        onListener.onSuccess();
                    } else {
                        onListener.onFailure();
                    }
                });
    }

    public void removedFolder(Activity activity, String folderId, OnListener onListener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("folder").child(folderId);
        databaseReference.removeValue()
                .addOnCompleteListener(task -> {
                    if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                        return;
                    }
                    if (task.isSuccessful()) {
                        onListener.onSuccess();
                    } else {
                        onListener.onFailure();
                    }
                });
    }


    public void loadWordTopicFromFirebase(Activity activity, String folderId, String topicId, OnLoadWordTopicListener onLoadWordTopicListener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("folder").child(folderId).child("topic")
                .child(topicId).child("word");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                    return;
                }
                List<WordTopic> wordTopics = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    WordTopic wordTopic = snapshot.getValue(WordTopic.class);
                    wordTopics.add(wordTopic);
                }
                onLoadWordTopicListener.onSuccess(wordTopics);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                    return;
                }
                onLoadWordTopicListener.onFailure();
            }
        });
    }


    public void loadTopicFromFirebase(Activity activity, String folderId, OnLoadTopicListener loadTopicListener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("folder").child(folderId).child("topic");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                    return;
                }
                List<Topic> topics = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Topic topic = snapshot.getValue(Topic.class);
                    topics.add(topic);
                }
                loadTopicListener.onSuccess(topics);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                    return;
                }
                loadTopicListener.onFailure();
            }
        });
    }


    public void loadFolderFromFirebase(Activity activity, String userId, OnLoadFolderListener onLoadFolderListener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("folder");


        databaseReference.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                    return;
                }
                List<Folder> folders = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Folder folder = snapshot.getValue(Folder.class);
                    folders.add(folder);
                }
                onLoadFolderListener.onSuccess(folders);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                    return;
                }
                onLoadFolderListener.onFailure();
            }

        });
    }

    public void pushTopicToFirebase(Activity activity, Topic topic, String folderId, OnListener onListener) {
        String key = databaseReference.child("folder").child(folderId).child("topic").push().getKey();
        topic.setId(key);
        if (key != null) {
            databaseReference.child("folder").child(folderId).child("topic").child(key).setValue(topic).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                        return;
                    }
                    if (task.isSuccessful()) {
                        onListener.onSuccess();
                    } else {
                        onListener.onFailure();
                    }
                }
            });
        } else {
            onListener.onFailure();
        }

    }

    public void pushWordTopicToFirebase(Activity activity, WordTopic wordTopic, String folderId, String topicId, OnListener onListener) {
        String key = databaseReference.child("folder").child(folderId).child("topic").child(topicId).child("word").push().getKey();
        wordTopic.setId(key);
        if (key != null) {
            databaseReference.child("folder").child(folderId).child("topic").child(topicId).child("word").child(key).setValue(wordTopic).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                        return;
                    }
                    if (task.isSuccessful()) {
                        onListener.onSuccess();
                    } else {
                        onListener.onFailure();
                    }
                }
            });
        } else {
            onListener.onFailure();
        }

    }


    public void pushFolderToFirebase(Activity activity, Folder folder, OnListener onListener) {
        String key = databaseReference.child("folder").push().getKey();
        folder.setId(key);
        if (key != null) {
            databaseReference.child("folder").child(key).setValue(folder).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
                        return;
                    }
                    if (task.isSuccessful()) {
                        onListener.onSuccess();
                    } else {
                        onListener.onFailure();
                    }
                }
            });
        } else {
            onListener.onFailure();
        }

    }

    public void updateLearnProgress(String progressRecordRefId, int progressPercentage, final Callback<Void> callback) {
        DatabaseReference progressUpdateRef = FirebaseDatabase.getInstance().getReference()
                .child("progress").child(progressRecordRefId);

        progressUpdateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer currentLearnProgress = dataSnapshot.child("learnProgress").getValue(Integer.class);
                    if (currentLearnProgress == null || progressPercentage > currentLearnProgress) {
                        progressUpdateRef.child("learnProgress").setValue(progressPercentage)
                                .addOnSuccessListener(aVoid -> callback.onResult(null))
                                .addOnFailureListener(callback::onError);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void getProgressRecordRefId(String userId, String courseId, final Callback<String> callback) {
        DatabaseReference progressRef = FirebaseDatabase.getInstance().getReference().child("progress");
        progressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean recordFound = false;
                String progressRecordRefId = null;

                for (DataSnapshot progressSnapshot : dataSnapshot.getChildren()) {
                    String userIdInRecord = progressSnapshot.child("userId").getValue(String.class);
                    String courseIdInRecord = progressSnapshot.child("courseId").getValue(String.class);

                    if (userId.equals(userIdInRecord) && courseId.equals(courseIdInRecord)) {
                        progressRecordRefId = progressSnapshot.getKey();
                        recordFound = true;
                        break;
                    }
                }

                if (!recordFound) {
                    DatabaseReference newProgressRef = FirebaseDatabase.getInstance().getReference().child("progress").push();
                    progressRecordRefId = newProgressRef.getKey();
                    Progress newProgress = new Progress();
                    newProgress.setUserId(userId);
                    newProgress.setCourseId(courseId);
                    newProgress.setLearnProgress(0);
                    newProgress.setReviewProgress(0);
                    newProgressRef.setValue(newProgress);
                    callback.onResult(newProgressRef.getKey());
                }

                if (recordFound) {
                    callback.onResult(progressRecordRefId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void fetchCourseWords(String courseId, final Callback<List<Word>> callback) {
        List<Task<DataSnapshot>> tasks = new ArrayList<>();

        databaseReference.child("course_words").orderByChild("courseId").equalTo(courseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                CourseWord courseWord = snapshot.getValue(CourseWord.class);
                                if (courseWord != null) {
                                    Task<DataSnapshot> wordFetchTask = databaseReference.child("words").child(courseWord.getWordId()).get();
                                    tasks.add(wordFetchTask);
                                }
                            }

                            Tasks.whenAllComplete(tasks).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                                @Override
                                public void onComplete(@NonNull Task<List<Task<?>>> task) {
                                    if (task.isSuccessful()) {
                                        List<Word> words = new ArrayList<>();
                                        for (Task<DataSnapshot> wordTask : tasks) {
                                            DataSnapshot wordSnapshot = wordTask.getResult();
                                            Word word = wordSnapshot.getValue(Word.class);
                                            if (word != null) {
                                                words.add(word);
                                            }
                                        }
                                        callback.onResult(words);
                                    } else {
                                        callback.onError(task.getException());
                                    }
                                }
                            });
                        } else {
                            callback.onResult(new ArrayList<>()); // No course words found
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onError(databaseError.toException());
                    }
                });
    }

    public void savePoint(Point point, SavePointCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("points").push().setValue(point)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }

    public interface SavePointCallback {
        void onSuccess();

        void onFailure(Exception e);
    }

    // Define the Callback interface if it's not already globally defined
    public interface Callback<T> {
        void onResult(T result);

        void onError(Exception e);
    }
}
