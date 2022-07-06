package com.android.brogrammers.sportsm8.userClasses;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.brogrammers.sportsm8.R;

/**
 * Created by agemcipe on 30.11.16.
 */

public class EditUserInformation extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_edit_user_information, null));
        return builder.create();
    }

}