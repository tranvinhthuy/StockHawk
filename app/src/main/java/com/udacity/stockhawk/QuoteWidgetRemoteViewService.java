package com.udacity.stockhawk;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.data.Contract;

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
