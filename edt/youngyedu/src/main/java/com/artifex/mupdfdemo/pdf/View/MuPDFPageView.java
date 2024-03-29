package com.artifex.mupdfdemo.pdf.View;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.EditText;

import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.pdf.PdfInterface.MuPDFView;
import com.artifex.mupdfdemo.pdf.PdfInterface.TextProcessor;
import com.artifex.mupdfdemo.pdf.bean.Annotation;
import com.artifex.mupdfdemo.pdf.bean.BitmapHolder;
import com.artifex.mupdfdemo.pdf.bean.LinkInfo;
import com.artifex.mupdfdemo.pdf.bean.PassClickResult;
import com.artifex.mupdfdemo.pdf.bean.PassClickResultChoice;
import com.artifex.mupdfdemo.pdf.bean.PassClickResultText;
import com.artifex.mupdfdemo.pdf.bean.PassClickResultVisitor;
import com.artifex.mupdfdemo.pdf.bean.TextWord;
import com.artifex.mupdfdemo.pdf.enumType.Hit;
import com.artifex.mupdfdemo.pdf.task.AsyncTask;
import com.yougy.ui.activity.R;


import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/29.
 */
public class MuPDFPageView extends PageView implements MuPDFView {
    private final MuPDFCore mCore;
    private AsyncTask<Void, Void, PassClickResult> mPassClick;
    private RectF mWidgetAreas[];
    private Annotation mAnnotations[];
    private int mSelectedAnnotationIndex = -1;
    private AsyncTask<Void, Void, RectF[]> mLoadWidgetAreas;
    private AsyncTask<Void, Void, Annotation[]> mLoadAnnotations;
    private AlertDialog.Builder mTextEntryBuilder;
    private AlertDialog.Builder mChoiceEntryBuilder;
    private AlertDialog mTextEntry;
    private EditText mEditText;
    private AsyncTask<String, Void, Boolean> mSetWidgetText;
    private AsyncTask<String, Void, Void> mSetWidgetChoice;
    private AsyncTask<PointF[], Void, Void> mAddStrikeOut;
    private AsyncTask<PointF[][], Void, Void> mAddInk;
    private AsyncTask<Integer, Void, Void> mDeleteAnnotation;
    private Runnable changeReporter;

    public MuPDFPageView(Context c, MuPDFCore core, Point parentSize) {
        super(c, parentSize);

        mCore = core;
        mTextEntryBuilder = new AlertDialog.Builder(c);
        mTextEntryBuilder.setTitle(getContext().getString(R.string.fill_out_text_field));
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mEditText = (EditText) inflater.inflate(R.layout.textentry, null);
        mTextEntryBuilder.setView(mEditText);
        mTextEntryBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mTextEntryBuilder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mSetWidgetText = new AsyncTask<String, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(String... arg0) {
                        return mCore.setFocusedWidgetText(mPageNumber, arg0[0]);
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        changeReporter.run();
                        if (!result)
                            invokeTextDialog(mEditText.getText().toString());
                    }
                };

