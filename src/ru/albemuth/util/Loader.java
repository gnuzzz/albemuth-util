package ru.albemuth.util;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

public abstract class Loader {

    protected abstract void loadNextString(String s) throws LoadException;

    public void load(String filePath) throws LoadException {
        load(filePath, "Cp1251");
    }

    public void load(String filePath, String encoding) throws LoadException {
        load(filePath, encoding, false);
    }

    public void load(String filePath, boolean asResource) throws LoadException {
        load(filePath, "Cp1251", asResource);
    }

    public void load(String filePath, String encoding, boolean asResource) throws LoadException {
        BufferedReader in = null;
        try {
            if (asResource) {
                InputStream resourceStream = Loader.class.getResourceAsStream(filePath);
                if (resourceStream == null) {
                    throw new FileNotFoundException("Can't found resource file " + filePath);
                }
                in = new BufferedReader(new InputStreamReader(resourceStream, encoding));
            } else {
                in = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), encoding));
            }
            for (String s = in.readLine(); s != null; s = in.readLine()) {
                loadNextString(s);
            }
        } catch (FileNotFoundException e) {
            throw new LoadException("Can't load file " + filePath + ": file not found", e);
        } catch (IOException e) {
            throw new LoadException("Can't load file " + filePath + ": IOException while reading: " + e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new LoadException("Can't load file " + filePath + ": can't close input reader", e);
                }
            }
        }
    }

    public static class SimpleLoader extends Loader {

        private List<String> data = new ArrayList<String>();

        protected void loadNextString(String s) throws LoadException {
            data.add(s);
        }

        public List<String> getData() {
            return data;
        }

    }

}
