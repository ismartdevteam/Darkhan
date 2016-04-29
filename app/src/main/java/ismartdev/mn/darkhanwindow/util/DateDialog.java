package ismartdev.mn.darkhanwindow.util;

/**
 * Created by Ulziiburen on 12/19/15.
 */

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

@SuppressLint("ValidFragment")
public class DateDialog extends DialogFragment implements OnDateSetListener {
    EditText txtdate;
    TextView txtTv;
    int type;
    public DateDialog(View view,int type){
        this.type=type;
        switch (type){
            case 0:
                txtdate=(EditText)view;
                break;
            case 1:
                txtTv=(TextView)view;
                break;
        }

    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {


// Use the current date as the default date in the dialog
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);


    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        //show to the selected date in the text box
        String date=year+"-"+(month+1)+"-"+day;
        switch (type){
            case 0:
                txtdate.setText(date);
                break;
            case 1:
                txtTv.setText(date);
                break;
        }

    }



}
