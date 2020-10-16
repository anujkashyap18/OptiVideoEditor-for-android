package com.obs.marveleditor.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.obs.marveleditor.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;

import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

@Metadata(
        mv = {1, 1, 18},
        bv = {1, 0, 3},
        k = 1,
        d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0015\n\u0002\b\u0007\b&\u0018\u00002\u00020\u0001:\u0001\u001eB\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0007\u001a\u00020\bH\u0002J\u0010\u0010\t\u001a\u0004\u0018\u00010\u00052\u0006\u0010\n\u001a\u00020\u0005J\u0006\u0010\u000b\u001a\u00020\fJ\u0012\u0010\r\u001a\u00020\b2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0016J\u0012\u0010\u0010\u001a\u00020\b2\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0016J+\u0010\u0013\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\u00152\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0017\u001a\u00020\u0018H\u0016¢\u0006\u0002\u0010\u0019J\b\u0010\u001a\u001a\u00020\bH\u0016J\b\u0010\u001b\u001a\u00020\bH&J\u0006\u0010\u001c\u001a\u00020\bJ\u0006\u0010\u001d\u001a\u00020\bR\u0016\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0006¨\u0006\u001f"},
        d2 = {"Lcom/obs/marveleditor/fragments/OptiBaseCreatorDialogFragment;", "Landroid/support/v4/app/DialogFragment;", "()V", "permissionsRequired", "", "", "[Ljava/lang/String;", "callPermissionSettings", "", "getMimeType", "url", "isRunning", "", "onCancel", "dialog", "Landroid/content/DialogInterface;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onRequestPermissionsResult", "requestCode", "", "permissions", "grantResults", "", "(I[Ljava/lang/String;[I)V", "onResume", "permissionsBlocked", "showInProgressToast", "stopRunningProcess", "CallBacks", "app_debug"}
)
public abstract class OptiBaseCreatorDialogFragment extends DialogFragment {
    private String[] permissionsRequired = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
    private HashMap _$_findViewCache;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStyle(1, R.style.share_dialog1);
        this.setCancelable(false);
    }

    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        Intrinsics.checkParameterIsNotNull(permissions, "permissions");
        Intrinsics.checkParameterIsNotNull(grantResults, "grantResults");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 130:
                String[] var6 = permissions;
                int var7 = permissions.length;

                for (int var5 = 0; var5 < var7; ++var5) {
                    String permission = var6[var5];
                    FragmentActivity var10000 = this.getActivity();
                    if (var10000 == null) {
                        throw new TypeCastException("null cannot be cast to non-null type android.app.Activity");
                    }

                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) var10000, permission)) {
//                        Toast.makeText(this.getContext(), (CharSequence) "Permission Denied", 0).show();
                    } else {
                        Context var8 = this.getContext();
                        if (var8 == null) {
                            Intrinsics.throwNpe();
                        }

                        if (ActivityCompat.checkSelfPermission(var8, this.permissionsRequired[0]) != 0) {
                            this.callPermissionSettings();
                        }
                    }
                }

                return;
            default:
        }
    }

    public void onCancel(@Nullable DialogInterface dialog) {
        super.onCancel(dialog);
        this.stopRunningProcess();
    }

    private final void callPermissionSettings() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        Context var10001 = this.getContext();
        if (var10001 == null) {
            Intrinsics.throwNpe();
        }

        Intrinsics.checkExpressionValueIsNotNull(var10001, "context!!");
        var10001 = var10001.getApplicationContext();
        Intrinsics.checkExpressionValueIsNotNull(var10001, "context!!.applicationContext");
        Uri uri = Uri.fromParts("package", var10001.getPackageName(), (String) null);
        intent.setData(uri);
        this.startActivityForResult(intent, 300);
    }

    public void onResume() {
        super.onResume();
        Context var10000 = this.getContext();
        if (var10000 == null) {
            Intrinsics.throwNpe();
        }

        if (ActivityCompat.checkSelfPermission(var10000, this.permissionsRequired[0]) != 0) {
            this.requestPermissions(this.permissionsRequired, 130);
        }

    }

    public abstract void permissionsBlocked();

    public final void stopRunningProcess() {
        FFmpeg.getInstance((Context) this.getActivity()).killRunningProcesses();
    }

    public final boolean isRunning() {
        FFmpeg var10000 = FFmpeg.getInstance((Context) this.getActivity());
        Intrinsics.checkExpressionValueIsNotNull(var10000, "FFmpeg.getInstance(activity)");
        return var10000.isFFmpegCommandRunning();
    }

    public final void showInProgressToast() {
//        Toast.makeText((Context) this.getActivity(), (CharSequence) "Operation already in progress! Try again in a while.", ).show();
    }

    @Nullable
    public final String getMimeType(@NotNull String url) {
        Intrinsics.checkParameterIsNotNull(url, "url");
        String type = (String) null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        return type;
    }

    public View _$_findCachedViewById(int var1) {
        if (this._$_findViewCache == null) {
            this._$_findViewCache = new HashMap();
        }

        View var2 = (View) this._$_findViewCache.get(var1);
        if (var2 == null) {
            View var10000 = this.getView();
            if (var10000 == null) {
                return null;
            }

            var2 = var10000.findViewById(var1);
            this._$_findViewCache.put(var1, var2);
        }

        return var2;
    }

    public void _$_clearFindViewByIdCache() {
        if (this._$_findViewCache != null) {
            this._$_findViewCache.clear();
        }

    }

    // $FF: synthetic method
    public void onDestroyView() {
        super.onDestroyView();
        this._$_clearFindViewByIdCache();
    }

    @Metadata(
            mv = {1, 1, 18},
            bv = {1, 0, 3},
            k = 1,
            d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\bf\u0018\u00002\u00020\u0001J\n\u0010\u0002\u001a\u0004\u0018\u00010\u0003H&J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0003H&J\b\u0010\u0007\u001a\u00020\u0005H&J\u0010\u0010\b\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u0003H&J\b\u0010\n\u001a\u00020\u0005H&J\b\u0010\u000b\u001a\u00020\u0005H&J\b\u0010\f\u001a\u00020\u0005H&J\u0010\u0010\r\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000fH&¨\u0006\u0010"},
            d2 = {"Lcom/obs/marveleditor/fragments/OptiBaseCreatorDialogFragment$CallBacks;", "", "getFile", "Ljava/io/File;", "onAudioFileProcessed", "", "convertedAudioFile", "onDidNothing", "onFileProcessed", "file", "openCamera", "openGallery", "reInitPlayer", "showLoading", "isShow", "", "app_debug"}
    )
    public interface CallBacks {
        void onDidNothing();

        void onFileProcessed(@NotNull File var1);

        @Nullable
        File getFile();

        void reInitPlayer();

        void onAudioFileProcessed(@NotNull File var1);

        void showLoading(boolean var1);

        void openGallery();

        void openCamera();
    }
}

