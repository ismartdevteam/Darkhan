package ismartdev.mn.darkhanwindow.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ismartdev.mn.darkhanwindow.R;
import ismartdev.mn.darkhanwindow.text.Bold;
import ismartdev.mn.darkhanwindow.text.Regular;

public class WalkItemFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TITLE = "title";
    private static final String IMG_SRC = "img";
    private static final String DESC = "desc";

    // TODO: Rename and change types of parameters
    private String desc;
    private String title;
    private int img_rs;


    public WalkItemFragment() {

    }


    public static WalkItemFragment newInstance(int img, String title,String desc) {
        WalkItemFragment fragment = new WalkItemFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(DESC, desc);
        args.putInt(IMG_SRC, img);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            desc = getArguments().getString(DESC);
            title = getArguments().getString(TITLE);
            img_rs = getArguments().getInt(IMG_SRC);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.activity_walktrough_item, container, false);
        ImageView image= (ImageView) v.findViewById(R.id.walk_image);
        Bold titleTx= (Bold) v.findViewById(R.id.walk_title);
        Regular descTx= (Regular) v.findViewById(R.id.walk_desc);

        image.setImageResource(img_rs);
        titleTx.setText(title);
        descTx.setText(desc);
        return  v;
    }


}
