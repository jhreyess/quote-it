package com.example.quoteit.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Build
import android.util.SizeF
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.example.quoteit.BuildConfig
import com.example.quoteit.R
import com.example.quoteit.data.network.QuotesApi
import kotlinx.coroutines.*

class QuoteItWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {

    CoroutineScope(Dispatchers.IO).launch {
        val quote = QuotesApi.retrofitService.getSingleQuote(BuildConfig.API_KEY, "es")
        val author = quote.author.name
        val content = quote.content

        withContext(Dispatchers.Main){
            val views = RemoteViews(context.packageName, R.layout.widget_qod)
            views.setTextViewText(R.id.widget_quote, content)
            views.setTextViewText(R.id.widget_author, author)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}