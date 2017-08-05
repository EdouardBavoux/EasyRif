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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Edouard on 29/07/2017.
 */


public class dialogueQuit extends DialogFragment{

    public onQuitIsConfirmedlistener QuitCom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        // Create the pop up dialog
        final View v = inflater.inflate(R.layout.boitediaquit, container, false);

        //Handle the confirmation of the deletion
        Button validQuit = (Button) v.findViewById(R.id.validQuit);
        validQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Action to perform when validating the deletion of the tab
                QuitCom.onQuitIsConfirmed();
                Log.d("dialog", "Quit the edition");
                dismiss();
            }
        });

        //Handle the cancelation of the deletion
        Button cancelQuit = (Button) v.findViewById(R.id.cancelQuit);
        cancelQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Action to perform when canceling the deletion of the tab
                Log.d("dialog", "Stay on edition");
                dismiss();
            }
        });

        return v ;
    }

    //Definition of the function to be defined in the mainActivity, to react after deletion order is confirmed.
    public interface onQuitIsConfirmedlistener{
        public void onQuitIsConfirmed();
    }
}

