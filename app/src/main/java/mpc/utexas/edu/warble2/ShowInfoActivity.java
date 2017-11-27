package mpc.utexas.edu.warble2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import mpc.utexas.edu.warble2.services.PhilipsHue.PhilipsHueService;
import mpc.utexas.edu.warble2.utils.PhilipsHueUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowInfoActivity extends AppCompatActivity {
    public String username = null;
    public String userid = null;
    private PhilipsHueService mService = PhilipsHueUtil.getService("http://192.168.1.74");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        userid = intent.getStringExtra("userid");

        setInfoText();
    }

    private void setInfoText() {
        mService.getInfo(userid).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String info = response.body().toString();

                    TextView infoText = findViewById(R.id.infoText);
                    infoText.setText(info);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
