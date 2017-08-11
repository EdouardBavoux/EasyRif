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

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import static android.animation.ObjectAnimator.ofArgb;

public class MainActivity extends Activity implements dialogueNameTab.onNameIsCommittedlistener {

    //Variables creation
    protected com.shawnlin.numberpicker.NumberPicker[] p = {null,null,null,null,null,null};
    protected RelativeLayout pickerLayout ;
    protected TextView[] dl = {null,null,null,null,null,null};
    protected Button[] previousChords = {null,null,null,null};
    protected Button[] previousEntries = {null,null,null,null};
    protected boolean wheelsLock=false;
    private ImageButton lock;
    private ImageButton reset;
    private ImageButton writeL;
    private ImageButton eraseL;
    private boolean tabIsSaved=true;
    private Button printT;
    private Button saveT;
    private Button chordHandler;
    protected Chord[] latestChords = {null,null,null,null};
    protected String[][] latestEntries = {null,null,null,null};
    protected int countReset=0;
    protected String[] tab[];
    protected String exportedTab;
    protected int sizeCol = 0;
    protected int iter;
    protected HorizontalScrollView scroll;
    protected String filename = null;
    protected String otab = "test1";
    protected String itab = "";
    protected FileOutputStream outputStream;
    protected FileInputStream inputStream;
    protected JSONObject Json;
    protected JSONObject iJson;
    protected TextView tabName = null;
    protected String[] TabList = null;
    protected Intent i2;
    protected ValueAnimator cursorColorAnimation=null;
    protected Integer indexEdition;
    protected Toast editionMode = null;
    boolean entryIsAGeneratedChordOrSaved = false;
    public String tabNameString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creation of the wheels/picker and tab lines
        int[] PickerId = new int[]{R.id.Picker6,R.id.Picker5,R.id.Picker4,R.id.Picker3,R.id.Picker2,R.id.Picker1};
        int[] dispLine = new int[]{R.id.dispLine1,R.id.dispLine2,R.id.dispLine3,R.id.dispLine4,R.id.dispLine5,R.id.dispLine6};

