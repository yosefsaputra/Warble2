package mpc.utexas.edu.warble2.ui;

import android.content.Intent;
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
import mpc.utexas.edu.warble2.database.User;
import mpc.utexas.edu.warble2.services.PhilipsHue.CreateUserRequest;
import mpc.utexas.edu.warble2.services.PhilipsHue.CreateUserResponse;
import mpc.utexas.edu.warble2.services.PhilipsHue.PhilipsHueService;
import mpc.utexas.edu.warble2.things.Bridge;
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

        List<Bridge> bridges = Bridge.getAllBridgesFromDatabase(getApplicationContext());
        final Bridge bridge = bridges.get(bridgePosition);

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

                                bridge.addUserToDatabase(getApplicationContext(), newUserNameEditText.getText().toString(), userId);

                                finish();
                                startActivity(getIntent());

                            } else if (resp.getError() != null) {
                                String error_message = resp.getError().getDescription();
                                Log.d(TAG, error_message);
                                toast = Toast.makeText(getApplicationContext(), (CharSequence) error_message, Toast.LENGTH_LONG);
                            } else {
                                String error_message = "Bridge does NOT respond correctly";
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

        ListView userList = (ListView) findViewById(R.id.userList);
        List<User> users = bridge.getAllUsersFromDatabase(getApplicationContext());
        List<String> userIds = new ArrayList<>();
        for (User user: users) {
            userIds.add(user.userId);
        }
        ArrayAdapter<String> usersListAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.user_layout, R.id.userNameTextView, userIds);
        userList.setAdapter(usersListAdapter);

        ListView lightList = (ListView) findViewById(R.id.lightList);
        String[] lights = {};
        ArrayAdapter<String> lightsListAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.light_layout, R.id.lightTextView, lights);
        lightList.setAdapter(lightsListAdapter);
    }
}
