/*
	original author: JavaZOOM, vorbisspi@javazoom.net, http://www.javazoom.net

	vorbisspi-repackaged - a modernized and modularized version of vorbisspi by javazoom
    Copyright (C) 2024 FireDragon91245

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
    USA
 */

package org.firedragon91245.spi.vorbis.sampled.file;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.*;
import org.tritonus.share.TDebug;
import org.tritonus.share.sampled.file.TAudioFileReader;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * This class implements the AudioFileReader class and provides an
 * Ogg Vorbis file reader for use with the Java Sound Service Provider Interface.
 */
public class VorbisAudioFileReader extends TAudioFileReader {
    private static final int INITIAL_READ_LENGTH = 64000;
    private static final int MARK_LIMIT = INITIAL_READ_LENGTH + 1;
    private SyncState oggSyncState_ = null;
    private StreamState oggStreamState_ = null;
    private Page oggPage_ = null;
    private Packet oggPacket_ = null;
    private Info vorbisInfo = null;
    private Comment vorbisComment = null;
    private byte[] buffer = null;
    private int bytes = 0;
    private int index = 0;
    private InputStream oggBitStream_ = null;

    public VorbisAudioFileReader() {
        super(MARK_LIMIT, true);
    }

    /**
     * Return the AudioFileFormat from the given file.
     */
    public AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException {
        if (TDebug.TraceAudioFileReader) TDebug.out("getAudioFileFormat(File file)");
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            inputStream.mark(MARK_LIMIT);
            inputStream.reset();
            // Get Vorbis file info such as length in seconds.
            VorbisFile vf = new VorbisFile(file.getAbsolutePath());
            return getAudioFileFormat(inputStream, (int) file.length(), Math.round((vf.time_total(-1)) * 1000));
        } catch (JOrbisException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Return the AudioFileFormat from the given URL.
     */
    public AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException {
        if (TDebug.TraceAudioFileReader) TDebug.out("getAudioFileFormat(URL url)");
        try (InputStream inputStream = url.openStream()) {
            return getAudioFileFormat(inputStream);
        }
    }

    /**
     * Return the AudioFileFormat from the given InputStream.
     */
    public AudioFileFormat getAudioFileFormat(InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        if (TDebug.TraceAudioFileReader) TDebug.out("getAudioFileFormat(InputStream inputStream)");
        try {
            if (!inputStream.markSupported()) inputStream = new BufferedInputStream(inputStream);
            inputStream.mark(MARK_LIMIT);
            return getAudioFileFormat(inputStream, AudioSystem.NOT_SPECIFIED, AudioSystem.NOT_SPECIFIED);
        } finally {
            inputStream.reset();
        }
    }

    /**
     * Return the AudioFileFormat from the given InputStream and length in bytes.
     */
    public AudioFileFormat getAudioFileFormat(InputStream inputStream, long medialength) throws UnsupportedAudioFileException, IOException {
        return getAudioFileFormat(inputStream, (int) medialength, AudioSystem.NOT_SPECIFIED);
    }


    /**
     * Return the AudioFileFormat from the given InputStream, length in bytes and length in milliseconds.
     */
    protected AudioFileFormat getAudioFileFormat(InputStream bitStream, int mediaLength, int totalms) throws UnsupportedAudioFileException, IOException {
        Map<String, Object> aff_properties = new HashMap<>();
        Map<String, Object> af_properties = new HashMap<>();

        if (totalms == AudioSystem.NOT_SPECIFIED) {
            totalms = 0;
        }
        if (totalms > 0) {
            aff_properties.put("duration", totalms * 1000);
        }
        oggBitStream_ = bitStream;
        init_jorbis();
        index = 0;
        try {
            readHeaders(aff_properties);
        } catch (IOException ioe) {
            if (TDebug.TraceAudioFileReader) {
                TDebug.out(ioe.getMessage());
            }
            throw new UnsupportedAudioFileException(ioe.getMessage());
        }

        String dmp = vorbisInfo.toString();
        if (TDebug.TraceAudioFileReader) {
            TDebug.out(dmp);
        }
        int ind = dmp.lastIndexOf("bitrate:");
        int minbitrate = -1;
        int nominalbitrate = -1;
        int maxbitrate = -1;
        if (ind != -1) {
            dmp = dmp.substring(ind + 8);
            StringTokenizer st = new StringTokenizer(dmp, ",");
            if (st.hasMoreTokens()) {
                minbitrate = Integer.parseInt(st.nextToken());
            }
            if (st.hasMoreTokens()) {
                nominalbitrate = Integer.parseInt(st.nextToken());
            }
            if (st.hasMoreTokens()) {
                maxbitrate = Integer.parseInt(st.nextToken());
            }
        }
        if (nominalbitrate > 0) af_properties.put("bitrate", nominalbitrate);
        af_properties.put("vbr", true);

        if (minbitrate > 0) aff_properties.put("ogg.bitrate.min.bps", minbitrate);
        if (maxbitrate > 0) aff_properties.put("ogg.bitrate.max.bps", maxbitrate);
        if (nominalbitrate > 0) aff_properties.put("ogg.bitrate.nominal.bps", nominalbitrate);
        if (vorbisInfo.channels > 0) aff_properties.put("ogg.channels", vorbisInfo.channels);
        if (vorbisInfo.rate > 0) aff_properties.put("ogg.frequency.hz", vorbisInfo.rate);
        if (mediaLength > 0) aff_properties.put("ogg.length.bytes", mediaLength);
        aff_properties.put("ogg.version", vorbisInfo.version);

        //AudioFormat.Encoding encoding = VorbisEncoding.VORBISENC;
        //AudioFormat format = new VorbisAudioFormat(encoding, vorbisInfo.rate, AudioSystem.NOT_SPECIFIED, vorbisInfo.channels, AudioSystem.NOT_SPECIFIED, AudioSystem.NOT_SPECIFIED, true,af_properties);

        // Patch from MS to ensure more SPI compatibility ...
        float frameRate = -1;
        if (nominalbitrate > 0) frameRate = (float) nominalbitrate / 8;
        else if (minbitrate > 0) frameRate = (float) minbitrate / 8;

        AudioFormat.Encoding encoding = VorbisEncoding.VORBISENC;
        // New Patch from MS:
        AudioFormat format = new VorbisAudioFormat(encoding, vorbisInfo.rate, AudioSystem.NOT_SPECIFIED, vorbisInfo.channels, 1, frameRate, false, af_properties);
        // Patch end

        return new VorbisAudioFileFormat(VorbisFileFormatType.OGG, format, AudioSystem.NOT_SPECIFIED, mediaLength, aff_properties);
    }

    /**
     * Return the AudioInputStream from the given InputStream.
     */
    public AudioInputStream getAudioInputStream(InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        if (TDebug.TraceAudioFileReader) TDebug.out("getAudioInputStream(InputStream inputStream)");
        return getAudioInputStream(inputStream, AudioSystem.NOT_SPECIFIED, AudioSystem.NOT_SPECIFIED);
    }

    /**
     * Return the AudioInputStream from the given InputStream.
     */
    public AudioInputStream getAudioInputStream(InputStream inputStream, int medialength, int totalms) throws UnsupportedAudioFileException, IOException {
        if (TDebug.TraceAudioFileReader)
            TDebug.out("getAudioInputStream(InputStream inputStreamint medialength, int totalms)");
        try {
            if (!inputStream.markSupported()) inputStream = new BufferedInputStream(inputStream);
            inputStream.mark(MARK_LIMIT);
            AudioFileFormat audioFileFormat = getAudioFileFormat(inputStream, medialength, totalms);
            inputStream.reset();
            return new AudioInputStream(inputStream, audioFileFormat.getFormat(), audioFileFormat.getFrameLength());
        } catch (UnsupportedAudioFileException | IOException e) {
            inputStream.reset();
            throw e;
        }
    }

    /**
     * Return the AudioInputStream from the given File.
     */
    public AudioInputStream getAudioInputStream(File file) throws UnsupportedAudioFileException, IOException {
        if (TDebug.TraceAudioFileReader) TDebug.out("getAudioInputStream(File file)");
        InputStream inputStream = new FileInputStream(file);
        try {
            return getAudioInputStream(inputStream);
        } catch (UnsupportedAudioFileException | IOException e) {
            inputStream.close();
            throw e;
        }
    }

    /**
     * Return the AudioInputStream from the given URL.
     */
    public AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException {
        if (TDebug.TraceAudioFileReader) TDebug.out("getAudioInputStream(URL url)");
        InputStream inputStream = url.openStream();
        try {
            return getAudioInputStream(inputStream);
        } catch (UnsupportedAudioFileException | IOException e) {
            if (inputStream != null) inputStream.close();
            throw e;
        }
    }

    /**
     * Reads headers and comments.
     */
    private void readHeaders(Map<String, Object> aff_properties) throws IOException {
        if (TDebug.TraceAudioConverter) TDebug.out("readHeaders(");

        int bufferMultiple_ = 4;
        int bufferSize_ = bufferMultiple_ * 256 * 2;
        index = oggSyncState_.buffer(bufferSize_);
        buffer = oggSyncState_.data;
        bytes = readFromStream(buffer, index);
        if (bytes == -1) {
            if (TDebug.TraceAudioConverter) TDebug.out("Cannot get any data from selected Ogg bitstream.");
            throw new IOException("Cannot get any data from selected Ogg bitstream.");
        }
        oggSyncState_.wrote(bytes);
        if (oggSyncState_.pageout(oggPage_) != 1) {
            if (bytes < bufferSize_) {
                throw new IOException("EOF");
            }
            if (TDebug.TraceAudioConverter) TDebug.out("Input does not appear to be an Ogg bitstream.");
            throw new IOException("Input does not appear to be an Ogg bitstream.");
        }
        oggStreamState_.init(oggPage_.serialno());
        vorbisInfo.init();
        vorbisComment.init();
        aff_properties.put("ogg.serial", oggPage_.serialno());
        if (oggStreamState_.pagein(oggPage_) < 0) {
            // error; stream version mismatch perhaps
            if (TDebug.TraceAudioConverter) TDebug.out("Error reading first page of Ogg bitstream data.");
            throw new IOException("Error reading first page of Ogg bitstream data.");
        }
        if (oggStreamState_.packetout(oggPacket_) != 1) {
            // no page? must not be vorbis
            if (TDebug.TraceAudioConverter) TDebug.out("Error reading initial header packet.");
            throw new IOException("Error reading initial header packet.");
        }
        if (vorbisInfo.synthesis_headerin(vorbisComment, oggPacket_) < 0) {
            // error case; not a vorbis header
            if (TDebug.TraceAudioConverter) TDebug.out("This Ogg bitstream does not contain Vorbis audio data.");
            throw new IOException("This Ogg bitstream does not contain Vorbis audio data.");
        }
        int i = 0;
        while (i < 2) {
            while (i < 2) {
                int result = oggSyncState_.pageout(oggPage_);
                if (result == 0) {
                    break;
                } // Need more data
                if (result == 1) {
                    oggStreamState_.pagein(oggPage_);
                    while (i < 2) {
                        result = oggStreamState_.packetout(oggPacket_);
                        if (result == 0) {
                            break;
                        }
                        if (result == -1) {
                            if (TDebug.TraceAudioConverter) TDebug.out("Corrupt secondary header.  Exiting.");
                            throw new IOException("Corrupt secondary header.  Exiting.");
                        }
                        vorbisInfo.synthesis_headerin(vorbisComment, oggPacket_);
                        i++;
                    }
                }
            }
            index = oggSyncState_.buffer(bufferSize_);
            buffer = oggSyncState_.data;
            bytes = readFromStream(buffer, index);
            if (bytes == -1) {
                break;
            }
            if (bytes == 0 && i < 2) {
                if (TDebug.TraceAudioConverter) TDebug.out("End of file before finding all Vorbis headers!");
                throw new IOException("End of file before finding all Vorbis  headers!");
            }
            oggSyncState_.wrote(bytes);
        }
        // Read Ogg Vorbis comments.
        byte[][] ptr = vorbisComment.user_comments;
        String currComment;
        int c = 0;
        for (byte[] value : ptr) {
            if (value == null) {
                break;
            }
            currComment = (new String(value, 0, value.length - 1, StandardCharsets.UTF_8)).trim();
            if (TDebug.TraceAudioConverter) TDebug.out(currComment);
            if (currComment.toLowerCase().startsWith("artist")) {
                aff_properties.put("author", currComment.substring(7));
            } else if (currComment.toLowerCase().startsWith("title")) {
                aff_properties.put("title", currComment.substring(6));
            } else if (currComment.toLowerCase().startsWith("album")) {
                aff_properties.put("album", currComment.substring(6));
            } else if (currComment.toLowerCase().startsWith("date")) {
                aff_properties.put("date", currComment.substring(5));
            } else if (currComment.toLowerCase().startsWith("copyright")) {
                aff_properties.put("copyright", currComment.substring(10));
            } else if (currComment.toLowerCase().startsWith("comment")) {
                aff_properties.put("comment", currComment.substring(8));
            } else if (currComment.toLowerCase().startsWith("genre")) {
                aff_properties.put("ogg.comment.genre", currComment.substring(6));
            } else if (currComment.toLowerCase().startsWith("tracknumber")) {
                aff_properties.put("ogg.comment.track", currComment.substring(12));
            } else {
                c++;
                aff_properties.put("ogg.comment.ext." + c, currComment);
            }
            aff_properties.put("ogg.comment.encodedby", new String(vorbisComment.vendor, 0, vorbisComment.vendor.length - 1));
        }
    }

    /**
     * Reads from the oggBitStream_ a specified number of Bytes(bufferSize_) worth
     * starting at index and puts them in the specified buffer[].
     *
     * @return the number of bytes read or -1 if error.
     */
    private int readFromStream(byte[] buffer, int index) {
        int bytes;
        try {
            bytes = oggBitStream_.read(buffer, index, 2048);
        } catch (Exception e) {
            if (TDebug.TraceAudioFileReader) {
                TDebug.out("Cannot Read Selected Song");
            }
            bytes = -1;
        }
        return bytes;
    }

    /**
     * Initializes all the jOrbis and jOgg vars that are used for song playback.
     */
    private void init_jorbis() {
        oggSyncState_ = new SyncState();
        oggStreamState_ = new StreamState();
        oggPage_ = new Page();
        oggPacket_ = new Packet();
        vorbisInfo = new Info();
        vorbisComment = new Comment();
        DspState vorbisDspState = new DspState();
        new Block(vorbisDspState);
        buffer = null;
        bytes = 0;
        oggSyncState_.init();
    }
}
