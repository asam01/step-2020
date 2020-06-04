/*
represents a particular key-value pair
*/

package com.google.sps.data;

public class Pair<K, V> {

  private final K key;
  private final V value;

  public Pair(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public K getKey() {
    return this.key;
  }

  public V getValue() {
    return this.value;
  }

  public String toString() {
    return this.key.toString() + ": " + this.value.toString();
  }
}