package com.zireck.requestcache.library.util;

import java.lang.reflect.Type;

public interface JsonSerializer {
  String toJson(Object object);
  Object fromJson(String string, Type type);
}
