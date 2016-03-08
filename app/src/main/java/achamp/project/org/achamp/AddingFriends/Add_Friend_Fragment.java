package achamp.project.org.achamp.AddingFriends;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import achamp.project.org.achamp.AchampFriend;
import achamp.project.org.achamp.BuildConfig;
import achamp.project.org.achamp.MainActivity;
import achamp.project.org.achamp.R;

import static achamp.project.org.achamp.R.color.Green;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Add_Friend_Fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Add_Friend_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Add_Friend_Fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private FriendListAdapter adapter;
    private ExpandableListView lv;
    private Handler handler;
    private Handler UIHandler;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Add_Friend_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Add_Friend_Fragment newInstance(String param1, String param2) {
        Add_Friend_Fragment fragment = new Add_Friend_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public Add_Friend_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add__friend_, container, false);
        lv = (ExpandableListView)view.findViewById(R.id.add_friend_list);

        setUpExpandList();
        UIHandler = new Handler();
        handler = new Handler(mListener.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final ArrayList<AchampFriend> achFriend = (ArrayList) get_friend_list("/getfriends", null);
                UIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((ArrayList<AchampFriend>)adapter.getDataHashMap().get("Friends")).addAll(achFriend);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        handler.post(new Runnable() {
            @Override
            public void run() {
                final ArrayList<AchampFriend> achFriend = (ArrayList) get_friend_list("/getallusers", null);
                UIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((ArrayList<AchampFriend>)adapter.getDataHashMap().get("Users")).addAll(achFriend);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        return view;

    }

    private void setUpExpandList()
    {
        ArrayList<AchampFriend> achFriendList = new ArrayList<AchampFriend>();
        ArrayList<AchampFriend> achUserList = new ArrayList<AchampFriend>();
        ArrayList<String> headerData = new ArrayList<String>();
        headerData.add("Friends");
        headerData.add("Users");
        HashMap<String, List<AchampFriend>> hmData = new HashMap<String, List<AchampFriend>>();
        hmData.put(headerData.get(0), achFriendList);
        hmData.put(headerData.get(1), achUserList);
        adapter = new FriendListAdapter(getActivity(),hmData, headerData);

        lv.setDivider(null);
        lv.setAdapter(adapter);


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class FriendListAdapter extends BaseExpandableListAdapter{
        Context con;
        HashMap<String, List<AchampFriend>> dataHashMap;
        List<String> headerList;
        public FriendListAdapter(Context con, HashMap<String, List<AchampFriend>> dataHashMap,
                                 List<String> headerList) {
            this.con = con;
            this.dataHashMap = dataHashMap;
            this.headerList = headerList;
        }

        public Map getDataHashMap()
        {
            return dataHashMap;
        }

        @Override
        public int getGroupCount() {
            return headerList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return dataHashMap.get(headerList.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return dataHashMap.get(headerList.get(groupPosition));
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return dataHashMap.get(headerList.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.group_list, null);
            }
            TextView groupName = (TextView) convertView.findViewById(R.id.grouplist_name);
            groupName.setText("  "+headerList.get(groupPosition));

            if(isExpanded){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    groupName.setBackground(getResources().getDrawable(R.drawable.list_button_gray, null));
                }
                else{
                    groupName.setBackground(getResources().getDrawable(R.drawable.list_button_gray));
                }
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    groupName.setBackground(getResources().getDrawable(R.drawable.list_button_blue, null));
                }
                else{
                    groupName.setBackground(getResources().getDrawable(R.drawable.list_button_blue));
                }
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.friend_list, null);
            }
            TextView groupName = (TextView) convertView.findViewById(R.id.friendlist_name);
            AchampFriend tempAchamp = dataHashMap.get(headerList.get(groupPosition)).get(childPosition);
            groupName.setText(tempAchamp.getUserName());
            if(tempAchamp.isFriend())
            {
                ((ImageButton)convertView.findViewById(R.id.friendlist_image)).setBackgroundColor(Color.GREEN);
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(final int groupPosition, final int childPosition) {
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

// 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Choose what happens for " +
                    dataHashMap.get(headerList.get(groupPosition)).get(childPosition).getUserName() + " :");

            if(headerList.get(groupPosition).equals("Friends")) {
                builder.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                    final int gr = groupPosition;
                    final int cp = childPosition;
                    public void onClick(DialogInterface dialog, int id) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                final ArrayList<AchampFriend> achFriend =
                                        (ArrayList) get_friend_list("/deletefriend",
                                                dataHashMap.get(headerList.get(gr)).get(cp).getUserName());
                                UIHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((ArrayList<AchampFriend>)adapter.getDataHashMap().get("Friends")).addAll(achFriend);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });

                    }
                });
            }
            else {
                builder.setNeutralButton("Add", new DialogInterface.OnClickListener() {
                        final int gr = groupPosition;
                        final int cp = childPosition;
                        public void onClick(DialogInterface dialog, int id) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    final ArrayList<AchampFriend> achFriend =
                                            (ArrayList) get_friend_list("/addfriends",
                                                    dataHashMap.get(headerList.get(gr)).get(cp).getUserName());
                                    UIHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            });

                        }
                });
            }

