package Tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ce.activizor.AppHelper;
import com.example.ce.activizor.ClassDataBaseImage;
import com.example.ce.activizor.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by CE on 10/7/2016.
 */

public class PopUpInvite {

    ListView lvTags;
    InviteTagsAdapter adapterTags;
    String inviteIds = "('')";
    Context context;
    String LOGTAG = "PopUpInvite: ";
    ClassDataBaseImage db;


    public PopUpInvite(Context context, ClassDataBaseImage db) {

        this.context =  context;
        this.db = db;

    }


    public void setInviteIds() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);;

        final ArrayList<Map> tagList = getTags();

        adapterTags = new InviteTagsAdapter(context, tagList);

        final View popupView = inflater.inflate(R.layout.aux_popup_listview, null);
        builder.setView(popupView);

        lvTags = (ListView) popupView.findViewById(R.id.lv_aux_popup_listview);
        lvTags.setAdapter(adapterTags);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String ids = "";

                for (Map tag : tagList) {

                    System.out.println(LOGTAG + tag.get(AppHelper.DB_USER_NAME).toString() + " " + tag.get("is_tagged").toString());

                    if (tag.get("is_tagged").equals("true")) {

                        ids += "'" + tag.get(AppHelper.DB_USER_ID).toString() + "',";

                    }
                }

                inviteIds = "(" + ids.substring(0,ids.length()-1) + ")";
                System.out.println(LOGTAG + inviteIds);
                dialog.cancel();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });

        AlertDialog b = builder.create();
        b.show();

    }

    private ArrayList<Map> getTags(){

        return db.dbQueries.getFriendsByUserId(AppHelper.getUserId(context));

    }

    public String getInviteIds() {

        return this.inviteIds;

    }

    private class InviteTagsAdapter extends ArrayAdapter {

        private Context context;

        ArrayList<Map> tagList;

        public InviteTagsAdapter(Context c, ArrayList<Map> tagList) {

            super(c, 0, tagList);

            context = c;
            this.tagList = tagList;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            System.out.println("getView called");
            CheckBox cbIsTagged;
            TextView tvTagName;
            Map<String, String> currentTag = tagList.get(position);
            currentTag.put("is_tagged", "false");

            if (convertView == null) {

                convertView = LayoutInflater.from(context).inflate(R.layout.aux_checkbox_textview, parent, false);
                cbIsTagged = (CheckBox) convertView.findViewById(R.id.cb_aux_checkbox_textview);
                tvTagName = (TextView) convertView.findViewById(R.id.tv_aux_checkbox_textview);
                convertView.setTag(new RowViewHolder(tvTagName, cbIsTagged));

                cbIsTagged.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        Map<String, String> tag = (Map<String, String>)buttonView.getTag();
                        tag.put("is_tagged", Boolean.toString(isChecked));

                    }
                });

            } else {

                RowViewHolder rowHolder = (RowViewHolder) convertView.getTag();
                cbIsTagged = rowHolder.getCheckBox();
                tvTagName = rowHolder.getTextView();

            }

            cbIsTagged.setTag(currentTag);
            tvTagName.setText(currentTag.get(AppHelper.DB_USER_NAME).toString());

            return convertView;

        }

        private class RowViewHolder {

            private TextView tvActName;
            private CheckBox cbIsTagged;

            public RowViewHolder(TextView tvActName, CheckBox cbIsTagged) {

                this.tvActName = tvActName;
                this.cbIsTagged = cbIsTagged;


            }

            public CheckBox getCheckBox() {

                return this.cbIsTagged;

            }

            public TextView getTextView() {

                return this.tvActName;

            }


        }

    }

}

