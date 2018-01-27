package mpc.utexas.edu.warble2.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mpc.utexas.edu.warble2.R;
import mpc.utexas.edu.warble2.database.AppDatabase;
import mpc.utexas.edu.warble2.database.BridgeDb;
import mpc.utexas.edu.warble2.services.PhilipsHue.CreateUserRequest;
import mpc.utexas.edu.warble2.services.PhilipsHue.CreateUserResponse;
import mpc.utexas.edu.warble2.services.PhilipsHue.PhilipsHueService;
import mpc.utexas.edu.warble2.things.Bridge;
import mpc.utexas.edu.warble2.things.Light;
import mpc.utexas.edu.warble2.things.Thing;
import mpc.utexas.edu.warble2.users.PhilipsHue.PhilipsUser;
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
        setContentView(R.layout.activity_bridge_view);

        Intent intent = getIntent();
        int bridgePosition = intent.getExtras().getInt("bridge_position");

        List<Bridge> bridges = Bridge.getAllDb(getApplicationContext());
        final Bridge bridge = bridges.get(bridgePosition);

        final BridgeDb bridgeDb = AppDatabase.getDatabase(getApplicationContext()).bridgeDao().getBridgeByUUID(bridge.getUUID());

        service = PhilipsHueUtil.getService(bridge.getBaseUrl());

        TextView bridgeTitle = (TextView) findViewById(R.id.bridgeTitle);
        bridgeTitle.setText(bridge.getName());

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

                                PhilipsUser user = new PhilipsUser(newUserNameEditText.getText().toString(), userId, bridgeDb.dbid);
                                user.addDb(getApplicationContext());

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

        ListView userListView = (ListView) findViewById(R.id.userList);
        List<PhilipsUser> users = PhilipsUser.getAllDb(getApplicationContext());
        List<String> userIds = new ArrayList<>();
        for (PhilipsUser user : users) {
            userIds.add(user.getId());
        }
        ArrayAdapter<String> usersListAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.user_layout, R.id.userNameTextView, userIds);
        userListView.setAdapter(usersListAdapter);

        new DiscoverLights().execute(bridge);
    }

    private class DiscoverLights extends AsyncTask<Bridge, Void, List<Light>> {
        @Override
        protected List<Light> doInBackground(Bridge... params){
            return params[0].discoverLights(getApplicationContext());
        }

        @Override
        protected void onPostExecute(List<Light> lights) {
            // Update Lights ListView
            ListView lightListView = (ListView) findViewById(R.id.lightList);
            List<String> lightIds = new ArrayList<>();
            for (Thing light : lights) {
                lightIds.add(light.getName());
            }
            ArrayAdapter<String> lightsListAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.light_layout, R.id.lightTextView, lightIds);
            lightListView.setAdapter(lightsListAdapter);

            // Store Lights to database
            for (Light light: lights) {
                light.addDb(getApplicationContext());
            }
        }
    }
}
