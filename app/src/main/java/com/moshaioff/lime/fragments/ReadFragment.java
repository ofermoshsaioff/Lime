package com.moshaioff.lime.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moshaioff.lime.Const;
import com.yoavram.lime.R;

/**
 * Created by ofer on 9/17/14.
 */
public class ReadFragment extends Fragment {

    // views
    TextView textView;

    String data;

    public static ReadFragment newInstance(String data) {
        ReadFragment fragment = new ReadFragment();

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
        View root = inflater.inflate(R.layout.read_fragment, null);
        textView = (TextView) root.findViewById(R.id.read_message);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        textView.setText(data);
    }

    public void setText(String result) {
        data = result;
        textView.setText(data);
    }
}
