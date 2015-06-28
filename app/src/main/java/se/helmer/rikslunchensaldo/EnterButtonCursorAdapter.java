package se.helmer.rikslunchensaldo;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.ResultReceiver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import se.helmer.rikslunchensaldo.model.Contract;

public class EnterButtonCursorAdapter extends ResourceCursorAdapter {

	private static String TAG = "EnterButtonCursorAdapter";
	private final ResultReceiver m_result_receiver = null;

    public EnterButtonCursorAdapter(Activity activity, Cursor c) {
        super(activity, R.layout.rowbuttonlayout, c);
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

        String description = cursor.getString(cursor.getColumnIndex(Contract.EnterButton.DESCRIPTION));
        String amount = cursor.getString(cursor.getColumnIndex(Contract.EnterButton.AMOUNT));

        row1.setText(description);
        row2.setText(amount);

	}
}
