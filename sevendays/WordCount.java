package edu.unl.hcc.concurrency.sevendays;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by chehe on 2017/4/28.
 */
public class WordCount {
  private static final HashMap<String, Integer> counts =
      new HashMap<String, Integer>();


  public static void main(String[] args)throws Exception {
    Iterable<Page> pages = new Pages(100000, "enwiki.xml");
    for(Page page: pages){
      Iterator<String> words = new Words(page.getText());
      for(String word:words)
        countWord(word);

    }

  }
  private static void countWord(String word){
    Integer currentCount = counts.get(word);
    if(currentCount == null) counts.put(word, 1);
    else counts.put(word, currentCount+1);
  }


}
