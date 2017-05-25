package cm.aptoide.pt.spotandshareandroid.view.radar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import cm.aptoide.pt.spotandshareandroid.R;
import cm.aptoide.pt.spotandshareandroid.group.Group;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by filipegoncalves on 30-08-2016.
 */
public class RadarTextView extends FrameLayout
    implements ViewTreeObserver.OnGlobalLayoutListener {

  private static final int idX = 0;
  private static final int idY = 1;
  private static final int idTxtLength = 2;
  private static final int idDist = 3;
  private static final int textSize = 12;
  private Random random;
  private int width;
  private int height;
  private int mode = RadarRippleView.MODE_OUT;
  private int fontColor = Color.parseColor("#000000");
  private int rippleViewDefaultColor = Color.parseColor("#aeaeae");
  private HotspotClickListener hotspotListener;
  private List<RadarRippleView> listOfHotspot;

  public RadarTextView(Context context) {
    super(context);
    init(null, context);
  }

  public RadarTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs, context);
  }

  public RadarTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs, context);
  }

  private void init(AttributeSet attrs, Context context) {
    random = new Random();
    getViewTreeObserver().addOnGlobalLayoutListener(this);
  }

  @Override public void onGlobalLayout() {
    int tmpWidth = getWidth();
    int tmpHeight = getHeight();
    if (width != tmpWidth || height != tmpHeight) {
      width = tmpWidth;
      height = tmpHeight;
    }
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  public void setOnHotspotClickListener(HotspotClickListener listener) {
    hotspotListener = listener;
  }

  public void show(final ArrayList<Group> vetorKeywords) {
    this.removeAllViews();

    if (width > 0 && height > 0 && vetorKeywords != null && vetorKeywords.size() > 0) {
      int xCenter = width >> 1;
      int yCenter = height >> 1;
      final int size = vetorKeywords.size();
      int xItem = width / (size + 1);
      int yItem = height / (size + 1);
      LinkedList<Integer> listX = new LinkedList<>();
      LinkedList<Integer> listY = new LinkedList<>();
      for (int i = 0; i < size; i++) {
        listX.add(i * xItem);
        listY.add(i * yItem + (yItem >> 2));
      }
      LinkedList<RadarRippleView> listTxtTop = new LinkedList<>();
      LinkedList<RadarRippleView> listTxtBottom = new LinkedList<>();

      listOfHotspot = new ArrayList<RadarRippleView>();

      for (int i = 0; i < size; i++) {
        final Group group = vetorKeywords.get(i);
        int ranColor = fontColor;
        int xy[] = randomXY(random, listX, listY, xItem);
        int txtSize = textSize;

        final RadarRippleView txt = new RadarRippleView(getContext());
        if (mode == RadarRippleView.MODE_IN) {
          txt.setMode(RadarRippleView.MODE_IN);
        } else {
          txt.setMode(RadarRippleView.MODE_OUT);
        }
        final String hotspotName = group.getDeviceName();
        txt.setText(hotspotName);
        txt.setTextColor(ranColor);

        txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, txtSize);
        txt.setGravity(Gravity.CENTER);
        txt.setOnClickListener(new OnClickListener() {
          @Override public void onClick(View view) {

            if (hotspotListener != null) {
              hotspotListener.onGroupClicked(group);
            }
          }
        });

        txt.startRippleAnimation();

        int strWidth = txt.getMeasuredWidth();
        xy[idTxtLength] = strWidth;
        if (xy[idX] + strWidth > width - (xItem)) {
          int baseX = width - strWidth;
          xy[idX] = baseX - xItem + random.nextInt(xItem >> 1);
        } else if (xy[idX] == 0) {
          xy[idX] = Math.max(random.nextInt(xItem), xItem / 3);
        }

        xy[idDist] = Math.abs(xy[idY] - yCenter);
        txt.setTag(xy);

        if (xy[idY] > yCenter) {
          listTxtBottom.add(txt);
        } else {
          listTxtTop.add(txt);
        }

        listOfHotspot.add(txt);
      }

      attach2Screen(listTxtTop, xCenter, yCenter, yItem);
      attach2Screen(listTxtBottom, xCenter, yCenter, yItem);
    }
  }

  private int[] randomXY(Random ran, LinkedList<Integer> listX, LinkedList<Integer> listY,
      int xItem) {
    int[] arr = new int[4];
    arr[idX] = listX.remove(ran.nextInt(listX.size()));
    arr[idY] = listY.remove(ran.nextInt(listY.size()));
    return arr;
  }

  public void deselectHotspot(Group group) {
    String aux = group.getDeviceName();
    for (int i = 0; i < listOfHotspot.size(); i++) {
      if (listOfHotspot.get(i)
          .getText()
          .toString()
          .equals(aux)) {
        listOfHotspot.get(i)
            .setTypeface(null, Typeface.NORMAL);
        listOfHotspot.get(i)
            .setEffectColor(rippleViewDefaultColor);
      }
    }
  }

  private void attach2Screen(LinkedList<RadarRippleView> listTxt, int xCenter, int yCenter,
      int yItem) {
    int size = listTxt.size();
    sortXYList(listTxt, size);
    for (int i = 0; i < size; i++) {
      RadarRippleView txt = listTxt.get(i);
      int[] iXY = (int[]) txt.getTag();
      int yDistance = iXY[idY] - yCenter;
      int yMove = Math.abs(yDistance);
      inner:
      for (int k = i - 1; k >= 0; k--) {
        int[] kXY = (int[]) listTxt.get(k)
            .getTag();
        int startX = kXY[idX];
        int endX = startX + kXY[idTxtLength];
        if (yDistance * (kXY[idY] - yCenter) > 0) {
          if (isXMixed(startX, endX, iXY[idX], iXY[idX] + iXY[idTxtLength])) {
            int tmpMove = Math.abs(iXY[idY] - kXY[idY]);
            if (tmpMove > yItem) {
              yMove = tmpMove;
            } else if (yMove > 0) {
              yMove = 0;
            }
            break inner;
          }
        }
      }

      if (yMove > yItem) {
        int maxMove = yMove - yItem;
        int randomMove = random.nextInt(maxMove);
        int realMove = Math.max(randomMove, maxMove >> 1) * yDistance / Math.abs(yDistance);
        iXY[idY] = iXY[idY] - realMove;
        iXY[idDist] = Math.abs(iXY[idY] - yCenter);
        sortXYList(listTxt, i + 1);
      }
      FrameLayout.LayoutParams layParams = new FrameLayout.LayoutParams(200, 200);
      layParams.gravity = Gravity.LEFT | Gravity.TOP;
      layParams.leftMargin = iXY[idX];
      layParams.topMargin = iXY[idY];
      addView(txt, layParams);
    }
  }

  private void sortXYList(LinkedList<RadarRippleView> listTxt, int endIdx) {
    for (int i = 0; i < endIdx; i++) {
      for (int k = i + 1; k < endIdx; k++) {
        if (((int[]) listTxt.get(k)
            .getTag())[idDist] < ((int[]) listTxt.get(i)
            .getTag())[idDist]) {
          RadarRippleView iTmp = listTxt.get(i);
          RadarRippleView kTmp = listTxt.get(k);
          listTxt.set(i, kTmp);
          listTxt.set(k, iTmp);
        }
      }
    }
  }

  private boolean isXMixed(int startA, int endA, int startB, int endB) {
    boolean result = false;
    if (startB >= startA && startB <= endA) {
      result = true;
    } else if (endB >= startA && endB <= endA) {
      result = true;
    } else if (startA >= startB && startA <= endB) {
      result = true;
    } else if (endA >= startB && endA <= endB) {
      result = true;
    }

    return result;
  }

  public void selectGroup(Group group) {
    for (int i = 0; i < listOfHotspot.size(); i++) {
      if (listOfHotspot.get(i)
          .getText()
          .equals(group.getDeviceName())) {
        listOfHotspot.get(i)
            .setEffectColor(getResources().getColor(R.color.aptoide_orange));
        listOfHotspot.get(i)
            .postInvalidate();
        listOfHotspot.get(i)
            .setTypeface(null, Typeface.BOLD);
      }
    }
  }

  public void stop() {
    listOfHotspot = null;
    hotspotListener = null;
  }

  public interface HotspotClickListener {
    void onGroupClicked(Group group);
  }
}

