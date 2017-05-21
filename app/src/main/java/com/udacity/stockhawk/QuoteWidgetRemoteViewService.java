package com.udacity.stockhawk;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by tranv on 20-May-17.
 */

public class QuoteWidgetRemoteViewService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            private Cursor data = null;

            @Override
            public void onCreate() {
                //do nothing
            }

            @Override
            public void onDataSetChanged() {
                if(data != null)
                    data.close();

                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query
                        (Contract.Quote.URI, new String[]{}, null, null, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {

            }

            @Override
            public int getCount() {
                return data == null? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {

                RemoteViews views = new RemoteViews
                        (getPackageName(), R.layout.widget_collection_item);
                if(data.moveToPosition(position)) {
                    views.setTextViewText(R.id.stock_symbol,
                            data.getString(data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)));
                    views.setTextViewText(R.id.stock_value,
                            data.getString(data.getColumnIndex(Contract.Quote.COLUMN_PRICE)));
                    float percentageChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

                    if (percentageChange > 0) {
                        views.setInt(R.id.stock_change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                    } else {
                        views.setInt(R.id.stock_change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                    }
                    DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
                    percentageFormat.setMaximumFractionDigits(2);
                    percentageFormat.setMinimumFractionDigits(2);
                    percentageFormat.setPositivePrefix("+");
                    views.setTextViewText(R.id.stock_change, percentageFormat.format(percentageChange / 100));
                }
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if(data != null && data.moveToPosition(position))
                    return data.getLong(Contract.Quote.POSITION_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
