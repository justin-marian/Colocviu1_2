package ro.pub.cs.systems.eim.colocviu1_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Colocviu1_2MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private static final String SAVED_SUM_KEY = "savedSum";
    private static final String SAVED_TERMS_KEY = "savedTerms";

    private int savedSum = 0;
    private String lastComputedTerms = "";
    private boolean isRestored = false;
    private boolean isServiceStarted = false;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Colocviu1_2Service.ACTION_SUM_BROADCAST.equals(intent.getAction())) {
                int sum = intent.getIntExtra(Colocviu1_2Service.EXTRA_SUM, 0);
                String timestamp = intent.getStringExtra(Colocviu1_2Service.EXTRA_TIMESTAMP);
                Toast.makeText(context, "Received sum: " + sum + " at " + timestamp, Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test01_2_main);

        EditText nextTermEditText = findViewById(R.id.nextTermEditText);
        EditText allTermsEditText = findViewById(R.id.allTermsEditText);
        Button addButton = findViewById(R.id.addButton);
        Button computeButton = findViewById(R.id.computeButton);

        if (savedInstanceState != null) {
            savedSum = savedInstanceState.getInt(SAVED_SUM_KEY);
            lastComputedTerms = savedInstanceState.getString(SAVED_TERMS_KEY, "");
            if (!lastComputedTerms.isEmpty()) {
                allTermsEditText.setText(lastComputedTerms);
                Toast.makeText(this, "Restored sum: " + savedSum, Toast.LENGTH_LONG).show();
                isRestored = true;
            }
        }

        addButton.setOnClickListener(v -> {
            String nextTerm = nextTermEditText.getText().toString().trim();
            if (!nextTerm.isEmpty()) {
                String allTerms = allTermsEditText.getText().toString();
                if (!allTerms.isEmpty()) {
                    allTerms += " + ";
                }
                allTerms += nextTerm;
                allTermsEditText.setText(allTerms);
                nextTermEditText.setText("");
                isRestored = false;
            }
        });

        computeButton.setOnClickListener(v -> {
            String allTerms = allTermsEditText.getText().toString();
            if (allTerms.isEmpty()) {
                Toast.makeText(this, "No terms to compute", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isRestored && allTerms.equals(lastComputedTerms)) {
                Toast.makeText(this, "Sum of terms (cached): " + savedSum, Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(Colocviu1_2MainActivity.this, Colocviu1_2SecondaryActivity.class);
                intent.putExtra("allTerms", allTerms);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        registerReceiver(broadcastReceiver, new IntentFilter(Colocviu1_2Service.ACTION_SUM_BROADCAST), Context.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            savedSum = data.getIntExtra("result", 0);
            lastComputedTerms = ((EditText) findViewById(R.id.allTermsEditText)).getText().toString();
            isRestored = true;

            if (savedSum > 10 && !isServiceStarted) {
                Intent serviceIntent = new Intent(this, Colocviu1_2Service.class);
                serviceIntent.putExtra(Colocviu1_2Service.EXTRA_SUM, savedSum);
                startService(serviceIntent);
                isServiceStarted = true;
            }

            Toast.makeText(this, "Sum of terms: " + savedSum, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isServiceStarted) {
            stopService(new Intent(this, Colocviu1_2Service.class));
            isServiceStarted = false;
        }
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_SUM_KEY, savedSum);
        outState.putString(SAVED_TERMS_KEY, lastComputedTerms);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedSum = savedInstanceState.getInt(SAVED_SUM_KEY);
        lastComputedTerms = savedInstanceState.getString(SAVED_TERMS_KEY, "");
    }
}
