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

import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.sound.sampled.AudioFormat;
import java.util.Map;

/**
 * @author JavaZOOM
 */
public class VorbisAudioFileFormat extends TAudioFileFormat {
    /**
     * Contructor.
     *
     * @param type
     * @param audioFormat
     * @param nLengthInFrames
     * @param nLengthInBytes
     */
    public VorbisAudioFileFormat(Type type, AudioFormat audioFormat, int nLengthInFrames, int nLengthInBytes, Map<String, Object> properties) {
        super(type, audioFormat, nLengthInFrames, nLengthInBytes, properties);
    }

    /**
     * Ogg Vorbis audio file format parameters.
     * Some parameters might be unavailable. So availability test is required before reading any parameter.
     *
     * <br>AudioFileFormat parameters.
     * <ul>
     * <li><b>duration</b> [Long], duration in microseconds.
     * <li><b>title</b> [String], Title of the stream.
     * <li><b>author</b> [String], Name of the artist of the stream.
     * <li><b>album</b> [String], Name of the album of the stream.
     * <li><b>date</b> [String], The date (year) of the recording or release of the stream.
     * <li><b>copyright</b> [String], Copyright message of the stream.
     * <li><b>comment</b> [String], Comment of the stream.
     * </ul>
     * <br>Ogg Vorbis parameters.
     * <ul>
     * <li><b>ogg.length.bytes</b> [Integer], length in bytes.
     * <li><b>ogg.bitrate.min.bps</b> [Integer], minimum bitrate.
     * <li><b>ogg.bitrate.nominal.bps</b> [Integer], nominal bitrate.
     * <li><b>ogg.bitrate.max.bps</b> [Integer], maximum bitrate.
     * <li><b>ogg.channels</b> [Integer], number of channels 1 : mono, 2 : stereo.
     * <li><b>ogg.frequency.hz</b> [Integer], sampling rate in hz.
     * <li><b>ogg.version</b> [Integer], version.
     * <li><b>ogg.serial</b> [Integer], serial number.
     * <li><b>ogg.comment.track</b> [String], track number.
     * <li><b>ogg.comment.genre</b> [String], genre field.
     * <li><b>ogg.comment.encodedby</b> [String], encoded by field.
     * <li><b>ogg.comment.ext</b> [String], extended comments (indexed):
     * <br>For instance :
     * <br>ogg.comment.ext.1=Something
     * <br>ogg.comment.ext.2=Another comment
     * </ul>
     */
    public Map<String, Object> properties() {
        return super.properties();
    }
}
