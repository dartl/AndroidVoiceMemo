package com.androidvoicememo;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

/**
 * Created by Dartl on 20.03.2016.
 */
public class ReferenceActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reference_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarTop_main);
        setSupportActionBar(myToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        cancelItem.setVisible(true);
        return true;
    }
}
