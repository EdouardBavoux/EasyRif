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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BrowsingActivity extends Activity {

    public ListView tabL;
    public Button newTab;
    public ArrayAdapter<String> tabAdapter;
    public List myTabs ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing);

        //Display the list of tabs that have been stored / saved previously
        tabL=(ListView) findViewById(R.id.tablist);
        myTabs = new ArrayList(Arrays.asList(fileList()));
        Log.d("BrowsingTabs","My files: "+ myTabs.toString());
        tabAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,myTabs);
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
                Intent i2 = new Intent(getApplicationContext(),MainActivity.class);
                i2.putExtra("tabNameInt", adapterView.getItemAtPosition(i).toString());
                startActivity(i2);
            }
        });

        //Handle the deletion of a saved tab if the user triggers it
        tabL.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                //adapterView l'AdapterView qui contient la vue sur laquelle le clic a été effectué, view qui est la vue en elle-même, position qui est la position de la vue dans la liste et enfin id qui est l'identifiant de la vue.
                Log.d("BrowsingTabs","To be deleted: "+ adapterView.getItemAtPosition(i).toString());
                dialogueDelete AskDelete = new dialogueDelete();
                AskDelete.show(getFragmentManager(),"Dialog");
                AskDelete.deletionCom = new dialogueDelete.onDeletionIsConfirmedlistener() {
                    @Override
                    public void onDeletionIsConfirmed() {
                        //We are back here is the deletion has been confimed by the user
                        boolean deleted = deleteFile(adapterView.getItemAtPosition(i).toString());
                        refreshTabList();
                        Log.d("BrowsingTabs","Deletion has been confirmed and executed");
                    }
                };

            return true;
            }
        });
    }

    //Definition of the function used to refresh the list whenever an item has been deleted or added.
    protected void refreshTabList() {
        //
        myTabs = new ArrayList(Arrays.asList(fileList()));
        tabAdapter.clear();
        tabAdapter.addAll(myTabs);
        tabAdapter.notifyDataSetChanged();
    }
}
