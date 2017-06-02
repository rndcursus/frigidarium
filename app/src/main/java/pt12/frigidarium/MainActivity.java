package pt12.frigidarium;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setTitle("Frigidarium");
        View headerView = navigationView.getHeaderView(0);
        TextView email_tv = (TextView) headerView.findViewById(R.id.email_tv);
        email_tv.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        TextView name_tv = (TextView) headerView.findViewById(R.id.name_tv);
        name_tv.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        //codeView = (TextView) findViewById(R.id.code_info);

        //SET MENU ITEM TO STOCK LIST
        navigationView.getMenu().getItem(1).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(1));
        requestPermissionsForCamera();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        navigationView.getMenu().getItem(1).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(1));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Intent intent = null;



        if (id == R.id.nav_camera) {

            if(permissionsGranted()){
                // CHECK IF PERMISSIONS GRANTED. IF NOT, REQUEST PERMISSIONS.
                intent = new Intent(this, BarcodeScanActivity.class);
                startActivity(intent);
            }
            else{
                requestPermissionsForCamera();
            }
        } else if (id == R.id.nav_stocklist) {
            fragment = StockFragment.newInstance(true);
        } else if (id == R.id.nav_shoppinglist) {
            fragment = StockFragment.newInstance(false);
        } else if (id == R.id.nav_manage) {
            fragment= new SettingsFragment();
        } else if (id == R.id.nav_stockchoose){
            fragment = StockListFragment.newInstance(1);
        }/*else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment, "FRAGMENT").commit();
        }else{
            Fragment oldFragment = fragmentManager.findFragmentByTag("FRAGMENT");
            if(oldFragment != null){
                fragmentManager.beginTransaction().remove(oldFragment).commit();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**
     * FUNCTION TO CHECK IF CAMERA PERMISSION IS GRANTED
     * @return
     */
    private boolean permissionsGranted(){
        String permission = "android.permission.CAMERA";
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * FUNCTION THAT REQUESTS THE PERMISSION FOR THE CAMERA
     */
    private void requestPermissionsForCamera(){
        final int PERMISSION_CODE = 123; // USED FOR CAMERA PERMISSIONS
        requestPermissions(new String[]{android.Manifest.permission.CAMERA}, PERMISSION_CODE); // REQUEST CAMERA PERMISSION
    }


}