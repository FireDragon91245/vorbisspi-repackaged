package org.firedragon91245.spi.testapp;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) {
        try(InputStream file = Main.class.getResourceAsStream("pigstep.ogg"))
        {
            AudioInputStream s = AudioSystem.getAudioInputStream(file);
            System.out.println(s.getFormat().toString());
        }
        catch (IOException | UnsupportedAudioFileException e)
        {
            e.printStackTrace();
        }
    }

}
