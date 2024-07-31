package lk.jiat.eshop;

import static android.content.Context.MODE_PRIVATE;

import com.google.firebase.storage.FirebaseStorage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountFragment extends Fragment {

    public static final String TAG = AccountFragment.class.getName();
    private View accountContainer;
    private String username, uEmail, mobile;
    private FirebaseFirestore firestore;
    private TextInputEditText userName, userGGmail, userMobile, userDad1,userDad2,userCity,userDmobile;
    private Button dataUpdateBtn, deliveryUpdateBtn, addProfileImageBtn, SaveDpBtn;
    private String documentId, imagepath;
    private TextView showName,showGmail,showMobile;
    private ImageView profileImage;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        firestore = FirebaseFirestore.getInstance();

        userName = view.findViewById(R.id.AccountUsername);
        userGGmail = view.findViewById(R.id.AccountEmail);
        userMobile = view.findViewById(R.id.AccountMobile);

        showName = view.findViewById(R.id.ProfileName);
        showGmail = view.findViewById(R.id.ProfileEmail);
        showMobile = view.findViewById(R.id.ProfileMobile);

        SaveDpBtn = view.findViewById(R.id.SaaveDpBtn);

        profileImage = view.findViewById(R.id.profileImage);

        userDad1 = view.findViewById(R.id.AccountAddressLine1);
        userDad2 = view.findViewById(R.id.AccountAddressLine2);
        userCity = view.findViewById(R.id.AccountCity);
        userDmobile = view.findViewById(R.id.AccountDeliveryMobile);

        dataUpdateBtn = view.findViewById(R.id.personalDataUpdateBtn);
        deliveryUpdateBtn = view.findViewById(R.id.deliveryUpdatebtn);
        addProfileImageBtn = view.findViewById(R.id.pimageAddBtn);

        accountContainer = view.findViewById(R.id.accountContainer);

        loadAnimation();

        SharedPreferences prefs = getActivity().getSharedPreferences("SIGNED_USER", MODE_PRIVATE);
        String userGmail = prefs.getString("userGmail", null);


        ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {

                            imagepath = result.toString();

                            profileImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            profileImage.setImageURI(result);

                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("IMAGE_PREFS", MODE_PRIVATE).edit();
                            editor.putString("imageUri", result.toString());
                            editor.apply();
                        }
                    }
                });

        if (userGmail != null) {

            searchUserByEmail(userGmail);
            loadProfileImage(userGmail);

            addProfileImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activityResultLauncher.launch("image/*");
                }
            });

            SaveDpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String imageId = UUID.randomUUID().toString();

                    CollectionReference imagesCollection = firestore.collection("user-images");

                    Map<String, Object> imageData = new HashMap<>();
                    imageData.put("userGmail", userGmail);
                    imageData.put("imageId", imageId);

                    imagesCollection.add(imageData)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    if (imagepath != null){
                                        uploadImageToStorage(imageId);
                                    } else{
                                        Toast.makeText(getContext(), "Please Touch Add Button and select Image First?", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                }
            });


        }else{

            int color = ContextCompat.getColor(getContext(), R.color.lowBlack);

            userName.setHint("Your not User . Please Login First");
            userName.setHintTextColor(color);
            userName.setEnabled(false);
            userGGmail.setHint("Your not User . Please Login First");
            userGGmail.setHintTextColor(color);
            userGGmail.setEnabled(false);
            userMobile.setHint("Your not User . Please Login First");
            userMobile.setHintTextColor(color);
            userMobile.setEnabled(false);

        }

        searchDeliveryDetailes(userGmail);

        dataUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String getUserName = String.valueOf(userName.getText());
                String getUserMobile = String.valueOf(userMobile.getText());

                if (!userName.isEnabled()){
                    Toast.makeText(view.getContext(), "Please Login First?", Toast.LENGTH_SHORT).show();
                }else {

                    if (getUserName.isEmpty()){
                        userName.setError("Please enter Display Name");
                    }else if(getUserMobile.isEmpty()){
                        userMobile.setError("Please enter Mobile Number");
                    }else {

                        firestore.collection("users")
                                .whereEqualTo("email", userGmail)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                documentId = document.getId();

                                                if (documentId != null && !documentId.isEmpty()) {
                                                    CollectionReference usersRef = firestore.collection("users");

                                                    Map<String, Object> updates = new HashMap<>();
                                                    updates.put("username", getUserName);
                                                    updates.put("mobile", getUserMobile);

                                                    DocumentReference userDocRef = usersRef.document(documentId);
                                                    userDocRef.update(updates)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(view.getContext(), "Update Successful", Toast.LENGTH_SHORT).show();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(view.getContext(), "Update Failed! Try again later", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(view.getContext(), "Invalid Document ID", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        } else {
                                            Toast.makeText(view.getContext(), "Firestore Query Failed", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                    }
                }

            }
        });

        deliveryUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String getAdLine1 = String.valueOf(userDad1.getText());
                String getAdLine2 = String.valueOf(userDad2.getText());
                String getCity = String.valueOf(userCity.getText());
                String getDMobile = String.valueOf(userDmobile.getText());

                    if (getAdLine1.isEmpty()){
                        userDad1.setError("Please enter Address Line 1 ");
                    }else if(getAdLine2.isEmpty()){
                        userDad2.setError("Please enter Address Line 2");
                    }else if(getCity.isEmpty()){
                        userCity.setError("Please enter City");
                    }else if(getDMobile.isEmpty()){
                        userDmobile.setError("Please enter Delivery Mobile Number");
                    }else {

                        firestore.collection("users")
                                .whereEqualTo("email", userGmail)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                documentId = document.getId();

                                                if (documentId != null && !documentId.isEmpty()) {
                                                    CollectionReference usersRef = firestore.collection("users");

                                                    Map<String, Object> updates = new HashMap<>();
                                                    updates.put("addressLine1", getAdLine1);
                                                    updates.put("addressLine2", getAdLine2);
                                                    updates.put("city", getCity);
                                                    updates.put("deliveryMobile", getDMobile);

                                                    DocumentReference userDocRef = usersRef.document(documentId);
                                                    userDocRef.update(updates)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(view.getContext(), "Update Successful", Toast.LENGTH_SHORT).show();
                                                                    searchDeliveryDetailes(userGmail);
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(view.getContext(), "Update Failed! Try again later", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                } else {
                                                    Toast.makeText(view.getContext(), "Invalid Document ID", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        } else {
                                            Toast.makeText(view.getContext(), "Firestore Query Failed", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });


                }

            }
        });

        return view;

    }

    private void uploadImageToStorage(String imageId) {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference().child("user-images");

        StorageReference imageRef = storageRef.child(imageId + ".jpg");

        Uri imageUri = Uri.parse(imagepath);
        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(getContext(), "Profile Image Added Successfull", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    private void searchUserByEmail(String email) {
        firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            username = documentSnapshot.getString("username");
                            mobile = documentSnapshot.getString("mobile");
                            uEmail = documentSnapshot.getString("email");

                            userName.setText(username);
                            userGGmail.setText(uEmail);
                            userMobile.setText(mobile);

                            showName.setText(username);
                            showGmail.setText(uEmail);
                            showMobile.setText(mobile);

                            int color = ContextCompat.getColor(getContext(), R.color.lowBlack);
                            userGGmail.setEnabled(false);
                            userGGmail.setTextColor(color);

                            Log.i(TAG, "User Data: Username - " + username + ", Mobile - " + mobile);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error searching for user by email", e);
                    }
                });
    }

    public void loadProfileImage(String uEmail) {

        CollectionReference userImagesRef = firestore.collection("user-images");

        userImagesRef.whereEqualTo("userGmail", uEmail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String imageId = documentSnapshot.getString("imageId");
                            if (imageId != null) {
                                fetchImageFromStorage(imageId);
                            } else {
                                profileImage.setImageResource(R.drawable.baseline_account_circle_24);
                            }
                            return;
                        }
                        profileImage.setImageResource(R.drawable.baseline_account_circle_24);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        profileImage.setImageResource(R.drawable.baseline_account_circle_24);
                    }
                });
    }

    private void fetchImageFromStorage(String imageId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("user-images");
        StorageReference imageRef = storageRef.child(imageId + ".jpg");

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                profileImage.setImageResource(R.drawable.baseline_account_circle_24);
            }
        });
    }


    public void loadAnimation(){

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(accountContainer, "scaleX", 1f, 0.98f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(accountContainer, "scaleY", 1f, 0.98f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(accountContainer, "alpha", 1f, 0.6f);

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

    public void searchDeliveryDetailes(String usermail){
        firestore.collection("users")
                .whereEqualTo("email", usermail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                int color = ContextCompat.getColor(getContext(), R.color.lowBlack);

                                String AdLine1 = document.get("addressLine1").toString();
                                String AdLine2 = document.get("addressLine2").toString();
                                String city = document.get("city").toString();
                                String Dmobile = document.get("deliveryMobile").toString();

                                userDad1.setText(AdLine1);
                                userDad2.setText(AdLine2);
                                userCity.setText(city);
                                userDmobile.setText(Dmobile);

                            }

                        }
                    }
                });

    }
}