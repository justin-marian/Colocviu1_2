package ro.pub.cs.systems.eim.colocviu1_2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Colocviu1_2SecondaryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String allTerms = intent.getStringExtra("allTerms");

        int sum = 0;
        if (allTerms != null && !allTerms.isEmpty()) {
            String[] terms = allTerms.split("\\+");
            for (String term : terms) {
                term = term.trim();
                try {
                    sum += Integer.parseInt(term);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("result", sum);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
