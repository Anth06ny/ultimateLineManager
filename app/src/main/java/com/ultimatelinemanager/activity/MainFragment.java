package com.ultimatelinemanager.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.formation.utils.LogUtils;
import com.formation.utils.ToastUtils;
import com.ultimatelinemanager.metier.exception.ExceptionA;

/**
 * Created by amonteiro on 28/04/2015.
 */
public class MainFragment extends Fragment {

    protected GeneriqueActivity generiqueActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        generiqueActivity = (GeneriqueActivity) getActivity();
        setHasOptionsMenu(true);
        //Excepte pour TeamFragment on affiche le bouton de retour
        generiqueActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(!(this instanceof TeamFragment));

        return null;
    }

    /* ---------------------------------
    // Methodes
    // -------------------------------- */

    protected void showError(ExceptionA exceptionA, boolean crashLytics) {
        LogUtils.logException(getClass(), exceptionA, true);
        ToastUtils.showToastOnUIThread(generiqueActivity, exceptionA.getMessageUtilisateur(), Toast.LENGTH_LONG);
    }
}
