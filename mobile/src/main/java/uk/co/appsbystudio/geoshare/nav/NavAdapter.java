package uk.co.appsbystudio.geoshare.nav;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import uk.co.appsbystudio.geoshare.R;

public class NavAdapter extends ArrayAdapter<NavItem> {

    private Context context;
    private Integer itemLayout;
    private List<NavItem> items;

    public NavAdapter(Context context, int resource, List<NavItem> objects) {
        super(context, resource, objects);

        this.context = context;
        this.itemLayout = resource;
        this.items = objects;
    }

    @Override
    public View getView(int position, View existingView, ViewGroup parent) {
        ItemHolder itemHolder;
        if(existingView == null) {
            existingView = ((AppCompatActivity)context).getLayoutInflater().inflate(itemLayout, parent, false);
            itemHolder = new ItemHolder();
            itemHolder.name = (TextView)existingView.findViewById(R.id.item_name);
            itemHolder.icon = (ImageView)existingView.findViewById(R.id.item_image);
            existingView.setTag(itemHolder);
        } else {
            itemHolder = (ItemHolder)existingView.getTag();
        }

        NavItem item = items.get(position);
        itemHolder.name.setText(context.getResources().getString(item.nameID));
        itemHolder.icon.setImageDrawable(context.getResources().getDrawable(item.imageID));

        return existingView;
    }

    private static class ItemHolder {
        TextView name;
        ImageView icon;
    }
}