        for (int i=0;i<p.length;i++)
        {
            p[i] = (com.shawnlin.numberpicker.NumberPicker) findViewById(PickerId[i]);
            p[i].setMinValue(0);
            p[i].setMaxValue(27);
            p[i].setDisplayedValues(new String[]{"-", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24","|", "X"});
            p[i].setValue(0);
            dl[i] = (TextView) findViewById(dispLine[i]);
        }

        //Handle the Lock and Reset of the wheels.
        reset = (ImageButton) findViewById(R.id.resetWheels);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //What to do when clciking on reset:
                Log.d("Zoomeb","Reset wheels has been clicked ");
                //Set wheels to -
                for (int i=0;i<dl.length;i++)
                {
                    p[i].setValue(0);
                }
                //Hide the reset button, show the lock one
                reset.setVisibility(View.GONE);
                lock.setVisibility(View.VISIBLE);

                //Toast for a second click
                if (countReset<2) {
                    Toast.makeText(getApplicationContext(), "If you click once again on the lock, wheels will be linked together",
                            Toast.LENGTH_LONG).show();
                    countReset++;
                }
                //Listen to the wheels: If the move, reset should reappear
                for (int i=0;i<dl.length;i++)
                {
                    p[i].setOnScrollListener(new com.shawnlin.numberpicker.NumberPicker.OnScrollListener() {
                        @Override
                        public void onScrollStateChange(com.shawnlin.numberpicker.NumberPicker view, int scrollState) {
                            // Override with nothing to perform !
                            if (reset.getVisibility()==View.GONE) {
                                reset.setVisibility(View.VISIBLE);
                                lock.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
        lock = (ImageButton) findViewById(R.id.lockWheels);
        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //What to do when lock button is clicked
                Log.d("Zoomeb","Lock wheels has been clicked ");
                if (wheelsLock)
                {
                    reset.setVisibility(View.GONE);
                    //Unlock the wheels !
                    for (int i=0;i<dl.length;i++)
                    {
                        p[i].setOnScrollListener(new com.shawnlin.numberpicker.NumberPicker.OnScrollListener() {
                            @Override
                            public void onScrollStateChange(com.shawnlin.numberpicker.NumberPicker view, int scrollState) {
                                // Override with nothing to perform !
                            }
                        });
                    }
                    //Highlight the Unlock !
                    lock.setBackgroundColor(getResources().getColor(R.color.superLightBlue));
                    for (int i=0 ; i<p.length;i++)
                    {
                        p[i].setSelectedTextColor(getResources().getColor(R.color.White));
                        p[i].setDividerColor(getResources().getColor(R.color.White));
                        p[i].setDividerThickness(3);
                    }
                    //Change the button
                    reset.setVisibility(View.VISIBLE);
                    lock.setVisibility(View.GONE);
                }
                else
                {
                    //Set wheels to -
                    for (int i=0;i<dl.length;i++)
                    {
                        p[i].setValue(0);
                    }
                    //Lock the wheels !
                    for (int h=0;h<p.length;h++)
                    {
                        p[h].setOnScrollListener(new com.shawnlin.numberpicker.NumberPicker.OnScrollListener() {
                            @Override
                            public void onScrollStateChange(com.shawnlin.numberpicker.NumberPicker view, int scrollState) {
                                for (int j=0 ; j<dl.length; j++)
                                    if (view.getId()!=p[j].getId())
                                    {
                                        p[j].setValue(view.getValue());
                                    }
                            }
                        });
                    }
                    //Highlight the lock !
                    lock.setBackgroundColor(getResources().getColor(R.color.Orange));
                    for (int k=0 ; k<p.length;k++)
                    {
                        p[k].setSelectedTextColor(getResources().getColor(R.color.Orange));
                        p[k].setDividerColor(getResources().getColor(R.color.Orange));
                        p[k].setDividerThickness(5);
                    }
                }
                wheelsLock=!wheelsLock;
                animatePickers();
            }
        });

        //Handle the loading of a tab stored in JSON
        i2=getIntent();
        Log.d("Zoomeb","tabNameInt is : "+ i2.getStringExtra("tabNameInt"));
        if(i2.getStringExtra("tabNameInt") ==null) {Log.d("Zoomeb","A fresh new tab from scratch !");}
        else {
            tabNameString = i2.getStringExtra("tabNameInt");
            //Load the tab in the container
            Log.d("Zoomeb","Let's load the following saved tab:"+ i2.getStringExtra("tabNameInt"));
            try {
                Log.d("Zoomeb","We enter the try");
                String tabString=null;
                if (i2.getStringExtra("tabNameInt").equals("A Magnificent Tab Example"))
                {
                    Log.d("Zoomeb","We are loading the tab example");
                    InputStream tabInputStream=getResources().openRawResource(R.raw.tabexample);
                    int value2;
                    // On utilise un StringBuffer pour construire la chaîne au fur et à mesure
                    StringBuffer lu2 = new StringBuffer();
                    // On lit les caractères les uns après les autres
                    while((value2 = tabInputStream.read()) != -1) {
                        // On écrit dans le fichier le caractère lu
                        lu2.append((char)value2);
                    }
                    tabString=lu2.toString();
                }
                else
                {
                    inputStream = openFileInput(i2.getStringExtra("tabNameInt"));
                    Log.d("Zoomeb","File is being opened");
                    int value2;
                    // On utilise un StringBuffer pour construire la chaîne au fur et à mesure
                    StringBuffer lu2 = new StringBuffer();
                    // On lit les caractères les uns après les autres
                    while((value2 = inputStream.read()) != -1) {
                        // On écrit dans le fichier le caractère lu
                        lu2.append((char)value2);
                    }
                    tabString=lu2.toString();
                }

                Log.d("Zoomeb","The file contains:"+tabString);

/*                //TEMPORARY, intent to share it with other applis.
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, lu2.toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);*/

                iJson= new JSONObject(tabString);
                for (int i=0;i<dl.length;i++)
                {
                    dl[i].setText(iJson.getJSONObject(i2.getStringExtra("tabNameInt")).getString("line"+(i+1)));
                }
                refreshEntireClickableTab();
            } catch (FileNotFoundException e) {
                Log.d("Zoomeb","Filedoes not exist");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("Zoomeb","IO exception");
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Handle the writting of a new column in the edited tab
        writeL = (ImageButton) findViewById(R.id.writeLine);
        writeL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //What to do when writeLine button is clicked
                tabIsSaved=false;
                String col1[] = {p[0].getDisplayedValues()[p[0].getValue()],p[1].getDisplayedValues()[p[1].getValue()], p[2].getDisplayedValues()[p[2].getValue()],p[3].getDisplayedValues()[p[3].getValue()],p[4].getDisplayedValues()[p[4].getValue()],p[5].getDisplayedValues()[p[5].getValue()]};
                Log.d("Zoomeb","New column is: "+ col1[0]+col1[1]+col1[2]+col1[3]+col1[4]+col1[5]);

                //Take into account that some colums may be 2-characters long
                sizeCol=Math.max(Math.max(Math.max(col1[0].length(),col1[1].length()),Math.max(col1[2].length(),col1[3].length())), Math.max(col1[4].length(),col1[5].length()));

                //COnsider if the value to be added is a "note" or a symbol
                boolean colIsANote=false;
                for (int i=0 ; i<col1.length ; i++)
                {
                    if (!(col1[i].equals("-")||col1[i].equals("|"))){
                        colIsANote = true ;
                    }
                }

                if (indexEdition==null) {
                    //Edition of the tab, from the last index
                    if (colIsANote) {
                        //We check if the previous entry was a separator (none of the line contains an int)
                        String[] prevEntry = {null, null, null, null, null, null};
                        boolean prevEntryIsSeparator = true;
                        for (int i = 0; i < dl.length; i++) {
                            prevEntry[i] = dl[i].getText().toString().substring(dl[0].length() - 1, dl[0].length());
                            if (!(prevEntry[i].equals("-") || prevEntry[i].equals("|"))) {
                                prevEntryIsSeparator = false;
                            }
                        }
                        if (!prevEntryIsSeparator) {
                            for (int i = 0; i < dl.length; i++) {
                                if (sizeCol == 2 && col1[i].length() == 1) {
                                    dl[i].setText(dl[i].getText() + "-" + col1[i] + "-");
                                } else {
                                    dl[i].setText(dl[i].getText() + "-" + col1[i]);
                                }
                            }
                        } else {
                            for (int i = 0; i < dl.length; i++) {
                                if (sizeCol == 2 && col1[i].length() == 1) {
                                    dl[i].setText(dl[i].getText() + col1[i] + "-");
                                } else {
                                    dl[i].setText(dl[i].getText() + col1[i]);
                                }
                            }
                        }
                    }
                    else
                    {
                        for (int i = 0; i < dl.length; i++) {
                            dl[i].setText(dl[i].getText() + col1[i]);
                        }

                    }
                    scroll = (HorizontalScrollView) findViewById(R.id.ScrollTab);
                    scroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                }
                else
                {
                    //Edition of the tab, from a given index (History mode)
                    Log.d("Zoomeb","Edition of the tab, from a given index  ");

                    if (colIsANote) {
                        //We check if the previous entry was a separator (none of the line contains an int)
                        String[] prevEntry = {null, null, null, null, null, null};
                        boolean prevEntryIsSeparator = true;
                        for (int i = 0; i < dl.length; i++) {
                            prevEntry[i] = dl[i].getText().toString().substring(indexEdition - 1, indexEdition);
                            if (!(prevEntry[i].equals("-") || prevEntry[i].equals("|"))) {
                                prevEntryIsSeparator = false;
                            }
                        }
                        Log.d("Zoomeb", "Previous entry is a separator : " + prevEntryIsSeparator + " And content is : " + prevEntry[0] + prevEntry[1] + prevEntry[2] + prevEntry[3] + prevEntry[4] + prevEntry[5]);

                        //We check the next character of each line, if it is not the last column
                        boolean nextEntryIsSeparator = false;
                        if (indexEdition + 2 < dl[0].length() - 1) {
                            String[] nextEntry = {null, null, null, null, null, null};
                            nextEntryIsSeparator = true;
                            for (int i = 0; i < dl.length; i++) {
                                nextEntry[i] = dl[i].getText().toString().substring(indexEdition + 1, indexEdition + 2);
                                if (!(nextEntry[i].equals("-") || nextEntry[i].equals("|"))) {
                                    nextEntryIsSeparator = false;
                                }
                            }
                            Log.d("Zoomeb", "Next entry is a separator : " + nextEntryIsSeparator + " And content is : " + nextEntry[0] + nextEntry[1] + nextEntry[2] + nextEntry[3] + nextEntry[4] + nextEntry[5]);
                        }

                        if (prevEntryIsSeparator && !nextEntryIsSeparator) {
                            //The previous entry is a separator, next one is not. We add - after
                            for (int i = 0; i < dl.length; i++) {
                                if (sizeCol == 2 && col1[i].length() == 1) {
                                    Log.d("Zoomeb", "Previous entry was ------ ");

                                    dl[i].setText(dl[i].getText().toString().substring(0, indexEdition) + col1[i] + "-" + "-" + dl[i].getText().toString().substring(indexEdition + 1, dl[i].length()));
                                } else {
                                    dl[i].setText(dl[i].getText().toString().substring(0, indexEdition) + col1[i] + "-" + dl[i].getText().toString().substring(indexEdition + 1, dl[i].length()));
                                }
                            }
                        } else if (!prevEntryIsSeparator && nextEntryIsSeparator) {
                            //The previous entry is NOT a separator, but next is : We should add one before
                            for (int i = 0; i < dl.length; i++) {
                                if (sizeCol == 2 && col1[i].length() == 1) {
                                    dl[i].setText(dl[i].getText().toString().substring(0, indexEdition) + "-" + col1[i] + "-" + dl[i].getText().toString().substring(indexEdition + 1, dl[i].length()));
                                } else {
                                    dl[i].setText(dl[i].getText().toString().substring(0, indexEdition) + "-" + col1[i] + dl[i].getText().toString().substring(indexEdition + 1, dl[i].length()));
                                }
                            }
                        } else if (!prevEntryIsSeparator && !nextEntryIsSeparator) {
                            //The previous and the next entries are NOT separators: We should add both)
                            for (int i = 0; i < dl.length; i++) {
                                if (sizeCol == 2 && col1[i].length() == 1) {
                                    dl[i].setText(dl[i].getText().toString().substring(0, indexEdition) + "-" + col1[i] + "-" + "-" + dl[i].getText().toString().substring(indexEdition + 1, dl[i].length()));
                                } else {
                                    dl[i].setText(dl[i].getText().toString().substring(0, indexEdition) + "-" + col1[i] + "-" + dl[i].getText().toString().substring(indexEdition + 1, dl[i].length()));
                                }
                            }
                        } else {
                            //The previous and the next entries are separators: We should add none)
                            for (int i = 0; i < dl.length; i++) {
                                if (sizeCol == 2 && col1[i].length() == 1) {
                                    dl[i].setText(dl[i].getText().toString().substring(0, indexEdition) + col1[i] + "-" + dl[i].getText().toString().substring(indexEdition + 1, dl[i].length()));
                                } else {
                                    dl[i].setText(dl[i].getText().toString().substring(0, indexEdition) + col1[i] + dl[i].getText().toString().substring(indexEdition + 1, dl[i].length()));
                                }
                            }
                        }

                        if (!prevEntryIsSeparator) {
                            sizeCol++;
                        }
                        if (!nextEntryIsSeparator) {
                            sizeCol++;
                        }
                    }
                    else {
                        for (int i = 0; i < dl.length; i++) {
                            dl[i].setText(dl[i].getText().toString().substring(0, indexEdition) + col1[i] + dl[i].getText().toString().substring(indexEdition + 1, dl[i].length()));
                        }
                    }
                    setEditingIndex(indexEdition+sizeCol);
                }
                refreshEntireClickableTab();

                //Second, update the recent Entry list
                if (!entryIsAGeneratedChordOrSaved) {
                    if (latestEntries[0] == null) {
                        //This is the first Entry to be added
                        Log.d("Zoomeb", "First entry to be added to previous entries ");
                        latestEntries[0] = col1;
                        TextView titlePreviousEntries = (TextView) findViewById(R.id.titlePreviousEntries);
                        LinearLayout previousEntries = (LinearLayout) findViewById(R.id.previousEntriesCont);
                        titlePreviousEntries.setVisibility(View.VISIBLE);
                        previousEntries.setVisibility(View.VISIBLE);
                    } else {
                        boolean entryIsDifferentFromPrev = false;
                        for (int i = 0; i < col1.length; i++) {
                            if (!latestEntries[0][i].equals(col1[i])) {
                                entryIsDifferentFromPrev = true;
                            }
                        }
                        if (entryIsDifferentFromPrev) {
                            Log.d("Zoomeb", "This is not the first entry, and it is different from the latest previous");
                            {
                                int nbEntryStored = 0;
                                for (int i = 0; i < latestEntries.length; i++) {
                                    if (latestEntries[i] != null) {
                                        nbEntryStored++;
                                    }
                                }
                                for (int i = nbEntryStored; i > 0; i--) {
                                    if (i < previousEntries.length) {
                                        latestEntries[i] = latestEntries[i - 1];
                                    }
                                }
                                latestEntries[0] = col1;
                            }
                        }
                    }
                    int[] ids = {R.id.PreviousEntry1, R.id.PreviousEntry2, R.id.PreviousEntry3, R.id.PreviousEntry4};
                    for (int i = 0; i < previousEntries.length; i++) {
                        previousEntries[i] = (Button) findViewById(ids[i]);
                        if (latestEntries[i] != null) {
                            previousEntries[i].setText(latestEntries[i][5] + "\n" + latestEntries[i][4] + "\n" + latestEntries[i][3] + "\n" + latestEntries[i][2] + "\n" + latestEntries[i][1] + "\n" + latestEntries[i][0]);

                            previousEntries[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //Defines what should be done when clicking on a previous entry
                                    entryIsAGeneratedChordOrSaved=true;
                                    reset.setVisibility(View.VISIBLE);
                                    lock.setVisibility(View.GONE);
                                    for (int j = 0; j < p.length; j++) {
                                        Integer EntryNb = Integer.valueOf(view.getContentDescription().toString()) - 1;
                                        Log.d("Zoomeb", "EntryNB is :" + EntryNb);
                                        if (latestEntries[EntryNb] != null) {
                                            String[] entryToBeDisplayed = latestEntries[EntryNb];
                                            switch (entryToBeDisplayed[j]) {
                                                case "-":
                                                    p[j].setValue(0);
                                                    break;
                                                case "|":
                                                    p[j].setValue(26);
                                                    break;
                                                case "X":
                                                    p[j].setValue(27);
                                                    break;
                                                default:
                                                    Integer val = Integer.valueOf(entryToBeDisplayed[j]) + 1;
                                                    p[j].setValue(val);
                                            }
                                        }
                                    }
                                    animatePickers();
                                    writeL.performClick();
                                }
                            });
                        }
                    }
                }
                else
                {
                    entryIsAGeneratedChordOrSaved =false;}
            }
        });

        //Handle the deletion of a previous column in the edited tab
        eraseL = (ImageButton) findViewById(R.id.undoLine);
        eraseL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Actions to do when clicking on Erase line
                tabIsSaved=false;
                if (indexEdition==null)
                {
                    Log.d("Zoomeb","sizeCol value is: "+ sizeCol);
                    switch(sizeCol){
                        case 0 :{
                            Log.d("Zoomeb","Deletion character by character ");
                            if (dl[0].length()<4) {
                                Log.d("Zoomeb","Nothing to be erased");
                            }
                            else {
                                Log.d("Zoomeb","Deletion of one column");
                                for (int i=0;i<dl.length;i++)
                                {
                                    dl[i].setText(dl[i].getText().toString().substring(0,dl[i].length()-1));
                                }
                            }
                        }
                        break;
                        case 1 :{
                            Log.d("Zoomeb","Last value was 2 char-long. Deletion starts");
                            for (int i=0;i<dl.length;i++)
                            {
                                dl[i].setText(dl[i].getText().toString().substring(0,dl[i].length()-sizeCol-1));
                            }
                            sizeCol=0;
                        }
                        break;
                        case 2 :{
                            Log.d("Zoomeb","Last value was 3 char-long. Deletion starts");
                            for (int i=0;i<dl.length;i++)
                            {
                                dl[i].setText(dl[i].getText().toString().substring(0,dl[i].length()-sizeCol-1));
                            }
                            sizeCol=0;
                        }
                        break;
                        default: {
                            Log.d("Zoomeb","There's something strange... In the neighborhood... ");

                        }
                    }
                    scroll = (HorizontalScrollView) findViewById(R.id.ScrollTab);
                    scroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                }
                else
                {
                    //Only deletion character by character in the middle of the tab.
                    Log.d("Zoomeb","Only deletion character by character in the middle of the tab ");
                    if (indexEdition<4) {
                        Log.d("Zoomeb","Nothing to be erased");
                    }
                    else {
                        Log.d("Zoomeb","Deletion of the column number: "+indexEdition);
                        for (int i=0;i<dl.length;i++)
                        {
                            String prevCont = dl[i].getText().toString().substring(0,indexEdition-1);
                            String nextCont = dl[i].getText().toString().substring(indexEdition+1,dl[i].length());
                            Log.d("Zoomeb","Before the selected item, we should keep: "+prevCont);
                            Log.d("Zoomeb","After the selected item, we should keep "+nextCont);
                            Log.d("Zoomeb","Content of the column to be deleted in column "+i+"is : "+dl[i].getText().toString().substring(indexEdition-1,indexEdition));
                            dl[i].setText(prevCont+nextCont);
                        }
                        setEditingIndex(indexEdition-1);
                    }

                }
                refreshEntireClickableTab();
            }
        });

        //Handle the feature to share tabs (whatsapp, clipboard,...)
        printT = (Button)findViewById(R.id.printTab);
        printT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Actions to perform when clicking on Print tab
                dialogueLineSize AskLineSize = new dialogueLineSize();
                AskLineSize.show(getFragmentManager(), "Dialog");

                AskLineSize.LineSizeCom = new dialogueLineSize.onLineSizeIsCommittedlistener() {
                    @Override
                    public void onLineSizeIsCommitted(Integer lineSize) {
                        //Actions to perform now that we have the user requested linewidth.

                        //First , Edition of the complete tab in a multi-line string:
                        exportedTab="```"+"Check out this riff :";
                        String Newligne=System.getProperty("line.separator");
                        int tableftlength=dl[0].length();
                        int linewidth=lineSize;
                        int i=0;
                        while(tableftlength>(linewidth*(i+1)))
                        {
                            //There is 6 lines to write before going to a new line
                            exportedTab=exportedTab+Newligne+Newligne+dl[5].getText().toString().substring(i*linewidth,(i+1)*linewidth)+Newligne+
                                    dl[4].getText().toString().substring(i*linewidth,(i+1)*linewidth)+Newligne+
                                    dl[3].getText().toString().substring(i*linewidth,(i+1)*linewidth)+Newligne+
                                    dl[2].getText().toString().substring(i*linewidth,(i+1)*linewidth)+Newligne+
                                    dl[1].getText().toString().substring(i*linewidth,(i+1)*linewidth)+Newligne+
                                    dl[0].getText().toString().substring(i*linewidth,(i+1)*linewidth);
                            i++;
                        }
                        //Now, the are just 6 lines left
                        exportedTab=exportedTab+Newligne+Newligne+dl[5].getText().toString().substring(i*linewidth,tableftlength-1)+Newligne+
                                dl[4].getText().toString().substring(i*linewidth,tableftlength-1)+Newligne+
                                dl[3].getText().toString().substring(i*linewidth,tableftlength-1)+Newligne+
                                dl[2].getText().toString().substring(i*linewidth,tableftlength-1)+Newligne+
                                dl[1].getText().toString().substring(i*linewidth,tableftlength-1)+Newligne+
                                dl[0].getText().toString().substring(i*linewidth,tableftlength-1)+"```";

                        //exportedTab="```"+"Check out this riff :"+Newligne+exportedTab+Newligne+dl[4].getText().toString()+Newligne+dl[3].getText().toString()+Newligne+dl[2].getText().toString()+Newligne+dl[1].getText().toString()+Newligne+dl[0].getText().toString()+"```";
                        Log.d("Zoomeb","The exportedTab should look like: "+Newligne+ exportedTab);



                        //Then, intent to share it with other applis.
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, exportedTab);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);

                    }
                };

            }
        });

        //Handle the Chord Handler

        chordHandler = (Button)findViewById(R.id.showChordHandler);
        chordHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Actions to perform when clicking on the chord creator
                Log.d("Zoomeb","Let's launch the chord creator");
                //Launch dialog box chord creator
                dialogueChordsHandler AskChord = new dialogueChordsHandler();
                AskChord.show(getFragmentManager(),"Dialog");

                AskChord.chordConf = new dialogueChordsHandler.onChordIsConfirmedlistener() {
                    @Override
                    public void onChordIsConfirmed(Chord objChord) {
                        //Action to perform when the name of the chord to be displayed has been validated.
                        entryIsAGeneratedChordOrSaved =true;
                        reset.setVisibility(View.VISIBLE);
                        lock.setVisibility(View.GONE);
                        //First, set the wheels
                        Log.d("Zoomeb","This time the chord is known in my main activity !!! "+ objChord.getContentAsString());
                        for (int i=0;i<dl.length;i++)
                        {
                            if (objChord.getContent()[i].equals("-"))
                            {
                                p[i].setValue(0);
                            }
                            else
                            {
                                Integer val = Integer.valueOf(objChord.getContent()[i])+1;
                                p[i].setValue(val);
                            }
                        }
                        //Second, write the line
                        writeL.performClick();

                        //Third, update the recent chords list
                        if (latestChords[0]==null)
                        {
                            //This is the first chord to be added
                            Log.d("Zoomeb","First chord to be added to previous chords ");
                            latestChords[0]=objChord;
                            TextView titlePreviousChords = (TextView) findViewById(R.id.titlePreviousChords);
                            LinearLayout previousChords = (LinearLayout) findViewById(R.id.previousChordsCont);
                            titlePreviousChords.setVisibility(View.VISIBLE);
                            previousChords.setVisibility(View.VISIBLE);
                        }
                        else if (!latestChords[0].getName().equals(objChord.getName())) {
                            Log.d("Zoomeb", "This is not the first chord, and it is different from the latest previous");
                            {
                                int nbChordStored = 0;
                                for (int i = 0; i < latestChords.length; i++) {
                                    if (latestChords[i] != null) {
                                        nbChordStored++;
                                    }
                                }
                                for (int i = nbChordStored ; i > 0 ; i--) {
                                    if (i<previousChords.length) {latestChords[i] = latestChords[i-1];}
                                }
                                latestChords[0] = objChord;
                            }
                        }
                        int[] ids = {R.id.previousChord0,R.id.previousChord1,R.id.previousChord2,R.id.previousChord3};
                        for (int i=0 ; i<previousChords.length ; i++) {
                            previousChords[i] = (Button) findViewById(ids[i]);
                            if (latestChords[i] != null) {
                                previousChords[i].setText(latestChords[i].getName());
                                previousChords[i].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //Defines what should be done when clicking on a previous chord
                                        for (int j=0;j<dl.length;j++)
                                        {
                                            entryIsAGeneratedChordOrSaved =true;
                                            reset.setVisibility(View.VISIBLE);
                                            lock.setVisibility(View.GONE);
                                            Integer chordNb = Integer.valueOf(view.getContentDescription().toString());
                                            if (latestChords[chordNb]!=null) {
                                                Chord chordToBeDisplayed = latestChords[chordNb];
                                                if (chordToBeDisplayed.getContent()[j].equals("-")) {
                                                    p[j].setValue(0);
                                                } else {
                                                    Integer val = Integer.valueOf(chordToBeDisplayed.getContent()[j]) + 1;
                                                    p[j].setValue(val);
                                                }
                                            }
                                        }
                                        animatePickers();
                                        writeL.performClick();
                                    }
                                });
                            }
                        }

                        // Third, highlight the wheels
                        animatePickers();
                    }
                };
            } });


        //Handle the Saving of tabs in JSON
        saveT = (Button)findViewById(R.id.SaveTab);
        saveT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Actions to perform when clicking on saveTab
                Log.d("Zoomeb","Let's save the tab");
                //Ask for the name of the tab
                dialogueNameTab AskName = new dialogueNameTab();
                AskName.show(getFragmentManager(),"Dialog");

                AskName.nameCom = new dialogueNameTab.onNameIsCommittedlistener() {
                    @Override
                    public void onNameIsCommitted(String iName) {
                        //Action to perfor when the name of the tab to be saved has been validated.
                        Log.d("Zoomeb","This time the name is known in my main activity !!! "+ iName);

                        String name = "TabEasyRif."+iName;
                        tabNameString = name;
                        // Create JSON object
                        try {
                            Json = new JSONObject();
                            JSONObject tabJson = new JSONObject();
                            for (int i=0;i<dl.length;i++)
                            {
                                tabJson.put("line"+(i+1), dl[i].getText().toString());

                            }
                            Json.put(name, tabJson);
                        } catch (JSONException e) {
                            Log.e("MYAPP", "unexpected JSON exception", e);
                        }
                        //Write in a File
                        try{
                            outputStream = openFileOutput(name,MODE_PRIVATE);
                            Log.d("Zoomeb","Looks good");
                            outputStream.write(Json.toString().getBytes());
                            if(outputStream != null)
                                outputStream.close();
                            Log.d("Zoomeb","The file path is:"+ getFilesDir());
                            TabList=fileList();
                            int i=0;
                            while (i<TabList.length) {
                                Log.d("Zoomeb","File list : "+ TabList[i]);
                                i++;
                            }
                            tabIsSaved=true;

                            //Refresh The list
                            //BrowsingActivity b = new BrowsingActivity();
                            //b.refreshTabList();

                            inputStream = openFileInput(name);
                            int value;
                            // On utilise un StringBuffer pour construire la chaîne au fur et à mesure
                            StringBuffer lu = new StringBuffer();
                            // On lit les caractères les uns après les autres
                            while((value = inputStream.read()) != -1) {
                                // On écrit dans le fichier le caractère lu
                                lu.append((char)value);
                            }
                            Log.d("Zoomeb","The file contains:"+lu.toString());
                        } catch (FileNotFoundException e) {
                            Log.d("Zoomeb","Filedoes not exist");
                            e.printStackTrace();
                        } catch (IOException e) {
                            Log.d("Zoomeb","IO exception");
                            e.printStackTrace();
                        }
                    }
                };
            }
        });


    }

    private void refreshEntireClickableTab() {
        //This method is used when loading a saved tab, to make it editable
        Log.d("Zoomeb","We enter the method refreshEntireClickableTab ");
        for (int lineIter = 0 ; lineIter<dl.length ; lineIter++)
        {
            final int line = lineIter;
            for (int charIter =0 ; charIter<dl[lineIter].length() ; charIter++)
            {
                SpannableString sps = new SpannableString(dl[lineIter].getText());
                ClickableSpan cls = new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        //Define what should be done when clicking on a part of the tab
                        Spanned spanned =  (Spanned) ((TextView) dl[line]).getText();
                        int indexClicked=spanned.getSpanStart(this);
                        Log.d("Zoomeb","Text has been clicked. Line is : "+line+" and Column is : "+ indexClicked );
                        setEditingIndex(indexClicked);
                    }
                };
                sps.setSpan(cls,charIter,charIter, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                dl[lineIter].setText(sps);
                dl[lineIter].setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    private void setEditingIndex(final int indexClicked) {
        //This method is used when we click somewhere in the tab, to edit a specific index
        final int realIndexClicked;
        if (indexClicked<4){realIndexClicked=3;}
        else {realIndexClicked=indexClicked;}
        Log.d("Zoomeb", "We enter the method setEditingIndex, avec argument égal à : "+realIndexClicked);
        if (realIndexClicked == dl[0].length() - 1 || realIndexClicked == dl[0].length()) {
            //Simple creation mode
            if (indexEdition != null) {
                if (editionMode != null) {
                    editionMode.cancel();
                }
                editionMode.makeText(this, "Back to simple edition mode.\nPress Back to exit this tab",
                        editionMode.LENGTH_LONG).show();
                indexEdition = null;
            }
            Log.d("Zoomeb", "Last column has been clicked");
            //TO DO : quit edition mode (remove highlight,...)
            for (int lineIter = 0; lineIter < dl.length; lineIter++) {
                //First, Remove the previous Editing cursor
                dl[lineIter].setText(removePreviousCursor(dl[lineIter].getText().toString()));

                //THen, reset the previous highlight
                final SpannableString sps = new SpannableString(dl[lineIter].getText());
                BackgroundColorSpan[] spansBck = sps.getSpans(0, sps.length(), BackgroundColorSpan.class);
                for (BackgroundColorSpan span : spansBck) {
                    sps.removeSpan(span);
                }
                StyleSpan[] spansStyle = sps.getSpans(0, sps.length(), StyleSpan.class);
                for (StyleSpan span : spansStyle) {
                    sps.removeSpan(span);
                }
                ForegroundColorSpan[] spansColor = sps.getSpans(0, sps.length(), ForegroundColorSpan.class);
                for (ForegroundColorSpan span : spansColor) {
                    sps.removeSpan(span);
                }
            }
            //Finally, autoscroll to the end and refresh clickability
            scroll = (HorizontalScrollView) findViewById(R.id.ScrollTab);
            scroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            refreshEntireClickableTab();
        } else {
            //History Edition mode
            if (indexEdition == null) {
                Log.d("Zoomeb", "Any column but the last has been clicked while we were NOT editing the history yet");
                if (editionMode!=null){
                    editionMode.cancel();
                }
                editionMode.makeText(this, "History Edition mode : \nPress back to return to normal mode",
                        editionMode.LENGTH_LONG).show();
                indexEdition = realIndexClicked;
            }
            indexEdition = realIndexClicked;
            for (int lineIter = 0; lineIter < dl.length; lineIter++) {
                //First, Remove the previous Editing cursor
                dl[lineIter].setText(removePreviousCursor(dl[lineIter].getText().toString()));

                //Then, add the Editing cursor at the right index
                dl[lineIter].setText(dl[lineIter].getText().toString().substring(0, realIndexClicked) + "_" + dl[lineIter].getText().toString().substring(realIndexClicked, dl[lineIter].getText().length()));

                //THen, reset the previous highlight
                final SpannableString sps = new SpannableString(dl[lineIter].getText());
                BackgroundColorSpan[] spansBck = sps.getSpans(0, sps.length(), BackgroundColorSpan.class);
                for (BackgroundColorSpan span : spansBck) {
                    sps.removeSpan(span);
                }
                StyleSpan[] spansStyle = sps.getSpans(0, sps.length(), StyleSpan.class);
                for (StyleSpan span : spansStyle) {
                    sps.removeSpan(span);
                }
                ForegroundColorSpan[] spansColor = sps.getSpans(0, sps.length(), ForegroundColorSpan.class);
                for (ForegroundColorSpan span : spansColor) {
                    sps.removeSpan(span);
                }

                //Then, create the present highlight
                StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                BackgroundColorSpan bckSpan = new BackgroundColorSpan(getResources().getColor(R.color.colorAccent));
                sps.setSpan(bckSpan, realIndexClicked, realIndexClicked + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sps.setSpan(boldSpan, realIndexClicked, realIndexClicked + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.colorAccent));
                sps.setSpan(colorSpan, realIndexClicked, realIndexClicked + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                dl[lineIter].setText(sps);
                dl[lineIter].setMovementMethod(LinkMovementMethod.getInstance());
            }
            refreshEntireClickableTab();
        }
    }


    private String removePreviousCursor(String iTabLine){
        String oTabLine=iTabLine;
        for (int i=0 ; i<iTabLine.length();i++)
        {
            if (iTabLine.substring(i,i+1).equals("_"))
            {

                Log.d("Zoomeb", "substring will be deleted : "+ iTabLine.substring(i,i+1) );
                oTabLine=iTabLine.substring(0,i)+iTabLine.substring(i+1,iTabLine.length());
            }
        }
        return oTabLine;
    }

    private void animatePickers() {
        pickerLayout=(RelativeLayout) findViewById(R.id.PickerLayout);
        pickerLayout.removeAllViews();
        for (int i=0;i<dl.length;i++)
        {
            pickerLayout.addView(p[i]);
        }

        Integer colorFrom = getResources().getColor(R.color.darkGreyBlue);
        Integer colorTo = getResources().getColor(R.color.Transparent);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(500);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                pickerLayout.setBackgroundColor((Integer)animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }

    @Override
    public void onBackPressed()
    {
        Log.d("Zoomeb", "button back has been pressed");
        if (indexEdition!=null)
        {
            setEditingIndex(dl[0].length()-1);
        }
        else {

            if (tabIsSaved) {
                super.onBackPressed();
            } else {
                dialogueQuit QuitEdition = new dialogueQuit();
                QuitEdition.show(getFragmentManager(), "Dialog");

                QuitEdition.QuitCom = new dialogueQuit.onQuitIsConfirmedlistener() {
                    @Override
                    public void onQuitIsConfirmed() {
                        //Intent i2 = new Intent(getApplicationContext(),BrowsingActivity.class);
                        //startActivity(i2);

                        MainActivity.super.onBackPressed();
                    }
                };
            }
        }
    }

    //implement public void onNameIsCommitted(String name);
    @Override
    public void onNameIsCommitted(String name){
        //Here we are
        Log.d("Zoomeb","Here we are resuming, name is to be saved.");
    }

}
