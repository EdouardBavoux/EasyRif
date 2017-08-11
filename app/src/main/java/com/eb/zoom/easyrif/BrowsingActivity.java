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

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.addAll;

public class BrowsingActivity extends Activity {

    public ListView tabL;
    public Button newTab;
    public MyCustomAdapter tabAdapter;
    public ArrayList myTabs ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing);

        //Display the list of tabs that have been stored / saved previously
        tabL=(ListView) findViewById(R.id.tablist);
        String[] listOfFiles = fileList();
        if (listOfFiles.length==0)
        {listOfFiles=new String[]{"A Magnificent Tab Example"};}
        myTabs = new ArrayList(Arrays.asList(listOfFiles));
        Log.d("BrowsingTabs","My files: "+ myTabs.toString());
        tabAdapter = new MyCustomAdapter(myTabs,this);
        tabL.setAdapter(tabAdapter);

        // Handle the creation of a new tab if the user select new tab
        newTab = (Button) findViewById(R.id.bNewTab);
        newTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Launch main Activity with empty tab
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                i.removeExtra("tabNameInt");
                startActivity(i);
            }
        });

        //Handle the opening of a saved tab if the user select
        tabL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //adapterView l'AdapterView qui contient la vue sur laquelle le clic a été effectué, view qui est la vue en elle-même, position qui est la position de la vue dans la liste et enfin id qui est l'identifiant de la vue.
                //Launch mainActivity with loaded tab
                if (l==1)
                {
                    Log.d("BrowsingTabs","Item has been clicked");
                    Intent i2 = new Intent(getApplicationContext(),MainActivity.class);
                    i2.putExtra("tabNameInt", adapterView.getItemAtPosition(i).toString());
                    startActivity(i2);
                }
                else
                {
                    Log.d("BrowsingTabs","To be deleted: "+ adapterView.getItemAtPosition(i).toString());
                    deleteTab(adapterView,i);
                }

            }
        });

        //Handle the deletion of a saved tab if the user triggers it
        tabL.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                //adapterView l'AdapterView qui contient la vue sur laquelle le clic a été effectué, view qui est la vue en elle-même, position qui est la position de la vue dans la liste et enfin id qui est l'identifiant de la vue.
                Log.d("BrowsingTabs","To be deleted: "+ adapterView.getItemAtPosition(i).toString());
                deleteTab(adapterView,i);
            return true;
            }
        });
    }
    //Definition of the deletion process
    protected void deleteTab(final AdapterView adapterView,final int i){

        Log.d("BrowsingTabs","Entering the deleteTab method for : "+adapterView.getItemAtPosition(i).toString().equals("A Magnificent Tab Example"));
        if (adapterView.getItemAtPosition(i).toString().equals("A Magnificent Tab Example"))
        {
            //The user tries to delete the tab example
            Log.d("BrowsingTabs", "The user tries to delete the tab example");
            Toast.makeText(this, "Create your own tabs, and this example will be hidden automatically",
                    Toast.LENGTH_LONG).show();
        }
        else {
            dialogueDelete AskDelete = new dialogueDelete();
            AskDelete.show(getFragmentManager(), "Dialog");
            AskDelete.deletionCom = new dialogueDelete.onDeletionIsConfirmedlistener() {
                @Override
                public void onDeletionIsConfirmed() {

                    Log.d("BrowsingTabs", "Deletion order has been confirmed :execution");
                    //We are back here is the deletion has been confimed by the user
                    boolean deleted = deleteFile(adapterView.getItemAtPosition(i).toString());
                    refreshTabList();
                    Log.d("BrowsingTabs", "Deletion has been confirmed and executed");
                }
            };
        }
    }

    //Definition of the function used to refresh the list whenever an item has been deleted or added.
    protected void refreshTabList() {
        //
        String[] listOfFiles = fileList();
        if (listOfFiles.length==0)
        {listOfFiles=new String[]{"A Magnificent Tab Example"};}
        myTabs = new ArrayList(Arrays.asList(listOfFiles));
        tabAdapter=new MyCustomAdapter(myTabs,this);
        tabAdapter=new MyCustomAdapter(myTabs,this);
        tabL.setAdapter(tabAdapter);

    }

    @Override
    public void onResume(){
        super.onResume();
        refreshTabList();

    }

 /*   @Override
    public void onBackPressed()
    {
        Log.d("BrowsingTabs", "button back has been pressed");
        super.onBackPressed();
    }*/


}