                mSetWidgetText.execute(mEditText.getText().toString());
            }
        });
        mTextEntry = mTextEntryBuilder.create();

        mChoiceEntryBuilder = new AlertDialog.Builder(c);
        mChoiceEntryBuilder.setTitle(getContext().getString(R.string.choose_value));

    }


    /////////////////////////////private method/////////////////////////////////////////
    private void invokeTextDialog(String text) {
        mEditText.setText(text);
        mTextEntry.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mTextEntry.show();
    }

    private void invokeChoiceDialog(final String[] options) {
        mChoiceEntryBuilder.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mSetWidgetChoice = new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... params) {
                        String[] sel = {params[0]};
                        mCore.setFocusedWidgetChoiceSelected(sel);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        changeReporter.run();
                    }
                };

                mSetWidgetChoice.execute(options[which]);
            }
        });
        AlertDialog dialog = mChoiceEntryBuilder.create();
        dialog.show();
    }


    private void loadAnnotations() {
        mAnnotations = null;
        if (mLoadAnnotations != null)
            mLoadAnnotations.cancel(true);
        mLoadAnnotations = new AsyncTask<Void, Void, Annotation[]>() {
            @Override
            protected Annotation[] doInBackground(Void... params) {
                return mCore.getAnnoations(mPageNumber);
            }

            @Override
            protected void onPostExecute(Annotation[] result) {
                mAnnotations = result;
            }
        };

        mLoadAnnotations.execute();
    }


    /////////////////////////////MuPDFView impl/////////////////////////////////////////
    @Override
    public void setScale(float scale) {

    }

    @Override
    public Hit passClickEvent(float x, float y) {

        float scale = mSourceScale * (float) getWidth() / (float) mSize.x;
        final float docRelX = (x - getLeft()) / scale;
        final float docRelY = (y - getTop()) / scale;
        boolean hit = false;
        int i;

        if (mAnnotations != null) {
            for (i = 0; i < mAnnotations.length; i++)
                if (mAnnotations[i].contains(docRelX, docRelY)) {
                    hit = true;
                    break;
                }

            if (hit) {
                switch (mAnnotations[i].type) {
                    case HIGHLIGHT:
                    case UNDERLINE:
                    case SQUIGGLY:
                    case STRIKEOUT:
                    case INK:
                        mSelectedAnnotationIndex = i;
                        setItemSelectBox(mAnnotations[i]);
                        return Hit.Annotation;
                }
            }
        }

        mSelectedAnnotationIndex = -1;
        setItemSelectBox(null);

        if (!MuPDFCore.javascriptSupported())
            return Hit.Nothing;

        if (mWidgetAreas != null) {
            for (i = 0; i < mWidgetAreas.length && !hit; i++)
                if (mWidgetAreas[i].contains(docRelX, docRelY))
                    hit = true;
        }

        if (hit) {
            mPassClick = new AsyncTask<Void, Void, PassClickResult>() {
                @Override
                protected PassClickResult doInBackground(Void... arg0) {
                    return mCore.passClickEvent(mPageNumber, docRelX, docRelY);
                }

                @Override
                protected void onPostExecute(PassClickResult result) {
                    if (result.changed) {
                        changeReporter.run();
                    }

                    result.acceptVisitor(new PassClickResultVisitor() {
                        @Override
                        public void visitText(PassClickResultText result) {
                            invokeTextDialog(result.text);
                        }

                        @Override
                        public void visitChoice(PassClickResultChoice result) {
                            invokeChoiceDialog(result.options);
                        }
                    });
                }
            };

            mPassClick.execute();
            return Hit.Widget;
        }

        return Hit.Nothing;

    }

    @Override
    public LinkInfo hitLink(float x, float y) {

        // Since link highlighting was implemented, the super class
        // PageView has had sufficient information to be able to
        // perform this method directly. Making that change would
        // make MuPDFCore.hitLinkPage superfluous.
        float scale = mSourceScale * (float) getWidth() / (float) mSize.x;
        float docRelX = (x - getLeft()) / scale;
        float docRelY = (y - getTop()) / scale;

        for (LinkInfo l : mLinks)
            if (l.rect.contains(docRelX, docRelY))
                return l;

        return null;
    }

    @Override
    public boolean copySelection() {

        final StringBuilder text = new StringBuilder();

        processSelectedText(new TextProcessor() {
            StringBuilder line;

            public void onStartLine() {
                line = new StringBuilder();
            }

            public void onWord(TextWord word) {
                if (line.length() > 0)
                    line.append(' ');
                line.append(word.w);
            }

            public void onEndLine() {
                if (text.length() > 0)
                    text.append('\n');
                text.append(line);
            }
        });

        if (text.length() == 0)
            return false;

        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.content.ClipboardManager cm = (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

            cm.setPrimaryClip(ClipData.newPlainText("MuPDF", text));
        } else {
            android.text.ClipboardManager cm = (android.text.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(text);
        }

        deselectText();

        return true;

    }

    @Override
    public boolean markupSelection(final Annotation.Type type) {

        final ArrayList<PointF> quadPoints = new ArrayList<PointF>();
        processSelectedText(new TextProcessor() {
            RectF rect;

            public void onStartLine() {
                rect = new RectF();
            }

            public void onWord(TextWord word) {
                rect.union(word);
            }

            public void onEndLine() {
                if (!rect.isEmpty()) {
                    quadPoints.add(new PointF(rect.left, rect.bottom));
                    quadPoints.add(new PointF(rect.right, rect.bottom));
                    quadPoints.add(new PointF(rect.right, rect.top));
                    quadPoints.add(new PointF(rect.left, rect.top));
                }
            }
        });

        if (quadPoints.size() == 0)
            return false;

        mAddStrikeOut = new AsyncTask<PointF[], Void, Void>() {
            @Override
            protected Void doInBackground(PointF[]... params) {
                addMarkup(params[0], type);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                loadAnnotations();
                update();
            }
        };

        mAddStrikeOut.execute(quadPoints.toArray(new PointF[quadPoints.size()]));

        deselectText();

        return true;

    }

    @Override
    public void deleteSelectedAnnotation() {

        if (mSelectedAnnotationIndex != -1) {
            if (mDeleteAnnotation != null)
                mDeleteAnnotation.cancel(true);

            mDeleteAnnotation = new AsyncTask<Integer, Void, Void>() {
                @Override
                protected Void doInBackground(Integer... params) {
                    mCore.deleteAnnotation(mPageNumber, params[0]);
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    loadAnnotations();
                    update();
                }
            };

            mDeleteAnnotation.execute(mSelectedAnnotationIndex);

            mSelectedAnnotationIndex = -1;
            setItemSelectBox(null);
        }

    }

    @Override
    public void deselectAnnotation() {
        mSelectedAnnotationIndex = -1;
        setItemSelectBox(null);
    }

    @Override
    public boolean saveDraw() {

        PointF[][] path = getDraw();

        if (path == null)
            return false;

        if (mAddInk != null) {
            mAddInk.cancel(true);
            mAddInk = null;
        }
        mAddInk = new AsyncTask<PointF[][], Void, Void>() {
            @Override
            protected Void doInBackground(PointF[][]... params) {
                mCore.addInkAnnotation(mPageNumber, params[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                loadAnnotations();
                update();
            }

        };

        mAddInk.execute(getDraw());
        cancelDraw();

        return true;

    }

    @Override
    public void setChangeReporter(Runnable reporter) {
        changeReporter = reporter;
    }


    @Override
    protected Bitmap drawPage(int sizeX, int sizeY, int patchX, int patchY, int patchWidth, int patchHeight) {
        return mCore.drawPage(mPageNumber, sizeX, sizeY, patchX, patchY, patchWidth, patchHeight);

    }


    @Override
    protected Bitmap updatePage(BitmapHolder h, int sizeX, int sizeY, int patchX, int patchY, int patchWidth, int patchHeight) {

        return mCore.updatePage(h, mPageNumber, sizeX, sizeY, patchX, patchY, patchWidth, patchHeight);

    }

    @Override
    protected LinkInfo[] getLinkInfo() {
        return mCore.getPageLinks(mPageNumber);
    }

    @Override
    protected TextWord[][] getText() {
        return mCore.textLines(mPageNumber);
    }

    @Override
    protected void addMarkup(PointF[] quadPoints, Annotation.Type type) {
        mCore.addMarkupAnnotation(mPageNumber, quadPoints, type);
    }


    @Override
    public void setPage(final int page, PointF size) {
        //  loadAnnotations();


        mLoadWidgetAreas = new AsyncTask<Void, Void, RectF[]>() {
            @Override
            protected RectF[] doInBackground(Void... arg0) {
                return mCore.getWidgetAreas(page);
            }

            @Override
            protected void onPostExecute(RectF[] result) {
                mWidgetAreas = result;
            }
        };

        mLoadWidgetAreas.execute();

        super.setPage(page, size);
    }


    @Override
    public void releaseResources() {
        if (mPassClick != null) {
            mPassClick.cancel(true);
            mPassClick = null;
        }

        if (mLoadWidgetAreas != null) {
            mLoadWidgetAreas.cancel(true);
            mLoadWidgetAreas = null;
        }

        if (mLoadAnnotations != null) {
            mLoadAnnotations.cancel(true);
            mLoadAnnotations = null;
        }

        if (mSetWidgetText != null) {
            mSetWidgetText.cancel(true);
            mSetWidgetText = null;
        }

        if (mSetWidgetChoice != null) {
            mSetWidgetChoice.cancel(true);
            mSetWidgetChoice = null;
        }

        if (mAddStrikeOut != null) {
            mAddStrikeOut.cancel(true);
            mAddStrikeOut = null;
        }

        if (mDeleteAnnotation != null) {
            mDeleteAnnotation.cancel(true);
            mDeleteAnnotation = null;
        }

        super.releaseResources();
    }
}
