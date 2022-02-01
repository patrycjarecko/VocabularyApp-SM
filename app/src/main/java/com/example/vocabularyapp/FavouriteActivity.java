package com.example.vocabularyapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    int done = 0;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    private VariableViewModel variableViewModel;
    HashMap<String, List<String>> expandableListDetail;
    HashMap<String, List<String>> variablesByStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        this.expandableListDetail = (HashMap<String, List<String>>) getIntent().getSerializableExtra("hashMap");
        this.variablesByStatus = (HashMap<String, List<String>>) getIntent().getSerializableExtra("varByStatus");

        expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        expandableListView = findViewById(R.id.expandableListView);
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);


    }


    private class CustomExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private List<String> expandableListTitle;
        private HashMap<String, List<String>> expandableListDetail;

        public CustomExpandableListAdapter(Context context, List<String>expandableListTitle, HashMap<String, List<String>> expandableListDetail) {
            this.context = context;
            this.expandableListDetail = expandableListDetail;
            this.expandableListTitle = expandableListTitle;
        }

        @Override
        public Object getChild(int listPosition, int expandedListPosition) {
            return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                    .get(expandedListPosition);
        }

        @Override
        public long getChildId(int listPosition, int expandedListPosition) {
            return expandedListPosition;
        }

        @Override
        public View getChildView(int listPosition, final int expandedListPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final String expandedListText = (String) getChild(listPosition, expandedListPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_item, null);
            }
            TextView expandedListTextView = convertView
                    .findViewById(R.id.expandedListItem);

            expandedListTextView.setText(expandedListText);
            if(variablesByStatus.get("Do nauczenia").contains(expandedListText)){
                expandedListTextView.setBackgroundColor(Color.parseColor("#EFB0B0"));
            }
            if(variablesByStatus.get("W trakcie nauki").contains(expandedListText)){
                expandedListTextView.setBackgroundColor(Color.parseColor("#ffedb8"));
            }
            if(variablesByStatus.get("Nauczone").contains(expandedListText)){
                expandedListTextView.setBackgroundColor(Color.parseColor("#c3edca"));
            }
            return convertView;
        }

        @Override
        public int getChildrenCount(int listPosition) {
            return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                    .size();
        }

        @Override
        public Object getGroup(int listPosition) {
            return this.expandableListTitle.get(listPosition);
        }

        @Override
        public int getGroupCount() {
            return this.expandableListTitle.size();
        }

        @Override
        public long getGroupId(int listPosition) {
            return listPosition;
        }

        @Override
        public View getGroupView(int listPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String listTitle = (String) getGroup(listPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_group, null);
            }
            TextView listTitleTextView = convertView
                    .findViewById(R.id.listTitle);
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setText(listTitle);
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int listPosition, int expandedListPosition) {
            return true;
        }
    }

}
