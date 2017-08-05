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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.json.JSONException;
import org.json.JSONObject;

import static android.animation.ObjectAnimator.ofArgb;

public class MainActivity extends Activity implements dialogueNameTab.onNameIsCommittedlistener {

    //Variables creation
    protected NumberPicker[] p = {null,null,null,null,null,null};
    protected RelativeLayout pickerLayout ;
    protected TextView[] dl = {null,null,null,null,null,null};
    protected Button[] previousChords = {null,null,null,null};
    private ImageButton reset;
    private ImageButton writeL;
    private ImageButton eraseL;
    private boolean tabIsSaved=true;
    private Button printT;
    private Button saveT;
    private Button chordHandler;
    protected Chord[] latestChords = {null,null,null,null};
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creation of the wheels/picker and tab lines
        int[] PickerId = new int[]{R.id.Picker1,R.id.Picker2,R.id.Picker3,R.id.Picker4,R.id.Picker5,R.id.Picker6};
        int[] dispLine = new int[]{R.id.dispLine1,R.id.dispLine2,R.id.dispLine3,R.id.dispLine4,R.id.dispLine5,R.id.dispLine6};

        for (int i=0;i<dl.length;i++)
        {
            p[i] = (NumberPicker) findViewById(PickerId[i]);
            p[i].setMinValue(0);
            p[i].setMaxValue(26);
            p[i].setDisplayedValues(new String[]{"-", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "X"});

            dl[i] = (TextView) findViewById(dispLine[i]);
        }

        //Handle the Reset of the wheels.
        reset = (ImageButton) findViewById(R.id.resetWheels);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //What to do when reset button is clicked
                for (int i=0;i<dl.length;i++)
                {
                    p[i].setValue(0);
                }
                animatePickers();


            }
        });

        //Handle the loading of a tab stored in JSON
        i2=getIntent();
        Log.d("Zoomeb","tabNameInt is : "+ i2.getStringExtra("tabNameInt"));
        if(i2.getStringExtra("tabNameInt") ==null) {Log.d("Zoomeb","A fresh new tab from scratch !");}
        else {
            //Load the tab in the container
            Log.d("Zoomeb","Let's load the following saved tab:"+ i2.getStringExtra("tabNameInt"));
            try {
                Log.d("Zoomeb","We enter the try");
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
                Log.d("Zoomeb","The file contains:"+lu2.toString());
                iJson= new JSONObject(lu2.toString());
                for (int i=0;i<dl.length;i++)
                {
                    dl[i].setText(iJson.getJSONObject(i2.getStringExtra("tabNameInt")).getString("line"+(i+1)));
                }
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
                for (int i=0;i<dl.length;i++)
                {
                    if (sizeCol==2 && col1[i].length()==1) {dl[i].setText(dl[i].getText()+"-"+col1[i]+"-");}
                    else {dl[i].setText(dl[i].getText()+"-"+col1[i]);}
                }
                scroll = (HorizontalScrollView)findViewById(R.id.ScrollTab);
                scroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });

        //Handle the deletion of a previous column in the edited tab
        eraseL = (ImageButton) findViewById(R.id.undoLine);
        eraseL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Actions to do when clicking on Erase line
                tabIsSaved=false;
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
                        //Action to perform when the name of the tab to be saved has been validated.
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
                        //Second, update the recent chords list
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
                        else if (!latestChords[0].equals(objChord)) {
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
                    public void onNameIsCommitted(String name) {
                        //Action to perfor when the name of the tab to be saved has been validated.
                        Log.d("Zoomeb","This time the name is known in my main activity !!! "+ name);

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

    private void animatePickers() {
        pickerLayout=(RelativeLayout) findViewById(R.id.PickerLayout);
        pickerLayout.removeAllViews();
        for (int i=0;i<dl.length;i++)
        {
            pickerLayout.addView(p[i]);
        }
        Integer colorFrom = getResources().getColor(R.color.darkGreyBlue);
        Integer colorTo = getResources().getColor(R.color.superLightBlue);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(600);
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
        if (tabIsSaved) {super.onBackPressed();}
        else {
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

    //implement public void onNameIsCommitted(String name);
    @Override
    public void onNameIsCommitted(String name){
        //Here we are
        Log.d("Zoomeb","Here we are resuming, name is to be saved.");
    }

}
