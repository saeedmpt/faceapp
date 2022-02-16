package com.ai.chatapp.component;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by saeed on 12/15/2016.
 */

public class ProgressRequestBody extends RequestBody {
    private MediaType mContentType;
    private File mFile;
    private String mPath;
    private UploadCallbacks mListener;

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage, long uploaded, long total);

        void onError();

        void onFinish();
    }

    public ProgressRequestBody(final MediaType contentType, final File file, final UploadCallbacks listener) {
        mContentType = contentType;
        mFile = file;
        mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return mContentType;
    }

    @Override
    public long contentLength() throws IOException {
        if (mFile != null) {
            return mFile.length();
        }
        return 0;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (mFile != null) {
            long fileLength = mFile.length();
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            FileInputStream in = new FileInputStream(mFile);
            long uploaded = 0;

            try {
                int read;
                while ((read = in.read(buffer)) != -1) {
                    uploaded += read;
                    sink.write(buffer, 0, read);
                    notifyProgress(uploaded, fileLength);
                }
            } finally {
                in.close();
            }
        }

    }

    // to avoid duplicated notification
    private int mLastProgress;

    private void notifyProgress(final long uploaded, final long total) {
        if (mListener != null) {
            // update progress on UI thread
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    int progress = Math.round(100.0f * uploaded / total);
                    if (mLastProgress != progress) {
                        mLastProgress = progress;
                        mListener.onProgressUpdate(progress, uploaded, total);
                    }
                }
            });
        }
    }
}