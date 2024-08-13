package org.example;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class CheckWebSait implements Runnable {
    private List<String> refList;

    public CheckWebSait(List<String> refList) {
        this.refList = refList;
    }

    @Override
    public void run() {
        for (int i = 0; i < refList.size(); i++) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URI(refList.get(i)));







                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
