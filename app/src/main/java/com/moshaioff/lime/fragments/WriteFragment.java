package com.moshaioff.lime.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.moshaioff.lime.Const;
import com.moshaioff.lime.MainActivity;
import com.yoavram.lime.R;

/**
 * Created by ofer on 9/17/14.
 */
public class WriteFragment extends Fragment implements View.OnClickListener {

    // views
    private EditText editText;
    private Button button;

    // data
    String data;

    public static WriteFragment newInstance(String data) {
        WriteFragment fragment = new WriteFragment();

        Bundle args = new Bundle();
        args.putString(Const.EXTRA_DATA, data);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            data = args.getString(Const.EXTRA_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.write_fragment, null);

        editText = (EditText) root.findViewById(R.id.edit_text);
        button = (Button) root.findViewById(R.id.send_button);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editText.setText(data);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        ((MainActivity)getActivity()).writeNFC(editText.getText().toString());
    }
}
