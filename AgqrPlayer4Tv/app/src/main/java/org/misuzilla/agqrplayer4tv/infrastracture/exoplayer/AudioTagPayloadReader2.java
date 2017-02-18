/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.misuzilla.agqrplayer4tv.infrastracture.exoplayer;

import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.Collections;

/**
 * Parses audio tags from an FLV stream and extracts AAC frames.
 */
/* package */ final class AudioTagPayloadReader2 extends TagPayloadReader2 {

    // Audio format
    private static final int AUDIO_FORMAT_AAC = 10;

    // AAC PACKET TYPE
    private static final int AAC_PACKET_TYPE_SEQUENCE_HEADER = 0;
    private static final int AAC_PACKET_TYPE_AAC_RAW = 1;

    // SAMPLING RATES
    private static final int[] AUDIO_SAMPLING_RATE_TABLE = new int[] {
            5500, 11000, 22000, 44000
    };

    // State variables
    private boolean hasParsedAudioDataHeader;
    private boolean hasOutputFormat;

    public AudioTagPayloadReader2(TrackOutput output) {
        super(output);
    }

    @Override
    public void seek() {
        // Do nothing.
    }

    @Override
    protected boolean parseHeader(ParsableByteArray data) throws TagPayloadReader2.UnsupportedFormatException {
        if (!hasParsedAudioDataHeader) {
            int header = data.readUnsignedByte();
            int audioFormat = (header >> 4) & 0x0F;
            int sampleRateIndex = (header >> 2) & 0x03;
            if (sampleRateIndex < 0 || sampleRateIndex >= AUDIO_SAMPLING_RATE_TABLE.length) {
                throw new UnsupportedFormatException("Invalid sample rate index: " + sampleRateIndex);
            }
            // TODO: Add support for MP3 and PCM.
            if (audioFormat != AUDIO_FORMAT_AAC) {
                throw new UnsupportedFormatException("Audio format not supported: " + audioFormat);
            }
            hasParsedAudioDataHeader = true;
        } else {
            // Skip header if it was parsed previously.
            data.skipBytes(1);
        }
        return true;
    }

    @Override
    protected void parsePayload(ParsableByteArray data, long timeUs) {
        int packetType = data.readUnsignedByte();
        // Parse sequence header just in case it was not done before.
        if (packetType == AAC_PACKET_TYPE_SEQUENCE_HEADER && !hasOutputFormat) {
            byte[] audioSpecifiConfig = new byte[data.bytesLeft()];
            data.readBytes(audioSpecifiConfig, 0, audioSpecifiConfig.length);
            Pair<Integer, Integer> audioParams = CodecSpecificDataUtil.parseAacAudioSpecificConfig(
                    audioSpecifiConfig);
            Format format = Format.createAudioSampleFormat(null, MimeTypes.AUDIO_AAC, null,
                    Format.NO_VALUE, Format.NO_VALUE, audioParams.second, audioParams.first,
                    Collections.singletonList(audioSpecifiConfig), null, 0, null);
            output.format(format);
            hasOutputFormat = true;
        } else if (packetType == AAC_PACKET_TYPE_AAC_RAW) {
            // Sample audio AAC frames
            int bytesToWrite = data.bytesLeft();
            output.sampleData(data, bytesToWrite);
            output.sampleMetadata(timeUs, C.BUFFER_FLAG_KEY_FRAME, bytesToWrite, 0, null);
        }
    }

}