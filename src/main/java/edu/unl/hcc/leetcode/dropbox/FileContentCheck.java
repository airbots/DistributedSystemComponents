package edu.unl.hcc.leetcode.dropbox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  /foo/1.txt
 /foo/2.txt
 /foo/bar/1.txt
 /foo/foo/3.txt -> same content, byte to byte, as 1.txt

 def group_files(root_path: str):
 return List<List<String>>

 group_files("/foo")
 returns
 [["/foo/1.txt", "/foo/bar/1.txt", "foo/bar/3/txt"], ["/foo/2.txt"]]

 long helper_get_size(path);
 */
public class FileContentCheck {

    long helper_get_size(String path){
         return 0;
    }

    List<List<String>> result = new ArrayList();
    public List<List<String>> groupFiles(String path) throws IOException {

        //boundary check now shown
        helper(path, result);
        return result;
    }



    private void helper(String path, List<List<String>> result) throws IOException {
    /*为了编译通过而做的步骤先comment
        //if path is a dir get all its files and check whether they are same or not, if not, insert to result;
        File file = new File(path);
        for(File f: file.getFiles()) {
            if(f.isDir()) {
                helper(f.toString(),result);
            } else {
                if(result.size()==0) {
                    ArrayList<String> tmpList = new ArrayList();
                    result.add(tmpList.add(f.toString));
                }
                boolean flag=false;
                for(List<String> l: result) {
                    if(isSameFile(f.toString(),l.getFirst())) {
                        //add file as string to this list
                        l.add(f.toString());
                        flag=true;
                        break;
                    }
                }
                if(!flag){
                    ArrayList<String> tmpList = new ArrayList();
                    result.add(tmpList.add(f.toString());
                }
            }
        }
        */
    }

    private boolean isSameFile(String path1, String path2) throws Exception {
      /*
        if(helper_get_size(path1)!=helper_get_size(path2)) return false;
        try{
            FileInputStream fis1 = new FileInputStream(new File(path1));
            FileInputStream fis2 = new FileInputStream(new File(path2));
            String line = fis1.readLine();
            while(line!=null){
                if(line.equals(fis2.readLine())) return false;
                line=fis1.readLine();
            }
            return true;
        } finally{
            fis1.close();
            fis2.close();
        }
        */
      return false;
    }


    static class Solution {
        public static void main(String[] args) {
            ArrayList<String> strings = new ArrayList<String>();
            strings.add("Hello, World!");
            strings.add("Welcome to CoderPad.");
            strings.add("This pad is running Java 8.");

            for (String string : strings) {
                System.out.println(string);
            }
        }
    }

}
