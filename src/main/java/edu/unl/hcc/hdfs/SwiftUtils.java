/**
 * Copyright (c) 2016 Oracle Corporation. All Rights Reserved. Title, ownership
 * rights, and intellectual property rights in and to this software remain with
 * Oracle Corporation. This software is protected by international copyright
 * laws and treaties, and may be protected by other law. Violation of copyright
 * laws may result in civil liability and criminal penalties.
 *
 * Created by chen.he@oracle.com.
 */

package edu.unl.hcc.hdfs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.swift.exceptions.SwiftConfigurationException;
import org.apache.hadoop.fs.swift.http.RestClientBindings;
import org.apache.hadoop.fs.swift.snative.SwiftNativeFileSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

public class SwiftUtils {

    SwiftNativeFileSystem fs = null;

    private static final int BUFFER_SIZE = 256;

    public SwiftUtils(Properties props) throws URISyntaxException, IOException {
        if ( props == null) throw new NullPointerException("Failed to find properties");
        fs = new SwiftNativeFileSystem();
        fs.initialize(new URI(props.getProperty("ocs.url")), getConfig(props));
    }

    public boolean deleteTarget(String str) throws IOException {
        try {
            fs.delete(new Path(str));
        } finally {
            fs.deleteOnExit(new Path((str)));
        }
        return true;
    }

    public boolean exists(String path) throws IOException {
        return fs.exists(new Path(path));
    }

    public boolean compareFileContent(String original, Path path) throws Exception {
        if ( fs.isDirectory(path)){
            System.out.println(path + " is a directory, please make sure given path is a file");
            return false;
        } else {
            FSDataInputStream input = null;
            try {
                input = fs.open(path, BUFFER_SIZE);
                String out= IOUtils.toString(input, "UTF-8");
                if (out.contains(original)) return true;
            } catch (Exception e) {
                throw new Exception("Exception when reading file: " + path,e);
            } finally {
                if (input != null) input.close();
            }
        }
        return false;
    }

    public void close() throws IOException {
        if ( fs != null) fs.close();
    }

    public static Configuration getConfig(Properties props) throws SwiftConfigurationException {
        Configuration config = new Configuration();
        if ( props == null) {
            return config;
        }
        config.addResource(new Path(props.getProperty("conf.dir")));

        String swiftService  = RestClientBindings.extractServiceName(props.getProperty("ocs.url"));
        if (swiftService.endsWith("/")) swiftService = swiftService.replaceAll("/+$", "");
        config.set("fs.swift.service." + swiftService + ".auth.url", props.getProperty("ocs.auth.url"));
        config.set("fs.swift.service." + swiftService + ".public", "true");
        config.set("fs.swift.service." + swiftService + ".tenant", props.getProperty("ocs.tenant"));
        config.set("fs.swift.service." + swiftService + ".username", props.getProperty("ocs.username"));
        config.set("fs.swift.service." + swiftService + ".password", props.getProperty("ocs.password"));

        return config;
    }

    public boolean contentMatches(String parentDir, String message) throws Exception{
        if ( !fs.exists(new Path(parentDir))) throw new Exception("Can not found directory " +parentDir+ "on OCS");
        for( FileStatus status:fs.listStatus(new Path(parentDir))){
            if(compareFileContent(message, status.getPath())) return true;
        }
        return false;
    }

    //For test purpose
    public String createFileReturnMessage(String dir, String filename) throws IOException {
        if (!fs.exists(new Path(dir))) {
            fs.mkdirs(new Path(dir));
        }
        String message;
        FSDataOutputStream fsdos;
        try  {
            fsdos = fs.create(new Path(dir + "/" + filename), true);
            message = System.currentTimeMillis() + ": Test file";
            fsdos.write(message.getBytes());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new IOException("Failed to write to file on OCS", ioe);
        }
        return message;
    }
}
