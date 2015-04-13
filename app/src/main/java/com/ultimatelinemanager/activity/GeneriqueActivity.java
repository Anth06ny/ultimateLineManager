package com.ultimatelinemanager.activity;

import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.formation.utils.LogUtils;
import com.formation.utils.ToastUtils;
import com.ultimatelinemanager.metier.exception.ExceptionA;

/**
 * Created by Anthony on 11/04/2015.
 */
public class GeneriqueActivity extends ActionBarActivity {

    protected void showError(ExceptionA exceptionA, boolean crashLytics) {
        LogUtils.logException(getClass(), exceptionA, true);
        ToastUtils.showToastOnUIThread(this, exceptionA.getMessageUtilisateur(), Toast.LENGTH_LONG);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        //On surcharge la flech de retour pour eviter de perdre les objet en intent
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
