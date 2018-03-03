package mpc.utexas.edu.warble2.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import mpc.utexas.edu.warble2.R;
import mpc.utexas.edu.warble2.database.LocationConverter;
import mpc.utexas.edu.warble2.things.Light;
import mpc.utexas.edu.warble2.things.PhilipsHue.PhilipsLight;
import mpc.utexas.edu.warble2.things.Thing;

public class ThingViewActivity extends AppCompatActivity {
    public static String TAG = "ThingViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thing_view);

        Intent intent = getIntent();

        long selectedThingDbid = (long) intent.getLongExtra("selectedThingDbid", 0);
        System.out.println(selectedThingDbid);
        final Thing thing = Thing.getThingByDbid(getApplicationContext(), selectedThingDbid);

        // Set Title
        TextView thingTitleTextView = (TextView) findViewById(R.id.thingTitleTextView);
        thingTitleTextView.setText((CharSequence) thing.getName());

        // Set Location
        TextView locationTextView = (TextView) findViewById(R.id.locationTextView);
        if (thing instanceof Light) {
            locationTextView.setText((CharSequence) LocationConverter.toString(((Light) thing).getLocation()));
        } else {
            locationTextView.setText("N/A");
        }

        // Set New Location
        final EditText newLocationEditText = (EditText) findViewById(R.id.newLocationEditText);
        Button newLocationSubmitButton = (Button) findViewById(R.id.newLocationSubmitButton);
        newLocationSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thing instanceof Light) {
                    ((Light) thing).setLocation(LocationConverter.toLocation(String.format("(%s)", newLocationEditText.getText())));
                    thing.updateDb(getApplicationContext());
                }
            }
        });
    }
}
