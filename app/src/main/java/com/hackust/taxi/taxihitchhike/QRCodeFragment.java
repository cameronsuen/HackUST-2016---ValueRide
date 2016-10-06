package com.hackust.taxi.taxihitchhike;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by cameron on 4/17/2016.
 */
public class QRCodeFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    public interface WebViewListener {
        public void onClick();
    }

    private WebViewListener mListener;

    public QRCodeFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static QRCodeFragment newInstance(String param1, String param2) {
        QRCodeFragment fragment = new QRCodeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_confirm, container, false);
        WebView view = (WebView) v.findViewById(R.id.webView);
        view.loadData("<html><head><style type='text/css'>body{margin:auto auto;text-align:center;} img{width:50%25;} </style></head><body><img src='http://10.89.197.135/taxi/qrcode.php'/></body></html>" ,"text/html",  "UTF-8");
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WebViewListener) {
            mListener = (WebViewListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
