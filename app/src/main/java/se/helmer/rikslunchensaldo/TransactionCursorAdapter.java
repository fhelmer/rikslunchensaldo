package se.helmer.rikslunchensaldo;

import android.content.Context;
import android.database.Cursor;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
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
        super(fragment.getActivity(), R.layout.rowlayout, c);
	}

	@Override
	public View newView(final Context context, Cursor cursor, ViewGroup parent) {
        final View view = super.newView(context,cursor,parent);

		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
        TextView row1 = (TextView) view.findViewById(R.id.logrow1);
        TextView row2 = (TextView) view.findViewById(R.id.logrow2);

        String dateStr = cursor.getString(cursor.getColumnIndex(Contract.Transaction.DATE));
        String description = cursor.getString(cursor.getColumnIndex(Contract.Transaction.DESCRIPTION));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(dateStr);

        } catch (ParseException pe) {
            Utils.error(TAG, "Parse exception "+pe);
        }

        String datestr2 = Utils.getFormattedTimeString(Utils.GmtToLocal(parsedDate));
        int id = cursor.getInt(cursor.getColumnIndex(Contract.LogMessage._ID));
        row1.setText(datestr2);
        row2.setText(description);

	}
}
