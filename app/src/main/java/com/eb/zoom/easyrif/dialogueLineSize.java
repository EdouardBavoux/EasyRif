/*Copyright 2017 Edouard Bavoux

This file is part of EasyRif.

        EasyRif is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        EasyRif is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with EasyRif.  If not, see <http://www.gnu.org/licenses/>.*/

package com.eb.zoom.easyrif;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by Edouard on 29/07/2017.
 */


public class dialogueLineSize extends DialogFragment{

    protected RadioGroup LineSize = null;
    protected RadioButton LineSizeSelected = null;
    protected RadioButton otherSize = null;
    protected Integer lineSizeInt;
    protected EditText lineEdit ;
    public onLineSizeIsCommittedlistener LineSizeCom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //Creation of the pop up dialog
        Log.d("dialog", "we're inside !");
        final View v = inflater.inflate(R.layout.boitedialinesize, container, false);

        //Handle the Validation of the export with the right size of line.
        otherSize = (RadioButton) v.findViewById(R.id.widthOther) ;
        otherSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lineEdit = (EditText) v.findViewById(R.id.fieldSize);
                lineEdit.setVisibility(View.VISIBLE);
                lineEdit.requestFocus();
            }
        });
        final Button confLineSize = (Button) v.findViewById(R.id.validFormExport);
        LineSize = (RadioGroup) v.findViewById(R.id.optionSizeContainer);

        confLineSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Action to perform when validating the form
                LineSizeSelected = (RadioButton) v.findViewById(LineSize.getCheckedRadioButtonId());
                lineEdit = (EditText) v.findViewById(R.id.fieldSize);
                if (LineSizeSelected.getContentDescription().toString().equals("other"))
                {
                    Log.d("dialog", "The size is in the editText");
                    //Fetch the value in EditText field
                    try {
                        lineSizeInt = Integer. parseInt(lineEdit.getText().toString());
                        if (lineSizeInt<1001 && lineSizeInt>4)
                        {
                            Log.d("dialog", "Line size after catch is: "+ lineSizeInt );
                            LineSizeCom.onLineSizeIsCommitted(lineSizeInt);
                            dismiss();
                        }
                        else {

                            Log.d("dialog", "The input is not in the range !!");
                            lineEdit.setHint("Only numbers between 5 and 1000 are accepted.");
                            lineEdit.setText("");
                            lineEdit.setHintTextColor(getResources().getColor(R.color.colorAccent));
                        }

                    }  catch (NumberFormatException e)
                    {
                        Log.d("dialog", "The input is not a number !!");
                        lineEdit.setHint("Only numbers between 10 and 1000 are accepted.");
                        lineEdit.setText("");
                        lineEdit.setHintTextColor(getResources().getColor(R.color.colorAccent));
                    }
                }
                else {

                    Log.d("dialog", "The size is predefined");
                    lineSizeInt = Integer.valueOf(LineSizeSelected.getContentDescription().toString());
                    Log.d("dialog", "Line size is: "+ lineSizeInt );
                    LineSizeCom.onLineSizeIsCommitted(lineSizeInt);
                    dismiss();
                }
            }
        });

        //Handle the key enter
        final EditText edittext = (EditText) v.findViewById(R.id.fieldSize);
        edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    confLineSize.performClick();
                    return true;
                }
                return false;
            }
        });

        return v ;
    }

    //Definition of the function to be defined in the mainActivity, to react after the name is confirmed.
    public interface onLineSizeIsCommittedlistener{
        public void onLineSizeIsCommitted(Integer lineSize);
    }
}
