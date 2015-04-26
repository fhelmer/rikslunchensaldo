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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import se.helmer.rikslunchensaldo.model.Contract;

public class TransactionCursorAdapter extends ResourceCursorAdapter {

	private static String TAG = "TransactionCursorAdapter";
	private final ResultReceiver m_result_receiver = null;

    public TransactionCursorAdapter(Fragment fragment, Cursor c) {
        super(fragment.getActivity(), R.layout.rowbuttonlayout, c);
	}

	@Override
	public View newView(final Context context, Cursor cursor, ViewGroup parent) {
        final View view = super.newView(context,cursor,parent);

        final Button b = (Button) view.findViewById(R.id.action_button);
        b.setOnClickListener(new OnClickListener() {

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
        // Crash crash = Crash.makeObject(cursor);

        TextView row1 = (TextView) view.findViewById(R.id.row1);
        TextView row2 = (TextView) view.findViewById(R.id.row2);

        String amount = cursor.getString(cursor.getColumnIndex(Contract.Transaction.AMOUNT));
        String balance = cursor.getString(cursor.getColumnIndex(Contract.Transaction.BALANCE));

        String datestr = cursor.getString(cursor.getColumnIndex(Contract.Transaction.DATE));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(datestr);

        } catch (ParseException pe) {
            Utils.error(TAG, "Parse exception "+pe);
        }

        String datestr2 = Utils.getFormattedTimeString(Utils.GmtToLocal(parsedDate));
        int id = cursor.getInt(cursor.getColumnIndex(Contract.Transaction._ID));
        row1.setText(datestr2);
        row2.setText(String.format("%-15s %-15s", amount, balance));

        Button actionButton = (Button) view.findViewById(R.id.action_button);

        actionButton.setTag(id);
	}
}
