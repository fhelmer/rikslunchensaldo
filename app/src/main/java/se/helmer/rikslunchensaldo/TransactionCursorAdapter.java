package se.helmer.rikslunchensaldo;

import android.content.Context;
import android.database.Cursor;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

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
        		Button b = (Button) v;
        		String txt = b.getText().toString();
    			// int crash_id = (Integer) v.getTag();
                /*
        	Bundle b = new Bundle();
            b.putString(BALANCE, balance);
            b.putString(LAST_TOPUP_DATE, lasttopupdate);
            b.putString(MESSAGE, message);
            b.putSerializable(LATEST_UPDATE, Utils.LocalToGmt(new Date()));

            final Intent intent = new Intent(context, MyIntentService.class);
            intent.putExtra("command", MyIntentService.SEND_BALANCE);
            intent.putExtra(PAYLOAD, b);
            ComponentName cm = context.startService(intent);
            */

            }});

		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
        // Crash crash = Crash.makeObject(cursor);

        TextView row1 = (TextView) view.findViewById(R.id.row1);
        TextView row2 = (TextView) view.findViewById(R.id.row2);

        String amount = cursor.getString(cursor.getColumnIndex(Contract.Transaction.AMOUNT));
        String description = cursor.getString(cursor.getColumnIndex(Contract.Transaction.DESCRIPTION));
        String id = cursor.getString(cursor.getColumnIndex(Contract.Transaction._ID));
        row1.setText(description);
        row2.setText(amount);

        Button actionButton = (Button) view.findViewById(R.id.action_button);

        actionButton.setTag(id);
	}
}
