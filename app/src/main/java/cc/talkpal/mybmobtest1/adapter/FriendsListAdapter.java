package cc.talkpal.mybmobtest1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cc.talkpal.mybmobtest1.R;
import cc.talkpal.mybmobtest1.bean.Relation;

/**
 * Created by admin on 2016/4/25.
 */
public class FriendsListAdapter extends BaseAdapter{
    private List<Relation> mData;
    private Context mContext;

    public FriendsListAdapter(Context context, List<Relation> mData) {
        mContext = context;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Relation item = (Relation) getItem(position);
        ViewHolder holder;
        if(convertView ==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_friend,parent,
                    false);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(item.getUsername());
        return convertView;
    }

    class ViewHolder{
        TextView tv_name;
    }
}
