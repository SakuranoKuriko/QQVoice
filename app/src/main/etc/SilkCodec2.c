/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 * 
 * This file is part of Sipdroid (http://www.sipdroid.org)
 * 
 * Sipdroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

 
#include <jni.h>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

/* Define codec specific settings */
#define MAX_BYTES_ENC_PER_FRAME     250 // Equals peak bitrate of 100 kbps 
#define MAX_BYTES_DEC_PER_FRAME     1024

#define MAX_INPUT_FRAMES        5
#define MAX_LBRR_DELAY          2
#define MAX_FRAME_LENGTH        480

#define	MAX_FRAME			480

#define LOG_TAG "silk" // text for log tag 

#include "SKP_Silk_SDK_API.h"
#include "SKP_Silk_SigProc_FIX.h"

#undef DEBUG_SILK24

// the header length of the RTP frame (must skip when en/decoding)
#define	RTP_HDR_SIZE	12

static int codec_open = 0;


/* encoder parameters */

    SKP_int32 encSizeBytes;
    void      *psEnc;

    /* default settings */
    SKP_int   fs_kHz = 24;
    //SKP_int   targetRate_bps = 20000;
    SKP_int   packetSize_ms = 20;
    SKP_int   frameSizeReadFromFile_ms = 20;
    SKP_int   packetLoss_perc = 0, smplsSinceLastPacket;
    SKP_int   INBandFec_enabled = 0, DTX_enabled = 0, quiet = 0;
    SKP_SILK_SDK_EncControlStruct encControl; // Struct for input to encoder
        

/* decoder parameters */

    jbyte payloadToDec[    MAX_BYTES_DEC_PER_FRAME * MAX_INPUT_FRAMES * ( MAX_LBRR_DELAY + 1 ) ];
    jshort out[ ( MAX_FRAME_LENGTH << 1 ) * MAX_INPUT_FRAMES ], *outPtr;
    SKP_int32 decSizeBytes;
    void      *psDec;
    SKP_SILK_SDK_DecControlStruct DecControl;

JNIEXPORT jint JNICALL Java_top_kuriko_jni_Silk_open
  (JNIEnv *env, jobject obj, jint compression, jint samplerate, jint ratebps) {
	int ret;

	if (codec_open++ != 0)
		return (jint)0;

	/* Set the samplingrate that is requested for the output */
    DecControl.sampleRate = (samplerate < 24000 ? samplerate : 24000);
		
    /* Create decoder */
    ret = SKP_Silk_SDK_Get_Decoder_Size( &decSizeBytes );
    psDec = malloc( decSizeBytes );

    /* Reset decoder */
    ret = SKP_Silk_SDK_InitDecoder( psDec );


    /* Create Encoder */
    ret = SKP_Silk_SDK_Get_Encoder_Size( &encSizeBytes );
    psEnc = malloc( encSizeBytes );
    
    /* Reset Encoder */
    ret = SKP_Silk_SDK_InitEncoder( psEnc, &encControl );
    
    /* Set Encoder parameters */
    encControl.sampleRate           = fs_kHz * 1000;
    encControl.packetSize           = packetSize_ms * fs_kHz;
    encControl.packetLossPercentage = packetLoss_perc;
    encControl.useInBandFEC         = INBandFec_enabled;
    encControl.useDTX               = DTX_enabled;
    encControl.complexity           = compression;
    encControl.bitRate              = ratebps;
	
	return (jint)0;
}

/*void Print_Decode_Error_Msg(int errcode) {
	switch (errcode) {
		case SKP_SILK_DEC_WRONG_SAMPLING_FREQUENCY:
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
				"!!!!!!!!!!! Decode_Error_Message: %d\nOutput sampling frequency lower than internal decoded sampling frequency\n", errcode);
			break;
		case SKP_SILK_DEC_PAYLOAD_TOO_LARGE:
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
				"!!!!!!!!!!! Decode_Error_Message: %d\nPayload size exceeded the maximum allowed 1024 bytes\n", errcode); 
			break;
		case SKP_SILK_DEC_PAYLOAD_ERROR:
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
				"!!!!!!!!!!! Decode_Error_Message: %d\nPayload has bit errors\n", errcode); 
			break;			
	}
}

void Print_Encode_Error_Msg(int errcode) {
	switch (errcode) {
		case SKP_SILK_ENC_INPUT_INVALID_NO_OF_SAMPLES:
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
				"!!!!!!!!!!! Decode_Error_Message: %d\nInput length is not a multiplum of 10 ms, or length is longer than the packet length\n", errcode);
			break;
		case SKP_SILK_ENC_FS_NOT_SUPPORTED:
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
				"!!!!!!!!!!! Decode_Error_Message: %d\nSampling frequency not 8000, 12000, 16000 or 24000 Hertz \n", errcode); 
			break;
		case SKP_SILK_ENC_PACKET_SIZE_NOT_SUPPORTED:
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
				"!!!!!!!!!!! Decode_Error_Message: %d\nPacket size not 20, 40, 60, 80 or 100 ms\n", errcode); 
			break;			
		case SKP_SILK_ENC_PAYLOAD_BUF_TOO_SHORT:
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
				"!!!!!!!!!!! Decode_Error_Message: %d\nAllocated payload buffer too short \n", errcode);
			break;
		case SKP_SILK_ENC_WRONG_LOSS_RATE:
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
				"!!!!!!!!!!! Decode_Error_Message: %d\nLoss rate not between 0 and 100 percent\n", errcode); 
			break;
		case SKP_SILK_ENC_WRONG_COMPLEXITY_SETTING:
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
				"!!!!!!!!!!! Decode_Error_Message: %d\nComplexity setting not valid, use 0, 1 or 2\n", errcode); 
			break;		
		case SKP_SILK_ENC_WRONG_INBAND_FEC_SETTING:
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
				"!!!!!!!!!!! Decode_Error_Message: %d\nInband FEC setting not valid, use 0 or 1\n", errcode);
			break;
		case SKP_SILK_ENC_WRONG_DTX_SETTING:
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
				"!!!!!!!!!!! Decode_Error_Message: %d\nDTX setting not valid, use 0 or 1\n", errcode); 
			break;
		case SKP_SILK_ENC_INTERNAL_ERROR:
			__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, 
				"!!!!!!!!!!! Decode_Error_Message: %d\nInternal encoder error\n", errcode); 
			break;				
	}
}//*/

JNIEXPORT jint JNICALL Java_top_kuriko_jni_Silk_encode
    (JNIEnv *env, jobject obj, jshortArray lin, jint offset, jbyteArray encoded, jint size) {

    jbyte	  enc_payload[ MAX_BYTES_DEC_PER_FRAME * MAX_INPUT_FRAMES ];
    jshort    in[ MAX_FRAME_LENGTH * MAX_INPUT_FRAMES ];	
	int ret,i,frsz=MAX_FRAME;
	SKP_int16 nBytes;
	unsigned int lin_pos = 0;
	
	if (!codec_open)
		return 0;


	for (i = 0; i < size; i+=MAX_FRAME) {

        (*env)->GetShortArrayRegion(env, lin, offset + i,frsz, in);
        /* max payload size */
        nBytes = MAX_BYTES_ENC_PER_FRAME * MAX_INPUT_FRAMES;

        ret = SKP_Silk_SDK_Encode( psEnc, &encControl, in, (SKP_int16)frsz, (SKP_uint8 *)enc_payload, &nBytes );
        if( ret ) 
            break;
        /* Write payload */
        (*env)->SetByteArrayRegion(env, encoded, RTP_HDR_SIZE+ lin_pos, nBytes, enc_payload);
		lin_pos += nBytes;
	}

    return (jint)lin_pos;
}

JNIEXPORT jint JNICALL Java_top_kuriko_jni_Silk_decode
    (JNIEnv *env, jobject obj, jbyteArray encoded, jshortArray lin, jint size) {
    //                                      in                 out      170817

    //                      1024          *        5         * (       2        + 1 )
    jbyte buffer [MAX_BYTES_DEC_PER_FRAME * MAX_INPUT_FRAMES * ( MAX_LBRR_DELAY + 1 ) ];
    //                   (       480         * 2 ) *        5
    jshort output_buffer[( MAX_FRAME_LENGTH << 1 ) * MAX_INPUT_FRAMES ];
//	SKP_int16	*outPtr;

	int ret;
	SKP_int16 len;
//	int	tot_len,frames;
	if (!codec_open)
		return 0;

    //                                            12
    (*env)->GetByteArrayRegion(env, encoded, RTP_HDR_SIZE, size, buffer);

    //  ||
    // \||/
    //  ∀ libc: Fatal signal 11 (SIGSEGV), code 1, fault addr 0xffb5d000 in tid 3279
    //(*env)->GetByteArrayRegion(env, encoded, 0, size, buffer);
//	outPtr = output_buffer;
//    tot_len = 0;
//	frames = 0;

//	do {
		ret = SKP_Silk_SDK_Decode( psDec, &DecControl, 0,(SKP_uint8 *) buffer, size, output_buffer,&len );//		frames++;
//		outPtr  += len;
//		tot_len += len;

//	} while( DecControl.moreInternalDecoderFrames );

    //  ||
    // \||/
    //  ∀ JNI DETECTED ERROR IN APPLICATION: JNI SetShortArrayRegion called with pending exception java.lang.ArrayIndexOutOfBoundsException
    (*env)->SetShortArrayRegion(env, lin, 0, len, output_buffer);
	return (jint)len;
}

JNIEXPORT void JNICALL Java_top_kuriko_jni_Silk_close
    (JNIEnv *env, jobject obj) {

	if (--codec_open != 0)
		return;
    /* Free decoder */
    free( psDec );
    /* Free Encoder */
    free( psEnc );
}
