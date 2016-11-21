package com.example.davidbuscholl.veranstalter.GUI.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.davidbuscholl.veranstalter.R;
import com.example.davidbuscholl.veranstalter.Helpers.ValidationRules;
import com.example.davidbuscholl.veranstalter.GUI.ServerErrorDialog;
import com.example.davidbuscholl.veranstalter.Helpers.Validation;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginRegisterActivity extends AppCompatActivity {
    public static LoginRegisterActivity lra;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lra = this;
        setContentView(R.layout.activity_login_register);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        /*
      The {@link ViewPager} that will host the section contents.
     */
        ViewPager mViewPager = (ViewPager) findViewById(R.id.logregContainer);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.logregTabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    /**
     * A placeholder fragment containing a simple view.
     */

    public static class LoginFragment extends Fragment {
        public LoginFragment() {
        }

        public static LoginFragment newInstance() {
            return new LoginFragment();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            final View rootview = inflater.inflate(R.layout.fragment_login, container, false);
            Button login = (Button) rootview.findViewById(R.id.loginButton);
            final EditText username = (EditText) rootview.findViewById(R.id.loginUsername);
            final EditText password = (EditText) rootview.findViewById(R.id.loginPassword);

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog progress = new ProgressDialog(getContext());
                    progress.setTitle("Ladevorgang");
                    progress.setMessage("Bitte warten...");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();

                    RequestQueue queue = Volley.newRequestQueue(rootview.getContext());
                    String url = "http://37.221.196.48/thesis/public/user/login";


                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progress.dismiss();
                            Log.d(LoginRegisterActivity.class.toString(), response);
                            try {
                                JSONObject ob = new JSONObject(response);
                                if (ob.has("error")) {
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Fehler")
                                            .setMessage("Benutzername oder Password falsch")
                                            .setIcon(R.drawable.ic_warning_black_24dp)
                                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).create().show();
                                } else {
                                    getContext().getSharedPreferences("de.dbuscholl.veranstalter", Context.MODE_PRIVATE).edit().putString("token", ob.getString("token")).apply();
                                    MainActivity.postLogin(getContext(),ob);
                                    lra.finish();
                                }
                            } catch (Exception e) {
                                ServerErrorDialog.show(getContext());
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("username", username.getText().toString().trim());
                            params.put("password", password.getText().toString().trim());
                            return params;
                        }
                    };
                    queue.add(stringRequest);

                }
            });
            return rootview;
        }
    }

    public static class RegisterFragment extends Fragment {
        public RegisterFragment() {
        }

        public static RegisterFragment newInstance() {
            return new RegisterFragment();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            final View rootview = inflater.inflate(R.layout.fragment_register, container, false);
            Button register = (Button) rootview.findViewById(R.id.registerButton);
            final EditText username = (EditText) rootview.findViewById(R.id.registerUsername);
            final EditText email = (EditText) rootview.findViewById(R.id.registerEmail);
            final EditText password = (EditText) rootview.findViewById(R.id.registerPassword);
            final EditText passwordagain = (EditText) rootview.findViewById(R.id.registerPasswordAgain);

            //validation

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog progress = new ProgressDialog(getContext());
                    progress.setTitle("Ladevorgang");
                    progress.setMessage("Bitte warten...");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();

                    boolean veranstalter = ((CheckBox) rootview.findViewById(R.id.register_cbVerantalter)).isChecked();
                    boolean fahrdienst = ((CheckBox) rootview.findViewById(R.id.register_cbFahrdienstleister)).isChecked();
                    boolean teilnehmer = ((CheckBox) rootview.findViewById(R.id.register_cbTeilnehmer)).isChecked();

                    final HashMap<String, String> source = new HashMap<String, String>();
                    final String usernamestring = username.getText().toString().trim();
                    final String passwordstring = password.getText().toString().trim();
                    final String passwordagainstring = passwordagain.getText().toString().trim();
                    final String emailstring = email.getText().toString().trim();
                    source.put("username",usernamestring);
                    source.put("password", passwordstring);
                    source.put("password_again", passwordagainstring);
                    source.put("email",emailstring);
                    int typecount = 0;
                    if(veranstalter) {
                        source.put("organizer", String.valueOf(veranstalter));
                        typecount++;
                    }
                    if(fahrdienst) {
                        source.put("driver", String.valueOf(fahrdienst));
                        typecount++;
                    }
                    if(teilnehmer) {
                        source.put("participant", String.valueOf(teilnehmer));
                        typecount++;
                    }
                    source.put("typecount",String.valueOf(typecount));

                    Validation validate = new Validation();
                    validate.check(source, ValidationRules.get());
                    if(!validate.hasPassed()) {
                        progress.dismiss();
                        String[] errors = validate.getErrors();
                        String message = "";

                        for(String error : errors) {
                            message += error + "\n";
                        }

                        new AlertDialog.Builder(getContext())
                                .setTitle("Fehler")
                                .setMessage(message)
                                .setIcon(R.drawable.ic_warning_black_24dp)
                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                        return;
                    }

                    RequestQueue queue = Volley.newRequestQueue(rootview.getContext());
                    String url = "http://37.221.196.48/thesis/public/user/register";


                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progress.dismiss();
                            Log.d(LoginRegisterActivity.class.toString(), response);
                            try {
                                JSONObject ob = new JSONObject(response);
                                if (ob.has("error")) {
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Fehler")
                                            .setMessage(ob.getString("error"))
                                            .setIcon(R.drawable.ic_warning_black_24dp)
                                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                } else if (ob.has("success")) {
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Geschafft!")
                                            .setMessage("Du hast dich erfolgreich registriert und kannst dich nun anmelden!")
                                            .setIcon(R.drawable.ic_check_black_24dp)
                                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                    lra.tabLayout.getTabAt(0).select();
                                } else {
                                    ServerErrorDialog.show(getContext());
                                    progress.dismiss();
                                }
                            } catch (Exception e) {
                                ServerErrorDialog.show(getContext());
                                progress.dismiss();
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            return source;
                        }
                    };
                    queue.add(stringRequest);
                }
            });
            return rootview;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                return LoginFragment.newInstance();
            } else {
                return RegisterFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Log In";
                case 1:
                    return "Register";
            }
            return null;
        }
    }
}
