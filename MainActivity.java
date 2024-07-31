package lk.jiat.eshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener {

    public static final String TAG = MainActivity.class.getName();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore firestore;
    private TextView userName,userGmailView;
    private ImageView userImage;
    private String username, uEmail, mobile,userGmail;
    public static final int SLIDE_NAV_HOME = R.id.slideNaveHome;
    public static final int SLIDE_NAV_LOGIN = R.id.SlideNavLogin;
    public static final int SLIDE_NAV_LOGOUT = R.id.SlideNavLogout;
    public static final int SLIDE_NAV_Account = R.id.SlideNavAccount;
    public static final int SLIDE_NAV_AboutUs = R.id.SlideNavAbout;
    public static final int SLIDE_NAV_Chat = R.id.SlideNavMessage;
    public static final int SLIDE_NAV_Cart = R.id.SlideNavSettings;
    public static final int Bottom_NAV_Account = R.id.bottomNavAccount;
    public static final int SLIDE_NAV_Wish = R.id.SlideNavWishlist;
    public static final int Bottom_NAV_Home = R.id.bottomNavHome;
    public static final int Bottom_NAV_Cart = R.id.bottomNavCart;
    public static final int Bottom_NAV_Orders = R.id.bottpmNavOrders;
    private MenuItem menuItem;

    private MenuItem navCategory,navHome,navAccount,navOrders,navWishlist,navMessage,navSetting,navLogin,navLogout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentContainerView container = findViewById(R.id.container);

        loadFragment(new HomeFragment());

        drawerLayout = findViewById(R.id.drawer1);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        userImage = findViewById(R.id.userDp);

        firestore = FirebaseFirestore.getInstance();
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        Menu navMenu = navigationView.getMenu();

         navCategory = navMenu.findItem(R.id.SlideNavCategory);
         navHome = navMenu.findItem(R.id.slideNaveHome);
         navAccount = navMenu.findItem(R.id.SlideNavAccount);
         navOrders = navMenu.findItem(R.id.SlideNavAbout);
         navWishlist = navMenu.findItem(R.id.SlideNavWishlist);
         navMessage = navMenu.findItem(R.id.SlideNavMessage);
         navSetting = navMenu.findItem(R.id.SlideNavSettings);
         navLogin = navMenu.findItem(R.id.SlideNavLogin);
         navLogout = navMenu.findItem(R.id.SlideNavLogout);


        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnItemSelectedListener(this);

        SharedPreferences prefs = getSharedPreferences("SIGNED_USER", MODE_PRIVATE);
        userGmail = prefs.getString("userGmail", null);

        if (userGmail != null) {

            searchUserByEmail(userGmail);
            loadProfileImage(userGmail);

        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout
                ,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userName = findViewById(R.id.userNameText);
                userGmailView = findViewById(R.id.userGmail);
                userImage = findViewById(R.id.userDp);

                loadProfileImage(userGmail);

                if (username != null) {
                    userName.setText(username);
                } else {
                    userName.setText("Default Username");
                }

                if (uEmail != null) {

                    userGmailView.setText(uEmail);

                    navHome.setVisible(true);
                    navAccount.setVisible(true);
                    navOrders.setVisible(true);
                    navMessage.setVisible(true);
                    navWishlist.setVisible(true);
                    navSetting.setVisible(true);
                    navLogout.setVisible(true);

                    navLogin.setVisible(false);

                } else {

                    userGmailView.setText("Default Gmail");

                    navHome.setVisible(true);
                    navLogin.setVisible(true);

                    navAccount.setVisible(false);
                    navOrders.setVisible(false);
                    navMessage.setVisible(false);
                    navWishlist.setVisible(false);
                    navSetting.setVisible(false);
                    navLogout.setVisible(false);

                }

                Drawable drawable = getResources().getDrawable(R.drawable.baseline_account_circle_24);
                userImage.setImageDrawable(drawable);

                drawerLayout.open();

            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("key")) {
            String receivedValue = intent.getStringExtra("key");

            if ("LoadCart".equals(receivedValue)) {
                loadFragment(new CartFragment());
                menuItem = bottomNavigationView.getMenu().findItem(R.id.bottomNavCart);
                menuItem.setChecked(true);
            } else if ("LoadWish".equals(receivedValue)){
                loadFragment(new WishListFragment());
            }
        }


    }

    public void loadProfileImage(String uEmail) {

        userImage = findViewById(R.id.userDp);

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
                                if (userImage != null){
                                    userImage.setImageResource(R.drawable.baseline_account_circle_24);
                                }
                            }
                            return;
                        }
                        if (userImage != null){
                            userImage.setImageResource(R.drawable.baseline_account_circle_24);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (userImage != null){
                            userImage.setImageResource(R.drawable.baseline_account_circle_24);
                        }
                    }
                });
    }

    private void fetchImageFromStorage(String imageId) {

        userImage = findViewById(R.id.userDp);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("user-images");
        StorageReference imageRef = storageRef.child(imageId + ".jpg");

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(userImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (userImage != null){
                    userImage.setImageResource(R.drawable.baseline_account_circle_24);
                }
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

                            // User found, you can retrieve and use the data
                             username = documentSnapshot.getString("username");
                             mobile = documentSnapshot.getString("mobile");
                             uEmail = documentSnapshot.getString("email");


                            // Do something with the user data
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == Bottom_NAV_Account) {

            loadFragment(new AccountFragment());

            navAccount.setChecked(true);

            navLogin.setChecked(false);
            navHome.setChecked(false);
            navLogout.setChecked(false);
            navCategory.setChecked(false);
            navMessage.setChecked(false);
            navOrders.setChecked(false);
            navSetting.setChecked(false);
            navWishlist.setChecked(false);

        } else if (item.getItemId() == Bottom_NAV_Home) {

            loadFragment(new HomeFragment());

            navHome.setChecked(true);

            navLogin.setChecked(false);
            navAccount.setChecked(false);
            navLogout.setChecked(false);
            navCategory.setChecked(false);
            navMessage.setChecked(false);
            navOrders.setChecked(false);
            navSetting.setChecked(false);
            navWishlist.setChecked(false);

        }else if (item.getItemId() == Bottom_NAV_Cart) {

            loadFragment(new CartFragment());

            navSetting.setChecked(true);

            navHome.setChecked(false);
            navLogin.setChecked(false);
            navAccount.setChecked(false);
            navLogout.setChecked(false);
            navCategory.setChecked(false);
            navMessage.setChecked(false);
            navOrders.setChecked(false);
            navWishlist.setChecked(false);

        }else if (item.getItemId() == Bottom_NAV_Orders) {

            loadFragment(new OrdersFragment());

            navOrders.setChecked(false);
            navHome.setChecked(false);
            navLogin.setChecked(false);
            navAccount.setChecked(false);
            navLogout.setChecked(false);
            navCategory.setChecked(false);
            navMessage.setChecked(false);
            navSetting.setChecked(false);
            navWishlist.setChecked(false);

        }

        if (item.getItemId() == SLIDE_NAV_LOGIN){

            Intent intent = new Intent(MainActivity.this,LogInActivity.class);
            startActivity(intent);

            navLogin.setChecked(true);

            navAccount.setChecked(false);
            navHome.setChecked(false);
            navLogout.setChecked(false);
            navCategory.setChecked(false);
            navMessage.setChecked(false);
            navOrders.setChecked(false);
            navSetting.setChecked(false);
            navWishlist.setChecked(false);

        } else if (item.getItemId() == SLIDE_NAV_HOME) {
           loadFragment(new HomeFragment());

            navLogin.setChecked(false);
            navAccount.setChecked(false);

            navHome.setChecked(true);

            navLogout.setChecked(false);
            navCategory.setChecked(false);
            navMessage.setChecked(false);
            navOrders.setChecked(false);
            navSetting.setChecked(false);
            navWishlist.setChecked(false);

            //close navigation
            drawerLayout.closeDrawer(GravityCompat.START);

            MenuItem menuItem1 = bottomNavigationView.getMenu().findItem(R.id.bottomNavCart);
            MenuItem menuItem2 = bottomNavigationView.getMenu().findItem(R.id.bottomNavHome);
            MenuItem menuItem3 = bottomNavigationView.getMenu().findItem(R.id.bottomNavAccount);
            MenuItem menuItem4 = bottomNavigationView.getMenu().findItem(R.id.bottpmNavOrders);

            menuItem1.setChecked(false);
            menuItem2.setChecked(true);
            menuItem3.setChecked(false);
            menuItem4.setChecked(false);


        } else if (item.getItemId() == SLIDE_NAV_LOGOUT) {

            navLogin.setChecked(false);
            navAccount.setChecked(false);
            navHome.setChecked(false);

            navLogout.setChecked(true);

            navCategory.setChecked(false);
            navMessage.setChecked(false);
            navOrders.setChecked(false);
            navSetting.setChecked(false);
            navWishlist.setChecked(false);

           logOut();

        }else if (item.getItemId() == SLIDE_NAV_Account) {
            loadFragment(new AccountFragment());

            navAccount.setChecked(true);

            navHome.setChecked(false);
            navLogout.setChecked(false);
            navCategory.setChecked(false);
            navLogin.setChecked(false);
            navMessage.setChecked(false);
            navOrders.setChecked(false);
            navSetting.setChecked(false);
            navWishlist.setChecked(false);

            drawerLayout.closeDrawer(GravityCompat.START);

            MenuItem menuItem1 = bottomNavigationView.getMenu().findItem(R.id.bottomNavCart);
            MenuItem menuItem2 = bottomNavigationView.getMenu().findItem(R.id.bottomNavHome);
            MenuItem menuItem3 = bottomNavigationView.getMenu().findItem(R.id.bottomNavAccount);
            MenuItem menuItem4 = bottomNavigationView.getMenu().findItem(R.id.bottpmNavOrders);

            menuItem1.setChecked(false);
            menuItem2.setChecked(false);
            menuItem3.setChecked(true);
            menuItem4.setChecked(false);

        }else if (item.getItemId() == SLIDE_NAV_Cart) {
            loadFragment(new CartFragment());

            navSetting.setChecked(true);

            navHome.setChecked(false);
            navLogout.setChecked(false);
            navCategory.setChecked(false);
            navLogin.setChecked(false);
            navMessage.setChecked(false);
            navOrders.setChecked(false);
            navAccount.setChecked(false);
            navWishlist.setChecked(false);

            drawerLayout.closeDrawer(GravityCompat.START);

            MenuItem menuItem1 = bottomNavigationView.getMenu().findItem(R.id.bottomNavCart);
            MenuItem menuItem2 = bottomNavigationView.getMenu().findItem(R.id.bottomNavHome);
            MenuItem menuItem3 = bottomNavigationView.getMenu().findItem(R.id.bottomNavAccount);
            MenuItem menuItem4 = bottomNavigationView.getMenu().findItem(R.id.bottpmNavOrders);

            menuItem1.setChecked(true);
            menuItem2.setChecked(false);
            menuItem3.setChecked(false);
            menuItem4.setChecked(false);

        }else if (item.getItemId() == SLIDE_NAV_AboutUs) {

            Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
            startActivity(intent);

            navSetting.setChecked(false);
            navHome.setChecked(false);
            navLogout.setChecked(false);
            navCategory.setChecked(false);
            navLogin.setChecked(false);
            navMessage.setChecked(false);

            navOrders.setChecked(true);

            navAccount.setChecked(false);
            navWishlist.setChecked(false);

            drawerLayout.closeDrawer(GravityCompat.START);

        }else if (item.getItemId() == SLIDE_NAV_Wish) {

         loadFragment(new WishListFragment());

            navSetting.setChecked(false);
            navHome.setChecked(false);
            navLogout.setChecked(false);
            navCategory.setChecked(false);
            navLogin.setChecked(false);
            navMessage.setChecked(false);
            navOrders.setChecked(false);
            navAccount.setChecked(false);

            navWishlist.setChecked(true);

            drawerLayout.closeDrawer(GravityCompat.START);

           MenuItem menuItem1 = bottomNavigationView.getMenu().findItem(R.id.bottomNavCart);
           MenuItem menuItem2 = bottomNavigationView.getMenu().findItem(R.id.bottomNavHome);
           MenuItem menuItem3 = bottomNavigationView.getMenu().findItem(R.id.bottomNavAccount);
           MenuItem menuItem4 = bottomNavigationView.getMenu().findItem(R.id.bottpmNavOrders);

           menuItem1.setChecked(false);
           menuItem2.setChecked(false);
           menuItem3.setChecked(false);
           menuItem4.setChecked(false);

        }else if (item.getItemId() == SLIDE_NAV_Chat) {

            loadFragment(new ChatFragment());

            navSetting.setChecked(false);
            navHome.setChecked(false);
            navLogout.setChecked(false);
            navCategory.setChecked(false);
            navLogin.setChecked(false);

            navMessage.setChecked(true);

            navOrders.setChecked(false);
            navAccount.setChecked(false);
            navWishlist.setChecked(false);

            drawerLayout.closeDrawer(GravityCompat.START);

        }


        return true;
    }

    public void loadFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.container, fragment);

        // Add the transaction to the back stack if needed
        fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void logOut(){

        FirebaseAuth.getInstance().signOut();

        SharedPreferences sharedPreferences = getSharedPreferences("SIGNED_USER", getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(MainActivity.this, "Log out Successful", Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


}