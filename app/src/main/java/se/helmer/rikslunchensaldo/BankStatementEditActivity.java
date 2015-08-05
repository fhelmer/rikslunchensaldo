package se.helmer.rikslunchensaldo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import se.helmer.rikslunchensaldo.model.Contract;

public class BankStatementEditActivity extends Activity {

    private EditText mDescription;
    private EditText mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bank_statement);
        fillData();
    }

    public void fillData() {

        mDescription = (EditText) findViewById(R.id.bank_statement_edit_description);
        mAmount = (EditText) findViewById(R.id.bank_statement_edit_amount);

        Button doneButton = (Button) findViewById(R.id.bank_statement_done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putString(Contract.BankStatement.DESCRIPTION, mDescription.getText().toString());
                b.putString(Contract.BankStatement.AMOUNT, mAmount.getText().toString());

                final Intent intent = new Intent(BankStatementEditActivity.this, MyIntentService.class);
                // intent.putExtra(MyIntentService.COMMAND, MyIntentService.ENTER_BUTTONS_ADD);
                intent.putExtra(MyIntentService.RECEIVER, MainActivity.getReceiver());
                intent.putExtra(MyIntentService.PAYLOAD, b);

                ComponentName cm = startService(intent);
            }
        });

        Button okButton = (Button) findViewById(R.id.bank_statement_done_button);
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                BankStatementEditActivity.this.finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_bank_statement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