// 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
    }
//    private class FriendListAdapter extends ArrayAdapter<AchampFriend> {
//        private Context con;
//        private ArrayList<AchampFriend> friendsArray;
//
//        public FriendListAdapter(Context context, ArrayList<AchampFriend> values) {
//            super(context, R.layout.friend_list, values);
//            con = context;
//            friendsArray = values;
//
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            LayoutInflater inf = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//            View view = (View) inf.inflate(R.layout.friend_list, parent, false);
//            view.setTag(position);
//            Log.d("FindErr", "the friend userId is = " + friendsArray.get(position).getUserName());
//            TextView friendUser = (TextView) view.findViewById(R.id.friendlist_name);
//            friendUser.setText(friendsArray.get(position).getUserName());
//            return view;
//        }
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
        public Looper getLooper();
    }



    public List<AchampFriend> get_friend_list(String urlExtra,@Nullable String seceondUser) {

        InputStream is = null;
        ArrayList<AchampFriend> tempFArray = new ArrayList<AchampFriend>();
        try {
            HttpURLConnection http = (HttpURLConnection) (new URL(MainActivity.myurl + urlExtra).openConnection());
            http.setConnectTimeout(2000);
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Accept", "application/json");
            http.setRequestMethod("POST");
            http.connect();

            JSONObject jsObj = new JSONObject();

            SharedPreferences prefs = getActivity().getSharedPreferences("usersession", Activity.MODE_PRIVATE);
            String username = prefs.getString("username", "");
            String password = prefs.getString("password", "");
            jsObj.put("username", username);
            jsObj.put("password", password);
            if(seceondUser != null) {
                jsObj.put("seconduser", seceondUser);
            }

            OutputStream os = http.getOutputStream();
            Writer wr = new OutputStreamWriter(os);

            wr.write(jsObj.toString());
            wr.flush();
            wr.close();

            is = http.getResponseCode() >= 400 ? http.getErrorStream() : http.getInputStream();

            JsonReader jr = new JsonReader(new InputStreamReader(is, "UTF-8"));

            jr.beginArray();
            while (jr.hasNext()) {
                jr.beginObject();
                while(jr.hasNext()) {
                    if (urlExtra.equals("/getfriends") || urlExtra.equals("/addfriends") || urlExtra.equals("/deletefriends")) {
                        Log.d("TESTERR", "jr = " + jr.nextName() + " " + jr.nextString());
                    }
                    if (jr.nextName().equals("username")) {
                        tempFArray.add(new AchampFriend("", jr.nextString(), true));
                    }
                }
                jr.endObject();
            }
            jr.endArray();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException ioE)
                {
                    ioE.printStackTrace();
                }

            }
        }
        return tempFArray;
    }
}
