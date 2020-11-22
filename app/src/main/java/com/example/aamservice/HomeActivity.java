package com.example.aamservice;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.aamservice.ui.UploadPost.UploadPostFragment;
import com.example.aamservice.ui.posts.PostsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_menu);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        Fragment fragment=new PostsFragment();
        setFragment(fragment);

    }

    public void setFragment(Fragment fragment){


        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment,fragment);
        fragmentTransaction.commit();

    }
    public BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


            switch (menuItem.getItemId()){

                case R.id.nav_post:

                    PostsFragment chatFragment=new PostsFragment();
                    setFragment(chatFragment);

                    return true;

                case R.id.nav_upload_post:

                    UploadPostFragment friendFragment=new UploadPostFragment();
                    setFragment(friendFragment);

                    return true;




            }


            return false;
        }
    };
}