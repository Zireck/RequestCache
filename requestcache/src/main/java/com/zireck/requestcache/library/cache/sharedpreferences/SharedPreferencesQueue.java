package com.zireck.requestcache.library.cache.sharedpreferences;

import android.content.SharedPreferences;
import com.google.gson.reflect.TypeToken;
import com.zireck.requestcache.library.cache.RequestQueue;
import com.zireck.requestcache.library.model.RequestModel;
import com.zireck.requestcache.library.util.serializer.JsonSerializer;
import com.zireck.requestcache.library.util.logger.Logger;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SharedPreferencesQueue implements RequestQueue {

  private static final String KEY_PENDING_REQUEST_QUEUE = "PENDING_REQUEST_QUEUE";

  private final SharedPreferences sharedPreferences;
  private final Logger logger;
  private final JsonSerializer jsonSerializer;
  private List<RequestModel> pendingRequestQueue =
      Collections.synchronizedList(new ArrayList<RequestModel>());
  private Iterator<RequestModel> pendingRequestQueueIterator;

  public SharedPreferencesQueue(SharedPreferences sharedPreferences, Logger logger,
      JsonSerializer jsonSerializer) {
    this.sharedPreferences = sharedPreferences;
    this.logger = logger;
    this.jsonSerializer = jsonSerializer;
  }

  @Override public boolean isEmpty() {
    return pendingRequestQueue.isEmpty();
  }

  @Override public void add(RequestModel requestModel) {
    pendingRequestQueue.add(requestModel);
  }

  @Override public void add(List<RequestModel> requestModels) {
    pendingRequestQueue.addAll(requestModels);
  }

  @Override public void loadToMemory() {
    String pendingRequestQueueString = sharedPreferences.getString(KEY_PENDING_REQUEST_QUEUE, "");
    if (pendingRequestQueueString.length() <= 0) {
      pendingRequestQueue = Collections.synchronizedList(new ArrayList<RequestModel>());
      pendingRequestQueueIterator = pendingRequestQueue.iterator();
    } else {
      Type pendingRequestQueueType = new TypeToken<List<RequestModel>>() {}.getType();
      pendingRequestQueue = Collections.synchronizedList(
          (List<RequestModel>) jsonSerializer.fromJson(pendingRequestQueueString,
              pendingRequestQueueType));
      pendingRequestQueueIterator = pendingRequestQueue.iterator();
    }
  }

  @Override public void persistToDisk() {
    String pendingRequestQueueString = jsonSerializer.toJson(pendingRequestQueue);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(KEY_PENDING_REQUEST_QUEUE, pendingRequestQueueString);

    editor.apply();
  }

  @Override public boolean hasNext() {
    if (pendingRequestQueue == null || pendingRequestQueue.isEmpty()) {
      return false;
    }

    if (pendingRequestQueueIterator == null) {
      pendingRequestQueueIterator = pendingRequestQueue.iterator();
    }

    return pendingRequestQueueIterator.hasNext();
  }

  @Override public RequestModel next() {
    return pendingRequestQueueIterator == null ? null : pendingRequestQueueIterator.next();
  }

  @Override public void remove() {
    if (pendingRequestQueueIterator == null) {
      logger.e("Cannot delete the current element when the iterator is null.");
      return;
    }

    pendingRequestQueueIterator.remove();
  }

  @Override public boolean clear() {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(KEY_PENDING_REQUEST_QUEUE, "");

    return editor.commit();
  }
}
