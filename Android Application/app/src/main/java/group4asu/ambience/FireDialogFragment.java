package group4asu.ambience;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by AmeyVikas on 3/15/2015.
 */
public class FireDialogFragment extends DialogFragment implements View.OnClickListener {
    Button cancel, ok;
    EditText ipEdit;
    Communicator communicator;
    public void onAttach(Activity activity){
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }
    @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.ip_dialog, null);
        cancel = (Button) view.findViewById(R.id.cancelIP);
        ok = (Button) view.findViewById(R.id.enterIP);
        ipEdit = (EditText) view.findViewById(R.id.editText);
        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);
        setCancelable(true);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.cancelIP)
        {
            communicator.onDialogMessage("cancel");
            dismiss();
        }
        else
        {
            //Toast.makeText(getActivity(),"IP added",Toast.LENGTH_LONG).show();
            communicator.onDialogMessage(ipEdit.getText().toString());
            dismiss();
        }
    }
    interface Communicator{
        public void onDialogMessage(String message);
    }
}
