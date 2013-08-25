package ru.albemuth.util;

import org.apache.log4j.Logger;

import java.io.*;

public class ResultsStorage {
    
    private static final Logger LOG                                             = Logger.getLogger(ResultsStorage.class);
    
    private String storageFilePath;

    public ResultsStorage(String storageFilePath) {
        this.storageFilePath = storageFilePath;
    }

    public String getStorageFilePath() {
        return storageFilePath;
    }
    
    public void store(Object result) throws IOException {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(storageFilePath));
            out.writeObject(result);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOG.error("Can't close output results stream", e);
                }
            }
        }
    }
    
    public <T> T load() throws IOException {
        File f = new File(storageFilePath);
        if (!f.exists()) {
            return null;
        }
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(storageFilePath));
            return (T)in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Can't load results from file " + storageFilePath + " class not found", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOG.error("Can't close input results stream", e);
                }
            }
        }
    }

}
