package org.firedragon91245.spi.testapp;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {

    public static void main(String[] args) {
        try(InputStream file = Main.class.getResourceAsStream("/pigstep.ogg"))
        {
            assert file != null;
            AudioInputStream s = AudioSystem.getAudioInputStream(file);
            System.out.println(s.getFormat().toString());
        }
        catch (IOException | UnsupportedAudioFileException e)
        {
            e.printStackTrace();
        }
    }

}
