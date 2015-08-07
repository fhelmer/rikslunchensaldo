package se.helmer.rikslunchensaldo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
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
    private int mId;
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mBundle = intent.getBundleExtra(MyIntentService.PAYLOAD);
        setContentView(R.layout.activity_edit_bank_statement);
        fillData();
    }

    public void fillData() {

        mDescription = (EditText) findViewById(R.id.bank_statement_edit_description);
        mAmount = (EditText) findViewById(R.id.bank_statement_edit_amount);

        mDescription.setText(mBundle.getString(Contract.BankStatement.DESCRIPTION));
        mAmount.setText(mBundle.getString(Contract.BankStatement.AMOUNT));
        mId = mBundle.getShort(Contract.BankStatement._ID);

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        final Intent intent = new Intent(BankStatementEditActivity.this, MyIntentService.class);
                        intent.putExtra(MyIntentService.COMMAND, MyIntentService.BANKSTATEMENT_DELETE);
                        intent.putExtra(MyIntentService.PAYLOAD, mBundle);
                        ComponentName cm = startService(intent);
                        BankStatementEditActivity.this.finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        Button deleteButton = (Button) findViewById(R.id.bank_statement_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BankStatementEditActivity.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });

        Button okButton = (Button) findViewById(R.id.bank_statement_done_button);
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mBundle.putString(Contract.BankStatement.AMOUNT, mAmount.getEditableText().toString());
                mBundle.putString(Contract.BankStatement.DESCRIPTION, mDescription.getEditableText().toString());

                final Intent intent = new Intent(BankStatementEditActivity.this, MyIntentService.class);
                intent.putExtra("command", MyIntentService.BANKSTATEMENT_UPDATE);
                intent.putExtra(MyIntentService.PAYLOAD, mBundle);
                ComponentName cm = startService(intent);

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
