package com.study.context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class UrlResource implements Resource {

    private URL url;

    public UrlResource(String url) throws IOException {
        this.url = new URL(url);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return url.openStream();
    }

    @Override
    public boolean exists() {
        return url != null;
    }

    @Override
    public boolean isReadable() {
        return exists();
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public File getFile() {
        return null;
    }
}
