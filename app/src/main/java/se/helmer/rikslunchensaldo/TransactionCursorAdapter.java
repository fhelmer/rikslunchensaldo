package se.helmer.rikslunchensaldo;

import android.content.Context;
import android.database.Cursor;
import android.os.ResultReceiver;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class TransactionCursorAdapter extends ResourceCursorAdapter {

	private static String TAG = "CrashCursorAdapter";
    private final TransactionFragment m_fragment;
	private final ResultReceiver m_result_receiver = null;
	private final int mMode = -1;

    public TransactionCursorAdapter(TransactionFragment fragment, Cursor c) {
        super(fragment.getActivity(), R.layout.rowbuttonlayout, c);
		m_fragment = fragment;
	}

	@Override
	public View newView(final Context context, Cursor cursor, ViewGroup parent) {
        final View view = super.newView(context,cursor,parent);

        final Button b = (Button) view.findViewById(R.id.button_fetch);
        b.setOnClickListener(new OnClickListener() {

        	@Override
        	public void onClick(View v) {
        		Button b = (Button) v;
        		String txt = b.getText().toString();
    			int crash_id = (Integer) v.getTag();
    			MainActivity ma = (MainActivity) m_fragment.getActivity();
                // if (txt.equals(Message.ACTION_UPLOAD)) {
                    /*
                     * Crash c = Crash.get(crash_id);
                     * c.setState(Crash.STATE_PENDING);
                     * b.setText(Message.ACTION_CANCEL); c.save();
                     * c.notifyChange(); ma.doWork(Message.CMD_UPLOAD_PENDING,
                     * crash_id); } else if (txt.equals(Message.ACTION_CANCEL))
                     * { Crash c = Crash.get(crash_id);
                     * c.setState(Crash.STATE_NEW); c.save(); c.notifyChange();
                     * UploadIntentService.cancel(crash_id); } else if
                     * (txt.equals(Message.ACTION_DELETE)) {
                     * ma.doWork(Message.CMD_DELETE, crash_id); } else if
                     * (txt.equals(Message.ACTION_RESTART)) { Crash c =
                     * Crash.get(crash_id); c.setState(Crash.STATE_NEW);
                     * c.setInfo("Re-queued by user"); c.save();
                     * c.notifyChange();
                     */
                // }
        	}});

        final Button editButton = null; // (Button)
                                        // view.findViewById(R.id.edit_button);
        editButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                String txt = b.getText().toString();
                int crash_id = (Integer) v.getTag();
                MainActivity ma = (MainActivity) m_fragment.getActivity();
                if (txt.equals("Edit")) {
                    // Intent i = new Intent(m_fragment.getActivity(),
                    // CrashDetailActivity.class);

                    // i.putExtra(MainActivity.CRASH_EDIT_INTENT,
                    // Crash.get(crash_id));

                    // Activity returns an result if called with startActivityForResult
                    // m_fragment.getActivity().startActivity(i);
                }
            }
        });

		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
        // Crash crash = Crash.makeObject(cursor);

        // TextView row1 = (TextView) view.findViewById(R.id.row1);
        // TextView row2 = (TextView) view.findViewById(R.id.row2);

        // row1.setText(crash.getName());
        // row2.setText(crash.getUploadInfo());

        // Button actionButton = (Button) view.findViewById(R.id.action_button);
        // Button editButton = (Button) view.findViewById(R.id.edit_button);

        // actionButton.setTag(crash.getId());
        // editButton.setTag(crash.getId());

	}
}
