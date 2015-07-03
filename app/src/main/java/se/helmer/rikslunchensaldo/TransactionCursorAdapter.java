package se.helmer.rikslunchensaldo;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import se.helmer.rikslunchensaldo.model.Contract;

public class TransactionCursorAdapter extends ResourceCursorAdapter {

	private static String TAG = "TransactionMessageCursorAdapter";
	private final ResultReceiver m_result_receiver = null;

    public TransactionCursorAdapter(Fragment fragment, Cursor c) {
        super(fragment.getActivity(), R.layout.rowtransactionbuttonlayout, c);
	}

	@Override
	public View newView(final Context context, Cursor cursor, ViewGroup parent) {
        final View view = super.newView(context,cursor,parent);

        final Button b = (Button) view.findViewById(R.id.delete_row_button);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String txt = button.getText().toString();
                int id = (Integer) v.getTag();

                final Bundle b = new Bundle();
                b.putInt(Contract.Transaction._ID, id);

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                final Intent intent = new Intent(context, MyIntentService.class);
                                intent.putExtra("command", MyIntentService.TRANSACTION_DELETE);
                                intent.putExtra(MyIntentService.PAYLOAD, b);
                                ComponentName cm = context.startService(intent);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }});

        return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
        TextView tvDate = (TextView) view.findViewById(R.id.tvRowDate);
        TextView tvDescription = (TextView) view.findViewById(R.id.tvRowDescription);
        TextView tvAmount = (TextView) view.findViewById(R.id.tvRowAmount);
        TextView tvEstimatedBalance = (TextView) view.findViewById(R.id.tvRowEstimatedBalance);
        TextView tvBankStatementBalance = (TextView) view.findViewById(R.id.tvRowBankSatementBalance);

        String dateStr = cursor.getString(cursor.getColumnIndex(Contract.Transaction.DATE));
        String description = cursor.getString(cursor.getColumnIndex(Contract.Transaction.DESCRIPTION));
        String amount = cursor.getString(cursor.getColumnIndex(Contract.Transaction.AMOUNT));
        String estimatedBalance =
                cursor.getString(cursor.getColumnIndex(Contract.Transaction.ESTIMATEDBALANCE));
        String bankstatementBalance =
                cursor.getString(cursor.getColumnIndex(Contract.Transaction.BANKSTATEMENTBALANCE));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(dateStr);

        } catch (ParseException pe) {
            Utils.error(TAG, "Parse exception "+pe);
        }

        String datestr2 = Utils.getFormattedTimeString(Utils.GmtToLocal(parsedDate));

        tvDate.setText(datestr2);
        tvDescription.setText(description);
        tvEstimatedBalance.setText("Est:"+estimatedBalance);
        tvAmount.setText(amount);
        tvBankStatementBalance.setText("Real:"+bankstatementBalance);

        int id = cursor.getInt(cursor.getColumnIndex(Contract.BankStatement._ID));
        Button deleteButton = (Button) view.findViewById(R.id.delete_row_button);
        deleteButton.setTag(id);

        // TODO: If estimatedBalance ==
	}
}
