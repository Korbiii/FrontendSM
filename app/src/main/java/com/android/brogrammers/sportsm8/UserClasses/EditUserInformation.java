package com.android.brogrammers.sportsm8.UserClasses;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
<<<<<<< HEAD:app/src/main/java/com/example/alex/helloworld/dialogs/EditUserInformation.java

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
=======
>>>>>>> 044cf5884e339459aa3498161251b165745978ba:app/src/main/java/com/android/brogrammers/sportsm8/UserClasses/EditUserInformation.java

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