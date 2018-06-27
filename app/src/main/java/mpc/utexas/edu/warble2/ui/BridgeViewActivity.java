package mpc.utexas.edu.warble2.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.R;
import mpc.utexas.edu.warble2.services.PhilipsHue.CreateUserRequest;
import mpc.utexas.edu.warble2.services.PhilipsHue.CreateUserResponse;
import mpc.utexas.edu.warble2.services.PhilipsHue.PhilipsHueService;
import mpc.utexas.edu.warble2.things.Bridge;
import mpc.utexas.edu.warble2.things.Light;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsBridge;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsLight;
import mpc.utexas.edu.warble2.things.Thing;
import mpc.utexas.edu.warble2.things.Wink.WinkLight;
import mpc.utexas.edu.warble2.users.PhilipsHue.PhilipsUser;
import mpc.utexas.edu.warble2.users.User;
import mpc.utexas.edu.warble2.utils.PhilipsHueUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BridgeViewActivity extends AppCompatActivity {
    private String TAG = "BridgeViewActivity";
    private PhilipsHueService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int bridgePosition = intent.getExtras().getInt("bridge_position");

        List<Bridge> bridges = Bridge.getAllDb(getApplicationContext());
        final Bridge bridge = bridges.get(bridgePosition);

        String bridgeToUser;
        try {
            bridgeToUser = (String) bridge.getClass().getField("bridgeToUser").get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            bridgeToUser = "manytoone";
        }

        if (bridgeToUser.equals("onetomany")) {
            setContentView(R.layout.activity_bridge_view_createuser);
            if (bridge.getClass() == PhilipsBridge.class) {
                setCreateUserFormPhilips(bridge);
                setUsersListViewPhilips(bridge);
            }
        } else {
            setContentView(R.layout.activity_bridge_view);
        }

        setTitleView(bridge);

        setLightsListView(bridge);
    }

    private void setTitleView(Bridge bridge) {
        TextView bridgeTitle = (TextView) findViewById(R.id.bridgeTitle);
        bridgeTitle.setText(bridge.getName());
    }

    private void setCreateUserFormPhilips(final Bridge bridge) {
        service = PhilipsHueUtil.getService(bridge.getBaseUrl());

        final EditText newUserNameEditText = (EditText) findViewById(R.id.newUserEditText);

        Button newUserNameButton = (Button) findViewById(R.id.newUserButton);
        newUserNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateUserRequest createUserRequest = new CreateUserRequest();
                createUserRequest.setDevicetype(newUserNameEditText.getText().toString());
                service.createUser(createUserRequest).enqueue(new Callback<List<CreateUserResponse>>() {
                    @Override
                    public void onResponse(Call<List<CreateUserResponse>> call, Response<List<CreateUserResponse>> response) {
                        Log.d(TAG, response.toString());
                        Toast toast;
                        for (CreateUserResponse resp: response.body()) {
                            if (resp.getSuccess() != null) {
                                String userId = resp.getSuccess().getUsername();
                                Log.d(TAG, userId);
                                toast = Toast.makeText(getApplicationContext(), (CharSequence) userId, Toast.LENGTH_LONG);

                                if (bridge.getUser() == null) {
                                    PhilipsUser user = new PhilipsUser(newUserNameEditText.getText().toString(), userId, null, null, null);
                                    user.addDb(getApplicationContext());
                                    bridge.setUser(user);
                                    bridge.updateDb(getApplicationContext());
                                } else {
                                    long userDbid = bridge.getUser().getDbid();
                                    PhilipsUser philipsUser = PhilipsUser.getUserByDbid(getApplicationContext(), userDbid);
                                    philipsUser.setId(userId);
                                    philipsUser.updateDb(getApplicationContext());
                                }

                                finish();
                                startActivity(getIntent());

                            } else if (resp.getError() != null) {
                                String error_message = resp.getError().getDescription();
                                Log.d(TAG, error_message);
                                toast = Toast.makeText(getApplicationContext(), (CharSequence) error_message, Toast.LENGTH_LONG);
                            } else {
                                String error_message = "BridgeDb does NOT respond correctly";
                                Log.d(TAG, error_message);
                                toast = Toast.makeText(getApplicationContext(), (CharSequence) error_message, Toast.LENGTH_LONG);
                            }
                            toast.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CreateUserResponse>> call, Throwable t) {
                        Log.d(TAG, t.toString());
                    }
                });
            }
        });
    }

    private void setUsersListViewPhilips(Bridge bridge) {
        // Update Users ListView
        ListView userListView = (ListView) findViewById(R.id.userList);
        // TODO Bug: This should only list down the users belong to a specific bridge
        List<User> users = new ArrayList<>();
        if (bridge.getUser() != null) {
            users.add(bridge.getUser());
        }

        List<String> userIds = new ArrayList<>();
        for (User user : users) {
            userIds.add(user.getId());
        }
        ArrayAdapter<String> usersListAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.user_layout, R.id.userNameTextView, userIds);
        userListView.setAdapter(usersListAdapter);
    }

    private void setLightsListView(Bridge bridge) {
        // Discover Lights and Update Lights ListView
        new DiscoverLights().execute(bridge);
    }

    private class DiscoverLights extends AsyncTask<Bridge, Void, List<Light>> {
        @Override
        protected List<Light> doInBackground(Bridge... params){
            return params[0].discoverLights(getApplicationContext());
        }

        @Override
        protected void onPostExecute(final List<Light> lights) {
            // Update Lights ListView
            ListView lightListView = (ListView) findViewById(R.id.lightList);
            List<String> lightIds = new ArrayList<>();
            for (Thing light : lights) {
                lightIds.add(light.getName());
            }

            LightArrayAdapter lightArrayAdapter = new LightArrayAdapter(getApplicationContext(), lights);
            lightListView.setAdapter(lightArrayAdapter);
            lightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getApplicationContext(), ThingViewActivity.class);
                    Light selectedLight = (Light) adapterView.getItemAtPosition(i);
                    intent.putExtra("selectedThingDbid", selectedLight.getDbid());
                    startActivity(intent);
                }
            });

            // Store Lights to database
            for (Light light: lights) {
                light.addDb(getApplicationContext());
            }
        }
    }

    public class LightArrayAdapter extends ArrayAdapter<Light> {
        private final Context context;
        private final List<Light> lights;

        public LightArrayAdapter(Context context, List<Light> lights) {
            super(context, -1, lights);
            this.context = context;
            this.lights = lights;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.light_layout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.lightTextView);
            textView.setText(lights.get(position).getName());
            return rowView;
        }
    }
}
