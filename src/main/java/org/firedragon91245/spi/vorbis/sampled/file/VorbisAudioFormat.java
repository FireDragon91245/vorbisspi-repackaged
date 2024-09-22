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

import org.tritonus.share.sampled.TAudioFormat;

import javax.sound.sampled.AudioFormat;
import java.util.Map;

/**
 * @author JavaZOOM
 */
public class VorbisAudioFormat extends TAudioFormat {
    /**
     * Constructor.
     *
     * @param encoding
     * @param nFrequency
     * @param SampleSizeInBits
     * @param nChannels
     * @param FrameSize
     * @param FrameRate
     * @param isBigEndian
     * @param properties
     */
    public VorbisAudioFormat(AudioFormat.Encoding encoding, float nFrequency, int SampleSizeInBits, int nChannels, int FrameSize, float FrameRate, boolean isBigEndian, Map<String, Object> properties) {
        super(encoding, nFrequency, SampleSizeInBits, nChannels, FrameSize, FrameRate, isBigEndian, properties);
    }

    /**
     * Ogg Vorbis audio format parameters.
     * Some parameters might be unavailable. So availability test is required before reading any parameter.
     *
     * <br>AudioFormat parameters.
     * <ul>
     * <li><b>bitrate</b> [Integer], bitrate in bits per seconds, average bitrate for VBR enabled stream.
     * <li><b>vbr</b> [Boolean], VBR flag.
     * </ul>
     */
    public Map properties() {
        return super.properties();
    }
}	
