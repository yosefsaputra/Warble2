package mpc.utexas.edu.warble2.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import mpc.utexas.edu.warble2.R;
import mpc.utexas.edu.warble2.services.PhilipsHue.CreateUserRequest;
import mpc.utexas.edu.warble2.services.PhilipsHue.CreateUserResponse;
import mpc.utexas.edu.warble2.services.PhilipsHue.PhilipsHueService;
import mpc.utexas.edu.warble2.utils.PhilipsHueUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        setSubmitButton();
    }

    private void setSubmitButton() {
        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Generalize the service
                PhilipsHueService mService = PhilipsHueUtil.getService("http://192.168.1.74");

                EditText newUserNameText = findViewById(R.id.newusername);

                // TODO to create developer from email address
                CreateUserRequest createUserRequest = new CreateUserRequest();
                createUserRequest.setDevicetype(newUserNameText.getText().toString());
                mService.createUser(createUserRequest).enqueue(new Callback<List<CreateUserResponse>>() {
                    @Override
                    public void onResponse(Call<List<CreateUserResponse>> call, Response<List<CreateUserResponse>> response) {
                        TextView debugText = findViewById(R.id.debugText);

                        if (response.isSuccessful()) {
                            try {
                                for (CreateUserResponse res : response.body()) {
                                    try {
                                        CreateUserResponse.Success successRes = res.getSuccess();
                                        String username = successRes.getUsername();
                                        debugText.setText(username);
                                    } catch (NullPointerException e) {
                                        CreateUserResponse.Error errorRes = res.getError();
                                        String description = errorRes.getDescription();
                                        debugText.setText(description);
                                    }
                                }
                            } catch (NullPointerException e) {
                                debugText.setText(
                                        String.format("Unknown error. HTTP code: %s. HTTP body: %s",
                                                response.code(),
                                                response.body()
                                        )
                                );
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<List<CreateUserResponse>> call, Throwable t) {}
                });
            }
        });
    }
}
