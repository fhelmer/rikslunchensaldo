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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import se.helmer.rikslunchensaldo.model.Contract;

public class BankStatementCursorAdapter extends ResourceCursorAdapter {

	private static String TAG = "BankStatementCursorAdapter";
	private final ResultReceiver m_result_receiver = null;

    public BankStatementCursorAdapter(Fragment fragment, Cursor c) {
        super(fragment.getActivity(), R.layout.rowbuttonlayout, c);
	}

	@Override
	public View newView(final Context context, final Cursor cursor, ViewGroup parent) {
        final View view = super.newView(context,cursor,parent);

        final Button b = (Button) view.findViewById(R.id.edit_row_button);
        b.setOnClickListener(new OnClickListener() {

        	@Override
        	public void onClick(View v) {
        		Button button = (Button) v;


        		String txt = button.getText().toString();
    			int id = (Integer) v.getTag();

                Intent intent = new Intent(context, BankStatementEditActivity.class);
                Bundle b = new Bundle();

                String amount = cursor.getString(cursor.getColumnIndex(Contract.BankStatement.AMOUNT));
                String description = cursor.getString(cursor.getColumnIndex(Contract.BankStatement.DESCRIPTION));

                b.putString(Contract.BankStatement.AMOUNT, amount);
                b.putString(Contract.BankStatement.DESCRIPTION, description);
                b.putInt(Contract.BankStatement._ID, id);

                intent.putExtra("payload", b);

                context.startActivity(intent);
            }});

		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
        TextView row1 = (TextView) view.findViewById(R.id.row1);
        TextView row2 = (TextView) view.findViewById(R.id.row2);

        String amount = cursor.getString(cursor.getColumnIndex(Contract.BankStatement.AMOUNT));
        String balance = cursor.getString(cursor.getColumnIndex(Contract.BankStatement.BALANCE));

        String datestr = cursor.getString(cursor.getColumnIndex(Contract.BankStatement.DATE));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(datestr);

        } catch (ParseException pe) {
            Utils.error(TAG, "Parse exception "+pe);
        }

        String datestr2 = Utils.getFormattedTimeString(Utils.GmtToLocal(parsedDate));
        int id = cursor.getInt(cursor.getColumnIndex(Contract.BankStatement._ID));
        row1.setText(datestr2);
        row2.setText(String.format("%-15s %-15s", amount, balance));

        Button actionButton = (Button) view.findViewById(R.id.edit_row_button);

        actionButton.setTag(id);
	}
}
