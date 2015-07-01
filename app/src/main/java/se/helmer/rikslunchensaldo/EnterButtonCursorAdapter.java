package se.helmer.rikslunchensaldo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import se.helmer.rikslunchensaldo.model.Contract;

public class EnterButtonCursorAdapter extends ResourceCursorAdapter {

	private static String TAG = "EnterButtonCursorAdapter";
	// private final ResultReceiver m_result_receiver = null;
	private final Activity mActivity;

    public EnterButtonCursorAdapter(Activity activity, Cursor c) {
        super(activity, R.layout.rowbuttonlayout, c);
		mActivity = activity;
	}

	@Override
	public View newView(final Context context, Cursor cursor, ViewGroup parent) {
        final View view = super.newView(context,cursor,parent);
		final Button b = (Button) view.findViewById(R.id.delete_row_button);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Bundle b = new Bundle();
				b.putInt(Contract.EnterButton._ID, (Integer) view.getTag());

				final Intent intent = new Intent(mActivity, MyIntentService.class);
				intent.putExtra(MyIntentService.COMMAND, MyIntentService.ENTER_BUTTONS_DELETE);
				intent.putExtra(MyIntentService.RECEIVER, MainActivity.getReceiver());
				intent.putExtra(MyIntentService.PAYLOAD, b);

				ComponentName cm = mActivity.startService(intent);
			}
		});

		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
        TextView row1 = (TextView) view.findViewById(R.id.row1);
        TextView row2 = (TextView) view.findViewById(R.id.row2);

        String description = cursor.getString(cursor.getColumnIndex(Contract.EnterButton.DESCRIPTION));
        String amount = cursor.getString(cursor.getColumnIndex(Contract.EnterButton.AMOUNT));
		int enterButtonId = cursor.getInt(cursor.getColumnIndex(Contract.EnterButton._ID));

        row1.setText(description);
        row2.setText(amount);

		Button deleteButton = (Button) view.findViewById(R.id.delete_row_button);
		deleteButton.setTag(enterButtonId);



	}
}
