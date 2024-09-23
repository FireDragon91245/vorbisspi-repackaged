module vorbisspi.repackaged.testapp {
    requires vorbisspi.repackaged.main;
    requires java.desktop;

    uses javax.sound.sampled.spi.AudioFileReader;
    uses javax.sound.sampled.spi.FormatConversionProvider;
}