/***********************************************************************
Copyright (c) 2006-2012, Skype Limited. All rights reserved.
Redistribution and use in source and binary forms, with or without
modification, (subject to the limitations in the disclaimer below)
are permitted provided that the following conditions are met:
- Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
- Neither the name of Skype Limited, nor the names of specific
contributors, may be used to endorse or promote products derived from
this software without specific prior written permission.
NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED
BY THIS LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
CONTRIBUTORS ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
***********************************************************************/


/*****************************/
/* Silk decoder test program */
/*****************************/

#include <jni.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "SKP_Silk_SDK_API.h"
#include "SKP_Silk_SigProc_FIX.h"

#ifdef __cplusplus
extern "C"
{
#endif

/* Define codec specific settings should be moved to h file */
#define MAX_BYTES_PER_FRAME     1024
#define MAX_INPUT_FRAMES        5
#define MAX_FRAME_LENGTH        480
#define FRAME_LENGTH_MS         20
#define MAX_API_FS_KHZ          48
#define MAX_LBRR_DELAY          2

#ifdef _SYSTEM_IS_BIG_ENDIAN
/* Function to convert a little endian int16 to a */
/* big endian int16 or vica verca                 */
void swap_endian(
    SKP_int16       vec[],              /*  I/O array of */
    SKP_int         len                 /*  I   length      */
)
{
    SKP_int i;
    SKP_int16 tmp;
    SKP_uint8 *p1, *p2;

    for( i = 0; i < len; i++ ){
        tmp = vec[ i ];
        p1 = (SKP_uint8 *)&vec[ i ]; p2 = (SKP_uint8 *)&tmp;
        p1[ 0 ] = p2[ 1 ]; p1[ 1 ] = p2[ 0 ];
    }
}
#endif

int encodefile(char *fsource, char *ftarget, int samplerate, int ratebps) {
    size_t counter;
    SKP_int32 k, totPackets, totActPackets, ret;
    SKP_int16 nBytes;
    double sumBytes, sumActBytes, nrg;
    SKP_uint8 payload[MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES];
    SKP_int16 in[FRAME_LENGTH_MS * MAX_API_FS_KHZ * MAX_INPUT_FRAMES];
    char speechInFileName[150], bitOutFileName[150];
    FILE *bitOutFile, *speechInFile;
    SKP_int32 encSizeBytes;
    void *psEnc;
#ifdef _SYSTEM_IS_BIG_ENDIAN
    SKP_int16 nBytes_LE;
#endif

    /* default settings */
    SKP_int32 API_fs_Hz = 24000;
    SKP_int32 max_internal_fs_Hz = 0;
    SKP_int32 targetRate_bps;
    SKP_int32 smplsSinceLastPacket, packetSize_ms = 20;
    SKP_int32 frameSizeReadFromFile_ms = 20;
    SKP_int32 packetLoss_perc = 0;
#if LOW_COMPLEXITY_ONLY
    SKP_int32 complexity_mode = 0;
#else
    SKP_int32 complexity_mode = 2;
#endif
    SKP_int32 DTX_enabled = 0, INBandFEC_enabled = 0;
    SKP_SILK_SDK_EncControlStruct encControl; // Struct for input to encoder
    SKP_SILK_SDK_EncControlStruct encStatus;  // Struct for status of encoder


    /* get arguments */
    strcpy(speechInFileName, fsource);
    strcpy(bitOutFileName, ftarget);
    /* If no max internal is specified, set to minimum of API fs and 24 kHz */
    max_internal_fs_Hz = API_fs_Hz;
    if (samplerate < max_internal_fs_Hz) {
        max_internal_fs_Hz = samplerate;
    }
    targetRate_bps = ratebps;

    /* Open files */
    speechInFile = fopen(speechInFileName, "rb");
    if (speechInFile == NULL) {
        exit(0);
    }
    bitOutFile = fopen(bitOutFileName, "wb");
    if (bitOutFile == NULL) {
        exit(0);
    }

    /* Add Silk header to stream */
    {

        static const char Silk_header[] = "#!SILK_V3";
        fwrite(Silk_header, sizeof(char), strlen(Silk_header), bitOutFile);
    }

    /* Create Encoder */
    ret = SKP_Silk_SDK_Get_Encoder_Size(&encSizeBytes);
    if (ret) {
        exit(0);
    }

    psEnc = malloc(encSizeBytes);

    /* Reset Encoder */
    ret = SKP_Silk_SDK_InitEncoder(psEnc, &encStatus);
    if (ret) {
        exit(0);
    }

    /* Set Encoder parameters */
    encControl.API_sampleRate = API_fs_Hz;
    encControl.maxInternalSampleRate = max_internal_fs_Hz;
    encControl.packetSize = (packetSize_ms * API_fs_Hz) / 1000;
    encControl.packetLossPercentage = packetLoss_perc;
    encControl.useInBandFEC = INBandFEC_enabled;
    encControl.useDTX = DTX_enabled;
    encControl.complexity = complexity_mode;
    encControl.bitRate = (targetRate_bps > 0 ? targetRate_bps : 0);

    if (API_fs_Hz > MAX_API_FS_KHZ * 1000 || API_fs_Hz < 0) {
        exit(0);
    }

    totPackets = 0;
    totActPackets = 0;
    sumBytes = 0.0;
    sumActBytes = 0.0;
    smplsSinceLastPacket = 0;

    while (1) {
        /* Read input from file */
        counter = fread(in, sizeof(SKP_int16), (frameSizeReadFromFile_ms * API_fs_Hz) / 1000,
                        speechInFile);
#ifdef _SYSTEM_IS_BIG_ENDIAN
        swap_endian( in, counter );
#endif
        if ((SKP_int) counter < ((frameSizeReadFromFile_ms * API_fs_Hz) / 1000)) {
            break;
        }

        /* max payload size */
        nBytes = MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES;

        /* Silk Encoder */
        ret = SKP_Silk_SDK_Encode(psEnc, &encControl, in, (SKP_int16) counter, payload, &nBytes);

        /* Get packet size */
        packetSize_ms = (SKP_int) ((1000 * (SKP_int32) encControl.packetSize) /
                                   encControl.API_sampleRate);

        smplsSinceLastPacket += (SKP_int) counter;

        if (((1000 * smplsSinceLastPacket) / API_fs_Hz) == packetSize_ms) {
            /* Sends a dummy zero size packet in case of DTX period  */
            /* to make it work with the decoder test program.        */
            /* In practice should be handled by RTP sequence numbers */
            totPackets++;
            sumBytes += nBytes;
            nrg = 0.0;
            for (k = 0; k < (SKP_int) counter; k++) {
                nrg += in[k] * (double) in[k];
            }
            if ((nrg / (SKP_int) counter) > 1e3) {
                sumActBytes += nBytes;
                totActPackets++;
            }

            /* Write payload size */
#ifdef _SYSTEM_IS_BIG_ENDIAN
            nBytes_LE = nBytes;
            swap_endian( &nBytes_LE, 1 );
            fwrite( &nBytes_LE, sizeof( SKP_int16 ), 1, bitOutFile );
#else
            fwrite(&nBytes, sizeof(SKP_int16), 1, bitOutFile);
#endif

            /* Write payload */
            fwrite(payload, sizeof(SKP_uint8), nBytes, bitOutFile);

            smplsSinceLastPacket = 0;

        }
    }

    /* Write dummy because it can not end with 0 bytes */
    nBytes = -1;

    /* Write payload size */
    fwrite(&nBytes, sizeof(SKP_int16), 1, bitOutFile);

    /* Free Encoder */
    free(psEnc);

    fclose(speechInFile);
    fclose(bitOutFile);

    return 0;
}

#ifdef _SYSTEM_IS_BIG_ENDIAN
/* Function to convert a little endian int16 to a */
/* big endian int16 or vica verca                 */
void swap_endian(
    SKP_int16       vec[],
    SKP_int         len
)
{
    SKP_int i;
    SKP_int16 tmp;
    SKP_uint8 *p1, *p2;

    for( i = 0; i < len; i++ ){
        tmp = vec[ i ];
        p1 = (SKP_uint8 *)&vec[ i ]; p2 = (SKP_uint8 *)&tmp;
        p1[ 0 ] = p2[ 1 ]; p1[ 1 ] = p2[ 0 ];
    }
}
#endif

/* Seed for the random number generator, which is used for simulating packet loss */
static SKP_int32 rand_seed = 1;

int decodefile( char* fsource, char* ftarget, int sampleRate)
{
    size_t    counter;
    SKP_int32 totPackets, i, k;
    SKP_int16 ret, len, tot_len;
    SKP_int16 nBytes;
    SKP_uint8 payload[    MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES * ( MAX_LBRR_DELAY + 1 ) ];
    SKP_uint8 *payloadEnd = NULL, *payloadToDec = NULL;
    SKP_uint8 FECpayload[ MAX_BYTES_PER_FRAME * MAX_INPUT_FRAMES ], *payloadPtr;
    SKP_int16 nBytesFEC;
    SKP_int16 nBytesPerPacket[ MAX_LBRR_DELAY + 1 ], totBytes;
    SKP_int16 out[ ( ( FRAME_LENGTH_MS * MAX_API_FS_KHZ ) << 1 ) * MAX_INPUT_FRAMES ], *outPtr;
    char      speechOutFileName[ 150 ], bitInFileName[ 150 ];
    FILE      *bitInFile, *speechOutFile;
    SKP_int32 API_Fs_Hz = 0;
    SKP_int32 decSizeBytes;
    void      *psDec;
    SKP_float loss_prob;
    SKP_int32 frames, lost, quiet;
    SKP_SILK_SDK_DecControlStruct DecControl;

    /* default settings */
    quiet     = 0;
    loss_prob = 0.0f;

    /* get arguments */
    strcpy( bitInFileName, fsource );
    strcpy( speechOutFileName, ftarget );
    API_Fs_Hz = sampleRate;

    /* Open files */
    bitInFile = fopen( bitInFileName, "rb" );
    if( bitInFile == NULL ) {
        exit( 0 );
    }

    /* Check Silk header */
    {
        char header_buf[ 50 ];
        fread(header_buf, sizeof(char), 1, bitInFile);
        header_buf[ strlen( "" ) ] = '\0'; /* Terminate with a null character */
        if( strcmp( header_buf, "" ) != 0 ) {
           counter = fread( header_buf, sizeof( char ), strlen( "!SILK_V3" ), bitInFile );
           header_buf[ strlen( "!SILK_V3" ) ] = '\0'; /* Terminate with a null character */
           if( strcmp( header_buf, "!SILK_V3" ) != 0 ) {
               /* Non-equal strings */
               exit( 0 );
           }
        } else {
           counter = fread( header_buf, sizeof( char ), strlen( "#!SILK_V3" ), bitInFile );
           header_buf[ strlen( "#!SILK_V3" ) ] = '\0'; /* Terminate with a null character */
           if( strcmp( header_buf, "#!SILK_V3" ) != 0 ) {
               /* Non-equal strings */
               exit( 0 );
           }
        }
    }

    speechOutFile = fopen( speechOutFileName, "wb" );
    if( speechOutFile == NULL ) {
        exit( 0 );
    }

    /* Set the samplingrate that is requested for the output */
    if( API_Fs_Hz == 0 ) {
        DecControl.API_sampleRate = 24000;
    } else {
        DecControl.API_sampleRate = API_Fs_Hz;
    }

    /* Initialize to one frame per packet, for proper concealment before first packet arrives */
    DecControl.framesPerPacket = 1;

    /* Create decoder */
    ret = SKP_Silk_SDK_Get_Decoder_Size( &decSizeBytes );
    psDec = malloc( decSizeBytes );

    /* Reset decoder */
    ret = SKP_Silk_SDK_InitDecoder( psDec );

    totPackets = 0;
    payloadEnd = payload;

    /* Simulate the jitter buffer holding MAX_FEC_DELAY packets */
    for( i = 0; i < MAX_LBRR_DELAY; i++ ) {
        /* Read payload size */
        counter = fread( &nBytes, sizeof( SKP_int16 ), 1, bitInFile );
#ifdef _SYSTEM_IS_BIG_ENDIAN
        swap_endian( &nBytes, 1 );
#endif
        /* Read payload */
        counter = fread( payloadEnd, sizeof( SKP_uint8 ), nBytes, bitInFile );

        if( ( SKP_int16 )counter < nBytes ) {
            break;
        }
        nBytesPerPacket[ i ] = nBytes;
        payloadEnd          += nBytes;
        totPackets++;
    }

    while( 1 ) {
        /* Read payload size */
        counter = fread( &nBytes, sizeof( SKP_int16 ), 1, bitInFile );
#ifdef _SYSTEM_IS_BIG_ENDIAN
        swap_endian( &nBytes, 1 );
#endif
        if( nBytes < 0 || counter < 1 ) {
            break;
        }

        /* Read payload */
        counter = fread( payloadEnd, sizeof( SKP_uint8 ), nBytes, bitInFile );
        if( ( SKP_int16 )counter < nBytes ) {
            break;
        }

        /* Simulate losses */
        rand_seed = SKP_RAND( rand_seed );
        if( ( ( ( float )( ( rand_seed >> 16 ) + ( 1 << 15 ) ) ) / 65535.0f >= ( loss_prob / 100.0f ) ) && ( counter > 0 ) ) {
            nBytesPerPacket[ MAX_LBRR_DELAY ] = nBytes;
            payloadEnd                       += nBytes;
        } else {
            nBytesPerPacket[ MAX_LBRR_DELAY ] = 0;
        }

        if( nBytesPerPacket[ 0 ] == 0 ) {
            /* Indicate lost packet */
            lost = 1;

            /* Packet loss. Search after FEC in next packets. Should be done in the jitter buffer */
            payloadPtr = payload;
            for( i = 0; i < MAX_LBRR_DELAY; i++ ) {
                if( nBytesPerPacket[ i + 1 ] > 0 ) {
                    SKP_Silk_SDK_search_for_LBRR( payloadPtr, nBytesPerPacket[ i + 1 ], ( i + 1 ), FECpayload, &nBytesFEC );
                    if( nBytesFEC > 0 ) {
                        payloadToDec = FECpayload;
                        nBytes = nBytesFEC;
                        lost = 0;
                        break;
                    }
                }
                payloadPtr += nBytesPerPacket[ i + 1 ];
            }
        } else {
            lost = 0;
            nBytes = nBytesPerPacket[ 0 ];
            payloadToDec = payload;
        }

        /* Silk decoder */
        outPtr = out;
        tot_len = 0;

        if( lost == 0 ) {
            /* No Loss: Decode all frames in the packet */
            frames = 0;
            do {
                /* Decode 20 ms */
                ret = SKP_Silk_SDK_Decode( psDec, &DecControl, 0, payloadToDec, nBytes, outPtr, &len );

                frames++;
                outPtr  += len;
                tot_len += len;
                if( frames > MAX_INPUT_FRAMES ) {
                    /* Hack for corrupt stream that could generate too many frames */
                    outPtr  = out;
                    tot_len = 0;
                    frames  = 0;
                }
                /* Until last 20 ms frame of packet has been decoded */
            } while( DecControl.moreInternalDecoderFrames );
        } else {
            /* Loss: Decode enough frames to cover one packet duration */
            for( i = 0; i < DecControl.framesPerPacket; i++ ) {
                /* Generate 20 ms */
                ret = SKP_Silk_SDK_Decode( psDec, &DecControl, 1, payloadToDec, nBytes, outPtr, &len );
                outPtr  += len;
                tot_len += len;
            }
        }

        totPackets++;

        /* Write output to file */
#ifdef _SYSTEM_IS_BIG_ENDIAN
        swap_endian( out, tot_len );
#endif
        fwrite( out, sizeof( SKP_int16 ), tot_len, speechOutFile );

        /* Update buffer */
        totBytes = 0;
        for( i = 0; i < MAX_LBRR_DELAY; i++ ) {
            totBytes += nBytesPerPacket[ i + 1 ];
        }
        SKP_memmove( payload, &payload[ nBytesPerPacket[ 0 ] ], totBytes * sizeof( SKP_uint8 ) );
        payloadEnd -= nBytesPerPacket[ 0 ];
        SKP_memmove( nBytesPerPacket, &nBytesPerPacket[ 1 ], MAX_LBRR_DELAY * sizeof( SKP_int16 ) );

    }

    /* Empty the recieve buffer */
    for( k = 0; k < MAX_LBRR_DELAY; k++ ) {
        if( nBytesPerPacket[ 0 ] == 0 ) {
            /* Indicate lost packet */
            lost = 1;

            /* Packet loss. Search after FEC in next packets. Should be done in the jitter buffer */
            payloadPtr = payload;
            for( i = 0; i < MAX_LBRR_DELAY; i++ ) {
                if( nBytesPerPacket[ i + 1 ] > 0 ) {
                    SKP_Silk_SDK_search_for_LBRR( payloadPtr, nBytesPerPacket[ i + 1 ], ( i + 1 ), FECpayload, &nBytesFEC );
                    if( nBytesFEC > 0 ) {
                        payloadToDec = FECpayload;
                        nBytes = nBytesFEC;
                        lost = 0;
                        break;
                    }
                }
                payloadPtr += nBytesPerPacket[ i + 1 ];
            }
        } else {
            lost = 0;
            nBytes = nBytesPerPacket[ 0 ];
            payloadToDec = payload;
        }

        /* Silk decoder */
        outPtr  = out;
        tot_len = 0;

        if( lost == 0 ) {
            /* No loss: Decode all frames in the packet */
            frames = 0;
            do {
                /* Decode 20 ms */
                ret = SKP_Silk_SDK_Decode( psDec, &DecControl, 0, payloadToDec, nBytes, outPtr, &len );

                frames++;
                outPtr  += len;
                tot_len += len;
                if( frames > MAX_INPUT_FRAMES ) {
                    /* Hack for corrupt stream that could generate too many frames */
                    outPtr  = out;
                    tot_len = 0;
                    frames  = 0;
                }
            /* Until last 20 ms frame of packet has been decoded */
            } while( DecControl.moreInternalDecoderFrames );
        } else {
            /* Loss: Decode enough frames to cover one packet duration */

            /* Generate 20 ms */
            for( i = 0; i < DecControl.framesPerPacket; i++ ) {
                ret = SKP_Silk_SDK_Decode( psDec, &DecControl, 1, payloadToDec, nBytes, outPtr, &len );
                outPtr  += len;
                tot_len += len;
            }
        }

        totPackets++;

        /* Write output to file */
#ifdef _SYSTEM_IS_BIG_ENDIAN
        swap_endian( out, tot_len );
#endif
        fwrite( out, sizeof( SKP_int16 ), tot_len, speechOutFile );

        /* Update Buffer */
        totBytes = 0;
        for( i = 0; i < MAX_LBRR_DELAY; i++ ) {
            totBytes += nBytesPerPacket[ i + 1 ];
        }
        SKP_memmove( payload, &payload[ nBytesPerPacket[ 0 ] ], totBytes * sizeof( SKP_uint8 ) );
        payloadEnd -= nBytesPerPacket[ 0 ];
        SKP_memmove( nBytesPerPacket, &nBytesPerPacket[ 1 ], MAX_LBRR_DELAY * sizeof( SKP_int16 ) );

    }

    /* Free decoder */
    free( psDec );

    /* Close files */
    fclose( speechOutFile );
    fclose( bitInFile );
    return 0;
}

JNIEXPORT jint JNICALL Java_top_kuriko_jni_Silk_encodefile
        (JNIEnv *env, jobject obj, jstring sourcefile, jstring targetfile, jint hz, jint bps){
    char* sfile = (char*) (*env)->GetStringUTFChars(env, sourcefile, 0);
    char* tfile = (char*) (*env)->GetStringUTFChars(env, targetfile, 0);
    return encodefile(sfile, tfile, hz, bps);
}

JNIEXPORT jint JNICALL Java_top_kuriko_jni_Silk_decodefile
        (JNIEnv *env, jobject obj, jstring sourcefile, jstring targetfile, jint hz){
    char* sfile = (char*) (*env)->GetStringUTFChars(env, sourcefile, 0);
    char* tfile = (char*) (*env)->GetStringUTFChars(env, targetfile, 0);
    return decodefile(sfile, tfile, hz);
}

#ifdef __cplusplus
}
#endif
