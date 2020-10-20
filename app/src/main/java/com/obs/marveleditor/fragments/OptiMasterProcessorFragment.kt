/*
 *
 *  Created by Optisol on Aug 2019.
 *  Copyright © 2019 Optisol Business Solutions pvt ltd. All rights reserved.
 *
 */

package com.obs.marveleditor.fragments

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.support.annotation.Nullable
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import com.obs.marveleditor.R
import com.obs.marveleditor.adapter.OptiVideoOptionsAdapter
import com.obs.marveleditor.interfaces.OptiFFMpegCallback
import com.obs.marveleditor.interfaces.OptiVideoOptionListener
import com.obs.marveleditor.stickerUtils.*
import com.obs.marveleditor.stickerUtils.MotionView.MotionViewCallback
import com.obs.marveleditor.utils.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OptiMasterProcessorFragment : Fragment(), OptiBaseCreatorDialogFragment.CallBacks,
    OptiVideoOptionListener, OptiFFMpegCallback {

    var tagName: String = OptiMasterProcessorFragment::class.java.simpleName
    private lateinit var rootView: View
    var videoUri: Uri? = null
    private var videoFile: File? = null
    private var permissionList: ArrayList<String> = ArrayList()
    private lateinit var preferences: SharedPreferences
    private lateinit var progressBar: ProgressBar
    private var tvVideoProcessing: TextView? = null
    private var handler: Handler = Handler()
    private var ibGallery: ImageButton? = null
    private var ibCamera: ImageButton? = null
    private var masterVideoFile: File? = null
    private var playbackPosition: Long = 0
    private var currentWindow: Int = 0
    private var ePlayer: PlayerView? = null
    private var pbLoading: ProgressBar? = null
    private var exoPlayer: SimpleExoPlayer? = null
    private var playWhenReady: Boolean? = false
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var rvVideoOptions: RecyclerView
    private lateinit var optiVideoOptionsAdapter: OptiVideoOptionsAdapter
    private lateinit var stickerAdapter: StickerAdapter
    private var videoOptions: ArrayList<String> = ArrayList()
    private var orientationLand: Boolean = false
    private var tvSave: ImageView? = null
    private var isLargeVideo: Boolean? = false
    private var mContext: Context? = null
    private var tvInfo: TextView? = null
    private var lists: MutableList<Int> = mutableListOf()
    private var fullView: ConstraintLayout? = null
    private var motionView: MotionView? = null
    private var textEntityEditPanel: View? = null

    private val stickerIds = intArrayOf(
        R.drawable.sticker_1,
        R.drawable.sticker_2,
        R.drawable.sticker_3,
        R.drawable.sticker_4,
        R.drawable.sticker_5,
        R.drawable.sticker_6,
        R.drawable.sticker_7,
        R.drawable.sticker_8,
        R.drawable.sticker_9,
        R.drawable.sticker_10,
        R.drawable.sticker_11,
        R.drawable.sticker_12
//        R.drawable.sticker_13,
//        R.drawable.sticker_14,
//        R.drawable.sticker_15,
//        R.drawable.sticker_6,
//        R.drawable.sticker_7,
//        R.drawable.sticker_8,
//        R.drawable.sticker_9,
//        R.drawable.sticker_10

    )

    private val motionViewCallback: MotionViewCallback = object : MotionViewCallback {
        override fun onEntitySelected(@Nullable entity: MotionEntity?) {
            if (entity is TextEntity) {
                textEntityEditPanel!!.visibility = View.VISIBLE
            } else {
                textEntityEditPanel!!.visibility = View.GONE
            }
        }

        override fun onEntityDoubleTap(entity: MotionEntity) {
            TODO("Not yet implemented")
        }
    }

    private var liste = object : StickerAdapter.ClickListener {
        override fun onClickListener(item: Int) {
            stickerAdd(item)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        rootView = inflater.inflate(R.layout.opti_video_processor_fragment, container, false)
        initView(rootView)
        return rootView
    }

    fun initView(rootView: View?) {
        ePlayer = rootView?.findViewById(R.id.ePlayer)
        tvSave = rootView?.findViewById(R.id.tvSave)
        pbLoading = rootView?.findViewById(R.id.pbLoading)
        ibGallery = rootView?.findViewById(R.id.ibGallery)
        ibCamera = rootView?.findViewById(R.id.ibCamera)
        progressBar = rootView?.findViewById(R.id.progressBar)!!
        tvVideoProcessing = rootView.findViewById(R.id.tvVideoProcessing)
        tvInfo = rootView.findViewById(R.id.tvInfo)
        motionView = rootView.findViewById(R.id.motionview)
        fullView = rootView.findViewById(R.id.fullView)

        val relative: RelativeLayout = rootView.findViewById(R.id.relative_view)

        val close: TextView = rootView.findViewById(R.id.close)
        val stickerRecycler: RecyclerView? = rootView.findViewById(R.id.show_stickers)!!
        val stickerLayout: LinearLayout = rootView.findViewById(R.id.sticker_rec_view)
        val emo: ImageButton = rootView.findViewById(R.id.ibEmoji)
        val saveRelative: ImageButton = rootView.findViewById(R.id.save_relative)

        val ffmpeg = FFmpeg.getInstance(context)
        val VideoOut2 =
            Environment.getExternalStorageDirectory().absolutePath + "/annu/" + System.currentTimeMillis() + "_test.mp4"


        ffmpeg.loadBinary(LoadBinaryResponseHandler())



        saveRelative.setOnClickListener {


            var outputFile: String =
                Environment.getExternalStorageDirectory().absolutePath + "/amap/" + System.currentTimeMillis() + "test_image.jpg"

            var view: View = relative
            val bitmap = Bitmap.createBitmap(720, 640, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)

            val files: File = File(outputFile)
            if (!file!!.exists()) {
                file!!.mkdirs()
            }

            val ostream = FileOutputStream(files)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream)
            Toast.makeText(context, "saving" + bitmap, Toast.LENGTH_SHORT).show()
            ostream.close()

            ffmpeg.execute(
                arrayOf<String>(
                    "-y",
                    "-i",
                    videoFile.toString(),
                    "-i",
                    outputFile,
                    "-filter_complex",
                    "[1:v] scale=1080:1920 [ovrl]; [0:v][ovrl]overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2",
                    VideoOut2
                ), object : ExecuteBinaryResponseHandler() {

                })
        }

        emo.setOnClickListener {
            stickerLayout.animate().translationY(0f).duration = 0
        }

        close.setOnClickListener {
            stickerLayout.animate().translationY(1000f).duration = 0
        }

        preferences = activity!!.getSharedPreferences(
            "fetch_permission", Context.MODE_PRIVATE
        )

//        var grid: GridLayoutManager(activity!!.applicationContext)
        gridLayoutManager = GridLayoutManager(
            activity!!.applicationContext, 4
        )
        gridLayoutManager.orientation = GridLayoutManager.VERTICAL
        stickerRecycler!!.layoutManager = gridLayoutManager

        for (i in stickerIds.indices) {
            lists.add(stickerIds[i])
        }

        stickerAdapter = StickerAdapter(
            activity!!.applicationContext,
            liste, lists
        )

        stickerRecycler.adapter = stickerAdapter

        rvVideoOptions = rootView.findViewById(R.id.rvVideoOptions)!!
        linearLayoutManager = LinearLayoutManager(activity!!.applicationContext)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rvVideoOptions.layoutManager = linearLayoutManager

        mContext = context

        //add video editing options
        //videoOptions.add(OptiConstant.FLIRT)
//        videoOptions.add(OptiConstant.TRIM)
//        videoOptions.add(OptiConstant.MUSIC)
//        videoOptions.add(OptiConstant.PLAYBACK)

        videoOptions.add(OptiConstant.TEXT)
//        videoOptions.add(OptiConstant.OBJECT)
//        videoOptions.add(OptiConstant.MERGE)
//        videoOptions.add(OptiConstant.TRANSITION)

        optiVideoOptionsAdapter =
            OptiVideoOptionsAdapter(
                videoOptions,
                activity!!.applicationContext,
                this,
                orientationLand
            )
        rvVideoOptions.adapter = optiVideoOptionsAdapter
        optiVideoOptionsAdapter.notifyDataSetChanged()

        checkStoragePermission(OptiConstant.PERMISSION_STORAGE)

        //load FFmpeg
        try {
            FFmpeg.getInstance(activity).loadBinary(object : FFmpegLoadBinaryResponseHandler {
                override fun onFailure() {
                    Log.v("FFMpeg", "Failed to load FFMpeg library.")
                }

                override fun onSuccess() {
                    Log.v("FFMpeg", "FFMpeg Library loaded!")
                }

                override fun onStart() {
                    Log.v("FFMpeg", "FFMpeg Started")
                }

                override fun onFinish() {
                    Log.v("FFMpeg", "FFMpeg Stopped")
                }
            })
        } catch (e: FFmpegNotSupportedException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        ibGallery?.setOnClickListener {
            openGallery()
        }

        ibCamera?.setOnClickListener {
            openCamera()
        }

        tvSave?.setOnClickListener {
            AlertDialog.Builder(context!!)
                .setTitle(OptiConstant.APP_NAME)
                .setMessage(getString(R.string.save_video))
                .setPositiveButton(getString(R.string.Continue)) { dialog, which ->
                    if (masterVideoFile != null) {
                        val outputFile = createSaveVideoFile()
                        OptiCommonMethods.copyFile(masterVideoFile, outputFile)
                        Toast.makeText(context, R.string.successfully_saved, Toast.LENGTH_SHORT)
                            .show()
                        OptiUtils.refreshGallery(outputFile.absolutePath, context!!)
                        tvSave!!.visibility = View.GONE
                    }
                }
                .setNegativeButton(R.string.cancel) { dialog, which -> }
                .show()
        }

        tvInfo?.setOnClickListener {
            OptiVideoOptionFragment.newInstance().apply {
                setHelper(this@OptiMasterProcessorFragment)
            }.show(fragmentManager, "OptiVideoOptionFragment")
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //for playing video in landscape mode
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.v(tagName, "orientation: ORIENTATION_LANDSCAPE")
            orientationLand = true
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.v(tagName, "orientation: ORIENTATION_PORTRAIT")
            orientationLand = false
        }
        optiVideoOptionsAdapter =
            OptiVideoOptionsAdapter(
                videoOptions,
                activity!!.applicationContext,
                this,
                orientationLand
            )
        rvVideoOptions.adapter = optiVideoOptionsAdapter
        optiVideoOptionsAdapter.notifyDataSetChanged()
    }

    override fun onAudioFileProcessed(convertedAudioFile: File) {

    }

    override fun reInitPlayer() {
        initializePlayer()
    }

    override fun onDidNothing() {
        initializePlayer()
    }

    override fun onFileProcessed(file: File) {
        tvSave!!.visibility = View.VISIBLE
        masterVideoFile = file
        isLargeVideo = false

        val extension = OptiCommonMethods.getFileExtension(masterVideoFile!!.absolutePath)

        //check video format before playing into exoplayer
        if (extension == OptiConstant.AVI_FORMAT) {
//            convertAviToMp4() //avi format is not supported in exoplayer
        } else {
            playbackPosition = 0
            currentWindow = 0
            initializePlayer()
        }
    }

    override fun showLoading(isShow: Boolean) {
        if (isShow) {
            progressBar.visibility = View.VISIBLE
            tvVideoProcessing!!.visibility = View.VISIBLE
            setProgressValue()
        } else {
            progressBar.visibility = View.INVISIBLE
            tvVideoProcessing!!.visibility = View.INVISIBLE
        }
    }

    fun setProgressValue() {
        var progressStatus = 1

        Thread(Runnable {
            while (progressStatus < 100) {
                progressStatus++
                handler.post {
                    progressBar.progress = progressStatus
                }
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }).start()
    }

    override fun getFile(): File? {
        return masterVideoFile
    }


    fun checkAllPermission(permission: Array<String>) {
        val blockedPermission = checkHasPermission(activity, permission)
        if (blockedPermission != null && blockedPermission.size > 0) {
            val isBlocked = isPermissionBlocked(activity, blockedPermission)
            if (isBlocked) {
                callPermissionSettings()
            } else {
                requestPermissions(permission, OptiConstant.RECORD_VIDEO)
            }
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            videoFile = OptiUtils.createVideoFile(context!!)
            Log.v(tagName, "videoPath1: " + videoFile!!.absolutePath)
            videoUri = FileProvider.getUriForFile(
                context!!,
                "com.obs.marveleditor.provider", videoFile!!
            )
            cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 240) //4 minutes
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 720)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoFile)
            startActivityForResult(cameraIntent, OptiConstant.RECORD_VIDEO)
        }
    }

    fun checkStoragePermission(permission: Array<String>) {
        val blockedPermission = checkHasPermission(activity, permission)
        if (blockedPermission != null && blockedPermission.size > 0) {
            val isBlocked = isPermissionBlocked(activity, blockedPermission)
            if (isBlocked) {
                callPermissionSettings()
            } else {
                requestPermissions(permission, OptiConstant.ADD_ITEMS_IN_STORAGE)
            }
        } else {
            itemStorageAction()
        }
    }

    fun itemStorageAction() {
        val sessionManager = OptiSessionManager()

        if (sessionManager.isFirstTime(activity!!)) {
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_1,
                "sticker_1",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_2,
                "sticker_2",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_3,
                "sticker_3",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_4,
                "sticker_4",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_5,
                "sticker_5",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_6,
                "sticker_6",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_7,
                "sticker_7",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_8,
                "sticker_8",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_9,
                "sticker_9",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_10,
                "sticker_10",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_11,
                "sticker_11",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_12,
                "sticker_12",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_13,
                "sticker_13",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_14,
                "sticker_14",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_15,
                "sticker_15",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_16,
                "sticker_16",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_17,
                "sticker_17",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_18,
                "sticker_18",
                context!!
            )
            OptiUtils.copyFileToInternalStorage(
                R.drawable.sticker_19,
                "sticker_19",
                context!!
            )

            OptiUtils.copyFontToInternalStorage(
                R.font.roboto_black,
                "roboto_black",
                context!!
            )

            sessionManager.setFirstTime(activity!!, false)
        }
    }

    var isFirstTimePermission: Boolean
        get() = preferences.getBoolean("isFirstTimePermission", false)
        set(isFirstTime) = preferences.edit().putBoolean("isFirstTimePermission", isFirstTime)
            .apply()

    val isMarshmallow: Boolean
        get() = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) or (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1)

    fun checkHasPermission(context: Activity?, permissions: Array<String>?): ArrayList<String> {
        permissionList = ArrayList()
        if (isMarshmallow && context != null && permissions != null) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionList.add(permission)
                }
            }
        }
        return permissionList
    }

    fun isPermissionBlocked(context: Activity?, permissions: ArrayList<String>?): Boolean {
        if (isMarshmallow && context != null && permissions != null && isFirstTimePermission) {
            for (permission in permissions) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                    return true
                }
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_CANCELED) return

        when (requestCode) {

            OptiConstant.VIDEO_GALLERY -> {
                data?.let {
                    setFilePath(resultCode, it, OptiConstant.VIDEO_GALLERY)
                }
            }

        }
    }

    fun setFilePath(resultCode: Int, data: Intent, mode: Int) {

        if (resultCode == RESULT_OK) {
            try {
                val selectedImage = data.data
                //  Log.e("selectedImage==>", "" + selectedImage)
                val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
                val cursor = context!!.contentResolver
                    .query(selectedImage!!, filePathColumn, null, null, null)
                if (cursor != null) {
                    cursor.moveToFirst()
                    val columnIndex = cursor
                        .getColumnIndex(filePathColumn[0])
                    val filePath = cursor.getString(columnIndex)
                    cursor.close()
                    if (mode == OptiConstant.VIDEO_GALLERY) {
                        Log.v(tagName, "filePath: $filePath")
                        masterVideoFile = File(filePath)

                        val extension =
                            OptiCommonMethods.getFileExtension(masterVideoFile!!.absolutePath)

                        val timeInMillis = OptiUtils.getVideoDuration(context!!, masterVideoFile!!)
                        Log.v(tagName, "timeInMillis: $timeInMillis")
                        val duration = OptiCommonMethods.convertDurationInMin(timeInMillis)
                        Log.v(tagName, "videoDuration: $duration")

                        //check if video is more than 4 minutes
                        if (duration < OptiConstant.VIDEO_LIMIT) {
                            //check video format before playing into exoplayer
                            if (extension == OptiConstant.AVI_FORMAT) {
//                                convertAviToMp4() //avi format is not supported in exoplayer
                            } else {
                                playbackPosition = 0
                                currentWindow = 0
                                initializePlayer()
                            }
                        } else {
                            Toast.makeText(
                                activity,
                                getString(R.string.error_select_smaller_video),
                                Toast.LENGTH_SHORT
                            ).show()

                            isLargeVideo = true
                            val uri = Uri.fromFile(masterVideoFile)
//                            val intent = Intent(context, OptiTrimmerActivity::class.java)
//                            intent.putExtra("VideoPath", filePath)
//                            intent.putExtra("VideoDuration", OptiCommonMethods.getMediaDuration(context, uri))
//                            startActivityForResult(intent, OptiConstant.MAIN_VIDEO_TRIM)
                        }
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || exoPlayer == null) {
            masterVideoFile?.let {
                if (!isLargeVideo!!) { //for the larger video player shouldn't resume on cancel in trimming view
                    initializePlayer()
                }
            }
        }
    }


    fun releasePlayer() {
        if (exoPlayer != null) {
//            playbackPosition = exoPlayer?.currentPosition!!
//            currentWindow = exoPlayer?.currentWindowIndex!!
//            playWhenReady = exoPlayer?.playWhenReady
//            exoPlayer?.release()
            exoPlayer!!.repeatMode
            exoPlayer = null
        }
    }

    fun initializePlayer() {
        try {
            tvInfo!!.visibility = View.GONE

            ePlayer?.useController = true
            exoPlayer = ExoPlayerFactory.newSimpleInstance(
                activity,
                DefaultRenderersFactory(activity),
                DefaultTrackSelector(), DefaultLoadControl()
            )

            ePlayer?.player = exoPlayer

            exoPlayer?.playWhenReady = true

            exoPlayer?.addListener(playerListener)


            exoPlayer?.prepare(
                VideoUtils.buildMediaSource(
                    Uri.fromFile(masterVideoFile),
                    VideoFrom.LOCAL
                )
            )

            exoPlayer?.seekTo(0)

            exoPlayer?.seekTo(currentWindow, playbackPosition)
        } catch (exception: Exception) {
            Log.v(tagName, "exception: " + exception.localizedMessage)
        }
    }

    val playerListener = object : Player.EventListener {
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        }

        override fun onSeekProcessed() {
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray?,
            trackSelections: TrackSelectionArray?
        ) {
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            Log.v(tagName, "onPlayerError: ${error.toString()}")
            Toast.makeText(mContext, "Video format is not supported", Toast.LENGTH_LONG).show()
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            pbLoading?.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        override fun onPositionDiscontinuity(reason: Int) {
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {

        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

            if (playWhenReady && playbackState == Player.STATE_READY) {
                // Active playback.
            } else if (playWhenReady) {
                // Not playing because playback ended, the player is buffering, stopped or
                // failed. Check playbackState and player.getPlaybackError for details.
            } else {
                // Paused by app.
            }
        }
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    fun checkPermission(requestCode: Int, permission: String) {
        requestPermissions(arrayOf(permission), requestCode)
    }

    override fun openGallery() {
        releasePlayer()
        checkPermission(OptiConstant.VIDEO_GALLERY, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun openCamera() {
        TODO("Not yet implemented")
    }

//    override fun openCamera() {
//        releasePlayer()
//        checkAllPermission(OptiConstant.PERMISSION_CAMERA)
//    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            OptiConstant.VIDEO_GALLERY -> {
                for (permission in permissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            activity as Activity,
                            permission
                        )
                    ) {
                        Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                        break
                    } else {
                        if (ActivityCompat.checkSelfPermission(
                                activity as Activity,
                                permission
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            //call the gallery intent
                            OptiUtils.refreshGalleryAlone(context!!)
                            val i = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            )
                            i.type = "video/*"
                            i.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("video/*"))
                            startActivityForResult(i, OptiConstant.VIDEO_GALLERY)
                        } else {
                            callPermissionSettings()
                        }
                    }
                }
                return
            }

        }
    }

    fun callPermissionSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", context!!.applicationContext.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 300)
    }

    fun createSaveVideoFile(): File {
        val timeStamp: String =
            SimpleDateFormat(OptiConstant.DATE_FORMAT, Locale.getDefault()).format(Date())
        val imageFileName: String = OptiConstant.APP_NAME + timeStamp + "_"

        val path =
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + OptiConstant.APP_NAME + File.separator + OptiConstant.MY_VIDEOS + File.separator
        val folder = File(path)
        if (!folder.exists())
            folder.mkdirs()

        return File.createTempFile(imageFileName, OptiConstant.VIDEO_FORMAT, folder)
    }

    fun showBottomSheetDialogFragment(bottomSheetDialogFragment: BottomSheetDialogFragment) {
        val bundle = Bundle()
        bottomSheetDialogFragment.arguments = bundle
        bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.tag)
    }

    override fun videoOption(option: String) {
        //based on selected video editing option - helper, file is passed
        when (option) {


            OptiConstant.TEXT -> {
                masterVideoFile?.let { file ->
                    val addTextFragment = OptiAddTextFragment()
                    addTextFragment.setHelper(this@OptiMasterProcessorFragment)
                    addTextFragment.setFilePathFromSource(file)
                    showBottomSheetDialogFragment(addTextFragment)
                }

                if (masterVideoFile == null) {
                    OptiUtils.showGlideToast(
                        activity!!,
                        getString(R.string.error_text)
                    )
                }
            }

            OptiConstant.OBJECT -> {
                masterVideoFile?.let { file ->
                    val addClipArtFragment = OptiAddClipArtFragment()
                    addClipArtFragment.setHelper(this@OptiMasterProcessorFragment)
                    addClipArtFragment.setFilePathFromSource(file)
                    showBottomSheetDialogFragment(addClipArtFragment)
                }

                if (masterVideoFile == null) {
                    OptiUtils.showGlideToast(
                        activity!!,
                        getString(R.string.error_sticker)
                    )
                }
            }
        }
    }

    override fun onProgress(progress: String) {
        TODO("Not yet implemented")
    }

    override fun onSuccess(convertedFile: File, type: String) {
        TODO("Not yet implemented")
    }

    override fun onFailure(error: Exception) {
        TODO("Not yet implemented")
    }

    override fun onNotAvailable(error: Exception) {
        TODO("Not yet implemented")
    }

    override fun onFinish() {
        TODO("Not yet implemented")
    }

    fun stickerAdd(stickerResId: Int) {
        val layer = Layer()
        val pica: Bitmap = BitmapFactory.decodeResource(resources, stickerResId)

        val entity = ImageEntity(layer, pica, motionView!!.width, motionView!!.height)
        motionView!!.addEntityAndPosition(entity)
    }

    class StickerAdapter(
        var context: Context,
        private val listener: ClickListener,
        var stringList: MutableList<Int> = mutableListOf()
    ) :
        RecyclerView.Adapter<StickerAdapter.StickerHolder>() {

        interface ClickListener {
            fun onClickListener(item: Int)
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): StickerHolder {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.custom_show_stickers, viewGroup, false)
            return StickerHolder(view)
        }

        override fun onBindViewHolder(stickerHolder: StickerHolder, i: Int) {
            stickerHolder.stickers.setImageResource(stringList[i])
            stickerHolder.stickers.setOnClickListener {
                listener.onClickListener(stringList[i])
            }
        }

        override fun getItemCount(): Int {
            return this.stringList.size
        }

        inner class StickerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var stickers: ImageView = itemView.findViewById(R.id.image_sticker)

        }
    }
}