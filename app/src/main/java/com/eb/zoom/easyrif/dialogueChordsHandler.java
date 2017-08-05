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
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.R.attr.id;
import static android.R.attr.visible;

/**
 * Created by Edouard on 04/08/2017.
 */

public class dialogueChordsHandler extends DialogFragment {

    final protected String[] Keys = {"A","B","C","D","E","F","G"};
    protected int[] idKeys;
    protected Button[] ButtonKeys = {null,null,null,null,null,null,null};
    public String selectedChord = null;
    protected ImageButton confirmChord;
    public onChordIsConfirmedlistener chordConf;
    protected String[] oChord=null;
    protected Chord objChord=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //Creation of the pop up dialog
        Log.d("ChordHandler", "Ready to create a chord");
        final View v = inflater.inflate(R.layout.boitediachords, container, false);

        //This should be accessible
        final TextView prev = (TextView) v.findViewById(R.id.chordPreview);

        //Set default values for Radio boxes:
        RadioGroup radioMm = (RadioGroup) v.findViewById(R.id.choiceMm);
        radioMm.check(R.id.major);


        RadioGroup radioAlt = (RadioGroup) v.findViewById(R.id.alter);
        radioAlt.check(R.id.natural);

        //Set the process when clicking on one of the Keys
        idKeys = new int[]{R.id.Akey,R.id.Bkey,R.id.Ckey,R.id.Dkey,R.id.Ekey,R.id.Fkey,R.id.Gkey};
        for (int i=0 ; i<7 ; i++)
        {
            ButtonKeys[i]=(Button) v.findViewById(idKeys[i]);
            ButtonKeys[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Process triggered when clicking on the key ButtonKeys[i]
                    Button b = (Button) view;
                    RadioGroup radioMm = (RadioGroup) v.findViewById(R.id.choiceMm);
                    RadioGroup radioAlt = (RadioGroup) v.findViewById(R.id.alter);
                    RadioButton sel1 = (RadioButton) v.findViewById(radioAlt.getCheckedRadioButtonId());
                    RadioButton sel2 = (RadioButton) v.findViewById(radioMm.getCheckedRadioButtonId());
                    selectedChord = b.getText().toString()+sel1.getContentDescription().toString()+sel2.getContentDescription();
                    Log.d("ChordHandler", "Key has been pressed : "+ selectedChord);
                    //Preview of the chord:
                    LinearLayout prevCont=(LinearLayout)v.findViewById(R.id.previewCont);
                    prevCont.setVisibility(View.VISIBLE);
                    objChord = findChord(selectedChord);
                    objChord.setKey(b.getText().toString()+sel1.getContentDescription().toString());
                    if (objChord!=null) {
                        prev.setText(objChord.getName() + " : " + objChord.getContentAsString());
                    }
                    else {
                        prev.setText("Chord is not in the database, sorry...");
                    }
                }
            });
        }

        //Reset the preview when changing the radio boxes afterwards:
        radioMm.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (objChord==null)
                {
                    //No Key has been selected, no display has been performed so far
                }
                else
                {
                    RadioGroup radioMm = (RadioGroup) v.findViewById(R.id.choiceMm);
                    RadioButton sel2 = (RadioButton) v.findViewById(radioMm.getCheckedRadioButtonId());
                    selectedChord = objChord.getKey()+sel2.getContentDescription();
                    objChord=findChord(selectedChord);
                    if (objChord!=null) {
                        prev.setText(objChord.getName() + " : " + objChord.getContentAsString());
                    }
                    else {
                        prev.setText("Chord is not in the database, sorry...");
                    }
                }
            }
        });

        //Set the commit button
        confirmChord =(ImageButton) v.findViewById(R.id.writeChord);
        confirmChord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Actions to perform when confirming the chord.
                if (objChord != null)
                {
                    chordConf.onChordIsConfirmed(objChord);
                    dismiss();
                }
            }
        });
        return  v;
    }

    private Chord findChord(String selectedChord) {
        //Here we should generate the requested chord tab.
        try {
            Log.d("ChordHandler","We enter the try");
            InputStream ins = getResources().openRawResource(R.raw.dbchords);
            Log.d("ChordHandler","File is being opened");
            int value2;
            // On utilise un StringBuffer pour construire la chaîne au fur et à mesure
            StringBuffer lu2 = new StringBuffer();
            // On lit les caractères les uns après les autres
            while((value2 = ins.read()) != -1) {
                // On écrit dans le fichier le caractère lu
                lu2.append((char)value2);
            }
            JSONArray iJson= new JSONArray(lu2.toString());
            boolean chordFoundInDB = false;
            for (int i=0 ; i<7 ; i++)
            {
                JSONArray chordsinAArray = iJson.getJSONObject(i).getJSONArray("chord");
                for (int j=0 ; j<2 ; j++)
                {
                    JSONObject specificChord = chordsinAArray.getJSONObject(j);
                    String ChordNameij = specificChord.getString("@name");
                    if (ChordNameij.equals(selectedChord))
                    {
                        Log.d("ChordHandler","Requested chord has been found in the db:"+ ChordNameij);
                        chordFoundInDB= true;
                        //Translate the format from the db in the internal datamodel
                        String[] tChord = translate(specificChord);
                        objChord = new Chord();
                        objChord.setcontent(new String[]{tChord[0], tChord[1], tChord[2], tChord[3], tChord[4], tChord[5]});
                        objChord.setName(ChordNameij);
                        Log.d("ChordHandler","objChord has been correctly populated in findChord function"+objChord.getContentAsString());
                    }
                }
            }
            if (chordFoundInDB==false)
            {
                Log.d("ChordHandler","Requested chord is not in our db... Sorry !");
                objChord=null;
            }
        } catch (FileNotFoundException e) {
            Log.d("ChordHandler","Filedoes not exist");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("ChordHandler","IO exception");
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return objChord ;
    }

    private String[] translate(JSONObject specificChord) {
        String[] oChord = {null, null, null, null, null, null};
        try {
            //Handle the chords not to be played
            JSONArray notPlayed = specificChord.getJSONArray("notPlayed");
            if (notPlayed!=null)
            {
                for (int i=0 ; i<notPlayed.length() ; i++ )
                {

                    int chordNumb = notPlayed.getInt(i);
                    oChord[chordNumb]="-";
                    Log.d("ChordHandler","Chord "+ chordNumb +" Should not be played");
                }
            }
            //Handles the chords for which a finger is pressed
            try {
                JSONArray fingers = specificChord.getJSONArray("fingers");
                if (fingers != null) {
                    for (int i = 0; i < fingers.length(); i++) {
                        JSONObject finger = fingers.getJSONObject(i);
                        int fretNumb = finger.getInt("@row");
                        int chordNumb = finger.getInt("@column");
                        oChord[chordNumb]=""+fretNumb;
                        Log.d("ChordHandler", "Chord " + chordNumb + "should be pressed in fret " + fretNumb);
                    }
                }
            } catch (JSONException e)
            {
                //Case where the is a 'barre'
                try {
                    JSONObject fingers= specificChord.getJSONObject("fingers");
                    JSONObject barre = fingers.getJSONObject("barre");
                    for (int i=barre.getInt("@start"); i<barre.getInt("@end")+1; i++)
                    {
                        oChord[i]=""+barre.getInt("@row");
                    }
                    JSONArray finger= fingers.getJSONArray("finger");
                    if (finger!=null)
                    {
                        for (int i = 0; i < finger.length(); i++) {
                            JSONObject fing = finger.getJSONObject(i);
                            int fretNumb = fing.getInt("@row");
                            int chordNumb = fing.getInt("@column");
                            oChord[chordNumb]=""+fretNumb;
                            Log.d("ChordHandler", "Chord " + chordNumb + "should be pressed in fret " + fretNumb);
                        }
                    }
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
            }
        } catch (JSONException e) {
        e.printStackTrace();
    }
        for (int i=0 ; i<oChord.length ; i++ )
        {
            if (oChord[i]==null)
            {
                oChord[i]=""+0;
            }
        }
        Log.d("ChordHandler", "The chord to be returned looks like that:" + oChord[0] +oChord[1]+oChord[2]+oChord[3]+oChord[4]+oChord[5]);
        return oChord;
    }

    //Definition of the function to be defined in the mainActivity, to react after the name is confirmed.
    public interface onChordIsConfirmedlistener{
        public void onChordIsConfirmed(Chord objChord);
    }
}