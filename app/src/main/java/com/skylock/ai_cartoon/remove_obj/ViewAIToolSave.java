package com.skylock.ai_cartoon.remove_obj;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skylock.ai_cartoon.R;
import com.skylock.ai_cartoon.model.ResultItem;

import java.util.logging.Logger;

/* loaded from: classes7.dex */
public class ViewAIToolSave extends LinearLayout {
    private final Logger logger;
    private ImageView btnAiTool;
    private TextView btnSave;
    private FrameLayout captureView;
    private Context context;
    private String feature;
    private String from;
    private Integer imageHeight;
    private String imageUri;
    private Integer imageWidth;

    private ViewListener viewListener;

    public ViewAIToolSave(Context context) {
        super(context);
        this.logger = Logger.getLogger(ViewAIToolSave.class.getName());
        this.feature = "viewaitoolsave";
        init(context);
    }

    public ViewAIToolSave(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.logger = Logger.getLogger(ViewAIToolSave.class.getName());
        this.feature = "viewaitoolsave";
        init(context);
    }

    public ViewAIToolSave(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.logger = Logger.getLogger(ViewAIToolSave.class.getName());
        this.feature = "viewaitoolsave";
        init(context);
    }

    public ViewAIToolSave(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.logger = Logger.getLogger(ViewAIToolSave.class.getName());
        this.feature = "viewaitoolsave";
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        View.inflate(context, R.layout.layout_aitool_save, this);
        this.btnSave = findViewById(R.id.tvSave);
        this.btnAiTool = findViewById(R.id.cvAiTool);
        //this.loading = new Loading(context);
        this.btnSave.setOnClickListener(new OnClickListener() { // from class: mobi.zeezoo.photoenhancer.feature.view.ViewAIToolSave.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ViewAIToolSave.this.viewListener != null) {
                    ViewAIToolSave.this.viewListener.onShowChooseSave();
                }
            }
        });
        this.btnAiTool.setOnClickListener(new OnClickListener() { // from class: mobi.zeezoo.photoenhancer.feature.view.ViewAIToolSave.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ViewAIToolSave.this.viewListener != null) {
                    ViewAIToolSave.this.viewListener.onClickAiTool();
                }
            }
        });
    }

    public String save(final ResultItem resultItem, String str) {
      /*  if (resultItem != null) {
            this.imageUri = resultItem.getUrlAfter();
        }
        this.logger.info("save imageUri: " + this.imageUri);
       // ResultActivity.saveUri = null;
        if (this.feature.equals("blurbg")) {
            captureView(this.captureView);
        } else {
            final Integer valueOf = Integer.valueOf(Resources.getSystem().getDisplayMetrics().widthPixels >= 1080 ? 768 : 480);
            Log.d("SAVE", "save: " + this.imageUri);
            this.logger.info("save file " + this.imageUri);
            if (this.imageUri.contains(d.v) || this.imageUri.contains(d.u)) {
                FileDownloader.downloadFile(this.context, this.imageUri, str, new FileDownloader.OnDownloadListener() { // from class: mobi.zeezoo.photoenhancer.feature.view.ViewAIToolSave.3
                    @Override // mobi.zeezoo.photoenhancer.network.FileDownloader.OnDownloadListener
                    public void onDownloadFailed(String str2) {
                    }

                    @Override // mobi.zeezoo.photoenhancer.network.FileDownloader.OnDownloadListener
                    public void onDownloadCompleted(String str2, String str3) {
                        ResultActivity.saveUri = "file://" + str2;
                        ViewAIToolSave.this.logger.info("save file uri:  " + ResultActivity.saveUri);
                        ResultItem resultItem2 = resultItem;
                        if (resultItem2 != null) {
                            resultItem2.setUrlSave(ResultActivity.saveUri);
                        }
                        if (str2 != null) {
                            PhotoLibrary photoLibrary = new PhotoLibrary(str3, str2, "file://" + str2, null, "Aiphoto");
                            photoLibrary.setThumbUri("file://" + str2);
                            Constants.addPhotoToCategory(ViewAIToolSave.this.context, "Aiphoto", photoLibrary, 0, false, valueOf);
                        }
                    }
                });
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                ResultActivity.saveUri = Constants.saveToStorage(this.context, BitmapFactory.decodeFile(this.imageUri.replace("file://", ""), options), "png", null);
                if (resultItem != null) {
                    resultItem.setUrlSave(ResultActivity.saveUri);
                }
            }
        }*/
        return "";
    }

    private void captureView(FrameLayout frameLayout) {
      /*  Bitmap createBitmap = Bitmap.createBitmap(this.imageWidth.intValue(), this.imageHeight.intValue(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        frameLayout.layout(frameLayout.getLeft(), frameLayout.getTop(), frameLayout.getRight(), frameLayout.getBottom());
        frameLayout.draw(canvas);
        ResultActivity.saveUri = Constants.saveToStorage(this.context, createBitmap, "png", null);
        this.loading.dismiss();
        if (ResultActivity.saveUri == null) {
            Constants.showToast(this.context.getApplicationContext(), this.context.getString(R.string.photo_save_failed));
        }*/
    }

    public void initView(String str, Integer num, Integer num2, String str2, FrameLayout frameLayout, String str3) {
        this.imageUri = str;
        this.imageWidth = num;
        this.imageHeight = num2;
        this.feature = str2;
        this.captureView = frameLayout;
        this.from = str3;
    }

    public void setImageUri(String str) {
        this.imageUri = str;
    }

    public void setOnClickViewListener(ViewListener viewListener) {
        this.viewListener = viewListener;
    }

    public TextView getBtnSave() {
        return this.btnSave;
    }

    public void setMoreToolIcon(Drawable drawable) {
        this.btnAiTool.setImageDrawable(drawable);
    }

    /* loaded from: classes7.dex */
    public interface ViewListener {
        void onClickAiTool();

        void onShowChooseSave();
    }
}
