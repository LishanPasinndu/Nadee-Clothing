package lk.jiat.eshop;

import static android.content.Context.MODE_PRIVATE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import lk.jiat.eshop.adapter.ChatAdapter;
import lk.jiat.eshop.adapter.OrderAdapter;
import lk.jiat.eshop.model.Message;
import lk.jiat.eshop.model.Orders;

public class ChatFragment extends Fragment {

    private ConstraintLayout main;
    private EditText messageText;
    private ImageButton sendBtn;
    private FirebaseFirestore firestore;
    private String uGmial;
    private ArrayList<Message> messages;
    private ChatAdapter itemAdapter;
    private RecyclerView chatView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        main = view.findViewById(R.id.main);

        firestore = FirebaseFirestore.getInstance();

        linearLayoutManager = new LinearLayoutManager(view.getContext());

        SharedPreferences prefs = getContext().getSharedPreferences("SIGNED_USER", MODE_PRIVATE);
        uGmial = prefs.getString("userGmail", null);

        sendBtn = view.findViewById(R.id.sendBtn);
        messageText = view.findViewById(R.id.messageInput);
        chatView = view.findViewById(R.id.chatRecyclerView);

        loadChat();
        loadAnimation();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String chat = messageText.getText().toString();

                if (chat.isEmpty()){
                    messageText.setError("Please enter your message first!");
                }else {

                    Calendar calendar = Calendar.getInstance();
                    Date now = calendar.getTime();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDate = dateFormat.format(now);

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                    String formattedTime = timeFormat.format(now);

                    Message messaage = new Message(uGmial,chat,formattedDate,formattedTime);
                    firestore.collection("Chat").add(messaage)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getContext(),"Message Sent",Toast.LENGTH_LONG).show();
                                    messageText.setText("");

                                    loadChat();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(),"Message cant Sent. Try Again Later",Toast.LENGTH_LONG).show();
                                }
                            });

                }

            }
        });

        return view;
    }

    public void loadAnimation(){

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(main, "scaleX", 1f, 0.98f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(main, "scaleY", 1f, 0.98f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(main, "alpha", 1f, 0.6f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, alpha);

        animatorSet.setDuration(700);

        final boolean[] isReverseAnimationTriggered = {false};

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isReverseAnimationTriggered[0]) {
                    animatorSet.reverse();
                    isReverseAnimationTriggered[0] = true;
                }
            }
        });

        animatorSet.start();
    }

    public void loadChat() {

        messages = new ArrayList<>();
        itemAdapter = new ChatAdapter(messages, ChatFragment.this);
        chatView.setLayoutManager(linearLayoutManager);
        chatView.setAdapter(itemAdapter);

        firestore.collection("Chat")
                .whereEqualTo("user", uGmial)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        for (DocumentChange change : value.getDocumentChanges()) {
                            Message item = change.getDocument().toObject(Message.class);
                            switch (change.getType()) {
                                case ADDED:
                                    messages.add(item);
                                    break;
                                case REMOVED:
                                    messages.remove(item);
                                    break;
                            }
                        }

                        itemAdapter.notifyDataSetChanged();
                    }
                });
    }


}