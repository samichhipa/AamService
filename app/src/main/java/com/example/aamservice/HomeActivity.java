package com.example.aamservice;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.aamservice.ui.UploadPost.OperatorFragment;
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

    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_menu);
        preferences=getSharedPreferences("PREF",MODE_PRIVATE);

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

                    if (preferences.getString("purpose","").equals("owner")){
                        UploadPostFragment friendFragment=new UploadPostFragment();
                        setFragment(friendFragment);

                    return true;

                    }else{
                        Toast.makeText(HomeActivity.this, "Only Owner Can Upload Post.", Toast.LENGTH_SHORT).show();
                    }

                case R.id.nav_operator:

                    if (preferences.getString("purpose","").equals("tenant")){
                        OperatorFragment operatorFragment=new OperatorFragment();
                        setFragment(operatorFragment);

                        return true;
                    }

                    else{
                    Toast.makeText(HomeActivity.this, "Only Tenant Can Select Operator", Toast.LENGTH_SHORT).show();
                }


            }


            return false;
        }
    };
}