open module vorbisspi.repackaged.main {
    requires java.desktop;
    requires tritonus.share;
    requires jorbis;

    exports org.firedragon91245.spi.vorbis.sampled.convert;
    exports org.firedragon91245.spi.vorbis.sampled.file;

    provides javax.sound.sampled.spi.AudioFileReader with org.firedragon91245.spi.vorbis.sampled.file.VorbisAudioFileReader;
    provides javax.sound.sampled.spi.FormatConversionProvider with org.firedragon91245.spi.vorbis.sampled.convert.VorbisFormatConversionProvider;
}