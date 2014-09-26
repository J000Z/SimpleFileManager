package com.hq.simplefilemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by jack on 9/2/14.
 */
public class NoticeDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
        public void onDialogGetInput(String operation, String input);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        System.out.println("Fragment Tag: " + getTag());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (getTag().equals("confirm")) {
            builder.setMessage(R.string.dialog_delete)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Send the positive button event back to the host activity
                            mListener.onDialogPositiveClick(NoticeDialogFragment.this);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Send the negative button event back to the host activity
                            mListener.onDialogNegativeClick(NoticeDialogFragment.this);
                        }
                    });
            return builder.create();
        } else if (getTag().equals("input_file")) {
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();
            builder.setTitle("New file name:");
            final View view = inflater.inflate(R.layout.input_dialog, null);

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            EditText editText = (EditText)view.findViewById(R.id.input);
                            mListener.onDialogGetInput("file", editText.getText().toString());
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            NoticeDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        } else if (getTag().equals("input_folder")) {
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();
            builder.setTitle("New file folder:");
            final View view = inflater.inflate(R.layout.input_dialog, null);
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            EditText editText = (EditText) view.findViewById(R.id.input);
                            mListener.onDialogGetInput("folder", editText.getText().toString());
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            NoticeDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        } else if (getTag().equals("edit")) {
            String filename = bundle.getString("input");
            //System.out.println(filename);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            builder.setTitle("New file name:");
            final View view = inflater.inflate(R.layout.input_dialog, null);
            EditText editText_input = ((EditText) view.findViewById(R.id.input));
            editText_input.setText(filename);
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(R.string.rename, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            EditText editText = (EditText) view.findViewById(R.id.input);
                            mListener.onDialogGetInput("edit", editText.getText().toString());
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            NoticeDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        } else if (getTag().equals("add_preference")) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            builder.setTitle("File type:");
            final View view = inflater.inflate(R.layout.input_dialog, null);
            builder.setView(view)
                    // Add action buttons
                    // Add action buttons
                    .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            EditText editText = (EditText) view.findViewById(R.id.input);
                            mListener.onDialogGetInput("add_preference", editText.getText().toString());
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            NoticeDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        } else {
            this.dismiss();
        }
        // Build the dialog and set up the button click handlers
        return null;
    }
}