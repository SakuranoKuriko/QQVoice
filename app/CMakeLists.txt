# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( silkcodec
             SHARED
             src/main/jni/SilkCodec.c
             src/main/jni/silk/src/SKP_Silk_A2NLSF.c
             src/main/jni/silk/src/SKP_Silk_CNG.c
             src/main/jni/silk/src/SKP_Silk_HP_variable_cutoff_FIX.c
             src/main/jni/silk/src/SKP_Silk_LBRR_reset.c
             src/main/jni/silk/src/SKP_Silk_LPC_inv_pred_gain.c
             src/main/jni/silk/src/SKP_Silk_LPC_stabilize.c
             src/main/jni/silk/src/SKP_Silk_LPC_synthesis_filter.c
             src/main/jni/silk/src/SKP_Silk_LPC_synthesis_order16.c
             src/main/jni/silk/src/SKP_Silk_LP_variable_cutoff.c
             src/main/jni/silk/src/SKP_Silk_LSF_cos_table.c
             src/main/jni/silk/src/SKP_Silk_LTP_analysis_filter_FIX.c
             src/main/jni/silk/src/SKP_Silk_LTP_scale_ctrl_FIX.c
             src/main/jni/silk/src/SKP_Silk_MA.c
             src/main/jni/silk/src/SKP_Silk_NLSF2A.c
             src/main/jni/silk/src/SKP_Silk_NLSF2A_stable.c
             src/main/jni/silk/src/SKP_Silk_NLSF_MSVQ_decode.c
             src/main/jni/silk/src/SKP_Silk_NLSF_MSVQ_encode_FIX.c
             src/main/jni/silk/src/SKP_Silk_NLSF_VQ_rate_distortion_FIX.c
             src/main/jni/silk/src/SKP_Silk_NLSF_VQ_sum_error_FIX.c
             src/main/jni/silk/src/SKP_Silk_NLSF_VQ_weights_laroia.c
             src/main/jni/silk/src/SKP_Silk_NLSF_stabilize.c
             src/main/jni/silk/src/SKP_Silk_NSQ.c
             src/main/jni/silk/src/SKP_Silk_NSQ_del_dec.c
             src/main/jni/silk/src/SKP_Silk_PLC.c
             src/main/jni/silk/src/SKP_Silk_VAD.c
             src/main/jni/silk/src/SKP_Silk_VQ_nearest_neighbor_FIX.c
             src/main/jni/silk/src/SKP_Silk_allpass_int.c
             src/main/jni/silk/src/SKP_Silk_ana_filt_bank_1.c
             src/main/jni/silk/src/SKP_Silk_apply_sine_window.c
             src/main/jni/silk/src/SKP_Silk_array_maxabs.c
             src/main/jni/silk/src/SKP_Silk_autocorr.c
             src/main/jni/silk/src/SKP_Silk_biquad.c
             src/main/jni/silk/src/SKP_Silk_biquad_alt.c
             src/main/jni/silk/src/SKP_Silk_burg_modified.c
             src/main/jni/silk/src/SKP_Silk_bwexpander.c
             src/main/jni/silk/src/SKP_Silk_bwexpander_32.c
             src/main/jni/silk/src/SKP_Silk_code_signs.c
             src/main/jni/silk/src/SKP_Silk_control_codec_FIX.c
             src/main/jni/silk/src/SKP_Silk_corrMatrix_FIX.c
             src/main/jni/silk/src/SKP_Silk_create_init_destroy.c
             src/main/jni/silk/src/SKP_Silk_dec_API.c
             src/main/jni/silk/src/SKP_Silk_decode_core.c
             src/main/jni/silk/src/SKP_Silk_decode_frame.c
             src/main/jni/silk/src/SKP_Silk_decode_indices_v4.c
             src/main/jni/silk/src/SKP_Silk_decode_parameters.c
             src/main/jni/silk/src/SKP_Silk_decode_parameters_v4.c
             src/main/jni/silk/src/SKP_Silk_decode_pulses.c
             src/main/jni/silk/src/SKP_Silk_decoder_set_fs.c
             src/main/jni/silk/src/SKP_Silk_detect_SWB_input.c
             src/main/jni/silk/src/SKP_Silk_enc_API.c
             src/main/jni/silk/src/SKP_Silk_encode_frame_FIX.c
             src/main/jni/silk/src/SKP_Silk_encode_parameters.c
             src/main/jni/silk/src/SKP_Silk_encode_parameters_v4.c
             src/main/jni/silk/src/SKP_Silk_encode_pulses.c
             src/main/jni/silk/src/SKP_Silk_find_LPC_FIX.c
             src/main/jni/silk/src/SKP_Silk_find_LTP_FIX.c
             src/main/jni/silk/src/SKP_Silk_find_pitch_lags_FIX.c
             src/main/jni/silk/src/SKP_Silk_find_pred_coefs_FIX.c
             src/main/jni/silk/src/SKP_Silk_gain_quant.c
             src/main/jni/silk/src/SKP_Silk_init_encoder_FIX.c
             src/main/jni/silk/src/SKP_Silk_inner_prod_aligned.c
             src/main/jni/silk/src/SKP_Silk_interpolate.c
             src/main/jni/silk/src/SKP_Silk_k2a.c
             src/main/jni/silk/src/SKP_Silk_k2a_Q16.c
             src/main/jni/silk/src/SKP_Silk_lin2log.c
             src/main/jni/silk/src/SKP_Silk_log2lin.c
             src/main/jni/silk/src/SKP_Silk_lowpass_int.c
             src/main/jni/silk/src/SKP_Silk_lowpass_short.c
             src/main/jni/silk/src/SKP_Silk_noise_shape_analysis_FIX.c
             src/main/jni/silk/src/SKP_Silk_pitch_analysis_core.c
             src/main/jni/silk/src/SKP_Silk_pitch_est_tables.c
             src/main/jni/silk/src/SKP_Silk_prefilter_FIX.c
             src/main/jni/silk/src/SKP_Silk_process_NLSFs_FIX.c
             src/main/jni/silk/src/SKP_Silk_process_gains_FIX.c
             src/main/jni/silk/src/SKP_Silk_pulses_to_bytes.c
             src/main/jni/silk/src/SKP_Silk_quant_LTP_gains_FIX.c
             src/main/jni/silk/src/SKP_Silk_range_coder.c
             src/main/jni/silk/src/SKP_Silk_regularize_correlations_FIX.c
             src/main/jni/silk/src/SKP_Silk_resample_1_2.c
             src/main/jni/silk/src/SKP_Silk_resample_1_2_coarse.c
             src/main/jni/silk/src/SKP_Silk_resample_1_2_coarsest.c
             src/main/jni/silk/src/SKP_Silk_resample_1_3.c
             src/main/jni/silk/src/SKP_Silk_resample_2_1_coarse.c
             src/main/jni/silk/src/SKP_Silk_resample_2_3.c
             src/main/jni/silk/src/SKP_Silk_resample_2_3_coarse.c
             src/main/jni/silk/src/SKP_Silk_resample_2_3_coarsest.c
             src/main/jni/silk/src/SKP_Silk_resample_2_3_rom.c
             src/main/jni/silk/src/SKP_Silk_resample_3_1.c
             src/main/jni/silk/src/SKP_Silk_resample_3_2.c
             src/main/jni/silk/src/SKP_Silk_resample_3_2_rom.c
             src/main/jni/silk/src/SKP_Silk_resample_3_4.c
             src/main/jni/silk/src/SKP_Silk_resample_4_3.c
             src/main/jni/silk/src/SKP_Silk_residual_energy16_FIX.c
             src/main/jni/silk/src/SKP_Silk_residual_energy_FIX.c
             src/main/jni/silk/src/SKP_Silk_scale_copy_vector16.c
             src/main/jni/silk/src/SKP_Silk_scale_vector.c
             src/main/jni/silk/src/SKP_Silk_schur.c
             src/main/jni/silk/src/SKP_Silk_schur64.c
             src/main/jni/silk/src/SKP_Silk_shell_coder.c
             src/main/jni/silk/src/SKP_Silk_sigm_Q15.c
             src/main/jni/silk/src/SKP_Silk_solve_LS_FIX.c
             src/main/jni/silk/src/SKP_Silk_sort.c
             src/main/jni/silk/src/SKP_Silk_sum_sqr_shift.c
             src/main/jni/silk/src/SKP_Silk_tables_LTP.c
             src/main/jni/silk/src/SKP_Silk_tables_NLSF_CB0_10.c
             src/main/jni/silk/src/SKP_Silk_tables_NLSF_CB0_16.c
             src/main/jni/silk/src/SKP_Silk_tables_NLSF_CB1_10.c
             src/main/jni/silk/src/SKP_Silk_tables_NLSF_CB1_16.c
             src/main/jni/silk/src/SKP_Silk_tables_gain.c
             src/main/jni/silk/src/SKP_Silk_tables_other.c
             src/main/jni/silk/src/SKP_Silk_tables_pitch_lag.c
             src/main/jni/silk/src/SKP_Silk_tables_pulses_per_block.c
             src/main/jni/silk/src/SKP_Silk_tables_sign.c
             src/main/jni/silk/src/SKP_Silk_tables_type_offset.c )
add_library( # Sets the name of the library.
             ffmpegcmd
             # Sets the library as a shared library.
             SHARED
             # Provides a relative path to your source file(s).
             src/main/jni/ffmpeg/cmdutils.c
             src/main/jni/ffmpeg/ffmpeg.c
             src/main/jni/ffmpeg/ffmpeg_filter.c
             src/main/jni/ffmpeg/ffmpeg_opt.c
             src/main/jni/ffmpeg_cmd.c)
add_library( ffmpeg
             SHARED
             IMPORTED )
add_library( silkcodecqq
             SHARED
             IMPORTED )
set_target_properties( ffmpeg
                       PROPERTIES IMPORTED_LOCATION
                       ../../../../libs/armeabi-v7a/libffmpeg.so )
set_target_properties( silkcodecqq
                       PROPERTIES IMPORTED_LOCATION
                       ../../../../libs/armeabi-v7a/libcodecsilk.so )
include_directories( src/main/jni/silk/interface
                     src/main/jni/silk/src )
include_directories( src/main/jni/include
                     src/main/jni)
target_link_libraries( ffmpegcmd
                       ffmpeg )
target_link_libraries( silkcodec
                       silkcodecqq )