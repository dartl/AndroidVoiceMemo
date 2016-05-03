package com.androidvoicememo;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

/**
 * Created by Dartl on 20.03.2016.
 */
public class ReferenceActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reference_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarTop);
        setSupportActionBar(myToolbar);
    }
}
