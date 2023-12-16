package com.app.duolingo;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.app.duolingo.adapter.FolderAdapter;
import com.app.duolingo.models.Folder;
import com.app.duolingo.models.Topic;
import com.app.duolingo.services.DatabaseService;
import com.app.duolingo.utils.DialogUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

public class LibraryFragment extends Fragment {
    private ImageButton btnAdd;
    private DatabaseService databaseService;
    private RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseService = new DatabaseService();
        initView(view);
        initEvent();
        initList();

    }

    private void addFolderDialog() {
        Dialog dialog = new Dialog(requireActivity());
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_add_folder, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText edtName = view.findViewById(R.id.edtName);
        TextView no = view.findViewById(R.id.btnNo);
        TextView yes = view.findViewById(R.id.btnYes);
        no.setOnClickListener(v -> dialog.dismiss());

        yes.setOnClickListener(v -> {
            dialog.dismiss();
            String t = edtName.getText().toString().trim();
            if (!t.isEmpty()) {
                Folder folder = new Folder();
                String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                folder.setTitle(t);
                folder.setUserId(uid);
                addFolder(folder);
            }
        });
    }

    private void addFolder(Folder folder) {
        DialogUtil.progressDlgShow(requireActivity(), "Loading");
        databaseService.pushFolderToFirebase(requireActivity(), folder, new DatabaseService.OnListener() {
            @Override
            public void onSuccess() {
                DialogUtil.progressDlgHide();
            }

            @Override
            public void onFailure() {
                DialogUtil.progressDlgHide();
            }
        });
    }

    FolderAdapter folderAdapter;

    private void initList() {
        folderAdapter = new FolderAdapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
        recyclerView.setAdapter(folderAdapter);
        folderAdapter.setOnListener(new FolderAdapter.OnListener() {
            @Override
            public void onClick(Folder folder) {
                Intent intent = new Intent(requireActivity(), TopicActivity.class);
                intent.putExtra("folderId", folder.getId());
                intent.putExtra("folderName", folder.getTitle());
                startActivity(intent);
            }

            @Override
            public void onLongClick(Folder folder) {
                showLongClickMenu(folder);
            }
        });

        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        databaseService.loadFolderFromFirebase(requireActivity(), uid, new DatabaseService.OnLoadFolderListener() {
            @Override
            public void onSuccess(List<Folder> folders) {
                folderAdapter.addList(folders);
            }

            @Override
            public void onFailure() {
                Toast.makeText(requireActivity(), "Failure", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showLongClickMenu(Folder folder) {
        BottomSheetDialog bottomDialog = new BottomSheetDialog(requireActivity());
        bottomDialog.setCancelable(true);
        bottomDialog.show();
        bottomDialog.setContentView(R.layout.layout_bottom_sheet);
        TextView btnDelete = bottomDialog.findViewById(R.id.tvDelete);
        TextView btnEdit = bottomDialog.findViewById(R.id.tvEdit);
        Objects.requireNonNull(btnDelete).setOnClickListener(v -> {
            bottomDialog.dismiss();
            showDialogDelete(folder);
        });

        Objects.requireNonNull(btnEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomDialog.dismiss();
                showDialogEdit(folder);
            }
        });
        View btnCancel = bottomDialog.findViewById(R.id.cancel);
        Objects.requireNonNull(btnCancel).setOnClickListener(v -> bottomDialog.dismiss());
    }

    private void showDialogEdit(Folder folder) {
        Dialog dialog = new Dialog(requireActivity());
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_add_folder, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText edtName = view.findViewById(R.id.edtName);
        edtName.setText(folder.getTitle());
        TextView no = view.findViewById(R.id.btnNo);
        TextView yes = view.findViewById(R.id.btnYes);
        no.setOnClickListener(v -> dialog.dismiss());

        yes.setOnClickListener(v -> {
            dialog.dismiss();
            String t = edtName.getText().toString().trim();
            if (!t.isEmpty()) {

                String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                folder.setTitle(t);
                folder.setUserId(uid);
                updateFolder(folder);
            }
        });
    }

    private void updateFolder(Folder folder) {
        DialogUtil.progressDlgShow(requireActivity(), "Loading");
        databaseService.updateFolder(requireActivity(), folder, new DatabaseService.OnListener() {
            @Override
            public void onSuccess() {
                DialogUtil.progressDlgHide();
                Toast.makeText(requireActivity(), "onSuccess", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                DialogUtil.progressDlgHide();
                Toast.makeText(requireActivity(), "onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogDelete(Folder folder) {
        Dialog dialog = new Dialog(requireActivity());
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_delete, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView no = view.findViewById(R.id.btnNo);
        TextView yes = view.findViewById(R.id.btnYes);
        no.setOnClickListener(v -> dialog.dismiss());

        yes.setOnClickListener(v -> {
            dialog.dismiss();
            DialogUtil.progressDlgShow(requireActivity(), "Loading");
            databaseService.removedFolder(requireActivity(), folder.getId(), new DatabaseService.OnListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(requireActivity(), "onSuccess", Toast.LENGTH_SHORT).show();
                    DialogUtil.progressDlgHide();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(requireActivity(), "onFailure", Toast.LENGTH_SHORT).show();
                    DialogUtil.progressDlgHide();
                }
            });

        });
    }
    

    private void initEvent() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFolderDialog();
            }
        });
    }

    private void initView(View view) {
        btnAdd = view.findViewById(R.id.btnAdd);
        recyclerView = view.findViewById(R.id.recyclerView);
    }
}